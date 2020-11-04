package com.pukkol.apkcenter.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.data.remote.api.request.ApiRequest;
import com.pukkol.apkcenter.data.remote.api.search.ApiSearch;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.API;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter
    implements
        ApiSearch.onDataResponseListener,
        ApiRequest.onDataResponseListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{
    private final Activity mActivity;
    private final Context mContext;
    private final SearchMvpView mSearchView;
    private final ApiSearch mApiSearch;
    private final ApiRequest mApiRequest;

    private final List<AppSmallModel> mDefaultApps;
    private List<String> mCurrentTitles;
    private String mLatestInput;

    public SearchPresenter(Activity activity, SearchMvpView searchView) {
        mContext = mActivity = activity;
        mSearchView = searchView;
        mApiSearch = new ApiSearch(this);
        mApiRequest = new ApiRequest(this);

        // load local stored suggestions
        DbSearchHelper dbSearch = new DbSearchHelper(mContext, this);
        mDefaultApps = dbSearch.getRecommended();
//        dbSearch.close();

        onAppsUpdate("");
    }


    public void onAppsUpdate(@NonNull String input) {
        if(input.equals("") && API.isNetworkAvailable(mContext)) {
            setApplications(mDefaultApps);
        } else if (!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
        } else {
            mApiSearch.onSearch(input);
        }
    }

    public void requestApp(@NonNull String input) {
        if(input.length() == 0) {
            mSearchView.updateMessage("Empty request not allowed");
        } else if(input.equals(mLatestInput)) {
            mSearchView.updateMessage("Already been requested");
        } else {
            mApiRequest.requestApp(input);
        }
        mLatestInput = input;
    }

    public void onAppClicked(int position) {
        DeviceUtil.hideKeyboard(mActivity);
        String title = mCurrentTitles.get(position);

        Intent intent = new Intent(mContext, AppActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("icon", "");
        intent.putExtra("star", "");
        mContext.startActivity(intent);
    }

    @Override
    public void onSearchResponse(int responseCode, List<String> titles) {
        if(responseCode == 500 || titles == null) {
            mSearchView.showError();
            return;
        }

        setTitles(titles, true);
    }

    @Override
    public void onRequestResponse(int responseCode, StatusModel response) {
        if(responseCode == 500) {
            mSearchView.showError();
            return;
        } else if(responseCode == 404) {
            mSearchView.updateMessage("Something goes wrong");
            return;
        }

        String message = response.getStatus();
        mSearchView.updateMessage(message);
        mLatestInput = message;
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

    private void setApplications(List<AppSmallModel> models) {
        if(models == null) return;

        mCurrentTitles = new ArrayList<>();
        for(AppSmallModel model : models) {
            mCurrentTitles.add(model.getTitle());
        }

        setTitles(mCurrentTitles, false);
    }

    private void setTitles(List<String> titles, boolean hideMenu) {
        if(titles == null) return;
        mCurrentTitles = titles;

        SearchAdapter adapter = new SearchAdapter(mContext, mCurrentTitles);
        mSearchView.showApplications(adapter, hideMenu);
    }

}
