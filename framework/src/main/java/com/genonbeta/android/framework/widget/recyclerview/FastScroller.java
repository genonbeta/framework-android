package com.genonbeta.android.framework.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.RecyclerViewScrollListener;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.SectionTitleProvider;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.Utils;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider.DefaultScrollerViewProvider;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider.ScrollerViewProvider;

public class FastScroller extends LinearLayout
{
    private static final int STYLE_NONE = -1;

    private final RecyclerViewScrollListener mScrollListener = new RecyclerViewScrollListener(this);
    private RecyclerView mRecyclerView;

    private View mBubble;
    private View mHandle;
    private TextView mBubbleTextView;

    private int mBubbleOffset;
    private int mHandleColor;
    private int mBubbleColor;
    private int mBubbleTextAppearance;
    private int mScrollerOrientation;

    //TODO the name should be fixed, also check if there is a better way of handling the visibility, because this is somewhat convoluted
    private int mMaxVisibility;

    private boolean mManuallyChangingPosition;

    private ScrollerViewProvider mViewProvider;
    private SectionTitleProvider mTitleProvider;

    public FastScroller(Context context)
    {
        this(context, null);
    }

    public FastScroller(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setClipChildren(false);
        TypedArray style = context.obtainStyledAttributes(attrs, R.styleable.GenfwFastScroller, R.attr.genfw_fastScrollStyle, 0);

        try {
            mBubbleColor = style.getColor(R.styleable.GenfwFastScroller_genfw_fastScrollBubbleColor, STYLE_NONE);
            mHandleColor = style.getColor(R.styleable.GenfwFastScroller_genfw_fastScrollHandleColor, STYLE_NONE);
            mBubbleTextAppearance = style.getResourceId(R.styleable.GenfwFastScroller_genfw_fastScrollBubbleTextAppearance, STYLE_NONE);
        } finally {
            style.recycle();
        }

        mMaxVisibility = getVisibility();
        setViewProvider(new DefaultScrollerViewProvider());
    }

    /**
     * Attach the {@link FastScroller} to {@link RecyclerView}. ShoulmHandle.setMinimumHeight((int) computedExtent);d be used after the adapter is set
     * to the {@link RecyclerView}. If the adapter implements SectionTitleProvider, the FastScroller
     * will show a bubble with title.
     *
     * @param recyclerView A {@link RecyclerView} to attach the {@link FastScroller} to.
     */
    public void setRecyclerView(RecyclerView recyclerView)
    {
        mRecyclerView = recyclerView;

        if (recyclerView.getAdapter() instanceof SectionTitleProvider)
            mTitleProvider = (SectionTitleProvider) recyclerView.getAdapter();

        recyclerView.addOnScrollListener(mScrollListener);

        invalidateVisibility();

        recyclerView.setOnHierarchyChangeListener(new OnHierarchyChangeListener()
        {
            @Override
            public void onChildViewAdded(View parent, View child)
            {
                invalidateVisibility();
            }

            @Override
            public void onChildViewRemoved(View parent, View child)
            {
                invalidateVisibility();
            }
        });
    }

    /**
     * Set the orientation of the {@link FastScroller}. The orientation of the {@link FastScroller}
     * should generally match the orientation of connected  {@link RecyclerView} for good UX but it's not enforced.
     * Note: This method is overridden from {@link LinearLayout#setOrientation(int)} but for {@link FastScroller}
     * it has a totally different meaning.
     *
     * @param orientation of the {@link FastScroller}. {@link #VERTICAL} or {@link #HORIZONTAL}
     */
    @Override
    public void setOrientation(int orientation)
    {
        mScrollerOrientation = orientation;
        //switching orientation, because orientation in linear layout
        //is something different than orientation of fast scroller
        super.setOrientation(orientation == HORIZONTAL ? VERTICAL : HORIZONTAL);
    }

