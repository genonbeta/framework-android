package com.genonbeta.android.framework.ui.callback;

import android.support.design.widget.Snackbar;

/**
 * created by: veli
 * date: 15/04/18 18:45
 */
public interface SnackbarSupport
{
	Snackbar createSnackbar(int resId, Object... objects);
}
