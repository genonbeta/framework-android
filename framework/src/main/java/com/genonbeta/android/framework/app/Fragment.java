package com.genonbeta.android.framework.app;

import android.support.design.widget.Snackbar;

import com.genonbeta.android.framework.ui.callback.SnackbarSupport;

/**
 * created by: veli
 * date: 7/31/18 11:54 AM
 */
public class Fragment
		extends android.support.v4.app.Fragment
		implements FragmentImpl, SnackbarSupport
{
	private boolean mIsMenuShown;

	public Snackbar createSnackbar(int resId, Object... objects)
	{
		return getView() != null
				? Snackbar.make(getView(), getString(resId, objects), Snackbar.LENGTH_LONG)
				: null;
	}

	public boolean isMenuShown()
	{
		return mIsMenuShown;
	}

	@Override
	public void setMenuVisibility(boolean menuVisible)
	{
		super.setMenuVisibility(menuVisible);
		mIsMenuShown = menuVisible;
	}
}
