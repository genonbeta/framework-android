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

package com.genonbeta.android.updatewithgithub;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.genonbeta.android.framework.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import androidx.appcompat.app.AlertDialog;

/**
 * Created by: veli
 * Date: 11/13/16 8:25 AM
 */

public class GitHubUpdater
{
	public static final String TAG = GitHubUpdater.class.getSimpleName();

	private Context mContext;
	private String mRepo;
	private int mThemeRes;
	private boolean mPreReleaseIncluded;

	public GitHubUpdater(Context context, String repo, int themeRes, boolean preReleaseIncluded)
	{
		mContext = context;
		mRepo = repo;
		mThemeRes = themeRes;
		mPreReleaseIncluded = preReleaseIncluded;
	}

	public void checkForUpdates(final boolean popupDialog, final OnInfoAvailableListener listener)
	{
		if (popupDialog)
			Toast.makeText(mContext, R.string.genfw_uwg_check_for_updates_ongoing, Toast.LENGTH_LONG).show();

		new Thread()
		{
			@Override
			public void run()
			{
				super.run();

				Looper.prepare();

				try {
					Log.d(TAG, "Checking updates");

					mContext.setTheme(mThemeRes);

					RemoteServer server = new RemoteServer(mRepo);
					String result = server.connect(null, null);

					Log.d(TAG, "Server connected");

					final PackageInfo packInfo = mContext.getPackageManager().getPackageInfo(mContext.getApplicationInfo().packageName, 0);
					final String appVersionName = packInfo.versionName;
					final String applicationName = getAppLabel(mContext);

					JSONArray releases = new JSONArray(result);

					if (releases.length() > 0) {
						Log.d(TAG, "Reading releases: (total) " + releases.length());

						for (int iterator = 0; iterator < releases.length(); iterator++)
						{
							JSONObject selectedRelease = releases.getJSONObject(iterator);

							final boolean isPreRelease = selectedRelease.getBoolean("prerelease");

							if (isPreRelease && !mPreReleaseIncluded)
								continue;

							final String lastVersionName = selectedRelease.getString("tag_name");
							final String lastVersionTitle = selectedRelease.getString("name");
							final String lastVersionDate = selectedRelease.getString("published_at");
							final String lastVersionBody = selectedRelease.getString("body");

							ComparableVersion comLast = new ComparableVersion(lastVersionName);
							ComparableVersion comCurr = new ComparableVersion(appVersionName);

							if (listener != null)
								listener.onInfoAvailable(comLast.compareTo(comCurr) > 0, lastVersionName, lastVersionTitle, lastVersionBody, lastVersionDate);

							final File updateFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + applicationName + " v" + lastVersionName + ".apk");

							if (popupDialog && comLast.compareTo(comCurr) > 0) {
								Log.d(TAG, "New version found: " + lastVersionName);

								if (selectedRelease.has("assets")) {
									Log.d(TAG, "Reading assets");

									JSONArray releaseAssets = selectedRelease.getJSONArray("assets");

									if (releaseAssets.length() > 0) {
										Log.d(TAG, "Assets is cached: (total) " + releaseAssets.length());

										final JSONObject firstAsset = releaseAssets.getJSONObject(0);
										final String downloadURL = firstAsset.getString("browser_download_url");

										DialogInterface.OnClickListener downloadStart = new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int which)
											{
												if (updateFile.isFile())
													updateFile.delete();

												DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
												DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(downloadURL));

												downloadRequest.setTitle(mContext.getString(R.string.genfw_uwg_downloading_update_title, applicationName, lastVersionName));
												downloadRequest.setDescription(mContext.getString(R.string.genfw_uwg_downloading_update_description, applicationName));
												downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, applicationName + " v" + lastVersionName + ".apk");
												downloadRequest.setMimeType("application/vnd.android.package-archive");
												downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

												manager.enqueue(downloadRequest);
											}
										};

										DialogInterface.OnClickListener openDownloads = new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(DialogInterface dialog, int which)
											{
												mContext.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
											}
										};

										AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
												.setTitle(R.string.genfw_uwg_update_available);

										if (updateFile.isFile()) {
											Log.d(TAG, "File already exists: " + updateFile.getName());

											dialog.setMessage(R.string.genfw_uwg_update_exists)
													.setNeutralButton(R.string.genfw_uwg_download, downloadStart)
													.setPositiveButton(R.string.genfw_uwg_open, openDownloads);
										} else {
											Log.d(TAG, "Update is downloadable");

											dialog.setMessage(String.format(mContext.getString(R.string.genfw_uwg_update_body), appVersionName, lastVersionName, lastVersionDate, lastVersionBody))
													.setPositiveButton(R.string.genfw_uwg_download_now, downloadStart);
										}

										dialog.setNegativeButton(R.string.genfw_uwg_later, null)
												.show();
									} else
										Log.d(TAG, "No downloadable file is provided");
								} else
									Toast.makeText(mContext, R.string.genfw_uwg_no_update_available, Toast.LENGTH_LONG).show();
							} else if (popupDialog)
								Toast.makeText(mContext, R.string.genfw_uwg_currently_latest_version_info, Toast.LENGTH_LONG).show();

							// Notify only the latest release without going through all releases
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					Log.d(TAG, "Error occurred");

					if (popupDialog)
						Toast.makeText(mContext, R.string.genfw_uwg_version_check_error, Toast.LENGTH_LONG).show();
				} finally {
					Looper.loop();
				}
			}
		}.start();
	}

	public String getAppLabel(Context context)
	{
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = null;

		try {
			applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
		} catch (final PackageManager.NameNotFoundException e) {
		}

		return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "Unknown");
	}

	public static boolean isNewVersion(Context context, String comparedVersionName)
	{
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0);

			ComparableVersion comparableGiven = new ComparableVersion(comparedVersionName);
			ComparableVersion comparableCurrent = new ComparableVersion(packageInfo.versionName);

			return comparableGiven.compareTo(comparableCurrent) > 0;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean isNewVersion(String comparedVersionName)
	{
		return isNewVersion(mContext, comparedVersionName);
	}

	public interface OnInfoAvailableListener
	{
		void onInfoAvailable(boolean newVersion, String versionName, String title, String description, String releaseDate);
	}
}
