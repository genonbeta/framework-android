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

/**
 * created by: Veli
 * date: 20.11.2017 00:15
 */

public class Interrupter
{
	private boolean mInterrupted = false;
	private boolean mInterruptedByUser = false;
	private List<Closer> mClosers = new ArrayList<>();

	public boolean addCloser(Closer closer)
	{
		synchronized (getClosers()) {
			return getClosers().add(closer);
		}
	}

	public boolean hasCloser(Closer closer)
	{
		synchronized (getClosers()) {
			return getClosers().contains(closer);
		}
	}

	public List<Closer> getClosers()
	{
		return mClosers;
	}

	public boolean interrupted()
	{
		return mInterrupted;
	}

	public boolean interruptedByUser()
	{
		return mInterruptedByUser;
	}

	public boolean interrupt()
	{
		return interrupt(true);
	}

	public boolean interrupt(boolean userAction)
	{
		if (userAction)
			mInterruptedByUser = true;

		if (interrupted())
			return false;

		mInterrupted = true;

		synchronized (getClosers()) {
			for (Closer closer : getClosers())
				closer.onClose(userAction);
		}

		return true;
	}

	public boolean removeCloser(Closer closer)
	{
		synchronized (getClosers()) {
			return getClosers().remove(closer);
		}
	}

	public void reset()
	{
		reset(true);
	}

	public void reset(boolean resetClosers)
	{
		mInterrupted = false;
		mInterruptedByUser = false;

		if (resetClosers)
			removeClosers();
	}

	public void removeClosers()
	{
		synchronized (getClosers()) {
			getClosers().clear();
		}
	}

	public interface Closer
	{
		void onClose(boolean userAction);
	}
}
