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

import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

/**
 * In order to not limit the list of items that can be provided by the list container to the {@link EngineConnection},
 * this class is expected to be given to it when the selection process is made with a list position like
 * using the {@link EngineConnection#setSelected(int)} method.
 *
 * @param <T> a derivative of {@link Selectable}
 */
public interface SelectableProvider<T extends Selectable>
{
    List<T> getSelectableList();
}
