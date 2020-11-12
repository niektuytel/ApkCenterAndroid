package com.pukkol.apkcenter.data.local.sql.installed;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.DatabaseHelper;
import com.pukkol.apkcenter.data.model.local.InstalledModel;
import com.pukkol.apkcenter.error.ExceptionCallback.onExceptionListener;

import java.io.File;
import java.util.List;

public class DbInstalledHelper implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "DbInstalledHelper";
    private static String sTableName = DbInstalledProfile.Table.TABLE_NAME;
    private static String sColumnTitle = DbInstalledProfile.Table.COLUMN_TITLE;
    private static String sColumnFilename = DbInstalledProfile.Table.COLUMN_FILENAME;
    private static String sColumnSuccess = DbInstalledProfile.Table.COLUMN_SUCCESS;

    private final DatabaseHelper mDb;
    private final onExceptionListener mCallback;

    public DbInstalledHelper(Context context, onExceptionListener exceptionCallback) {
        mDb = DatabaseHelper.getInstance(context);
        mCallback = exceptionCallback;
    }

    public void close() {
        if (mDb != null) {
            mDb.close();
        }
    }

    /**
     * add title of application that we try to install apk for
     **/
    public boolean addApplication(InstalledModel model) {
        ContentValues cv = DbInstalledProfile.toContentValues(model);
        return mDb.insert(sTableName, cv);
    }

    /**
     * check if title application is successful installed on device
     **/
    public boolean installSucceeded(String title) {
        return mDb.updateOnName(sTableName, sColumnSuccess, true, sColumnTitle, title);
    }

    /**
     * is application installed successfully on device
     **/
    public boolean isInstalled(String title) {
        return mDb.getValueOnName(sTableName, sColumnTitle, title, sColumnSuccess, false);
    }


    /**
     * get all installed filenames from sql-lite
     **/
    private List<String> getInstalledFileNames() {
        return mDb.getValuesOnName(sTableName, sColumnFilename);
    }


    /**
     * clean the storage if he found one of the installed files, than he will delete the file from downloaded
     **/
    public void cleanSdCard() {
        File path = Environment.getExternalStorageDirectory();
        for (String filename : getInstalledFileNames()) {
            File file = new File(path, filename);

            // delete file
            if (file.exists()) {
                boolean success = file.delete();
                Log.i(TAG, "deleting file :" + file.getAbsolutePath() + " succeeded:" + success);
            }
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

}