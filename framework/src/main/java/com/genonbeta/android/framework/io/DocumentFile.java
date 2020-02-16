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

package com.genonbeta.android.framework.io;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;

import androidx.annotation.RequiresApi;

/**
 * created by: Veli
 * date: 17.02.2018 22:36
 */

abstract public class DocumentFile
{
	public static final String TAG = DocumentFile.class.getSimpleName();

	private final DocumentFile mParent;
	private Uri mOriginalUri;

	public DocumentFile(DocumentFile parent, Uri originalUri)
	{
		mParent = parent;
		setOriginalUri(originalUri);
	}

	public static DocumentFile fromFile(File file)
	{
		return new LocalDocumentFile(null, file);
	}

	public static DocumentFile fromUri(Context context, Uri uri, boolean prepareTree) throws FileNotFoundException
	{
		if (Build.VERSION.SDK_INT >= 21)
			try {
				return new TreeDocumentFile(null, context, prepareTree ? prepareUri(uri) : uri, uri);
			} catch (Exception e) {
				// it was expected it might not be TreeDocumentFile
			}

		try {
			return new StreamDocumentFile(new StreamInfo(context, uri), uri);
		} catch (Exception e) {
			// Now something is wrong
		}

		throw new FileNotFoundException("Failed to create right connection for " + uri.toString());
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof DocumentFile && getUri() != null && getUri().equals(((DocumentFile) obj).getUri());
	}

	public Uri getOriginalUri()
	{
		return mOriginalUri;
	}

	protected void setOriginalUri(Uri uri) {
		mOriginalUri = uri;
	}

	public abstract DocumentFile createFile(String mimeType, String displayName);

	public abstract DocumentFile createDirectory(String displayName);

	public abstract Uri getUri();

	public abstract String getName();

	public abstract String getType();

	public DocumentFile getParentFile()
	{
		return mParent;
	}

	public abstract boolean isDirectory();

	public abstract boolean isFile();

	public abstract boolean isVirtual();

	public abstract long lastModified();

	public abstract long length();

	public abstract boolean canRead();

	public abstract boolean canWrite();

	public abstract boolean delete();

	public abstract boolean exists();

	public abstract DocumentFile[] listFiles();

	public DocumentFile findFile(String displayName)
	{
		for (DocumentFile doc : listFiles()) {
			if (displayName.equals(doc.getName())) {
				return doc;
			}
		}
		return null;
	}

	public abstract boolean renameTo(String displayName);

	public abstract void sync() throws Exception;

	protected static void closeQuietly(Closeable closeable)
	{
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	@RequiresApi(21)
	protected static Uri prepareUri(Uri treeUri)
	{
		return DocumentsContract.buildDocumentUriUsingTree(treeUri, DocumentsContract.getTreeDocumentId(treeUri));
	}
}
