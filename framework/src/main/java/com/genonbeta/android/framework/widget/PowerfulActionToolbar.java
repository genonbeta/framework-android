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

abstract public class PowerfulActionToolbar<E extends Toolbar, ReturningObject extends PowerfulActionEngine.PowerfulActionEngineImpl>
        implements PowerfulActionEngine.EngineCallback<ReturningObject>
{
    private E mToolbar;
    private Context mContext;
    private View mContainerLayout;
    private MenuInflater mMenuInflater;
    private PowerfulActionEngine mEngine;
    private PowerfulActionEngine.OnSelectionTaskListener<ReturningObject> mTaskListener;
    private boolean mFinishAllowed = true;

    public PowerfulActionToolbar(Context context, E toolbar)
    {
        mContext = context;
        mToolbar = toolbar;
        mMenuInflater = new MenuInflater(getContext());
        mEngine = new PowerfulActionEngine(context, this);
    }

    public boolean isFinishAllowed()
    {
        return mFinishAllowed;
    }

    public View getContainerLayout()
    {
        return mContainerLayout;
    }

    public Context getContext()
    {
        return mContext;
    }

    public PowerfulActionEngine getEngine()
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

    public void setOnSelectionTaskListener(PowerfulActionEngine.OnSelectionTaskListener taskListener)
    {
        mTaskListener = taskListener;
    }

    public void setFinishAllowed(boolean allow)
    {
        mFinishAllowed = allow;
    }

    protected void updateVisibility(int visibility)
    {
        int animationId = visibility == View.VISIBLE ? android.R.anim.fade_in : android.R.anim.fade_out;
        View view = getContainerLayout() == null ? mToolbar : getContainerLayout();

        if (visibility == View.VISIBLE) {
            view.setVisibility(visibility);
            view.setAnimation(AnimationUtils.loadAnimation(getContext(), animationId));

            if (mTaskListener != null)
                mTaskListener.onSelectionTask(true, onReturningObject());
        } else {
            view.setAnimation(AnimationUtils.loadAnimation(getContext(), animationId));
            view.setVisibility(visibility);

            if (mTaskListener != null)
                mTaskListener.onSelectionTask(false, onReturningObject());
        }
    }

    @Override
    public <T extends Selectable> boolean onStart(@NonNull PowerfulActionEngine.Callback<T, ReturningObject> callback, boolean forceStart)
    {
        return callback instanceof ToolbarCallback
                && ((ToolbarCallback<T, ReturningObject>) callback).onPrepareActionMenu(getContext(), onReturningObject());
    }

    @Override
    public boolean onReload(final PowerfulActionEngine.Callback callback)
    {
        getToolbar().getMenu().clear();

        updateVisibility(View.VISIBLE);

        if (mFinishAllowed) {
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
                    boolean didTrigger = callback instanceof ToolbarCallback
                            && ((ToolbarCallback) callback).onActionMenuItemSelected(getContext(), onReturningObject(), menuItem);

                    if (didTrigger && mFinishAllowed)
                        mEngine.finish(callback);

                    return didTrigger;
                }
            };

            for (int menuPos = 0; menuPos < getToolbar().getMenu().size(); menuPos++)
                getToolbar().getMenu()
                        .getItem(menuPos)
                        .setOnMenuItemClickListener(defListener);
        }

        return true;
    }

    @Override
    public <E extends Selectable> boolean onCheck(PowerfulActionEngine.Callback<E, ReturningObject> callback, E selectable, boolean selected, int position)
    {
        return true;
    }

    @Override
    public boolean onFinish(PowerfulActionEngine.Callback callback)
    {
        updateVisibility(View.GONE);
        return true;
    }

    public interface ToolbarCallback<T extends Selectable, ReturningObject extends PowerfulActionEngine.PowerfulActionEngineImpl>
            extends PowerfulActionEngine.Callback<T, ReturningObject>
    {
        boolean onPrepareActionMenu(Context context, ReturningObject actionMode);

        boolean onCreateActionMenu(Context context, ReturningObject actionMode, Menu menu);

        boolean onActionMenuItemSelected(Context context, ReturningObject actionMode, MenuItem item);
    }
}
