package com.genonbeta.android.framework.app;

import android.content.Context;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentActivity;

/**
 * created by: veli
 * date: 7/31/18 12:56 PM
 */
public interface FragmentImpl
{
    Snackbar createSnackbar(int resId, Object... objects);

    FragmentActivity getActivity();

    Context getContext();
}
