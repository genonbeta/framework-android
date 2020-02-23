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

import androidx.annotation.Nullable;
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
     * @return a quick call of {@link SelectableHost#getSelectableList()}
     * @see #getSelectableHost()
     */
    List<T> getHostList();

    /**
     * @return a quick call of {@link SelectableProvider#getSelectableList()}
     * @see #getSelectableProvider()
     */
    List<T> getSelectionList();

    /**
     * @return the host that holds the selected objects
     * @see SelectableHost
     */
    SelectableHost<T> getSelectableHost();

    /**
     * @return the provider that is used to identify the selectable object when used with {@link #setSelected(int)}
     * @see SelectableProvider
     */
    SelectableProvider<T> getSelectableProvider();

    /**
     * Ensure that the given selectable object is stored in {@link SelectableHost}
     *
     * @param selectable that needs to be checked whether it is stored
     * @return true when it exists in the host's list
     */
    boolean isSelected(T selectable);

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
     * Find the selectable in the list with the ViewHolder that knows where it is positioned. What this does is get the
     * position and call {@link #setSelected(int)}.
     *
     * @param holder to use {@link RecyclerView.ViewHolder#getAdapterPosition()} with
     * @see #setSelected(Selectable, boolean, int)
     */
    boolean setSelected(RecyclerView.ViewHolder holder);

    /**
     * Find the selectable in {@link #getSelectionList()}
     *
     * @see #setSelected(Selectable, boolean, int)
     */
    boolean setSelected(int position);

    /**
     * Alter the state of the selectable without specifying the its location in {@link #getSelectionList()}. Even
     * though it shouldn't be important to have the position, it may later be required to be used with
     * {@link IPerformerEngine#check(IEngineConnection, Selectable, boolean, int)}. Also because the new state is not
     * specified, it will be the opposite what it previously was.
     *
     * @see #setSelected(Selectable, boolean, int)
     */
    boolean setSelected(T selectable);

    /**
     * Apart from {@link #setSelected(Selectable)}, this does not make decision on the new state.
     *
     * @see #setSelected(Selectable, boolean, int)
     */
    boolean setSelected(T selectable, boolean selected);

    /**
     * The same as {@link #setSelected(Selectable)}, but this time the position is also provided.
     *
     * @param selectable to be altered
     *
     * @see #setSelected(Selectable, boolean, int)
     */
    boolean setSelected(T selectable, int position);

    /**
     * Mark the given selectable with the given state 'selected'. If it is already in that state
     * return true and don't call {@link IPerformerEngine#check(IEngineConnection, Selectable, boolean, int)}.
     *
     * @param selectable to be altered
     * @param selected   is the new state
     * @param position   where the selectable is located in {@link #getSelectionList()} which can also be
     *                   {@link RecyclerView#NO_POSITION} if it is not known
     * @return true if the new state is applied or was already the same
     */
    boolean setSelected(T selectable, boolean selected, int position);
}
