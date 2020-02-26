/*
 * Copyright (C) 2020 Veli Tasalı
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.genonbeta.android.framework.util.actionperformer;

import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

/**
 * This class takes care of connecting {@link IPerformerEngine} to the UI element that needs to be free of limitations
 * like knowing whether the {@link T} is something that it can work on. It does that by extending from
 * {@link IBaseEngineConnection}. Also note that the term "connection" is used loosely and doesn't mean that there is an
 * IPC connection or whatsoever.
 *
 * @param <T> The derivative of the {@link Selectable} class
 */
public interface IEngineConnection<T extends Selectable> extends IBaseEngineConnection
{
    /**
     * Add a listener that will only be called by this specific connection or more connections with same T parameter.
     *
     * @param listener to be called when the selection state of a selectable changes
     * @return true when the listener is added or already exist
     */
    boolean addSelectionListener(SelectionListener<T> listener);

    /**
     * @return a quick call of {@link SelectableHost#getSelectableList()}
     * @see #getSelectableHost()
     */
    List<T> getSelectedItemList();

    /**
     * @return a quick call of {@link SelectableProvider#getSelectableList()}
     * @see #getSelectableProvider()
     */
    List<T> getAvailableList();

    /**
     * @return the host that holds the selected objects
     * @see SelectableHost
     */
    SelectableHost<T> getSelectableHost();

    /**
     * @return the provider that is used to identify the selectable object
     * @see SelectableProvider
     */
    SelectableProvider<T> getSelectableProvider();

    /**
     * Ensure that the given selectable object is stored in {@link SelectableHost}
     *
     * @param selectable that needs to be checked whether it is stored
     * @return true when it exists in the host's list
     */
    boolean isSelectedOnHost(T selectable);

    /**
     * Remove the previously added listener.
     *
     * @param listener to be removed
     * @return true when the listened was exist and now removed
     */
    boolean removeSelectionListener(SelectionListener<T> listener);

    /**
     * @param host to hold the items marked as selected
     */
    void setSelectableHost(SelectableHost<T> host);

    /**
     * @param provider that gives the items
     * @see #getSelectableProvider()
     */
    void setSelectableProvider(SelectableProvider<T> provider);

    /**
     * Alter the state of the selectable without specifying its location in {@link #getAvailableList()}. Even
     * though it shouldn't be important to have the position, it may later be required to be used with
     * {@link IPerformerEngine#check(IEngineConnection, Selectable, boolean, int)}. Also because the new state is not
     * specified, it will be the opposite what it previously was.
     *
     * @return true if the given selectable is selected
     * @throws CouldNotAlterException when the call fails to complete for some reason (see error msg for details)
     * @see #setSelected(Selectable, int)
     */
    boolean setSelected(T selectable) throws CouldNotAlterException;

    /**
     * Apart from {@link #setSelected(Selectable)}, this does not make decision on the new state.
     *
     * @see #setSelected(Selectable, int, boolean)
     */
    boolean setSelected(T selectable, boolean selected);

    /**
     * The same as {@link #setSelected(Selectable)}, but this time the position is also provided. Also, because,
     * the new state will be based upon the old state, the methods that don't take the new state as a parameter will
     * return the new state instead of the result of the call. The result of the call can still be determined by using
     * try-catch blocks.
     *
     * @param selectable to be altered
     * @return true if the given selectable is selected
     * @throws CouldNotAlterException when the call fails to complete for some reason (see error msg for details)
     * @see #setSelected(Selectable, int, boolean)
     */
    boolean setSelected(T selectable, int position) throws CouldNotAlterException;

    /**
     * Mark the given selectable with the given state 'selected'. If it is already in that state
     * return true and don't call {@link IPerformerEngine#check(IEngineConnection, Selectable, boolean, int)}.
     * The return value reflects if the call is successful, not the selection state.
     *
     * @param selectable to be altered
     * @param position   where the selectable is located in {@link #getAvailableList()} which can also be
     *                   {@link RecyclerView#NO_POSITION} if it is not known
     * @param selected   is the new state
     * @return true if the new state is applied or was already the same
     */
    boolean setSelected(T selectable, int position, boolean selected);

    /**
     * This is only called by the {@link IEngineConnection} owning it. The idea here is that you want to update
     * the UI according to changes made on the connection, but don't want to be warned when connections unrelated to
     * what you are dealing with changes as it happens with {@link PerformerListener} on {@link IPerformerEngine}.
     *
     * @param <T> type that this listener will be called from
     *
     */
    public interface SelectionListener<T extends Selectable>
    {
        void onSelected(IPerformerEngine engine, IEngineConnection<T> owner, T selectable, boolean isSelected,
                        int position);
    }
}
