package com.genonbeta.android.framework.app;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

/**
 * created by: Veli
 * date: 28.03.2018 09:42
 */

abstract public class DynamicRecyclerViewFragment<T, V extends RecyclerViewAdapter.ViewHolder, Z extends RecyclerViewAdapter<T, V>>
		extends RecyclerViewFragment<T, V, Z>
{
	@Override
	public RecyclerView.LayoutManager onLayoutManager()
	{
		return new GridLayoutManager(getContext(), isScreenLarge() ? 2 : 1);
	}

	public boolean isScreenLandscape()
	{
		return getContext() != null &&
				getContext().getResources().getBoolean(R.bool.genfw_screen_isLandscape);
	}

	public boolean isScreenLarge()
	{
		return getContext() != null &&
				getContext().getResources().getBoolean(R.bool.genfw_screen_isLarge);
	}
}
