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

package com.genonbeta.android.framework.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.genonbeta.android.framework.preference.SuperPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * created by: veli
 * date: 7/31/18 8:50 AM
 */
public class PreferenceUtils
{
	public static final String TAG = PreferenceUtils.class.getSimpleName();

	public static <T> boolean applyGeneric(String key, T object, SharedPreferences.Editor editor)
	{
		Log.d(TAG, "Put setting: " + key + " => " + String.valueOf(object));

		if (object instanceof Boolean)
			editor.putBoolean(key, (Boolean) object);
		else if (object instanceof Float)
			editor.putFloat(key, (Float) object);
		else if (object instanceof Integer)
			editor.putInt(key, (Integer) object);
		else if (object instanceof Long)
			editor.putLong(key, (Long) object);
		else if (object instanceof String)
			editor.putString(key, (String) object);
		else if (object instanceof Set)
			editor.putStringSet(key, (Set<String>) object);
		else
			return false;

		return true;
	}

	public static int sync(SharedPreferences... objects)
	{
		if (objects.length < 2)
			return 0;

		List<SharedPreferences> preferencesList = new ArrayList<>(Arrays.asList(objects));

		Collections.sort(preferencesList, new Comparator<SharedPreferences>()
		{
			@Override
			public int compare(SharedPreferences source1, SharedPreferences source2)
			{
				long comp1 = source1.getLong(SuperPreferences.KEY_SYNC_TIME, source1.getAll().size());
				long comp2 = source2.getLong(SuperPreferences.KEY_SYNC_TIME, source2.getAll().size());

				// last updated must come first
				return MathUtils.compare(comp2, comp1);
			}
		});

		long syncTime = System.currentTimeMillis();
		SharedPreferences chosenSource = preferencesList.get(0);

		chosenSource.edit()
				.apply();

		Map<String, ?> items = chosenSource.getAll();
		preferencesList.remove(0);

		int totalRegistered = 0;

		for (SharedPreferences syncingPreference : preferencesList)
			totalRegistered += syncPreferences(items, syncingPreference, syncTime);

		return totalRegistered;
	}

	public static int syncPreferences(SharedPreferences from, SharedPreferences to)
	{
		return syncPreferences(from.getAll(), to);
	}

	public static int syncPreferences(Map<String, ?> from, SharedPreferences to)
	{
		return syncPreferences(from, to, System.currentTimeMillis());
	}

	public static int syncPreferences(Map<String, ?> from, SharedPreferences to, long syncTime)
	{
		int totalRegistered = 0;
		SharedPreferences.Editor editor = to.edit();

		for (String key : from.keySet())
			if (applyGeneric(key, from.get(key), editor))
				totalRegistered++;

		editor.apply();

		return totalRegistered;
	}
}
