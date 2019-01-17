package com.genonbeta.android.framework.widget;

import android.content.Context;

import com.genonbeta.android.framework.object.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView;

public class PowerfulActionEngine<ReturningObject extends PowerfulActionEngine.PowerfulActionEngineImpl>
{
    private Context mContext;
    private EngineCallback<ReturningObject> mEngineCallback;
    private Map<Callback, Holder> mActiveActionModes = new ArrayMap<>();

    public PowerfulActionEngine(Context context, EngineCallback<ReturningObject> engineCallback)
    {
        mContext = context;
        mEngineCallback = engineCallback;
    }

    public <T extends Selectable> boolean check(@NonNull PowerfulActionEngine.Callback<T, ReturningObject> callback, T selectable, boolean selected, int position)
    {
        if (!selectable.setSelectableSelected(selected))
            return false;

        if (!hasActive(callback))
            start(callback);

        if (!mEngineCallback.onCheck(callback, selectable, selected, position))
            return false;

        if (selected)
            getHolder(callback).getSelectionList().add(selectable);
        else
            getHolder(callback).getSelectionList().remove(selectable);

        callback.onItemChecked(getContext(), mEngineCallback.onReturningObject(), selectable, position);

        return true;
    }

    public void finish(@NonNull final PowerfulActionEngine.Callback callback)
    {
        final PowerfulActionEngine.Holder holder = mActiveActionModes.get(callback);

        if (holder != null && mEngineCallback.onFinish(callback)) {
            callback.onFinish(getContext(), mEngineCallback.onReturningObject());

            mActiveActionModes.remove(callback);

            reload(callback);
        }
    }

    public Map<Callback, Holder> getActiveActionModes()
    {
        return mActiveActionModes;
    }

    public Context getContext()
    {
        return mContext;
    }

    public <T extends Selectable> PowerfulActionEngine.Holder<T> getHolder(PowerfulActionEngine.Callback<T, ReturningObject> callback)
    {
        return mActiveActionModes.get(callback);
    }

    public boolean hasActive(PowerfulActionEngine.Callback callback)
    {
        return mActiveActionModes.containsKey(callback);
    }

    public boolean reload(final PowerfulActionEngine.Callback callback)
    {
        if (callback == null
                || !mActiveActionModes.containsKey(callback)
                || !mEngineCallback.onReload(callback)) {
            finish(callback);
            return false;
        }

        return true;
    }

    public <T extends Selectable> boolean start(@NonNull final PowerfulActionEngine.Callback<T, ReturningObject> callback)
    {
        return start(callback, false);
    }

    public <T extends Selectable> boolean start(@NonNull final PowerfulActionEngine.Callback<T, ReturningObject> callback, boolean forceStart)
    {
        if ((mActiveActionModes.containsKey(callback) && !forceStart)
                || !mEngineCallback.onStart(callback, forceStart)) {
            finish(callback);
            return false;
        }

        mActiveActionModes.put(callback, new PowerfulActionEngine.Holder<>());

        return reload(callback);
    }

    public interface EngineCallback<ReturningObject extends PowerfulActionEngineImpl>
    {
        <T extends Selectable> boolean onStart(@NonNull final PowerfulActionEngine.Callback<T, ReturningObject> callback, boolean forceStart);

        boolean onReload(PowerfulActionEngine.Callback callback);

        <T extends Selectable> boolean onCheck(Callback<T, ReturningObject> callback, T selectable, boolean selected, int position);

        boolean onFinish(Callback callback);

        ReturningObject onReturningObject();
    }

    public interface Callback<T extends Selectable, ReturningObject extends PowerfulActionEngineImpl>
    {
        List<T> getSelectableList();

        void onItemChecked(Context context, ReturningObject actionMode, T selectable, int position);

        void onFinish(Context context, ReturningObject actionMode);
    }

    public static class SelectorConnection<T extends Selectable, ReturningObject extends PowerfulActionEngineImpl>
    {
        private ReturningObject mMode;
        private Callback<T, ReturningObject> mCallback;

        public SelectorConnection(ReturningObject mode, Callback<T, ReturningObject> callback)
        {
            mMode = mode;
            mCallback = callback;
        }

        public Callback<T, ReturningObject> getCallback()
        {
            return mCallback;
        }

        public ReturningObject getMode()
        {
            return mMode;
        }

