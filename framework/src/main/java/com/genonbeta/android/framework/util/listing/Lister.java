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

package com.genonbeta.android.framework.util.listing;

import java.util.ArrayList;
import java.util.List;

/**
 * created by: Veli
 * date: 29.03.2018 01:26
 */
public class Lister<T, V extends Merger<T>>
{
    private List<V> mList = new ArrayList<>();

    public Lister()
    {
    }

    public List<V> getList()
    {
        return mList;
    }

    public void offer(T object, V merger)
    {
        int index = getList().indexOf(merger);

        if (index == -1)
            getList().add(merger);
        else
            merger = getList().get(index);

        merger.getBelongings().add(object);
    }
}
