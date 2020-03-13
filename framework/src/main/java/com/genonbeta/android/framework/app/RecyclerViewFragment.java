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

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

/**
 * created by: veli
 * date: 26.03.2018 11:45
 */

abstract public class RecyclerViewFragment<T, V extends RecyclerViewAdapter.ViewHolder,
        E extends RecyclerViewAdapter<T, V>> extends ListFragment<RecyclerView, T, E>
{
    private final Handler mHandler = new Handler();
    private final Runnable mRequestFocus = new Runnable()
    {
        @Override
        public void run()
        {
            getListViewInternal().focusableViewAvailable(getListViewInternal());
        }
    };

    @Override
    protected void onListRefreshed()
    {
        super.onListRefreshed();
        setListShown(getAdapter().getCount() > 0);
    }

    @Override
    protected void ensureList()
    {
        mHandler.post(mRequestFocus);
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
        return new LinearLayoutManager(getContext());
    }

    @Override
    protected View generateDefaultView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        return inflater.inflate(R.layout.genfw_listfragment_default_rv, container, false);
    }

    @Override
    protected void setListAdapter(E adapter, boolean hadAdapter)
    {
        getListView().setAdapter(adapter);
    }

    @Override
    protected void setListView(RecyclerView listView)
    {
        super.setListView(listView);
        listView.setLayoutManager(getLayoutManager());
    }
}
