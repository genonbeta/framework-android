package com.genonbeta.android.framework.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.genonbeta.android.framework.io.DocumentFile;
import com.genonbeta.android.framework.io.LocalDocumentFile;
import com.genonbeta.android.framework.io.StreamDocumentFile;
import com.genonbeta.android.framework.io.StreamInfo;
import com.genonbeta.android.framework.io.TreeDocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.FileNameMap;
import java.net.URI;
import java.net.URLConnection;
import java.util.Locale;

/**
 * created by: veli
 * date: 7/31/18 8:14 AM
 */
public class FileUtils
{
    public static final String TAG = FileUtils.class.getSimpleName();

    public static void copy(Context context, DocumentFile source, DocumentFile destination,
                            Interrupter interrupter, int bufferLength, int socketTimeout) throws Exception
    {
        ContentResolver resolver = context.getContentResolver();

        InputStream inputStream = resolver.openInputStream(source.getUri());
        OutputStream outputStream = resolver.openOutputStream(destination.getUri());

        if (inputStream == null || outputStream == null)
            throw new IOException("Failed to open streams to start copying");

        byte[] buffer = new byte[bufferLength];
        int len = 0;
        long lastRead = System.currentTimeMillis();

        while (len != -1) {
            if ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();

                lastRead = System.currentTimeMillis();
            }

            if ((System.currentTimeMillis() - lastRead) > socketTimeout || interrupter.interrupted())
                throw new Exception("Timed out or interrupted. Exiting!");
        }

