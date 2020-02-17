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

package com.genonbeta.android.framework.ui;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import com.genonbeta.android.framework.object.Selectable;
import com.genonbeta.android.framework.util.actionperformer.IBaseEngineConnection;
import com.genonbeta.android.framework.util.actionperformer.IPerformerEngine;
import com.genonbeta.android.framework.util.actionperformer.PerformerListener;

abstract public class ActionPerformerMenu implements PerformerListener, MenuItem.OnMenuItemClickListener
{
    private Context mContext;
    private MenuInflater mMenuInflater;
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public ActionPerformerMenu(Context context, @NonNull OnMenuItemClickListener menuItemClickListener)
    {
        mContext = context;
        mMenuInflater = new MenuInflater(getContext());
        mOnMenuItemClickListener = menuItemClickListener;
    }

    public Context getContext()
    {
        return mContext;
    }

    public MenuInflater getMenuInflater()
    {
        return mMenuInflater;
    }

    public void start(Menu targetMenu, @MenuRes int loadedMenuRes)
    {
        targetMenu.clear();
        getMenuInflater().inflate(loadedMenuRes, targetMenu);

        for (int i = 0; i < targetMenu.size(); i++)
            targetMenu.getItem(i).setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onSelection(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable,
                            boolean isSelected, int position)
    {
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return mOnMenuItemClickListener.onActionPerformerClick(item);
    }

    interface OnMenuItemClickListener
    {
        boolean onActionPerformerClick(MenuItem item);
    }
}
