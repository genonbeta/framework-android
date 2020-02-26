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

import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

/**
 * If you want to be informed when {@link IEngineConnection#setSelected} is invoked you can do so with this listener.
 * Unlike {@link PerformerCallback}, this doesn't have the ability to manipulate the process. This is informed after
 * the process is done.
 */
public interface PerformerListener
{
    /**
     * This is called when the state of a {@link Selectable} has changed.
     *
     * @param engine     that is holding an instance of this class
     * @param owner      is the connection that is making the call
     * @param selectable is the {@link Selectable} whose state has been changed
     * @param isSelected is the new state that has been set
     * @param position   is where the {@link Selectable} is positioned in
     *                   {@link SelectableProvider#getSelectableList()}
     */
    void onSelected(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable, boolean isSelected,
                    int position);

    /**
     * This method is called after a list of {@link Selectable}s has changed.
     *
     * @param engine         that is holding an instance of this class
     * @param owner          is the connection that is making the call
     * @param selectableList is the list of {@link Selectable}s whose states have been changed
     * @param isSelected     is the new state that has been set
     * @param positions      are where the {@link Selectable}s are positioned in
     *                       {@link SelectableProvider#getSelectableList()}
     */
    void onSelected(IPerformerEngine engine, IBaseEngineConnection owner, List<? extends Selectable> selectableList,
                    boolean isSelected, int[] positions);
}
