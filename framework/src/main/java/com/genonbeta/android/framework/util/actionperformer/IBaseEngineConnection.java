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

package com.genonbeta.android.framework.util.actionperformer;

import androidx.annotation.Nullable;
import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

/**
 * The base class for engine connection. This is free of generics for most compatibility.
 */
public interface IBaseEngineConnection
{
    /**
     * Compile the list of available items
     *
     * @return the list that is available with {@link SelectableProvider}
     */
    List<? extends Selectable> getGenericAvailableList();

    /**
     * Compile the list of selected items.
     *
     * @return the list that is available with {@link SelectableHost}
     */
    List<? extends Selectable> getGenericSelectedItemList();

    /**
     * Titles are only for helping the end-user know the connection in a UX manner.
     *
     * @return the representing title
     */
    @Nullable
    CharSequence getDefinitiveTitle();

    /**
     * Set the engine provider usually an {@link android.app.Activity} implementing it.
     *
     * @param engineProvider the provider
     */
    void setEngineProvider(@Nullable PerformerEngineProvider engineProvider);

    /**
     * @return the provider that will supply us with {@link IPerformerEngine} that we will operate on.
     */
    PerformerEngineProvider getEngineProvider();

    /**
     * Set the title that may be used by UI elements that need to identify this connection for user.
     *
     * @param title use to identify this connection
     */
    void setDefinitiveTitle(CharSequence title);
}
