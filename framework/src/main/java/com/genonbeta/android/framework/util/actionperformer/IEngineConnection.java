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

import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

/**
 * This class takes care of connecting {@link IPerformerEngine} to the UI element that needs to be free of limitations
 * like knowing whether the {@link T} is something that it can work on. It does that by extending from
 * {@link IBaseEngineConnection} and allowing the UI element only worrying about whether it
 *
 * @param <T> The derivative of the {@link Selectable} class
 */
public interface IEngineConnection<T extends Selectable> extends IBaseEngineConnection
{
    /**
     * @return the engine we are operating on
     */
    IPerformerEngine getEngine();

    /**
     * @return a quick call of {@link SelectableHost#getSelectableList()}
     * @see #getSelectableHost()
     */
    List<T> getHostList();

    /**
     * @return a quick call of {@link SelectableProvider#getSelectableList()}
     * @see #getSelectableProvider()
     */
    List<T> getSelectionList();


    SelectableHost<T> getSelectableHost();

    SelectableProvider<T> getSelectableProvider();

    boolean isSelected(T selectable);

    void setEngine(IPerformerEngine engine);

    void setSelectableHost(SelectableHost<T> host);

    void setSelectableProvider(SelectableProvider<T> provider);

    boolean setSelected(RecyclerView.ViewHolder holder);

    boolean setSelected(int position);

    boolean setSelected(T selectable);

    boolean setSelected(T selectable, boolean selected);

    boolean setSelected(T selectable, int position);

    boolean setSelected(T selectable, boolean selected, int position);
}