        public List<T> getSelectedItemList()
        {
            Holder<T> holder = getMode().getHolder(getCallback());

            return holder == null ? new ArrayList<T>() : holder.getSelectionList();
        }

        public boolean isSelected(T selectable)
        {
            Holder<T> holder = getMode().getHolder(getCallback());
            return holder != null && holder.getSelectionList().contains(selectable);
        }

        public boolean selectionActive()
        {
            return getMode().hasActive(getCallback());
        }

        public boolean setSelected(RecyclerView.ViewHolder holder)
        {
            return setSelected(holder.getAdapterPosition());
        }

        public boolean setSelected(int position)
        {
            return setSelected(getCallback().getSelectableList().get(position), position);
        }

        public boolean setSelected(T selectable)
        {
            return setSelected(selectable, !isSelected(selectable), -1);
        }

        public boolean setSelected(T selectable, boolean selected)
        {
            return setSelected(selectable, selected, -1);
        }

        public boolean setSelected(T selectable, int position)
        {
            return setSelected(selectable, !isSelected(selectable), position);
        }

        public boolean setSelected(T selectable, boolean selected, int position)
        {
            // if it is already the same
            if (selected == isSelected(selectable))
                return selected;

            return getMode().check(getCallback(), selectable, selected, position);
        }

        public boolean removeSelected(T selectable)
        {
            if (!getMode().hasActive(getCallback()))
                return false;

            return getMode().getHolder(getCallback())
                    .getSelectionList()
                    .remove(selectable);
        }
    }

    public static class Holder<T extends Selectable>
    {
        private final List<T> mSelectionList = new ArrayList<>();

        public List<T> getSelectionList()
        {
            synchronized (mSelectionList) {
                return mSelectionList;
            }
        }
    }

    public interface PowerfulActionEngineImpl
    {
        <T extends Selectable, M extends PowerfulActionEngineImpl> boolean check(@NonNull PowerfulActionEngine.Callback<T, M> callback, T selectable, boolean selected, int position);

        void finish(@NonNull final PowerfulActionEngine.Callback callback);

        <T extends Selectable, M extends PowerfulActionEngineImpl> PowerfulActionEngine.Holder<T> getHolder(PowerfulActionEngine.Callback<T, M> callback);

        boolean hasActive(PowerfulActionEngine.Callback callback);

        boolean reload(final PowerfulActionEngine.Callback callback);

        <T extends Selectable, M extends PowerfulActionEngineImpl> boolean start(@NonNull final PowerfulActionEngine.Callback<T, M> callback);

        <T extends Selectable, M extends PowerfulActionEngineImpl> boolean start(@NonNull final PowerfulActionEngine.Callback<T, M> callback, boolean forceStart);
    }

    public interface OnSelectionTaskListener<ReturningObject extends PowerfulActionEngineImpl>
    {
        void onSelectionTask(boolean started, ReturningObject actionMode);
    }

    public static class Implementation implements PowerfulActionEngineImpl
    {
        private PowerfulActionEngine mEngine;

        public Implementation(PowerfulActionEngine engine)
        {
            mEngine = engine;
        }

        @Override
        public <T extends Selectable, M extends PowerfulActionEngineImpl> boolean check(@NonNull Callback<T, M> callback, T selectable, boolean selected, int position)
        {
            return mEngine.check(callback, selectable, selected, position);
        }

        @Override
        public void finish(@NonNull Callback callback)
        {
            mEngine.finish(callback);
        }

        @Override
        public <T extends Selectable, M extends PowerfulActionEngineImpl> Holder<T> getHolder(Callback<T, M> callback)
        {
            return mEngine.getHolder(callback);
        }

        @Override
        public boolean hasActive(Callback callback)
        {
            return mEngine.hasActive(callback);
        }

        @Override
        public boolean reload(Callback callback)
        {
            return mEngine.reload(callback);
        }

        @Override
        public <T extends Selectable, M extends PowerfulActionEngineImpl> boolean start(@NonNull Callback<T, M> callback)
        {
            return mEngine.start(callback);
        }

        @Override
        public <T extends Selectable, M extends PowerfulActionEngineImpl> boolean start(@NonNull Callback<T, M> callback, boolean forceStart)
        {
            return mEngine.start(callback, forceStart);
        }
    }
}
