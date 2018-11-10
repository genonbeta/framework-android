package com.genonbeta.android.framework.widget.recyclerview.fastscroll.provider;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.View;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.widget.recyclerview.FastScroller;

import androidx.annotation.AnimatorRes;

/**
 * Created by Michal on 05/08/16.
 * Animates showing and hiding elements of the {@link FastScroller} (handle and bubble).
 * The decision when to show/hide the element should be implemented via {@link ViewBehavior}.
 */
public class VisibilityAnimationManager
{
    protected final View mView;

    protected AnimatorSet mHideAnimator;
    protected AnimatorSet mShowAnimator;

    private float mPivotXRelative;
    private float mPivotYRelative;

    protected VisibilityAnimationManager(final View view, @AnimatorRes int showAnimator, @AnimatorRes int hideAnimator, float pivotXRelative, float pivotYRelative, int hideDelay)
    {
        mView = view;
        mPivotXRelative = pivotXRelative;
        mPivotYRelative = pivotYRelative;
        mHideAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), hideAnimator);
        mHideAnimator.setStartDelay(hideDelay);
        mHideAnimator.setTarget(view);
        mShowAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(), showAnimator);
        mShowAnimator.setTarget(view);
        mHideAnimator.addListener(new AnimatorListenerAdapter()
        {
            //because onAnimationEnd() goes off even for canceled animations
            boolean mWasCanceled;

            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);

                if (!mWasCanceled)
                    view.setVisibility(View.INVISIBLE);

                mWasCanceled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                super.onAnimationCancel(animation);
                mWasCanceled = true;
            }
        });

        updatePivot();
    }

    public void show()
    {
        mHideAnimator.cancel();

        if (mView.getVisibility() == View.INVISIBLE) {
            mView.setVisibility(View.VISIBLE);
            updatePivot();
            mShowAnimator.start();
        }
    }

    public void hide()
    {
        updatePivot();
        mHideAnimator.start();
    }

    protected void updatePivot()
    {
        mView.setPivotX(mPivotXRelative * mView.getMeasuredWidth());
        mView.setPivotY(mPivotYRelative * mView.getMeasuredHeight());
    }

    public static abstract class AbsBuilder<T extends VisibilityAnimationManager>
    {
        protected final View mView;
        protected int mShowAnimatorResource = R.animator.genfw_fastscroll_default_show;
        protected int mHideAnimatorResource = R.animator.genfw_fastscroll_default_hide;
        protected int mHideDelay = 1000;
        protected float mPivotX = 0.5f;
        protected float mPivotY = 0.5f;

        public AbsBuilder(View view)
        {
            mView = view;
        }

        public AbsBuilder<T> withShowAnimator(@AnimatorRes int showAnimatorResource)
        {
            mShowAnimatorResource = showAnimatorResource;
            return this;
        }

        public AbsBuilder<T> withHideAnimator(@AnimatorRes int hideAnimatorResource)
        {
            mHideAnimatorResource = hideAnimatorResource;
            return this;
        }

        public AbsBuilder<T> withHideDelay(int hideDelay)
        {
            mHideDelay = hideDelay;
            return this;
        }

        public AbsBuilder<T> withPivotX(float pivotX)
        {
            mPivotX = pivotX;
            return this;
        }

        public AbsBuilder<T> withPivotY(float pivotY)
        {
            mPivotY = pivotY;
            return this;
        }

        public abstract T build();
    }

    public static class Builder extends AbsBuilder<VisibilityAnimationManager>
    {
        public Builder(View view)
        {
            super(view);
        }

        public VisibilityAnimationManager build()
        {
            return new VisibilityAnimationManager(mView, mShowAnimatorResource, mHideAnimatorResource, mPivotX, mPivotY, mHideDelay);
        }
    }
}
