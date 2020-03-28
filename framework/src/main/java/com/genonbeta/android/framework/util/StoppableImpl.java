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

package com.genonbeta.android.framework.util;

import java.util.ArrayList;
import java.util.List;

public class StoppableImpl implements Stoppable
{
    private boolean mInterrupted = false;
    private boolean mInterruptedByUser = false;
    private List<Stoppable.Closer> mClosers = new ArrayList<>();

    @Override
    public boolean addCloser(Stoppable.Closer closer)
    {
        synchronized (getClosers()) {
            return getClosers().add(closer);
        }
    }

    @Override
    public boolean hasCloser(Stoppable.Closer closer)
    {
        synchronized (getClosers()) {
            return getClosers().contains(closer);
        }
    }

    @Override
    public List<Stoppable.Closer> getClosers()
    {
        return mClosers;
    }

    @Override
    public boolean isInterrupted()
    {
        return mInterrupted;
    }

    @Override
    public boolean isInterruptedByUser()
    {
        return mInterruptedByUser;
    }

    @Override
    public boolean interrupt()
    {
        return interrupt(true);
    }

    @Override
    public boolean interrupt(boolean userAction)
    {
        if (userAction)
            mInterruptedByUser = true;

        if (isInterrupted())
            return false;

        mInterrupted = true;

        synchronized (getClosers()) {
            for (Stoppable.Closer closer : getClosers())
                closer.onClose(userAction);
        }

        return true;
    }

    @Override
    public boolean removeCloser(Stoppable.Closer closer)
    {
        synchronized (getClosers()) {
            return getClosers().remove(closer);
        }
    }

    @Override
    public void reset()
    {
        reset(true);
    }

    @Override
    public void reset(boolean resetClosers)
    {
        mInterrupted = false;
        mInterruptedByUser = false;

        if (resetClosers)
            removeClosers();
    }

    @Override
    public void removeClosers()
    {
        synchronized (getClosers()) {
            getClosers().clear();
        }
    }
}
