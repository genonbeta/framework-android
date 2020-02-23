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
 * A UI-related class that handles {@link IEngineConnection} and {@link PerformerListener} to help them communicate with
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
     * {@link PerformerListener} classes.
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
     * Compile the list of selectables that are held in the host of their owners, in other words, make
     * a list of selectables that are marked as selected.
     *
     * @return the compiled list
     */
    List<? extends Selectable> getSelectionList();
}
