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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import androidx.annotation.Nullable;
import com.genonbeta.android.framework.util.FileUtils;

import java.io.*;
import java.net.URI;

/**
 * created by: Veli
 * date: 4.10.2017 12:36
 */

public class StreamInfo
{
    public String friendlyName;
    public String mimeType;
    public Uri uri;
    public Type type;
    public long size;

    @Nullable
    public File file;

    private ContentResolver mResolver;

    public StreamInfo()
    {

    }

    public StreamInfo(Context context, Uri uri) throws FileNotFoundException, StreamCorruptedException, FolderStateException
    {
        if (!loadStream(context, uri))
            throw new StreamCorruptedException("Content was not able to route a stream. Empty result is returned");
    }

    public static StreamInfo getStreamInfo(Context context, Uri uri) throws FileNotFoundException, StreamCorruptedException, FolderStateException
    {
        return new StreamInfo(context, uri);
    }

    private boolean loadStream(Context context, Uri uri) throws FolderStateException, FileNotFoundException
    {
        String uriType = uri.toString();

        this.uri = uri;

        if (uriType.startsWith("content")) {
            mResolver = context.getContentResolver();
            Cursor cursor = mResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                    if (nameIndex != -1 && sizeIndex != -1) {
                        this.friendlyName = cursor.getString(nameIndex);
                        this.size = cursor.getLong(sizeIndex);
                        this.mimeType = mResolver.getType(uri);
                        this.type = Type.Stream;

                        return true;
                    }
                }

                cursor.close();
            }
        } else if (uriType.startsWith("file")) {
            File file = new File(URI.create(uriType));

            if (file.canRead()) {
                if (file.isDirectory())
                    throw new FolderStateException();

                this.friendlyName = file.getName();
                this.size = file.length();
                this.mimeType = FileUtils.getFileContentType(file.getName());
                this.type = Type.File;
                this.file = file;

                return true;
            }
        }

        return false;
    }

    public ContentResolver getContentResolver()
    {
        return mResolver;
    }

    public OutputStream openOutputStream() throws FileNotFoundException
    {
        return file == null
                ? getContentResolver().openOutputStream(uri, "wa")
                : new FileOutputStream(file, true);
    }

    public InputStream openInputStream() throws FileNotFoundException
    {
        return file == null
                ? getContentResolver().openInputStream(uri)
                : new FileInputStream(file);
    }

    public enum Type
    {
        Stream,
        File
    }

    public static class FolderStateException extends Exception
    {
    }
}
