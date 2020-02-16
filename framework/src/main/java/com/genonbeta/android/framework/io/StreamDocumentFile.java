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

import android.net.Uri;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * created by: Veli
 * date: 18.02.2018 00:24
 */

public class StreamDocumentFile extends DocumentFile
{
	private StreamInfo mStream;

	public StreamDocumentFile(StreamInfo streamInfo, Uri original)
	{
		super(null, original);
		mStream = streamInfo;
	}

	@Override
	public DocumentFile createFile(String mimeType, String displayName)
	{
		return null;
	}

	@Override
	public DocumentFile createDirectory(String displayName)
	{
		return null;
	}

	@Override
	public Uri getUri()
	{
		return mStream.uri;
	}

	@Nullable
	public File getFile()
	{
		return mStream.file;
	}

	@Override
	public String getName()
	{
		return mStream.friendlyName;
	}

	public StreamInfo getStream()
	{
		return mStream;
	}

	@Override
	public String getType()
	{
		return mStream.mimeType;
	}

	@Override
	public boolean isDirectory()
	{
		return false;
	}

	@Override
	public boolean isFile()
	{
		return true;
	}

	@Override
	public boolean isVirtual()
	{
		return false;
	}

	@Override
	public long lastModified()
	{
		return 0;
	}

	@Override
	public long length()
	{
		return mStream.size;
	}

	@Override
	public boolean canRead()
	{
		return true;
	}

	@Override
	public boolean canWrite()
	{
		return true;
	}

	@Override
	public boolean delete()
	{
		return false;
	}

	@Override
	public boolean exists()
	{
		return true;
	}

	@Override
	public DocumentFile[] listFiles()
	{
		return new DocumentFile[0];
	}

	@Override
	public boolean renameTo(String displayName)
	{
		return false;
	}

	@Override
	public void sync()
	{
	}
}
