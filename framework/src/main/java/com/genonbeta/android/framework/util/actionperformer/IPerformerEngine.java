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
 * A UI-related class that handles {@link IEngineConnection} and {@link PerformerCallback} to help them communicate with
 * the UI and each other.
 *
 * @see PerformerEngine as an implementation example
 */
public interface IPerformerEngine
{

    /**
     * This is called when we want to ensure if there is any {@link IBaseEngineConnection} on any slot.
     *
     * @return true when there is at least one
     */
    boolean hasActiveSlots();

    /**
     * Ensure that the related connection is known and has an active slot in the list of connections.
     *
     * @param selectionConnection is the connection that should have an active connection
     * @return true if there is already a connection or added a new one.
     */
    boolean ensureSlot(PerformerEngineProvider provider, IBaseEngineConnection selectionConnection);

    /**
     * Inform all the {@link PerformerListener} objects after the {@link #check(IEngineConnection, Selectable, boolean,
     * int)} call. Unlike that method, this doesn't have any ability to manipulate the task.
     *
     * @param engineConnection that is making the call
     * @param selectable       item that is being updated
     * @param isSelected       true when {@link Selectable} is being marked as selected
     * @param position         the position of the {@link Selectable} in the list which should be
     *                         {@link RecyclerView#NO_POSITION} if it is not known.
     * @param <T>              type of selectable expected to be received and used over {@link IEngineConnection}
     */
    <T extends Selectable> void informListeners(IEngineConnection<T> engineConnection, T selectable,
                                                boolean isSelected, int position);

    /**
     * Remove the connection from the list that is no longer needed.
     *
     * @param selectionConnection is the connection to be removed
     * @return true when the connection exists and removed
     */
    boolean removeSlot(IBaseEngineConnection selectionConnection);

    /**
     * Remove all the connection instances from the known connections list.
     */
    void removeSlots();

    /**
     * This is a call that is usually made by {@link IEngineConnection#setSelected} to notify the
     * {@link PerformerCallback} classes.
     *
     * @param engineConnection that is making the call
     * @param selectable       item that is being updated
     * @param isSelected       true when {@link Selectable} is being marked as selected
     * @param position         the position of the {@link Selectable} in the list which should be
     *                         {@link RecyclerView#NO_POSITION} if it is not known.
     * @param <T>              type of selectable expected to be received and used over {@link IEngineConnection}
     */
    <T extends Selectable> boolean check(IEngineConnection<T> engineConnection, T selectable, boolean isSelected,
                                         int position);

    /**
     * Compile the list of selectables that are held in the host of their owners, in other words, make a list of
     * selectables that are marked as selected from all connections. The problem is, though this is easier to
     * access each element, it isn't easy to refer to their owners after they are referred to as generic
     * {@link Selectable}. The better approach is to never mention them outside of their context.
     *
     * @return the compiled list
     */
    List<? extends Selectable> getSelectionList();

    /**
     * If you need to individually refer to the list elements without losing their identity in the process, you can
     * use this method to access the each connection and make changes in their own context.
     *
     * @return a compiled list of connections
     */
    List<IBaseEngineConnection> getConnectionList();

    /**
     * Add a listener to be called when something changes on the selection and manipulate it before completing the
     * process.
     *
     * @param callback to be called during the process
     * @return true when the callback already exists or added
     * @see #removePerformerCallback(PerformerCallback)
     */
    boolean addPerformerCallback(PerformerCallback callback);

    /**
     * Add a listener to be called after something changes on the selection list.
     *
     * @param listener to be called.
     * @return true when the listener is added or on the list
     * @see #removePerformerListener(PerformerListener)
     */
    boolean addPerformerListener(PerformerListener listener);

    /**
     * Remove the previously added callback.
     *
     * @param callback to be removed
     * @return true when the callback was in the list and removed
     * @see #addPerformerCallback(PerformerCallback)
     */
    boolean removePerformerCallback(PerformerCallback callback);

    /**
     * Remove a previously added listener from the list of listeners that are called when a selectable state changes.
     *
     * @param listener to be removed
     * @return true when the listener was on the list and removed
     * @see #addPerformerListener(PerformerListener)
     */
    boolean removePerformerListener(PerformerListener listener);
}
