package com.genonbeta.android.framework.util.listing;

import java.util.ArrayList;
import java.util.List;

/**
 * created by: Veli
 * date: 29.03.2018 01:26
 */
abstract public class Merger<T>
{
	public List<T> mBelongings = new ArrayList<>();

	abstract public boolean equals(Object obj);

	public List<T> getBelongings()
	{
		return mBelongings;
	}
}
