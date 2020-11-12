package com.pukkol.apkcenter.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.report.DbReportHelper;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.data.model.remote.AboutUsModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.remote.api.search.ApiAboutUs;
import com.pukkol.apkcenter.data.remote.api.search.ApiReport;
import com.pukkol.apkcenter.data.remote.api.search.ApiSearch;
import com.pukkol.apkcenter.data.remote.api.search.SearchApiStructure;
import com.pukkol.apkcenter.data.remote.api.www.ApiApkCombo;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.ui.search.listItem.ItemAdapter;
import com.pukkol.apkcenter.util.API;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchPresenter <Model>
    implements
        SearchApiStructure.onDataResponseListener,
        ApiAboutUs.onDataResponseListener,
        ItemAdapter.onListItemClickListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{
    private final Activity mActivity;
    private final Context mContext;
    private final SearchMvpView mSearchView;
    private DbReportHelper mDbReport;
    private SearchApiStructure<Model> mApi;
    private ApiApkCombo mApiApkCombo;

    private final ListItemsAdapter<Model> mAdapter;

    private List<Model> mDefaultModels;
    private String mSearchHint;
    private String mLatestInput;

    public SearchPresenter(@NonNull Activity activity, int resHintId, SearchMvpView searchView) {
        mContext = mActivity = activity;
        mSearchHint = activity.getString(resHintId);
        mSearchView = searchView;

        // load data
        if (mActivity.getString(resHintId).equals(mActivity.getString(R.string.search_hint))) {
            mAdapter = new ListItemsAdapter<>(mActivity, this);
        } else {
            mApiApkCombo = new ApiApkCombo(this);
            mDbReport = new DbReportHelper(mContext, this);

            mAdapter = new ListItemsAdapter<>(mActivity, mDbReport, this);
        }

        onReload();
    }

    public void onReload() {
        if (API.isNetworkAvailable(mContext)) {
            new Thread(this::loadDefaultData).start();
        } else {
            mSearchView.showErrorInternet();
        }
    }

    public void onSearch(@NonNull String input) {
        mLatestInput = input;
        if (input.equals("") && API.isNetworkAvailable(mContext)) {
            mAdapter.updateData((ArrayList<Model>) (mDefaultModels));
            mSearchView.showAdapter(mAdapter);
        } else if (!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
        } else {
            new Thread(() -> mApi.onSearch(input)).start();

            // search apps from world wide web
            if (mApiApkCombo != null) {
                new Thread(() -> mApiApkCombo.onSearch(input)).start();
            }
        }
    }

    public void onReportAdd(RequestModel model) {
        if (API.isNetworkAvailable(mContext)) {
            mApi.onReportAdd(model);
        } else {
            mSearchView.showErrorInternet();
        }
    }

    public void onReportRemove(RequestModel model) {
        if (API.isNetworkAvailable(mContext)) {
            mApi.onReportRemove(model);
        } else {
            mSearchView.showErrorInternet();
        }
    }

    public void onAboutUs() {
        if (API.isNetworkAvailable(mContext)) {
            ApiAboutUs apiAboutUs = new ApiAboutUs(this);
            apiAboutUs.getAboutUs();
        } else {
            mSearchView.showErrorInternet();
        }
    }


    @Override
    public void onSearchResponse(int responseCode, List<?> applications) {
        if (responseCode == 500) {
            mSearchView.showError();
        } else {
            mAdapter.updateData((ArrayList<Model>) applications);
            mSearchView.showAdapter(mAdapter);
        }
    }

    @Override
    public void onSearchResponseCallback(int responseCode, List<?> applications, String onInput) {
        if(responseCode == 500) {
            mSearchView.showError();
            return;
        } else if( applications == null || applications.size() == 0) {
            return;
        }

        if(mLatestInput.equals(onInput)) {
            mAdapter.addData((ArrayList<Model>) applications);
            mSearchView.showAdapter(mAdapter);
        }
    }

    @Override
    public void onReportResponse(int responseCode, StatusModel response) {
        if(responseCode == 500) {
            mSearchView.showError();
        } else if(!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
        }
    }

    @Override
    public void onAboutResponse(int responseCode, AboutUsModel model) {
        if(responseCode == 500) {
            mSearchView.showError();
        } else if(!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
        }

        mSearchView.showContact(model);
    }


    public void close() {
        if(mDbReport != null) {
            mDbReport.close();
        }
    }

    @Override
    public boolean isCurrentInput(String input) {
        return mSearchView.currentInput().equals(input);
    }

    @Override
    public void onItemClicked(SearchModel model) {
        if(mSearchHint.equals(mActivity.getString(R.string.search_hint))) {
            DeviceUtil.hideKeyboard(mActivity);

            Intent intent = new Intent(mContext, AppActivity.class);
            intent.putExtra("title", model.getTitle());
            intent.putExtra("icon", model.getIcon());
            intent.putExtra("star", model.getWebsiteUrl());
            mContext.startActivity(intent);
        }
    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mContext, throwable);
        mSearchView.showError();
    }


    private void loadDefaultData() {
        mDefaultModels = new ArrayList<>();

        if(mSearchHint.equals(mActivity.getString(R.string.search_hint))) {
            mApi = (SearchApiStructure<Model>) new ApiSearch(this);

            // load storage
            DbSearchHelper dbSearch = new DbSearchHelper(mContext, this);
            mDefaultModels = mApi.toModels(dbSearch.getRecommended(), (Model) new SearchModel());

            onSearch("");
        } else {
            mApi = (SearchApiStructure<Model>) new ApiReport(this);

            // load default api
            if(API.isNetworkAvailable(mContext)){
                try {
                    mDefaultModels = mApi.getPopular();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
