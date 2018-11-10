package com.genonbeta.android.framework.widget.recyclerview.fastscroll;

import com.genonbeta.android.framework.widget.recyclerview.FastScroller;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Michal on 04/08/16.
 * Responsible for updating the handle / bubble position when user scrolls the {@link RecyclerView}.
 */
public class RecyclerViewScrollListener extends RecyclerView.OnScrollListener
{
    private final FastScroller mScroller;
    private List<ScrollerListener> mListeners = new ArrayList<>();
    private int mOldScrollState = RecyclerView.SCROLL_STATE_IDLE;

    public RecyclerViewScrollListener(FastScroller scroller)
    {
        this.mScroller = scroller;
    }

    public void addScrollerListener(ScrollerListener listener)
    {
        mListeners.add(listener);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newScrollState)
    {
        super.onScrollStateChanged(recyclerView, newScrollState);

        if (newScrollState == RecyclerView.SCROLL_STATE_IDLE && mOldScrollState != RecyclerView.SCROLL_STATE_IDLE)
            mScroller.getViewProvider().onScrollFinished();
        else if (newScrollState != RecyclerView.SCROLL_STATE_IDLE && mOldScrollState == RecyclerView.SCROLL_STATE_IDLE)
            mScroller.getViewProvider().onScrollStarted();

        mOldScrollState = newScrollState;
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy)
    {
        if (mScroller.shouldUpdateHandlePosition()) {
            updateHandlePosition(rv);
        }
    }

    public void updateHandlePosition(RecyclerView rv)
    {
        float relativePos;
        if (mScroller.isVertical()) {
            int offset = rv.computeVerticalScrollOffset();
            int extent = rv.computeVerticalScrollExtent();
            int range = rv.computeVerticalScrollRange();
            relativePos = offset / (float) (range - extent);
        } else {
            int offset = rv.computeHorizontalScrollOffset();
            int extent = rv.computeHorizontalScrollExtent();
            int range = rv.computeHorizontalScrollRange();
            relativePos = offset / (float) (range - extent);
        }
        mScroller.setScrollerPosition(relativePos);
        notifyListeners(relativePos);
    }

    public void notifyListeners(float relativePos)
    {
        for (ScrollerListener listener : mListeners) listener.onScroll(relativePos);
    }

    public interface ScrollerListener
    {
        void onScroll(float relativePos);
    }
}
