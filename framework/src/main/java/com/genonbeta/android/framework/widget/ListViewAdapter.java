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

package com.genonbeta.android.framework.widget;

import android.content.Context;
import android.view.LayoutInflater;

/**
 * Created by: veli
 * Date: 12/4/16 11:54 PM
 */

abstract public class ListViewAdapter<T> extends android.widget.BaseAdapter implements ListAdapterImpl<T>
{
    public Context mContext;
    private LayoutInflater mInflater;

    public ListViewAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onDataSetChanged()
    {
        notifyDataSetChanged();
    }

    public Context getContext()
    {
        return mContext;
    }

    public LayoutInflater getInflater()
    {
        return mInflater;
    }
}
