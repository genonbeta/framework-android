package com.genonbeta.android.framework.widget.recyclerview.fastscroll;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.genonbeta.android.framework.widget.recyclerview.FastScroller;

import java.util.ArrayList;
import java.util.List;


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
    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy)
    {
        if (mScroller.shouldUpdateHandlePosition())
            updateHandlePosition(rv);
    }

    public void updateHandlePosition(RecyclerView rv)
    {
        int offset;
        int extent;
        int range;

        if (mScroller.isVertical()) {
            offset = rv.computeVerticalScrollOffset();
            extent = rv.computeVerticalScrollExtent();
            range = rv.computeVerticalScrollRange();
        } else {
            offset = rv.computeHorizontalScrollOffset();
            extent = rv.computeHorizontalScrollExtent();
            range = rv.computeHorizontalScrollRange();
        }

        //float relativePos = offset / (float) (range - extent);
        // Subtracting extent introduces opposite direction numbers
        // even though the direction of scrolling is the same.
        // To overcome this, while preserving the highest and lowest possible
        // location for the bubble, we slowly add the extent number.

        // Another attempt to sync the positions for scrolling the view versus the handle
        //float computedExtent = (float) extent * (offset / (float) (range - extent));
        //float relativePos = (offset + computedExtent) / (float) range;
        float relativePos = offset <= extent
                ? (offset <= 0 ? 0f : ((float) offset + ((float) extent * (offset / (float) (range - extent)))) / range)
                : (offset + extent) / (float) range;

        mScroller.setScrollerPosition(relativePos);
        notifyListeners(relativePos);
    }

    public void notifyListeners(float relativePos)
    {
        for (ScrollerListener listener : mListeners)
            listener.onScroll(relativePos);
    }

    public interface ScrollerListener
    {
        void onScroll(float relativePos);
    }
}
