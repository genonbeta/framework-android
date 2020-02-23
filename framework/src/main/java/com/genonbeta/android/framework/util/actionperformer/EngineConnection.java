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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.genonbeta.android.framework.object.Selectable;

import java.util.List;

public class EngineConnection<T extends Selectable> implements IEngineConnection<T>
{
    private PerformerEngineProvider mEngineProvider;
    private SelectableProvider<T> mSelectableProvider;
    private SelectableHost<T> mSelectableHost;
    private CharSequence mDefinitiveTitle;

    public EngineConnection(@NonNull PerformerEngineProvider provider, @NonNull SelectableHost<T> host)
    {
        setEngineProvider(provider);
        setSelectableHost(host);
    }

    protected boolean changeSelectionState(T selectable, boolean selected)
    {
        return selectable.setSelectableSelected(selected) && (selected && getHostList().add(selectable))
                || (!selected && getHostList().remove(selectable));
    }

    @Override
    @Nullable
    public CharSequence getDefinitiveTitle()
    {
        return mDefinitiveTitle;
    }

    @Override
    public PerformerEngineProvider getEngineProvider()
    {
        return mEngineProvider;
    }

    @Override
    public List<? extends Selectable> getSelectableList()
    {
        return getSelectionList();
    }

    public List<T> getHostList()
    {
        return getSelectableHost().getSelectableList();
    }

    public List<T> getSelectionList()
    {
        return getSelectableProvider().getSelectableList();
    }

    public SelectableHost<T> getSelectableHost()
    {
        return mSelectableHost;
    }

    public SelectableProvider<T> getSelectableProvider()
    {
        return mSelectableProvider;
    }

    public boolean isSelected(T selectable)
    {
        return getHostList().contains(selectable);
    }

    public void setDefinitiveTitle(CharSequence title)
    {
        mDefinitiveTitle = title;
    }

    @Override
    public void setEngineProvider(@Nullable PerformerEngineProvider engineProvider)
    {
        mEngineProvider = engineProvider;
    }

    public void setSelectableHost(SelectableHost<T> host)
    {
        mSelectableHost = host;
    }

    public void setSelectableProvider(@Nullable SelectableProvider<T> provider)
    {
        mSelectableProvider = provider;
    }

    public boolean setSelected(RecyclerView.ViewHolder holder)
    {
        return setSelected(holder.getAdapterPosition());
    }

    public boolean setSelected(int position)
    {
        return setSelected(mSelectableProvider.getSelectableList().get(position), position);
    }

    public boolean setSelected(T selectable)
    {
        return setSelected(selectable, !isSelected(selectable));
    }

    public boolean setSelected(T selectable, boolean selected)
    {
        return setSelected(selectable, selected, RecyclerView.NO_POSITION);
    }

    public boolean setSelected(T selectable, int position)
    {
        return setSelected(selectable, !isSelected(selectable), position);
    }

    public boolean setSelected(T selectable, boolean selected, int position)
    {
        // if it is already the same
        if (selected == isSelected(selectable))
            return selected;

        IPerformerEngine performerEngine = getEngineProvider().getPerformerEngine();

        return performerEngine != null && performerEngine.check(this, selectable, selected, position)
                && changeSelectionState(selectable, selected);
    }
}