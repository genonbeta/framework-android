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

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.view.SupportMenuInflater;
import com.genonbeta.android.framework.object.Selectable;
import com.genonbeta.android.framework.util.actionperformer.*;

import java.util.List;

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
    private final Context mContext;
    private final MenuInflater mMenuInflater;
    private final Callback mCallback;

    /**
     * Create an instance of PerformerMenu that to handle menus and listeners together.
     *
     * @param context  to access resources
     * @param callback to inform about and get input for selections, and menu item clicks
     */
    public PerformerMenu(Context context, @NonNull Callback callback)
    {
        mContext = context;
        mMenuInflater = new SupportMenuInflater(getContext());
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

    public boolean invokeMenuItemSelected(MenuItem menuItem)
    {
        return mCallback.onPerformerMenuSelected(this, menuItem);
    }

    /**
     * Load the given menu by calling {@link Callback#onPerformerMenuList(PerformerMenu, MenuInflater, Menu)}.
     *
     * @param targetMenu to populate
     * @return true when the given menu is populated
     */
    public boolean load(Menu targetMenu)
    {
        if (!populateMenu(targetMenu))
            return false;

        for (int i = 0; i < targetMenu.size(); i++)
            targetMenu.getItem(i).setOnMenuItemClickListener(this);

        return true;
    }

    /**
     * This is a call similar to {@link android.app.Activity#onCreateOptionsMenu(Menu)}. This creates the menu list
     * which will be provided by {@link Callback#onPerformerMenuList(PerformerMenu, MenuInflater, Menu)}. If you
     * are not willing to make the {@link #invokeMenuItemSelected(MenuItem)} calls manually, use
     * {@link #load(Menu)} so that menu item selection calls will be handled directly by the {@link Callback}.
     * <p>
     * The main difference is that when you want to work with more than one {@link IEngineConnection}, the best is to
     * avoid using this, because you will often will not able to treat each {@link IEngineConnection} individually.
     * However, for example, if you are using a fragment and want to bridge default fragment callbacks like
     * {@link androidx.fragment.app.Fragment#onOptionsItemSelected(MenuItem)} with this, it is best to use this so
     * that you can trigger menu creation as needed. To give an example again, you may want to keep a boolean variable
     * that goes 'selectionActivated' which will be used to assess whether the menu items will represent the selection.
     * And to reset the menus you can use {@link Activity#invalidateOptionsMenu()} method.
     *
     * @param targetMenu to be populated.
     */
    public boolean populateMenu(Menu targetMenu)
    {
        return mCallback.onPerformerMenuList(this, this.getMenuInflater(), targetMenu);
    }

    /**
     * Register the callbacks of this instance, so that any change will be reported to us.
     *
     * @param engine that we are going to be informed about
     */
    public void setUp(IPerformerEngine engine)
    {
        engine.addPerformerListener(this);
        engine.addPerformerCallback(this);
    }

    /**
     * Unregister the previously registered callbacks of this instance.
     *
     * @param engine that we are no longer to be informed about
     */
    public void dismantle(IPerformerEngine engine)
    {
        engine.removePerformerCallback(this);
        engine.removePerformerListener(this);
    }

    @Override
    public boolean onSelection(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable,
                               boolean isSelected, int position)
    {
        return mCallback.onPerformerMenuItemSelection(this, engine, owner, selectable, isSelected,
                position);
    }

    @Override
    public boolean onSelection(IPerformerEngine engine, IBaseEngineConnection owner,
                               List<? extends Selectable> selectableList, boolean isSelected, int[] positions)
    {
        return mCallback.onPerformerMenuItemSelection(this, engine, owner, selectableList, isSelected,
                positions);
    }

    @Override
    public void onSelected(IPerformerEngine engine, IBaseEngineConnection owner, Selectable selectable,
                           boolean isSelected, int position)
    {
        mCallback.onPerformerMenuItemSelected(this, engine, owner, selectable, isSelected, position);
    }

    @Override
    public void onSelected(IPerformerEngine engine, IBaseEngineConnection owner,
                           List<? extends Selectable> selectableList, boolean isSelected, int[] positions)
    {
        mCallback.onPerformerMenuItemSelected(this, engine, owner, selectableList, isSelected, positions);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        return mCallback.onPerformerMenuSelected(this, item);
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
        boolean onPerformerMenuSelected(PerformerMenu performerMenu, MenuItem item);

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
         * Called when a {@link Selectable} is being altered. This is called during the process which is not still
         * finished.
         *
         * @param performerMenu  instance that redirects the call
         * @param engine         owning the {@link IBaseEngineConnection}
         * @param owner          that is managing the selection list and informing the {@link IPerformerEngine}
         * @param selectableList that is being altered
         * @param isSelected     is true when the new state is selected or false if otherwise
         * @param positions      where the selectables are at on {@link SelectableProvider}
         * @return true if there is no problem with altering the state of selection of the selectable
         */
        boolean onPerformerMenuItemSelection(PerformerMenu performerMenu, IPerformerEngine engine,
                                             IBaseEngineConnection owner, List<? extends Selectable> selectableList,
                                             boolean isSelected, int[] positions);

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

        /**
         * Called after the {@link #onPerformerMenuItemSelection(PerformerMenu, IPerformerEngine, IBaseEngineConnection,
         * List, boolean, int[])} to inform about the new state of the list of selectables.
         *
         * @param performerMenu  instance that redirects the call
         * @param engine         owning the {@link IBaseEngineConnection}
         * @param owner          that is managing the selection list and informing the {@link IPerformerEngine}
         * @param selectableList that is being altered
         * @param isSelected     is true when the new state is selected or false if otherwise
         * @param positions      where the selectables are at on {@link SelectableProvider}
         */
        void onPerformerMenuItemSelected(PerformerMenu performerMenu, IPerformerEngine engine,
                                         IBaseEngineConnection owner, List<? extends Selectable> selectableList,
                                         boolean isSelected, int[] positions);
    }
}
