package com.genonbeta.android.framework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;

import com.genonbeta.android.framework.object.Selectable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * created by: Veli
 * date: 19.11.2017 10:01
 */

public class PowerfulActionMode extends Toolbar implements PowerfulActionEngine.PowerfulActionEngineImpl
{
    private PowerfulActionToolbar<Toolbar, PowerfulActionMode> mToolbar;

    public PowerfulActionMode(Context context)
    {
        super(context);
        initialize();
    }

    public PowerfulActionMode(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    public PowerfulActionMode(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    protected void initialize()
    {
        mToolbar = new PowerfulActionToolbar<Toolbar, PowerfulActionMode>(getContext(), this)
        {
            @Override
            public PowerfulActionMode onReturningObject()
            {
                return PowerfulActionMode.this;
            }
        };
    }

    public PowerfulActionToolbar<Toolbar, PowerfulActionMode> getEngineToolbar()
    {
        return mToolbar;
    }

    public <T extends Selectable> boolean check(@NonNull Callback<T> callback, T selectable, boolean selected, int position)
    {
        return getEngineToolbar().getEngine().check(callback, selectable, selected, position);
    }

    public void finish(@NonNull final Callback callback)
    {
        getEngineToolbar().getEngine().finish(callback);
    }

    public View getContainerLayout()
    {
        return getEngineToolbar().getContainerLayout();
    }

    public <T extends Selectable> PowerfulActionEngine.Holder<T> getHolder(Callback<T> callback)
    {
        return getEngineToolbar().getEngine().getHolder(callback);
    }

    public MenuInflater getMenuInflater()
    {
        return getEngineToolbar().getMenuInflater();
    }

    public boolean hasActive(Callback callback)
    {
        return getEngineToolbar().getEngine().hasActive(callback);
    }

    public boolean reload(final Callback callback)
    {
        return getEngineToolbar().getEngine().reload(callback);
    }

    public void setContainerLayout(View containerLayout)
    {
        getEngineToolbar().setContainerLayout(containerLayout);
    }

    public void setOnSelectionTaskListener(OnSelectionTaskListener taskListener)
    {
        getEngineToolbar().setOnSelectionTaskListener(taskListener);
    }

    public <T extends Selectable> boolean start(@NonNull final Callback<T> callback)
    {
        return getEngineToolbar().getEngine().start(callback);
    }

    public <T extends Selectable> boolean start(@NonNull final Callback<T> callback, boolean forceStart)
    {
        return getEngineToolbar().getEngine().start(callback, forceStart);
    }

    @Override
    public <T extends Selectable, M extends PowerfulActionEngine.PowerfulActionEngineImpl> boolean check(@NonNull PowerfulActionEngine.Callback<T, M> callback, T selectable, boolean selected, int position)
    {
        return getEngineToolbar().getEngine().check(callback, selectable, selected, position);
    }

    @Override
    public void finish(@NonNull PowerfulActionEngine.Callback callback)
    {
        getEngineToolbar().getEngine().finish(callback);
    }

    @Override
    public <T extends Selectable, M extends PowerfulActionEngine.PowerfulActionEngineImpl> PowerfulActionEngine.Holder<T> getHolder(PowerfulActionEngine.Callback<T, M> callback)
    {
        return getEngineToolbar().getEngine().getHolder(callback);
    }

    @Override
    public boolean hasActive(PowerfulActionEngine.Callback callback)
    {
        return getEngineToolbar().getEngine().hasActive(callback);
    }

    @Override
    public boolean reload(PowerfulActionEngine.Callback callback)
    {
        return getEngineToolbar().getEngine().reload(callback);
    }

    @Override
    public <T extends Selectable, M extends PowerfulActionEngine.PowerfulActionEngineImpl> boolean start(@NonNull PowerfulActionEngine.Callback<T, M> callback)
    {
        return getEngineToolbar().getEngine().start(callback);
    }

    @Override
    public <T extends Selectable, M extends PowerfulActionEngine.PowerfulActionEngineImpl> boolean start(@NonNull PowerfulActionEngine.Callback<T, M> callback, boolean forceStart)
    {
        return getEngineToolbar().getEngine().start(callback, forceStart);
    }

    protected void updateVisibility(int visibility)
    {
        getEngineToolbar().updateVisibility(visibility);
    }

    public interface Callback<T extends Selectable> extends PowerfulActionToolbar.ToolbarCallback<T, PowerfulActionMode>
    {

    }

    public interface OnSelectionTaskListener extends PowerfulActionEngine.OnSelectionTaskListener<PowerfulActionMode>
    {

    }

    public static class SelectorConnection<T extends Selectable> extends PowerfulActionEngine.SelectorConnection<T, PowerfulActionMode>
    {
        public SelectorConnection(PowerfulActionMode mode, Callback<T> callback)
        {
            super(mode, callback);
        }
    }
}