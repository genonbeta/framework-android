package com.genonbeta.android.framework.app;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        return new GridLayoutManager(getContext(), isScreenLarge() && !isHorizontalOrientation() ? 2 : 1,
                isHorizontalOrientation() ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false);
    }

    @Override
    public boolean onSetListAdapter(Z adapter)
    {
        adapter.setUseHorizontalOrientation(isHorizontalOrientation());
        return super.onSetListAdapter(adapter);
    }

    public boolean isHorizontalOrientation()
    {
        return false;
    }

    public boolean isScreenLowDensity()
    {
        return getContext() != null &&
                getContext().getResources().getBoolean(R.bool.genfw_screen_isBelowDensity);
    }

    public boolean isScreenHighDensity()
    {
        return getContext() != null &&
                getContext().getResources().getBoolean(R.bool.genfw_screen_isHighDensity);
    }

    public boolean isScreenXHighDensity()
    {
        return getContext() != null &&
                getContext().getResources().getBoolean(R.bool.genfw_screen_isXHighDensity);
    }

    public boolean isScreenXXXHighDensity()
    {
        return getContext() != null &&
                getContext().getResources().getBoolean(R.bool.genfw_screen_isXXXHighDensity);
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
