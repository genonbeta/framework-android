package com.genonbeta.android.framework.app;

import android.view.View;

import com.genonbeta.android.framework.ui.callback.SnackbarSupport;
import com.google.android.material.snackbar.Snackbar;

/**
 * created by: veli
 * date: 7/31/18 11:54 AM
 */
public class Fragment
		extends androidx.fragment.app.Fragment
		implements FragmentImpl, SnackbarSupport
{
	private boolean mIsMenuShown;
	private View mSnackbarContainer;
	private int mSnackbarLength = Snackbar.LENGTH_LONG;

	public Snackbar createSnackbar(int resId, Object... objects)
	{
		View drawOverView = mSnackbarContainer == null ? getView() : mSnackbarContainer;

		return drawOverView!= null
				? Snackbar.make(drawOverView, getString(resId, objects), mSnackbarLength)
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

	public void setSnackbarLength(int length)
	{
		mSnackbarLength = length;
	}

	public void setSnackbarContainer(View view)
	{
		mSnackbarContainer = view;
	}
}
