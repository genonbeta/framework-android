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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.transition.TransitionManager;
import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.ListAdapterBase;

import java.util.List;

/**
 * Created by: veli
 * Date: 12/3/16 9:57 AM
 */

public abstract class ListFragment<Z extends ViewGroup, T, E extends ListAdapterBase<T>> extends Fragment
        implements ListFragmentBase<T>
{
    public static final String TAG = ListFragment.class.getSimpleName();

    public static final int LAYOUT_DEFAULT_EMPTY_LIST_VIEW = R.layout.genfw_layout_listfragment_emptyview;
    public static final int TASK_ID_REFRESH = 0;

    private E mAdapter;
    private Z mListView;
    private ViewGroup mEmptyListContainerView;
    private TextView mEmptyListTextView;
    private ImageView mEmptyListImageView;
    private ProgressBar mProgressBar;
    private Button mEmptyListActionButton;
    private RefreshLoaderCallback mRefreshLoaderCallback = new RefreshLoaderCallback();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        findViewDefaultsFromMainView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        LoaderManager.getInstance(this).initLoader(TASK_ID_REFRESH, null, mRefreshLoaderCallback);
    }

    protected void onPrepareRefreshingList()
    {
    }

    protected void onListRefreshed()
    {
    }

    public AsyncTaskLoader<List<T>> createLoader()
    {
        return new ListLoader<>(mAdapter);
    }

    protected abstract void ensureList();

    protected void findViewDefaultsFrom(View view)
    {
        if (view == null)
            return;

        setListView((Z) view.findViewById(R.id.genfw_customListFragment_listView));
        setEmptyListContainerView((ViewGroup) view.findViewById(R.id.genfw_customListFragment_emptyView));
        setEmptyListTextView((TextView) view.findViewById(R.id.genfw_customListFragment_emptyTextView));
        setEmptyListImageView((ImageView) view.findViewById(R.id.genfw_customListFragment_emptyImageView));
        setEmptyListActionButton((Button) view.findViewById(R.id.genfw_customListFragment_emptyActionButton));
        setProgressBar((ProgressBar) view.findViewById(R.id.genfw_customListFragment_progressBar));
    }

    protected void findViewDefaultsFromMainView()
    {
        findViewDefaultsFrom(getView());
    }

    public E getAdapter()
    {
        return mAdapter;
    }

    protected abstract View generateDefaultView(LayoutInflater inflater, ViewGroup container, Bundle savedState);

    public ViewGroup getEmptyListContainerView()
    {
        ensureList();
        return mEmptyListContainerView;
    }

    public ImageView getEmptyListImageView()
    {
        ensureList();
        return mEmptyListImageView;
    }

    public TextView getEmptyListTextView()
    {
        ensureList();
        return mEmptyListTextView;
    }

    public Button getEmptyListActionButton()
    {
        ensureList();
        return mEmptyListActionButton;
    }

    public E getListAdapter()
    {
        return mAdapter;
    }

    protected final Z getListViewInternal()
    {
        return mListView;
    }

    public Z getListView()
    {
        ensureList();
        return getListViewInternal();
    }

    public RefreshLoaderCallback getLoaderCallbackRefresh()
    {
        return mRefreshLoaderCallback;
    }

    public ProgressBar getProgressBar()
    {
        ensureList();
        return mProgressBar;
    }

    public void refreshList()
    {
        getLoaderCallbackRefresh().requestRefresh();
    }

    protected void setEmptyListContainerView(ViewGroup container)
    {
        mEmptyListContainerView = container;
    }

    protected void setEmptyListActionButton(Button button)
    {
        mEmptyListActionButton = button;
    }

    protected void setEmptyListImage(int resId)
    {
        getEmptyListImageView().setImageResource(resId);
    }

    protected void setEmptyListImageView(ImageView view)
    {
        mEmptyListImageView = view;
    }

    protected void setEmptyListText(CharSequence text)
    {
        getEmptyListTextView().setText(text);
    }

    protected void setEmptyListTextView(TextView view)
    {
        mEmptyListTextView = view;
    }

    protected void setListAdapter(E adapter)
    {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;

        setListAdapter(adapter, hadAdapter);
    }

    protected abstract void setListAdapter(E adapter, boolean hadAdapter);

    public void setListLoading(boolean loading)
    {
        setListLoading(loading, true);
    }

    public void setListLoading(boolean loading, boolean animate)
    {
        ensureList();

        ProgressBar progressBar = getProgressBar();
        ViewGroup emptyListContainer = getEmptyListContainerView();

        // progress is shown == loading
        // container is not shown == progress cannot be shown
        if (progressBar == null || (progressBar.getVisibility() == View.VISIBLE) == loading
                || emptyListContainer == null || emptyListContainer.getVisibility() != View.VISIBLE)
            return;

        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (animate)
            TransitionManager.beginDelayedTransition(emptyListContainer);
    }

    protected void setListShown(boolean shown)
    {
        setListShown(shown, true);
    }

    protected void setListShown(boolean shown, boolean animate)
    {
        Z listView = getListView();
        ViewGroup emptyListContainer = getEmptyListContainerView();

        if (listView != null && (listView.getVisibility() == View.VISIBLE) != shown) {
            listView.setVisibility(shown ? View.VISIBLE : View.GONE);
            if (animate)
                listView.startAnimation(AnimationUtils.loadAnimation(getContext(), shown ? android.R.anim.fade_in
                        : android.R.anim.fade_out));
        }

        if (emptyListContainer != null && (emptyListContainer.getVisibility() == View.VISIBLE) == shown)
            emptyListContainer.setVisibility(shown ? View.GONE : View.VISIBLE);
    }

    protected void setListView(Z listView)
    {
        mListView = listView;
    }

    protected void setProgressBar(ProgressBar progressBar)
    {
        mProgressBar = progressBar;
    }

    public void showEmptyListActionButton(boolean show)
    {
        getEmptyListActionButton().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void useEmptyListActionButton(String buttonText, View.OnClickListener clickListener)
    {
        Button actionButton = getEmptyListActionButton();

        actionButton.setText(buttonText);
        actionButton.setOnClickListener(clickListener);

        showEmptyListActionButton(true);
    }

    private class RefreshLoaderCallback implements LoaderManager.LoaderCallbacks<List<T>>
    {
        private boolean mRunning = false;
        private boolean mReloadRequested = false;

        @NonNull
        @Override
        public Loader<List<T>> onCreateLoader(int id, Bundle args)
        {
            mReloadRequested = false;
            mRunning = true;

            if (getAdapter().getCount() == 0)
                setListShown(false);

            setListLoading(true);

            return createLoader();
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<T>> loader, List<T> data)
        {
            if (isResumed()) {
                onPrepareRefreshingList();

                getAdapter().onUpdate(data);
                getAdapter().onDataSetChanged();
                setListLoading(false);
                onListRefreshed();
            }

            if (isReloadRequested())
                refresh();

            mRunning = false;
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<T>> loader)
        {

        }

        public boolean isRunning()
        {
            return mRunning;
        }

        public boolean isReloadRequested()
        {
            return mReloadRequested;
        }

        public void refresh()
        {
            LoaderManager.getInstance(ListFragment.this)
                    .restartLoader(TASK_ID_REFRESH, null, mRefreshLoaderCallback);
        }

        public boolean requestRefresh()
        {
            if (isRunning() && isReloadRequested())
                return false;

            if (!isRunning())
                refresh();
            else
                mReloadRequested = true;

            return true;
        }
    }

    public static class ListLoader<G> extends AsyncTaskLoader<List<G>>
    {
        private ListAdapterBase<G> mAdapter;

        public ListLoader(ListAdapterBase<G> adapter)
        {
            super(adapter.getContext());
            mAdapter = adapter;
        }

        @Override
        protected void onStartLoading()
        {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<G> loadInBackground()
        {
            return mAdapter.onLoad();
        }
    }
}
