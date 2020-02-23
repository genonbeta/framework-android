package com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.genonbeta.android.framework.widget.recyclerview.FastScroller;

/**
 * Created by Michal on 05/08/16.
 * Provides {@link View}s and their behaviors for the handle and bubble of the fastscroller.
 */
public abstract class ScrollerViewProvider
{
    private FastScroller mScroller;
    private ViewBehavior mHandleBehavior;
    private ViewBehavior mBubbleBehavior;

    public void setFastScroller(FastScroller scroller)
    {
        mScroller = scroller;
    }

    protected Context getContext()
    {
        return mScroller.getContext();
    }

    protected FastScroller getScroller()
    {
        return mScroller;
    }

    /**
     * @param container The container {@link FastScroller} for the view to inflate properly.
     * @return A view which will be by the {@link FastScroller} used as a handle.
     */
    public abstract View provideHandleView(ViewGroup container);

    /**
     * @param container The container {@link FastScroller} for the view to inflate properly.
     * @return A view which will be by the {@link FastScroller} used as a bubble.
     */
    public abstract View provideBubbleView(ViewGroup container);

    /**
     * Bubble view has to provide a {@link TextView} that will show the index title.
     *
     * @return A {@link TextView} that will hold the index title.
     */
    public abstract TextView provideBubbleTextView();

    /**
     * To offset the position of the bubble relative to the handle. E.g. in {@link DefaultScrollerViewProvider}
     * the sharp corner of the bubble is aligned with the center of the handle.
     *
     * @return the position of the bubble in relation to the handle (according to the orientation).
     */
    public abstract int getBubbleOffset();

    @Nullable
    protected abstract ViewBehavior provideHandleBehavior();

    @Nullable
    protected abstract ViewBehavior provideBubbleBehavior();

    protected ViewBehavior getHandleBehavior()
    {
        if (mHandleBehavior == null)
            mHandleBehavior = provideHandleBehavior();

        return mHandleBehavior;
    }

    protected ViewBehavior getBubbleBehavior()
    {
        if (mBubbleBehavior == null)
            mBubbleBehavior = provideBubbleBehavior();

        return mBubbleBehavior;
    }

    public void onHandleGrabbed()
    {
        if (getHandleBehavior() != null) getHandleBehavior().onHandleGrabbed();
        if (getBubbleBehavior() != null) getBubbleBehavior().onHandleGrabbed();
    }

    public void onHandleReleased()
    {
        if (getHandleBehavior() != null) getHandleBehavior().onHandleReleased();
        if (getBubbleBehavior() != null) getBubbleBehavior().onHandleReleased();
    }

    public void onScrollStarted()
    {
        if (getHandleBehavior() != null) getHandleBehavior().onScrollStarted();
        if (getBubbleBehavior() != null) getBubbleBehavior().onScrollStarted();
    }

    public void onScrollFinished()
    {
        if (getHandleBehavior() != null) getHandleBehavior().onScrollFinished();
        if (getBubbleBehavior() != null) getBubbleBehavior().onScrollFinished();
    }
}