        outputStream.close();
        inputStream.close();
    }

    public static DocumentFile fetchDirectories(DocumentFile directoryFile, String path) throws IOException
    {
        return fetchDirectories(directoryFile, path, true);
    }

    public static DocumentFile fetchDirectories(DocumentFile directoryFile, String path, boolean createIfNotExists) throws IOException
    {
        DocumentFile currentDirectory = directoryFile;
        String[] pathArray = path.split(File.separator);

        for (String currentPath : pathArray) {
            if (currentDirectory == null)
                throw new IOException("Failed to create directories: " + path);

            DocumentFile existingOne = currentDirectory.findFile(currentPath);

            if (existingOne != null && !existingOne.isDirectory())
                throw new IOException("A file exists for of directory name: " + currentPath + " ; " + path);

            currentDirectory = existingOne == null && createIfNotExists
                    ? currentDirectory.createDirectory(currentPath)
                    : existingOne;
        }

        return currentDirectory;
    }

    public static DocumentFile fetchFile(DocumentFile directoryFile, String path, String displayName) throws IOException
    {
        return fetchFile(directoryFile, path, displayName, true);
    }

    public static DocumentFile fetchFile(DocumentFile directoryFile, String path, String displayName, boolean createIfNotExists) throws IOException
    {
        DocumentFile documentFile = path == null ? directoryFile : fetchDirectories(directoryFile, path, createIfNotExists);

        if (documentFile != null) {
            DocumentFile existingOne = documentFile.findFile(displayName);

            if (existingOne != null) {
                if (!existingOne.isFile())
                    throw new IOException("A directory exists for of file name");

                return existingOne;
            }

            if (createIfNotExists) {
                DocumentFile createdFile = documentFile.createFile(null, displayName);

                if (createdFile != null)
                    return createdFile;
            }
        }

        throw new IOException("Failed to create file: " + path);
    }

    public static DocumentFile fromUri(Context context, Uri uri) throws FileNotFoundException
    {
        String uriType = uri.toString();

        if (uriType.startsWith("file"))
            return DocumentFile.fromFile(new File(URI.create(uriType)));

        return DocumentFile.fromUri(context, uri, false);
    }

    public static String geActionTypeToView(String type)
    {
        if ("application/vnd.android.package-archive".equals(type)
                && Build.VERSION.SDK_INT >= 14)
            return Intent.ACTION_INSTALL_PACKAGE;

        return Intent.ACTION_VIEW;
    }

    public static String getFileContentType(String fileUrl)
    {
        FileNameMap nameMap = URLConnection.getFileNameMap();
        String fileType = nameMap.getContentTypeFor(fileUrl);

        return (fileType == null) ? "*/*" : fileType;
    }

    public static String getFileExtension(String fileName)
    {
        final int lastDot = fileName.lastIndexOf('.');

        if (lastDot >= 0) {
            final String extension = fileName.substring(lastDot + 1).toLowerCase();
            final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

            if (mime != null)
                return "." + extension;
        }

        return "";
    }

    public static Intent getOpenIntent(Context context, DocumentFile file)
    {
        if (Build.VERSION.SDK_INT >= 24
                || (Build.VERSION.SDK_INT == 23 && !Intent.ACTION_INSTALL_PACKAGE
                .equals(geActionTypeToView(file.getType())))) {
            return getOpenIntent(FileUtils.getSecureUriSilently(context, file), file.getType())
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        return getOpenIntent(file.getOriginalUri(), file.getType());
    }

    public static Intent getOpenIntent(Uri url, String type)
    {
        return new Intent(geActionTypeToView(type)).setDataAndType(url, type);
    }

    public static Uri getSecureUri(Context context, DocumentFile documentFile) throws IOException
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || documentFile instanceof TreeDocumentFile)
            return documentFile.getUri();

        if (documentFile instanceof StreamDocumentFile)
            return getSecureUri(context, ((StreamDocumentFile) documentFile).getStream());

        if (documentFile instanceof LocalDocumentFile)
            return getSelfProviderFile(context, ((LocalDocumentFile) documentFile).getFile());

        throw new IOException("Cannot gather right method to create uri");
    }

    public static Uri getSecureUriSilently(Context context, DocumentFile documentFile)
    {
        try {
            return getSecureUri(context, documentFile);
        } catch (Throwable e) {
            // do nothing
            Log.d(TAG, String.format(Locale.US,
                    "Cannot create secure uri for the file %s with error message '%s'",
                    documentFile.getName(),
                    e.getMessage()));
        }

        return documentFile.getUri();
    }

    public static Uri getSecureUri(Context context, StreamInfo streamInfo)
    {
        return StreamInfo.Type.File.equals(streamInfo.type)
                ? getSelfProviderFile(context, streamInfo.file)
                : streamInfo.uri;
    }

    public static Uri getSelfProviderFile(Context context, File file)
    {
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileprovider", file);
    }

    public static String getUniqueFileName(DocumentFile documentFolder, String fileName, boolean tryActualFile)
    {
        if (tryActualFile && documentFolder.findFile(fileName) == null)
            return fileName;

        int pathStartPosition = fileName.lastIndexOf(".");

        String mergedName = pathStartPosition != -1 ? fileName.substring(0, pathStartPosition) : fileName;
        String fileExtension = pathStartPosition != -1 ? fileName.substring(pathStartPosition) : "";

        if (mergedName.length() == 0
                && fileExtension.length() > 0) {
            mergedName = fileExtension;
            fileExtension = "";
        }

        for (int exceed = 1; exceed < 999; exceed++) {
            String newName = mergedName + " (" + exceed + ")" + fileExtension;

            if (documentFolder.findFile(newName) == null)
                return newName;
        }

        return fileName;
    }

    public static boolean move(Context context, DocumentFile targetFile, DocumentFile destinationFile,
                               Interrupter interrupter, int bufferLength, int socketTimeout) throws Exception
    {
        if (!(targetFile instanceof LocalDocumentFile)
                || !(destinationFile instanceof LocalDocumentFile)
                || !((LocalDocumentFile) targetFile).getFile().renameTo(((LocalDocumentFile) destinationFile).getFile()))
            copy(context, targetFile, destinationFile, interrupter, bufferLength, socketTimeout);

        // syncs the file with latest data if it is database based
        destinationFile.sync();

        if (targetFile.length() == destinationFile.length()) {
            targetFile.delete();
            return true;
        }

        return false;
    }

    public static boolean openUri(Context context, DocumentFile file) {
        return openUri(context, getOpenIntent(context, file));
    }

    public static boolean openUri(Context context, Uri uri) {
        return openUri(context, getOpenIntent(uri, context.getContentResolver().getType(uri)));
    }

    public static boolean openUri(Context context, Intent intent)
    {
        try {
            context.startActivity(intent);
            return true;
        } catch (Throwable e) {
            Log.d(TAG, String.format(Locale.US,
                    "Open uri request failed with error message '%s'",
                    e.getMessage()));
        }

        return false;
    }

    public static String sizeExpression(long bytes, boolean notUseByte)
    {
        int unit = notUseByte ? 1000 : 1024;

        if (bytes < unit)
            return bytes + " B";

        int expression = (int) (Math.log(bytes) / Math.log(unit));
        String prefix = (notUseByte ? "kMGTPE" : "KMGTPE").charAt(expression - 1) + (notUseByte ? "i" : "");

        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, expression), prefix);
    }
}
