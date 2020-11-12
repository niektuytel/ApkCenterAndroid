package com.pukkol.apkcenter.ui.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.service.InstallApkService;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledHelper;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.remote.api.app.ApiApp;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.about.AboutActivity;
import com.pukkol.apkcenter.util.API;

public class AppPresenter
        implements
        ApiApp.onDataResponseListener,
        ServiceConnection,
        InstallApkService.onActionCallBack,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler {
    private final DbSearchHelper mDbSearch;
    private final DbInstalledHelper mDbInstalled;
    private final Activity mActivity;
    private final Context mContext;
    private final AppMvpView mAppView;
    private AppModel mAppModel;
    private ApiApp mApi;
    private final Intent mBindIntent;
    private InstallApkService mService;

    private boolean isInstallable = true;
    private boolean mServiceHaveError = false;

    public AppPresenter(Activity activity, AppMvpView appView) {
        mContext = mActivity = activity;
        mAppView = appView;

        mDbSearch = new DbSearchHelper(mContext, this);
        mBindIntent = new Intent(activity, InstallApkService.class);
        mDbInstalled = new DbInstalledHelper(mContext, this);
        mApi = new ApiApp(this);

        // bind service when is running
        if (InstallApkService.isServiceRunning()) {
            isInstallable = false;
            mAppView.showInstall(InstallState.OCCUPIED);
            mActivity.bindService(mBindIntent, this, Context.BIND_AUTO_CREATE);
        }

        // display ui
        Intent intent = activity.getIntent();
        String title = intent.getStringExtra("title");
        onReload(title);
    }

    public void onReload(String title) {
        if (API.isNetworkAvailable(mActivity)) {
            mApi.getApp(title);
        } else {
            mAppView.showErrorInternet();
        }
    }

    public void onAboutClicked() {
        Intent intent = new Intent(mContext, AboutActivity.class);
        intent.putExtra("content", mAppModel.getApk().getAbout());
        mContext.startActivity(intent);
    }

    public void onAppButtonClicked(String currentState) {
        String installAppState = mContext.getString(R.string.button_install);


        // install application
        if (installAppState.equals(currentState)) {
            String title = mAppModel.getTitle();
            String filename = mAppModel.getApk().getUrl();

            startService(title, filename);
        }
    }

    private void createImages(String[] images) {
        ImagesAdapter adapter = new ImagesAdapter(mActivity, images, 325);
        mAppView.showImages(adapter);
    }

    private void updateSearch() {
        String title = mAppModel.getTitle();
        String icon = mAppModel.getApk().getIcon();
        String websiteUrl = mAppModel.getWebsiteUrl();
        double star = Double.parseDouble(mAppModel.getApk().getReviews().getStar());
        int used = 1;
        String limit = mAppModel.getLimit();
        long currentTime = System.currentTimeMillis();

        AppSmallModel model = new AppSmallModel(title, icon, websiteUrl, star, used, limit, currentTime);
        mDbSearch.updateSearch(model);
    }

    private void startService(String title, String filename) {
        if (API.isNetworkAvailable(mActivity)) {
            if (isInstallable) {
                isInstallable = false;
                mBindIntent.putExtra("title", title);
                mBindIntent.putExtra("filename", filename);

                mActivity.startService(mBindIntent);
                mActivity.bindService(mBindIntent, this, Context.BIND_AUTO_CREATE);
                mAppView.showInstall(InstallState.OCCUPIED);
            }
        } else {
            mAppView.showErrorInternet();
        }
    }

    public void onBindService() {
        if (InstallApkService.isServiceRunning()) {
            mAppView.showInstall(InstallState.OCCUPIED);
            isInstallable = false;
            mActivity.bindService(mBindIntent, this, Context.BIND_AUTO_CREATE);
        } else {
            isInstallable = true;
        }
    }

    @Override
    public void onResponseApp(int responseCode, AppModel application) {
        if (responseCode == 200) {
            mAppModel = application;

            // app
            new Thread(
                    () -> {
                        mAppView.showText(application);
                        mAppView.showPegi(application.getApk().getPegi());
                        mAppView.showIcon(application.getApk().getIcon());
                        createImages(application.getApk().getImages());

                        boolean exist = mDbInstalled.isInstalled(mAppModel.getTitle());
                        if (exist) {
                            mAppView.showInstall(InstallState.INSTALLED);
                        } else {
                            mAppView.showInstall(InstallState.INSTALLABLE);
                        }
                    }
            ).start();

            // store to the ai
            updateSearch();

        } else {
            mAppView.showError();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        InstallApkService.LocalBinder binder = (InstallApkService.LocalBinder) iBinder;
        mService = binder.getService();
        if (mService != null) {
            mService.setActionCallback(this);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }

    @Override
    public void onServiceProgress(long total, long current) {
        if (mServiceHaveError) return;

        int percentage = (int) (((double) current / (double) total) * 100.00);
        String txt = "busy downloading " + percentage + "%";
        mAppView.showProgress(txt);
    }

    @Override
    public void onServiceError(String title) {
        mServiceHaveError = true;
        mService = null;
        mAppView.showInstall(InstallState.SOMETHING_WRONG);
    }

    @Override
    public void onServiceFinished(String title) {
        if (mAppModel.getTitle().equals(title)) {
            mAppView.showInstall(InstallState.INSTALLED);
        } else {
            mAppView.showInstall(InstallState.INSTALLABLE);
        }
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mContext, throwable);
        mAppView.showError();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }


}