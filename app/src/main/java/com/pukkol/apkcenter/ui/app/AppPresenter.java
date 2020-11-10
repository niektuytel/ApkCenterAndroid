package com.pukkol.apkcenter.ui.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.pukkol.apkcenter.BuildConfig;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledHelper;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.local.InstalledModel;
import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.remote.api.apk.ApiApk;
import com.pukkol.apkcenter.data.remote.api.app.ApiApp;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.about.AboutActivity;
import com.pukkol.apkcenter.util.API;
import com.pukkol.apkcenter.util.Value;

import java.io.File;

public class AppPresenter
    implements
        ApiApk.onDataResponseListener,
        ApiApp.onDataResponseListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler

{
    private static final String TAG = AppActivity.class.getSimpleName();
    private static final Integer LIMIT_MINUTES = 15;

    private final DbSearchHelper mDbSearch;
    private final DbInstalledHelper mDbInstalled;
    private final Activity mActivity;
    private final Context mContext;
    private final AppMvpViewMy mAppView;
    private AppModel mAppModel;
    private ApiApp mApi;

    private String mTitle;
    private boolean mIsLimitPaid = false;

    public AppPresenter(Activity activity, AppMvpViewMy appView) {
        mContext = mActivity = activity;
        mAppView = appView;
        mDbSearch = new DbSearchHelper(mContext, this);
        mDbInstalled = new DbInstalledHelper(mContext, this);
        mApi = new ApiApp(this);

        Intent intent = mActivity.getIntent();
        mTitle = intent.getStringExtra("title");

        onReload();
    }

    public void onReload() {
        mApi.getApp(mTitle);
    }

    public void onAboutClicked() {
        Intent intent = new Intent(mContext, AboutActivity.class);
        intent.putExtra("content", mAppModel.getApk().getAbout());
        mContext.startActivity(intent);
    }

    public void onAppButtonClicked(String currentState) {
        String installAppState = mContext.getString(R.string.button_install);

        String appTitle = mAppModel.getTitle();
        String apkFileName = mAppModel.getApk().getUrl();

        // install application
        if(installAppState.equals(currentState)) {
            startDownload(appTitle, apkFileName);
            mAppView.showInstallState(201);
        }
    }

    public void onLimitCalled(boolean payLimit) {
        // check
        if(mAppModel.getLimit() == null || mAppModel.getWebsiteUrl() == null) {
            return;
        }

        String title = mAppModel.getTitle();
        String currentLimit = mDbSearch.getLimit(title);
        String storedLimit = mAppModel.getLimit();

        if(!mIsLimitPaid) {
            mIsLimitPaid = payLimit;

            // check
            if(storedLimit.equals("None")){
                mAppView.showLimit(-1, 0, true, storedLimit);
                return;
            } else if (!Value.isNumeric(storedLimit) || !Value.isNumeric(currentLimit)) {
                mAppView.showLimit(-2, 0, false, storedLimit);
                return;
            }

            int limit = Integer.parseInt(currentLimit);
            boolean display = limit > 0;

            // pay limit
            if(payLimit && limit > 0) {
                limit -= 1;

                boolean response = mDbSearch.updateLimit(title, String.valueOf(limit));
                String state = response ? "Succeeded" : "Failed";
                Log.i(TAG, "Pay limit: " + state);
            }

            //display
            {
                int maxLimit = Integer.parseInt(storedLimit);

                int g = (int)Math.round((double)200 * ((double)limit / (double) maxLimit));
                int r = 200 - g;
                int b = 0;
                Log.i(TAG, "Display limit color [ R=" + r + ", G=" + g + ", B=" + b + " ]");

                mAppView.showLimit(limit, Color.rgb(r, g, b), display, storedLimit);
            }


            // start timer for removing 1 from limit if time expired
            if(payLimit) {
                new Thread(this::onLimitTimer).start();
            }
        }
    }

    public void onLimitTimer() {
        // check
        if(mAppModel.getLimit() == null || mAppModel.getWebsiteUrl() == null) {
            return;
        }

        long start = System.currentTimeMillis();
        long tokenExpired = start + (1000 * 60 * LIMIT_MINUTES);

        while(System.currentTimeMillis() < tokenExpired) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // on limit called
        onLimitCalled(true);
    }

    @Override
    public void onApkResponse(int responseCode) {
        Log.i(TAG, "response code download apk: " + responseCode);
        mAppView.showInstallState(responseCode);

        String title = mAppModel.getTitle();
        String filename = mAppModel.getApk().getUrl();

        // add local storage that it is installed
        if(responseCode == 200) {

            InstalledModel model = new InstalledModel(title, filename);
            mDbInstalled.addApplication(model);

            // download apk on device
            launchApk(filename);
        }
    }

    @Override
    public void onAppResponse(int responseCode, AppModel application) {
        if(responseCode == 200) {
            mAppModel = application;
            boolean containsApp = (application.getApk() != null);
            boolean containsWww = (application.getWebsiteUrl() != null);

            // app
            if(containsApp) {
                new Thread(
                        () -> {
                            mAppView.showAppLayout();
                            mAppView.showAppText(application);
                            mAppView.showAppPegi(application.getApk().getPegi());
                            mAppView.showAppIcon(application.getApk().getIcon());
                            createImages(application.getApk().getImages(), containsWww);

                            stateInstalled();
                        }
                ).start();
            }


            // website
            if(containsWww) {
                new Thread(
                        () -> {
                            mAppView.showWebsiteLayout();
                            mAppView.showWebsite(application.getWebsiteUrl(), getRequiredDomain());
                        }
                ).start();
            }

            // store to the ai
            updateSearch();
            onLimitCalled(false);

        } else {
            mAppView.showError();
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

    public String getDefaultUrl() {
        return mAppModel.getWebsiteUrl();
    }

    public boolean isLimitPaid() {
        return mIsLimitPaid;
    }


    //private functions
    private void createImages(String[] images, boolean containsWww) {
        ImagesAdapter adapter = new ImagesAdapter(mActivity, images, 325);
        mAppView.showAppImages(adapter, containsWww);
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

    private void launchApk(String filename) {
        File path = Environment.getExternalStorageDirectory();
        File installFile = new File(path, filename);
        Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider", installFile);

        Intent intents = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intents.setData(apkUri);
        intents.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mContext.startActivity(intents);
    }

    private void startDownload(String title, String filename) {
        if(API.isNetworkAvailable(mContext)) {
            ApiApk mApi = new ApiApk(this);
            mApi.downloadApk(title, filename);
        }
    }

    public String getRequiredDomain() {
        String requiredDomain = mAppModel.getWebsiteUrl().replace("https://", "");
        requiredDomain = requiredDomain.replace("http://", "");
        requiredDomain = requiredDomain.split("/")[0];
        return requiredDomain;
    }

    private void stateInstalled() {
        String title = mAppModel.getTitle();
        boolean exist = mDbInstalled.hasAppTitle(title);

        if(exist) {
            mAppView.showInstallState(200);
        }
    }

}