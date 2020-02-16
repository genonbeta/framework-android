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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * created by: Veli
 * date: 18.11.2017 16:00
 */

public class SweetImageLoader<Object, ImageType> extends AsyncTask<Object, Void, ImageType>
{
	@SuppressLint("StaticFieldLeak")
	private Context mContext;
	private Object mObject;
	private Handler<Object, ImageType> mHandler;
	private WeakReference<ImageView> mImageViewReference;

	public SweetImageLoader(Handler<Object, ImageType> handler, Context context)
	{
		mHandler = handler;
		mContext = context;
	}

	@Override
	protected void onPostExecute(ImageType image)
	{
		ImageView imageView = getImageViewReference().get();

		if (isCancelled() || imageView == null)
			return;

		Holder holder = (Holder) imageView.getTag();

		if (getObject().equals(holder.getObject())) {
			imageView.setAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));

			if (!isCancelled() && image != null) {
				if (image instanceof Bitmap)
					imageView.setImageBitmap((Bitmap) image);
				else if (image instanceof Drawable)
					imageView.setImageDrawable((Drawable) image);
				else
					throw new UnsupportedOperationException("Class for loading is not supported: " + image.getClass().getName());
			}
		}
	}

	@SafeVarargs
	@Override
	protected final ImageType doInBackground(Object... params)
	{
		mObject = params[0];
		return mHandler.onLoadBitmap(getObject());
	}

	public Context getContext()
	{
		return mContext;
	}

	public WeakReference<ImageView> getImageViewReference()
	{
		return mImageViewReference;
	}

	public Object getObject()
	{
		return mObject;
	}

	public void setImageView(ImageView imageView)
	{
		this.mImageViewReference = new WeakReference<>(imageView);
	}

	public void setObject(Object object)
	{
		this.mObject = object;
	}

	public static <J, Y> void load(Handler<J, Y> handler, Context context, ImageView imageView, J object)
	{
		if (imageView.getTag() == null)
			imageView.setTag(new Holder());

		Holder viewHolder = (Holder) imageView.getTag();

		if (!object.equals(viewHolder.getObject())) {
			if (viewHolder.getSweetImageLoader() != null)
				viewHolder.getSweetImageLoader().cancel(true);

			imageView.setImageBitmap(null);

			viewHolder.object = object;
			viewHolder.sweetImageLoader = new SweetImageLoader<>(handler, context);

			viewHolder.getSweetImageLoader().setImageView(imageView);
			viewHolder.getSweetImageLoader().execute(object);
		}
	}

	public interface Handler<V, L>
	{
		public L onLoadBitmap(V object);
	}

	private static class Holder
	{
		public SweetImageLoader sweetImageLoader;
		public java.lang.Object object;

		public java.lang.Object getObject()
		{
			return object;
		}

		public SweetImageLoader getSweetImageLoader()
		{
			return sweetImageLoader;
		}
	}
}
