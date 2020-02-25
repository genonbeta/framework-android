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
import androidx.annotation.NonNull;
import com.genonbeta.android.framework.object.Selectable;
import com.genonbeta.android.framework.util.actionperformer.*;

/**
 * The idea here is that this class bridges one or more menus with a {@link IEngineConnection} to perform a specific
 * task whenever a new selectable is adder or removed and whenever the any item on a menu is clicked.
 * <p>
 * The class that is responsible for the performer menu should also provide the {@link IPerformerEngine}
 * to which this will add callbacks and listeners.
 * <p>
 * Because {@link Selectable} is referred to as the base class, the {@link Callback} methods shouldn't be used to
 * identify the derivatives. Instead, you should use the engine connection to identify the objects.
 */
public class PerformerMenu implements PerformerCallback, PerformerListener, MenuItem.OnMenuItemClickListener
{
    private Context mContext;
    private MenuInflater mMenuInflater;
    private Callback mCallback;

    /**
     * Create an instance of PerformerMenu that to handle menus and listeners together.
     *
     * @param context  to access resources
     * @param callback to inform about and get input for selections, and menu item clicks
     */
    public PerformerMenu(Context context, @NonNull Callback callback)
    {
        mContext = context;
        mMenuInflater = new MenuInflater(getContext());
        mCallback = callback;
    }

    /**
     * @return the application context
     */
    public Context getContext()
    {
        return mContext;
    }

    /**
     * @return the inflate the menu resources
     */
    public MenuInflater getMenuInflater()
    {
        return mMenuInflater;
    }

    /**
     * Load the given menu by calling {@link Callback#onPerformerMenuList(PerformerMenu, MenuInflater, Menu)} after
     * clearing it.
     *
     * @param targetMenu to populate
     * @return true when the given menu is populated
     */
    public boolean load(Menu targetMenu)
    {
        targetMenu.clear();

        if (!mCallback.onPerformerMenuList(this, this.getMenuInflater(), targetMenu))
            return false;

        for (int i = 0; i < targetMenu.size(); i++)
            targetMenu.getItem(i).setOnMenuItemClickListener(this);

        return true;
    }

    public void setUp(IPerformerEngine engine)
    {
        engine.addPerformerListener(this);
        engine.addPerformerCallback(this);
    }

    @Override
    public boolean onSelection(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable,
                               boolean isSelected, int position)
    {
        return mCallback.onPerformerMenuItemSelection(this, engine, owner, selectable, isSelected,
                position);
    }

    @Override
    public void onSelected(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable,
                           boolean isSelected, int position)
    {
        mCallback.onPerformerMenuItemSelected(this, engine, owner, selectable, isSelected,
                position);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return mCallback.onPerformerMenuClick(this, item);
    }

    /**
     * The callback to connect the menu actions to. The data will be redirected from the other callbacks.
     */
    public interface Callback
    {
        /**
         * Called when {@link PerformerMenu#load(Menu)} is invoked to populate the menu.
         *
         * @param performerMenu instance that redirects the call
         * @param inflater      to inflate the menus with
         * @param targetMenu    to populate
         * @return true when there was not problem populating the menu
         */
        boolean onPerformerMenuList(PerformerMenu performerMenu, MenuInflater inflater, Menu targetMenu);

        /**
         * Called when a menu item on a populated menu (with callbacks registered) was clicked.
         *
         * @param performerMenu instance that redirects the call
         * @param item          that was clicked.
         * @return true when the input is known and the descendant is not needed the perform any other action
         */
        boolean onPerformerMenuClick(PerformerMenu performerMenu, MenuItem item);

        /**
         * Called when a {@link Selectable} is being altered. This is called during the process which is not still
         * finished.
         *
         * @param performerMenu instance that redirects the call
         * @param engine        owning the {@link IBaseEngineConnection}
         * @param owner         that is managing the selection list and informing the {@link IPerformerEngine}
         * @param selectable    that is being altered
         * @param isSelected    is true when the new state is selected or false if otherwise
         * @param position      where the selectable is at on {@link SelectableProvider}
         * @return true if there is no problem with altering the state of selection of the selectable
         */
        boolean onPerformerMenuItemSelection(PerformerMenu performerMenu, IPerformerEngine engine,
                                             IBaseEngineConnection owner, Selectable selectable, boolean isSelected,
                                             int position);

        /**
         * Called after the {@link #onPerformerMenuItemSelection(PerformerMenu, IPerformerEngine, IBaseEngineConnection,
         * Selectable, boolean, int)} to inform about the new state of the selectable.
         *
         * @param performerMenu instance that redirects the call
         * @param engine        owning the {@link IBaseEngineConnection}
         * @param owner         that is managing the selection list and informing the {@link IPerformerEngine}
         * @param selectable    that is being altered
         * @param isSelected    is true when the new state is selected or false if otherwise
         * @param position      where the selectable is at on {@link SelectableProvider}
         */
        void onPerformerMenuItemSelected(PerformerMenu performerMenu, IPerformerEngine engine,
                                         IBaseEngineConnection owner, Selectable selectable, boolean isSelected,
                                         int position);
    }
}