    /**
     * Set the background color of the bubble.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    public void setBubbleColor(int color)
    {
        mBubbleColor = color;
        invalidate();
    }

    /**
     * Set the background color of the handle.
     *
     * @param color Color in hex notation with alpha channel, e.g. 0xFFFFFFFF
     */
    public void setHandleColor(int color)
    {
        mHandleColor = color;
        invalidate();
    }

    /**
     * Sets the text appearance of the bubble.
     *
     * @param textAppearanceResourceId The id of the resource to be used as text appearance of the bubble.
     */
    public void setBubbleTextAppearance(int textAppearanceResourceId)
    {
        mBubbleTextAppearance = textAppearanceResourceId;
        invalidate();
    }

    /**
     * Add a {@link RecyclerViewScrollListener.ScrollerListener}
     * to be notified of user scrolling
     *
     * @param listener
     */
    public void addScrollerListener(RecyclerViewScrollListener.ScrollerListener listener)
    {
        mScrollListener.addScrollerListener(listener);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        initHandleMovement();
        mBubbleOffset = mViewProvider.getBubbleOffset();

        applyStyling(); //TODO this doesn't belong here, even if it works

        if (!isInEditMode()) {
            //sometimes recycler starts with a defined scroll (e.g. when coming from saved state)

            // Disabled since this was triggering another change
            // on layout every time it was scrolled (esp. manual handle change).
            //mScrollListener.updateHandlePosition(mRecyclerView);
        }

    }

    private void applyStyling()
    {
        if (mBubbleColor != STYLE_NONE)
            setBackgroundTint(mBubbleTextView, mBubbleColor);

        if (mHandleColor != STYLE_NONE)
            setBackgroundTint(mHandle, mHandleColor);

        if (mBubbleTextAppearance != STYLE_NONE)
            TextViewCompat.setTextAppearance(mBubbleTextView, mBubbleTextAppearance);
    }

    private void setBackgroundTint(View view, int color)
    {
        final Drawable background = DrawableCompat.wrap(view.getBackground());
        DrawableCompat.setTint(background.mutate(), color);
        Utils.setBackground(view, background);
    }

    private void initHandleMovement()
    {
        mHandle.setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                requestDisallowInterceptTouchEvent(true);
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (mTitleProvider != null && event.getAction() == MotionEvent.ACTION_DOWN)
                        mViewProvider.onHandleGrabbed();

                    mManuallyChangingPosition = true;

                    float relativePos = getRelativeTouchPosition(event);

                    setScrollerPosition(relativePos);
                    setRecyclerViewPosition(relativePos);

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mManuallyChangingPosition = false;
                    mRecyclerView.scrollBy(0, 0);

                    if (mTitleProvider != null)
                        mViewProvider.onHandleReleased();

                    return true;
                }

