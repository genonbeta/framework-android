package com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider;

import android.graphics.drawable.InsetDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.recyclerview.fastscroll.Utils;

import androidx.core.content.ContextCompat;

/**
 * Created by Michal on 05/08/16.
 */
public class DefaultScrollerViewProvider extends ScrollerViewProvider
{
    protected View mBubble;
    protected View mHandle;

    @Override
    public View provideHandleView(ViewGroup container)
    {
        mHandle = new View(getContext());

        int verticalInset = getScroller().isVertical() ? 0 : getContext().getResources().getDimensionPixelSize(R.dimen.genfw_fastscroll_handle_inset);
        int horizontalInset = !getScroller().isVertical() ? 0 : getContext().getResources().getDimensionPixelSize(R.dimen.genfw_fastscroll_handle_inset);
        InsetDrawable handleBg = new InsetDrawable(ContextCompat.getDrawable(getContext(), R.drawable.genfw_fastscroll_default_handle), horizontalInset, verticalInset, horizontalInset, verticalInset);
        Utils.setBackground(mHandle, handleBg);

        int handleWidth = getContext().getResources().getDimensionPixelSize(getScroller().isVertical() ? R.dimen.genfw_fastscroll_handle_clickable_width : R.dimen.genfw_fastscroll_handle_height);
        int handleHeight = getContext().getResources().getDimensionPixelSize(getScroller().isVertical() ? R.dimen.genfw_fastscroll_handle_height : R.dimen.genfw_fastscroll_handle_clickable_width);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(handleWidth, handleHeight);
        mHandle.setLayoutParams(params);

        return mHandle;
    }

    @Override
    public View provideBubbleView(ViewGroup container)
    {
        return mBubble = LayoutInflater.from(getContext()).inflate(R.layout.genfw_fastscroll_default_bubble, container, false);
    }

    @Override
    public TextView provideBubbleTextView()
    {
        return (TextView) mBubble;
    }

    @Override
    public int getBubbleOffset()
    {
        return (int) (getScroller().isVertical() ? ((float) mHandle.getHeight() / 2f) - mBubble.getHeight() : ((float) mHandle.getWidth() / 2f) - mBubble.getWidth());
    }

    @Override
    protected ViewBehavior provideHandleBehavior()
    {
        return null;
    }

    @Override
    protected ViewBehavior provideBubbleBehavior()
    {
        return new DefaultBubbleBehavior(new VisibilityAnimationManager.Builder(mBubble).withPivotX(1f).withPivotY(1f).build());
    }
}
