package com.genonbeta.android.framework.app;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.ListViewAdapter;

/**
 * created by: veli
 * date: 26.03.2018 10:48
 */

abstract public class ListViewFragment<T, E extends ListViewAdapter<T>> extends ListFragment<ListView, T, E>
{
	private ListView mListView;

	final private Handler mHandler = new Handler();

	final private Runnable mRequestFocus = new Runnable()
	{
		@Override
		public void run()
		{
			mListView.focusableViewAvailable(mListView);
		}
	};

	final private AdapterView.OnItemClickListener mOnClickListener
			= new AdapterView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
		{
			onListItemClick((ListView) parent, v, position, id);
		}
	};

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mListView = view.findViewById(R.id.genfw_customListFragment_listView);

		if (mListView == null)
			mListView = onListView(getContainer(), getListViewContainer());

		mListView.setOnItemClickListener(mOnClickListener);
		mListView.setEmptyView(getEmptyView());

		return view;
	}

	@Override
	protected ListView onListView(View mainContainer, ViewGroup listViewContainer)
	{
		ListView listView = new ListView(getContext());

		listView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));

		listViewContainer.addView(listView);

		return listView;
	}

	@Override
	protected void onEnsureList()
	{
		mListView.setEmptyView(getEmptyView());
		mHandler.post(mRequestFocus);
	}

	@Override
	public boolean onSetListAdapter(E adapter)
	{
		if (mListView == null)
			return false;

		mListView.setAdapter(adapter);

		return true;
	}

	public void onListItemClick(ListView l, View v, int position, long id)
	{
	}

	public ListView getListView()
	{
		onEnsureList();
		return mListView;
	}
}
