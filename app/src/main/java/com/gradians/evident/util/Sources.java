package com.gradians.evident.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.gradians.evident.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by adamarla on 3/20/17.
 */

public class Sources {

    public boolean download(final Context ctx) {
        String deviceState = Environment.getExternalStorageState();
        boolean writeable = Environment.MEDIA_MOUNTED.equals(deviceState);

        Log.d("EvidentApp", "Writeable " + writeable);
        if (!writeable) return false;

        String url = ctx.getResources().getString(R.string.linode) + "sources.zip";
        Log.d("EvidentApp", "downloading " + url);
        Ion.with(ctx)
                .load(url)
                .write(new File(ctx.getExternalFilesDir(null), "sources.zip"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File zip) {
                        Log.d("EvidentApp", "download complete Success = " + (zip != null && zip.exists()));
                        if (zip != null && zip.exists()) unzip(ctx, zip);
                        else Log.d("EvidentApp", e.getMessage());
                    }
                });
        return true;
    }

    private void unzip(final Context ctx, final File zip) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                File target = ctx.getExternalFilesDir(null) ;
                try {
                    InputStream inpStream = new FileInputStream(zip) ;
                    ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inpStream)) ;
                    ZipEntry zEntry ;
                    byte buffer[] = new byte[2048] ;
                    int bytesRead ;
                    while (( zEntry = zipStream.getNextEntry()) != null) {
                        String zName = zEntry.getName() ;
                        Log.d("EvidentApp", zName);
                        File zFile = new File(target, zName),
                                zParent = zFile.getParentFile() ;

                        if (zEntry.isDirectory()) continue ;

                        // Ensure path to file exists
                        if (!zParent.exists() && !zParent.mkdirs()) continue ;

                        FileOutputStream output = new FileOutputStream(zFile) ;
                        while ((bytesRead = zipStream.read(buffer)) > 0) {
                            output.write(buffer, 0, bytesRead) ;
                        }
                        output.close() ;
                        zipStream.closeEntry();
                    }
                    zipStream.close();
                    inpStream.close();
                } catch(IOException e) {
                    return false;
                }
                return true ;
            }

            @Override
            protected void onPostExecute(Boolean unzipped) {
                Log.d("EvidentApp", "Unzipped");
            }
        };
        task.execute() ;
    }

}