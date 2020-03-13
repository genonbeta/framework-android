/*
 * Copyright (C) 2020 Veli Tasalı
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.genonbeta.android.framework.app;

import android.view.View;
import com.genonbeta.android.framework.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * created by: veli
 * date: 7/31/18 11:54 AM
 */
public class Fragment extends androidx.fragment.app.Fragment implements FragmentBase
{
    private View mSnackbarContainer;
    private int mSnackbarLength = Snackbar.LENGTH_LONG;

    public Snackbar createSnackbar(int resId, Object... objects)
    {
        View drawOverView = mSnackbarContainer == null ? getView() : mSnackbarContainer;
        return drawOverView != null ? Snackbar.make(drawOverView, getString(resId, objects), mSnackbarLength) : null;
    }

    public boolean isScreenLandscape()
    {
        return getResources().getBoolean(R.bool.genfw_screen_isLandscape);
    }

    public boolean isScreenSmall()
    {
        return getResources().getBoolean(R.bool.genfw_screen_isSmall);
    }

    public boolean isScreenNormal()
    {
        return getResources().getBoolean(R.bool.genfw_screen_isNormal);
    }

    public boolean isScreenLarge()
    {
        return getResources().getBoolean(R.bool.genfw_screen_isLarge);
    }

    public boolean isXScreenLarge()
    {
        return getResources().getBoolean(R.bool.genfw_screen_isXLarge);
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
