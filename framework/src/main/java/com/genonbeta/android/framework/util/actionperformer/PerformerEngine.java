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

import java.util.ArrayList;
import java.util.List;

public class PerformerEngine implements IPerformerEngine
{
    final private List<IBaseEngineConnection> mConnectionList = new ArrayList<>();
    final private List<PerformerListener> mPerformerListenerList = new ArrayList<>();

    public <T extends Selectable> boolean check(IEngineConnection<T> engineConnection, T selectable, boolean isSelected,
                                                int position)
    {
        synchronized (mPerformerListenerList) {
            for (PerformerListener listener : mPerformerListenerList)
                listener.onSelection(this, engineConnection, selectable, isSelected, position);
        }

        return true;
    }

    @Override
    public boolean ensureSlot(IBaseEngineConnection selectionConnection)
    {
        synchronized (mConnectionList) {
            return mConnectionList.contains(selectionConnection) || mConnectionList.add(selectionConnection);
        }
    }

    @Override
    public boolean removeSlot(IBaseEngineConnection selectionConnection)
    {
        synchronized (mConnectionList) {
            return mConnectionList.remove(selectionConnection);
        }
    }

    @Override
    public void removeSlots()
    {
        synchronized (mConnectionList) {
            mConnectionList.clear();
        }
    }

    public boolean addPerformerListener(PerformerListener listener)
    {
        synchronized (mPerformerListenerList) {
            return mPerformerListenerList.contains(listener) || mPerformerListenerList.add(listener);
        }
    }

    public boolean removePerformerListener(PerformerListener listener)
    {
        synchronized (mPerformerListenerList) {
            return mPerformerListenerList.remove(listener);
        }
    }
}