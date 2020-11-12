package com.pukkol.apkcenter.data.remote.api.app;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.API;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ApiApk
        implements
        Thread.UncaughtExceptionHandler {
    private final onDataResponseListener mCallback;

    private int mBlockSize = 0;
    private long mTotalSize = 0;
    private long mCurrentSize = 0;

    public ApiApk(onDataResponseListener callback) {
        mCallback = callback;
    }

    public void downloadApk(String title, final String filename) {
        String urlString = API.sIpAddress + "apkcenter/apk/" + title;
        Thread progress = new Thread(this::displayProgress);

        try {
            //create url and connect
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();

            // prepare
            byte[] data = new byte[1024];
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, filename);
            mTotalSize = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(file);
            progress.start();

            while ((mBlockSize = input.read(data)) != -1) {
                mCurrentSize += mBlockSize;
                output.write(data, 0, mBlockSize);
            }

            // close streams
            output.flush();
            output.close();
            input.close();

            if (mCallback != null) {
                mCallback.onResponseApk(200);
            }

        } catch (Exception e) {
            mBlockSize = -1;

            if (mCallback != null) {
                mCallback.onException(e);
            }

        }
    }

    private void displayProgress() {
        while (mBlockSize != -1) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
                // this is not really needed when he failed on sleeping he will continue
                // and that wil give only a little more cpu usage (more accurate callback)
            }

            if (mBlockSize == -1) {
                return;
            }

            mCallback.onDownloadProgress(mTotalSize, mCurrentSize);
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onResponseApk(int responseCode);

        void onDownloadProgress(long totalSize, long currentSize);
    }


}