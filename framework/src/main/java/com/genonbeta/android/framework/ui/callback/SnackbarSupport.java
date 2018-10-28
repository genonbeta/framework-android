package com.genonbeta.android.framework.ui.callback;

import com.google.android.material.snackbar.Snackbar;

/**
 * created by: veli
 * date: 15/04/18 18:45
 */
public interface SnackbarSupport
{
	Snackbar createSnackbar(int resId, Object... objects);
}
