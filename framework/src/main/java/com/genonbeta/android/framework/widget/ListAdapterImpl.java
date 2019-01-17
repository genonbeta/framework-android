package com.genonbeta.android.framework.widget;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * created by: veli
 * date: 26.03.2018 11:12
 */

public interface ListAdapterImpl<T>
{
	void onDataSetChanged();

	List<T> onLoad();

	void onUpdate(List<T> passedItem);

	Context getContext();

	int getCount();

	LayoutInflater getInflater();

	List<T> getList();
}
