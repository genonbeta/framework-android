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

package com.genonbeta.android.framework.app;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

/**
 * created by: Veli
 * date: 28.03.2018 09:42
 */

abstract public class DynamicRecyclerViewFragment<T, V extends RecyclerViewAdapter.ViewHolder,
        Z extends RecyclerViewAdapter<T, V>> extends RecyclerViewFragment<T, V, Z>
{
    public GridLayoutManager generateGridLayoutManager()
    {
        return new GridLayoutManager(getContext(), isScreenLarge() && !isHorizontalOrientation() ? 2 : 1,
                isHorizontalOrientation() ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager()
    {
        return generateGridLayoutManager();
    }

    public boolean isHorizontalOrientation()
    {
        return false;
    }

    @Override
    protected void setListAdapter(Z adapter, boolean hadAdapter)
    {
        adapter.setUseHorizontalOrientation(isHorizontalOrientation());
        super.setListAdapter(adapter, hadAdapter);
    }
}
