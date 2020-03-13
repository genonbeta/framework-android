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
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.ListViewAdapter;

/**
 * created by: veli
 * date: 26.03.2018 10:48
 */

public abstract class ListViewFragment<T, E extends ListViewAdapter<T>> extends ListFragment<ListView, T, E>
{
    private final Handler mHandler = new Handler();
    private final Runnable mRequestFocus = new Runnable()
    {
        @Override
        public void run()
        {
            getListViewInternal().focusableViewAvailable(getListView());
        }
    };
    private final AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id)
        {
            onListItemClick((ListView) parent, v, position, id);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(mOnClickListener);
    }

    public void onListItemClick(ListView l, View v, int position, long id)
    {
    }

    @Override
    protected void ensureList()
    {
        getListViewInternal().setEmptyView(getEmptyListContainerView());
        mHandler.post(mRequestFocus);
    }

    @Override
    protected View generateDefaultView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        return inflater.inflate(R.layout.genfw_listfragment_default_lv, container, false);
    }
}
