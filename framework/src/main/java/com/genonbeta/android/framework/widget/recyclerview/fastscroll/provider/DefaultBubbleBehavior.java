package com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider;

public class DefaultBubbleBehavior implements ViewBehavior
{
    private final VisibilityAnimationManager mAnimationManager;

    public DefaultBubbleBehavior(VisibilityAnimationManager animationManager)
    {
        mAnimationManager = animationManager;
    }

    @Override
    public void onHandleGrabbed()
    {
        mAnimationManager.show();
    }

    @Override
    public void onHandleReleased()
    {
        mAnimationManager.hide();
    }

    @Override
    public void onScrollStarted()
    {

    }

    @Override
    public void onScrollFinished()
    {

    }
}
