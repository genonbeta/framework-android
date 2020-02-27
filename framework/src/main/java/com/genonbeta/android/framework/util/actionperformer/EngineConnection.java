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
import com.genonbeta.android.framework.object.Selectable;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class EngineConnection<T extends Selectable> implements IEngineConnection<T>
{
    private PerformerEngineProvider mEngineProvider;
    private SelectableProvider<T> mSelectableProvider;
    private SelectableHost<T> mSelectableHost;
    private CharSequence mDefinitiveTitle;
    private final List<SelectionListener<T>> mSelectionListenerList = new ArrayList<>();

    public EngineConnection(@NonNull PerformerEngineProvider provider, @NonNull SelectableHost<T> host)
    {
        setEngineProvider(provider);
        setSelectableHost(host);
    }

    @Override
    public boolean addSelectionListener(SelectionListener<T> listener)
    {
        synchronized (mSelectionListenerList) {
            return mSelectionListenerList.contains(listener) || mSelectionListenerList.add(listener);
        }
    }

    protected boolean changeSelectionState(T selectable, boolean selected, int position)
    {
        if (selected != selectable.isSelectableSelected() && selectable.setSelectableSelected(selected)) {
            IPerformerEngine engine = getEngineProvider().getPerformerEngine();
            SelectableHost<T> host = getSelectableHost();

            if (selected)
                host.getSelectableList().add(selectable);
            else
                host.getSelectableList().remove(selectable);

            if (engine != null)
                engine.informListeners(this, selectable, selected, position);

            for (SelectionListener<T> listener : mSelectionListenerList)
                listener.onSelected(engine, this, selectable, selected, position);

            return true;
        }

        return false;
    }

    protected void changeSelectionState(List<T> selectableList, boolean selected, int[] positions)
    {
        IPerformerEngine engine = getEngineProvider().getPerformerEngine();

        if(engine != null) {
            for (T selectable : selectableList) {
                if (selected != selectable.isSelectableSelected() && selectable.setSelectableSelected(selected))
                    if (selected)
                        getSelectedItemList().add(selectable);
                    else
                        getSelectedItemList().remove(selectable);
            }

            engine.informListeners(this, selectableList, selected, positions);

            for (SelectionListener<T> listener : mSelectionListenerList)
                listener.onSelected(engine, this, selectableList, selected, positions);
        }
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
    public List<? extends Selectable> getGenericSelectedItemList()
    {
        return getSelectedItemList();
    }

    @Override
    public List<? extends Selectable> getGenericAvailableList()
    {
        return getAvailableList();
    }

    @Override
    public List<T> getSelectedItemList()
    {
        return getSelectableHost().getSelectableList();
    }

    @Override
    public List<T> getAvailableList()
    {
        return getSelectableProvider().getSelectableList();
    }

    @Override
    public SelectableHost<T> getSelectableHost()
    {
        return mSelectableHost;
    }

    @Override
    public SelectableProvider<T> getSelectableProvider()
    {
        return mSelectableProvider;
    }

    @Override
    public boolean isSelectedOnHost(T selectable)
    {
        return getSelectedItemList().contains(selectable);
    }

    @Override
    public boolean removeSelectionListener(SelectionListener<T> listener)
    {
        synchronized (mSelectionListenerList) {
            return mSelectionListenerList.remove(listener);
        }
    }

    @Override
    public void setDefinitiveTitle(CharSequence title)
    {
        mDefinitiveTitle = title;
    }

    @Override
    public void setEngineProvider(@Nullable PerformerEngineProvider engineProvider)
    {
        mEngineProvider = engineProvider;
    }

    @Override
    public void setSelectableHost(SelectableHost<T> host)
    {
        mSelectableHost = host;
    }

    @Override
    public void setSelectableProvider(@Nullable SelectableProvider<T> provider)
    {
        mSelectableProvider = provider;
    }

    @Override
    public boolean setSelected(ViewHolder holder) throws SelectableNotFoundException, CouldNotAlterException
    {
        return setSelected(holder.getAdapterPosition());
    }

    @Override
    public boolean setSelected(int position) throws SelectableNotFoundException, CouldNotAlterException
    {
        try {
            return setSelected(mSelectableProvider.getSelectableList().get(position), position);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new SelectableNotFoundException("The selectable at the given position " + position + " could not " +
                    "be found. ");
        }
    }

    @Override
    public boolean setSelected(T selectable) throws CouldNotAlterException
    {
        return setSelected(selectable, NO_POSITION);
    }

    @Override
    public boolean setSelected(T selectable, boolean selected)
    {
        return setSelected(selectable, NO_POSITION, selected);
    }

    @Override
    public boolean setSelected(T selectable, int position) throws CouldNotAlterException
    {
        boolean newState = !isSelectedOnHost(selectable);

        if (!setSelected(selectable, position, newState, true))
            throw new CouldNotAlterException("The selectable " + selectable + " state couldn't be altered. The " +
                    "reason may be that the engine was not available or selectable was not allowed to alter state");

        return newState;
    }

    @Override
    public boolean setSelected(T selectable, int position, boolean selected)
    {
        return setSelected(selectable, position, selected, false);
    }

    @Override
    public boolean setSelected(List<T> selectableList, int[] positions, boolean selected)
    {
        IPerformerEngine engine = getEngineProvider().getPerformerEngine();

        if (engine != null && engine.check(this, selectableList, selected, positions)) {
            changeSelectionState(selectableList, selected, positions);
            return true;
        }

        return false;
    }

    private boolean setSelected(T selectable, int position, boolean selected, boolean checked)
    {
        // if it is already the same
        if (!checked && selected == isSelectedOnHost(selectable)) {
            if (selectable.isSelectableSelected() != selected && !selectable.setSelectableSelected(selected)) {
                // Selectable was known as selected, but not selected and failed to change the state
                getSelectedItemList().remove(selectable);
                return false;
            }

            return selected;
        }

        IPerformerEngine performerEngine = getEngineProvider().getPerformerEngine();

        return performerEngine != null && performerEngine.check(this, selectable, selected, position)
                && changeSelectionState(selectable, selected, position);
    }
}