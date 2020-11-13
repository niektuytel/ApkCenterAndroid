package com.pukkol.apkcenter.data.local.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.pukkol.apkcenter.BuildConfig;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledHelper;
import com.pukkol.apkcenter.data.model.local.InstalledModel;
import com.pukkol.apkcenter.data.remote.api.app.ApiApk;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.ui.app.AppActivity;

import java.io.File;

public class InstallApkService extends Service
        implements
        ApiApk.onDataResponseListener,
        Thread.UncaughtExceptionHandler {
    private static final String CHANNEL_ID = "my_channel_install_apk";
    private static final int NOTIFICATION_ID = 234;
    private static InstallApkService mInstance = null;
    private final IBinder mBinder = new LocalBinder();
    private onActionCallBack mCallback;
    private NotificationManager mNotificationManager;
    private ApiApk mApi;
    private DbInstalledHelper mDbInstalled;
    private NotificationCompat.Builder mBuilder;

    private String mTitle;
    private String filename;

    /**
     * check the current state of the service
     */
    public static boolean isServiceRunning() {
        try {
            // If instance was not cleared but the service was destroyed an Exception will be thrown
            return mInstance != null && mInstance.ping();
        } catch (NullPointerException e) {
            // destroyed/not-started
            return false;
        }
    }

    /**
     * set up handlers
     */
    @Override
    public void onCreate() {
        mInstance = this;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mApi = new ApiApk(this);
        mDbInstalled = new DbInstalledHelper(this, this);
    }

    /**
     * downloading apk task
     **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTitle = intent.getStringExtra("title");
        filename = intent.getStringExtra("filename");

        // Display a notification
        showNotification(mTitle);

        new Thread(
                () -> {
                    // let database know we going to install some apk (so when error he will delete it)
                    InstalledModel model = new InstalledModel(mTitle, filename, false);
                    mDbInstalled.addApplication(model);

                    mApi.downloadApk(mTitle, filename);
                }
        ).start();

        return START_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
    }

    /**
     * rebind to callback to the activity (installing data)
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * unbind callback to the activity (installing data)
     **/
    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    /**
     * remove whole notification
     **/
    @Override
    public void onDestroy() {
        mInstance = null;
        stopSelf();
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * any exception will been handled
     **/
    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(this, throwable);

        if (mCallback != null) mCallback.onServiceError(mTitle);

        showErrorNotification();
        stopSelf();
        mInstance = null;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    /**
     * install apk to device
     **/
    @Override
    public void onResponseApk(int responseCode) {
        // add local storage that it is installed
        if (responseCode == 200) {
            // download apk on device
            File path = Environment.getExternalStorageDirectory();
            File file = new File(path, filename);
            Uri apkProvider = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);

            if (!file.exists()) {
                return;
            }

            mDbInstalled.installSucceeded(mTitle);

            Intent installIntent = new Intent();
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            installIntent.setDataAndType(apkProvider, "application/vnd.android.package-archive");
            this.startActivity(installIntent);

            if (mCallback != null) {
                mCallback.onServiceFinished(mTitle);
            }
        }

        // clean up
        onDestroy();
    }

    @Override
    public void onDownloadProgress(long totalSize, long currentSize) {
        if (mCallback != null) {
            mBuilder.setProgress((int) totalSize, (int) currentSize, false);
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            mCallback.onServiceProgress(totalSize, currentSize);
        }
    }

    /**
     * display notifications service
     **/
    private void showNotification(String title) {

        // create notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "my_channel_for_apk";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // start activity when click on it
        Intent startIntent = new Intent(this, AppActivity.class);
        startIntent.putExtra("title", title);
        startIntent.setAction(String.valueOf(System.currentTimeMillis()));
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // create
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)  // the status icon
                .setContentTitle("download " + Uri.decode(title)) // the label of the entry
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(100, 1, false);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void showErrorNotification() {
        // create
        mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)  // the status icon
                .setContentTitle("Error on downloading " + Uri.decode(mTitle));

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private boolean ping() {
        return true;
    }

    /**
     * response action callback of a operation, so the ui can update the same data
     */
    public void setActionCallback(onActionCallBack callback) {
        mCallback = callback;
    }

    public interface onActionCallBack {
        void onServiceProgress(long totalProgress, long currentProgress);

        void onServiceError(String title);

        void onServiceFinished(String title);
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public InstallApkService getService() {
            return InstallApkService.this;
        }
    }

}