                return false;
            }
        });
    }

    private float getRelativeTouchPosition(MotionEvent event)
    {
        if (isVertical()) {
            float yInParent = event.getRawY() - Utils.getViewRawY(mHandle);
            return yInParent / (getHeight() - mHandle.getHeight());
        }

        float xInParent = event.getRawX() - Utils.getViewRawX(mHandle);
        return xInParent / (getWidth() - mHandle.getWidth());
    }

    @Override
    public void setVisibility(int visibility)
    {
        mMaxVisibility = visibility;
        invalidateVisibility();
    }

    private void invalidateVisibility()
    {
        if (
                mRecyclerView.getAdapter() == null ||
                        mRecyclerView.getAdapter().getItemCount() == 0 ||
                        mRecyclerView.getChildAt(0) == null ||
                        isRecyclerViewNotScrollable() ||
                        mMaxVisibility != View.VISIBLE
        ) {
            super.setVisibility(INVISIBLE);
        } else {
            super.setVisibility(VISIBLE);
        }
    }

    private boolean isRecyclerViewNotScrollable()
    {
        if (isVertical())
            return mRecyclerView.getChildAt(0).getHeight() * mRecyclerView.getAdapter().getItemCount() <= mRecyclerView.getHeight();

        return mRecyclerView.getChildAt(0).getWidth() * mRecyclerView.getAdapter().getItemCount() <= mRecyclerView.getWidth();
    }

    private void setRecyclerViewPosition(float relativePos)
    {
        if (mRecyclerView == null || mRecyclerView.getAdapter() == null)
            return;

        int offset;
        int extent;
        int range;

        if (isVertical()) {
            offset = mRecyclerView.computeVerticalScrollOffset();
            extent = mRecyclerView.computeVerticalScrollExtent();
            range = mRecyclerView.computeVerticalScrollRange();
        } else {
            offset = mRecyclerView.computeHorizontalScrollOffset();
            extent = mRecyclerView.computeHorizontalScrollExtent();
            range = mRecyclerView.computeHorizontalScrollRange();
        }

        {
            View viewUnder = isVertical()
                    ? mRecyclerView.findChildViewUnder(0, mHandle.getY())
                    : mRecyclerView.findChildViewUnder(mHandle.getX(), 0);

            RecyclerView.ViewHolder viewHolder = viewUnder == null
                    ? null
                    : mRecyclerView.findContainingViewHolder(viewUnder);

            int itemCount = mRecyclerView.getAdapter().getItemCount();
            int targetPos = viewHolder == null ? -1 : viewHolder.getAdapterPosition();

            if (targetPos < 0 || targetPos >= itemCount)
                // This is an old fallback method.
                // The problem is it assumes each child has the same length.
                // Let's hope that the method above always locates what is under.
                targetPos = (int) Utils.getValueInRange(0, itemCount - 1, (int) (relativePos * (float) itemCount));

            if (mTitleProvider != null && mBubbleTextView != null)
                mBubbleTextView.setText(mTitleProvider.getSectionTitle(targetPos));
        }

        //float computedExtent = (float) extent * (offset / (float) (range - extent));
        //float currentOffset = offset + computedExtent;
        //  For: Change-a
        float expectedPosition = range * relativePos;

        // Change-a
        // We don't want to scroll the view when it still covers all the content that is requested.
        if (expectedPosition <= extent && offset == 0)
            return;

        int difference = (int) (expectedPosition - (offset + extent));

        if (isVertical())
            mRecyclerView.scrollBy(0, difference);
        else
            mRecyclerView.scrollBy(difference, 0);
    }

    public void setScrollerPosition(float relativePos)
    {
        if (isVertical()) {
            mBubble.setY(Utils.getValueInRange(
                    0,
                    getHeight() - mBubble.getHeight(),
                    relativePos * (getHeight() - mHandle.getHeight()) + mBubbleOffset)
            );

            mHandle.setY(Utils.getValueInRange(
                    0,
                    getHeight() - mHandle.getHeight(),
                    relativePos * (getHeight() - mHandle.getHeight())));
        } else {
            mBubble.setX(Utils.getValueInRange(
                    0,
                    getWidth() - mBubble.getWidth(),
                    relativePos * (getWidth() - mHandle.getWidth()) + mBubbleOffset)
            );

            mHandle.setX(Utils.getValueInRange(
                    0,
                    getWidth() - mHandle.getWidth(),
                    relativePos * (getWidth() - mHandle.getWidth()))
            );
        }
    }

    public boolean isVertical()
    {
        return mScrollerOrientation == VERTICAL;
    }

    public boolean shouldUpdateHandlePosition()
    {
        return mHandle != null && !mManuallyChangingPosition && mRecyclerView.getChildCount() > 0;
    }

    public ScrollerViewProvider getViewProvider()
    {
        return mViewProvider;
    }

    /**
     * Enables custom layout for {@link FastScroller}.
     *
     * @param viewProvider A {@link ScrollerViewProvider} for the {@link FastScroller} to use when building layout.
     */
    public void setViewProvider(ScrollerViewProvider viewProvider)
    {
        removeAllViews();
        mViewProvider = viewProvider;

        viewProvider.setFastScroller(this);

        mBubble = viewProvider.provideBubbleView(this);
        mHandle = viewProvider.provideHandleView(this);
        mBubbleTextView = viewProvider.provideBubbleTextView();

        addView(mBubble);
        addView(mHandle);
    }
}
