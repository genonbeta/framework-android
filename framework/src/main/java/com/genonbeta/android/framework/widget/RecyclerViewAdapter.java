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
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by: veli
 * date: 26.03.2018 11:46
 */

public abstract class RecyclerViewAdapter<T, V extends RecyclerViewAdapter.ViewHolder> extends RecyclerView.Adapter<V>
        implements ListAdapterBase<T>
{
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean mHorizontalOrientation;

    public RecyclerViewAdapter(Context context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onDataSetChanged()
    {
        notifyDataSetChanged();
    }

    public boolean isHorizontalOrientation()
    {
        return mHorizontalOrientation;
    }

    public Context getContext()
    {
        return mContext;
    }

    @Override
    public int getCount()
    {
        return getItemCount();
    }

    public LayoutInflater getInflater()
    {
        return mInflater;
    }

    public void setUseHorizontalOrientation(boolean use)
    {
        mHorizontalOrientation = use;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View itemView)
        {
            super(itemView);
        }

        public boolean isSelected()
        {
            return itemView.isSelected();
        }

        public void setSelected(boolean selected)
        {
            itemView.setSelected(selected);
        }
    }

    public interface OnClickListener
    {
        void onClick(ViewHolder holder);
    }
}
