package com.genonbeta.android.framework.widget;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.genonbeta.android.framework.R;
import com.genonbeta.android.framework.object.Selectable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import com.genonbeta.android.framework.util.actionperformer.PerformerEngine;

abstract public class PowerfulActionToolbar<E extends Toolbar>
{
    private E mToolbar;
    private Context mContext;
    private View mContainerLayout;
    private MenuInflater mMenuInflater;
    private PerformerEngine mEngine;
    private boolean mCloseButtonShowing = true;

    public PowerfulActionToolbar(Context context, E toolbar)
    {
        mContext = context;
        mToolbar = toolbar;
        mMenuInflater = new MenuInflater(getContext());
        mEngine = new PerformerEngine();
    }

    public boolean isCloseButtonShowing()
    {
        return mCloseButtonShowing;
    }

    public View getContainerLayout()
    {
        return mContainerLayout;
    }

    public Context getContext()
    {
        return mContext;
    }

    public PerformerEngine getEngine()
    {
        return mEngine;
    }

    public MenuInflater getMenuInflater()
    {
        return mMenuInflater;
    }

    public E getToolbar()
    {
        return mToolbar;
    }

    public void setContainerLayout(View containerLayout)
    {
        mContainerLayout = containerLayout;
    }

    public void setCloseButtonShowing(boolean show)
    {
        mCloseButtonShowing = show;
    }

    protected void updateVisibility(int visibility)
    {
        int animationId = visibility == View.VISIBLE ? android.R.anim.fade_in : android.R.anim.fade_out;
        View view = getContainerLayout() == null ? mToolbar : getContainerLayout();

        if (visibility == View.VISIBLE) {
            view.setVisibility(visibility);
            view.setAnimation(AnimationUtils.loadAnimation(getContext(), animationId));
        } else {
            view.setAnimation(AnimationUtils.loadAnimation(getContext(), animationId));
            view.setVisibility(visibility);
        }
    }

    @Override
    public boolean onReload(final PerformerEngine.Callback callback)
    {
        getToolbar().getMenu().clear();

        updateVisibility(View.VISIBLE);

        if (mCloseButtonShowing) {
            getToolbar().setNavigationIcon(R.drawable.genfw_close_white_24dp);
            getToolbar().setNavigationContentDescription(android.R.string.cancel);
            getToolbar().setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mEngine.finish(callback);
                }
            });
        }

        boolean result = callback instanceof ToolbarCallback
                && ((ToolbarCallback) callback).onCreateActionMenu(getContext(), onReturningObject(), getToolbar().getMenu());

        // As we can't define the !?*_- listener with ease I had to hack into it using this
        if (result) {
            MenuItem.OnMenuItemClickListener defListener = new MenuItem.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem)
                {
                    boolean didTrigger = ((ToolbarCallback) callback).onActionMenuItemSelected(getContext(), onReturningObject(), menuItem);

                    if (didTrigger && mCloseButtonShowing)
                        mEngine.finish(callback);

                    return didTrigger;
                }
            };

            for (int menuPos = 0; menuPos < getToolbar().getMenu().size(); menuPos++)
                getToolbar().getMenu().getItem(menuPos).setOnMenuItemClickListener(defListener);
        }

        return true;
    }
}
