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

package com.genonbeta.android.framework.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * created by: veli
 * date: 26.03.2018 11:45
 */

abstract public class RecyclerViewFragment<T, V extends RecyclerViewAdapter.ViewHolder,
		E extends RecyclerViewAdapter<T, V>> extends ListFragment<RecyclerView, T, E>
{
	private RecyclerView mRecyclerView;

	final private Handler mHandler = new Handler();

	final private Runnable mRequestFocus = new Runnable()
	{
		@Override
		public void run()
		{
			mRecyclerView.focusableViewAvailable(mRecyclerView);
		}
	};

	@Override
	protected void onListRefreshed()
	{
		super.onListRefreshed();

		boolean isEmpty = getAdapter().getCount() == 0;

		getEmptyView().setVisibility(isEmpty ? View.VISIBLE : View.GONE);
		getListView().setVisibility(isEmpty ? View.GONE : View.VISIBLE);
	}

	public RecyclerView.LayoutManager onLayoutManager()
	{
		return getDefaultLayoutManager();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mRecyclerView = view.findViewById(R.id.genfw_customListFragment_listView);

		if (mRecyclerView == null)
			mRecyclerView = onListView(getContainer(), getListViewContainer());

		return view;
	}

	@Override
	protected RecyclerView onListView(View mainContainer, ViewGroup listViewContainer)
	{
		RecyclerView recyclerView = new RecyclerView(getContext());

		recyclerView.setLayoutManager(onLayoutManager());

		recyclerView.setLayoutParams(new GridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		listViewContainer.addView(recyclerView);

		return recyclerView;
	}

	@Override

	protected void onEnsureList()
	{
		mHandler.post(mRequestFocus);
	}

	@Override
	public boolean onSetListAdapter(E adapter)
	{
		if (mRecyclerView == null)
			return false;

		mRecyclerView.setAdapter(adapter);

		return true;
	}

	public RecyclerView.LayoutManager getDefaultLayoutManager()
	{
		return new LinearLayoutManager(getContext());
	}

	@Override
	public RecyclerView getListView()
	{
		return mRecyclerView;
	}
}
