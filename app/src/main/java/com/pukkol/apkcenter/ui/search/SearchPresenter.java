package com.pukkol.apkcenter.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.remote.SearchModel;
import com.pukkol.apkcenter.data.model.remote.StatusModel;
import com.pukkol.apkcenter.data.remote.api.search.SearchApiRequest;
import com.pukkol.apkcenter.data.remote.api.search.SearchApiSearch;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.data.remote.api.SearchApiStructure;
import com.pukkol.apkcenter.ui.search.listElement.ElementAdapter;
import com.pukkol.apkcenter.util.API;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchPresenter <Model>
    implements
        SearchApiStructure.onDataResponseListener,
        ElementAdapter.onListItemClickListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{
    private final Activity mActivity;
    private final Context mContext;
    private final SearchMvpView mSearchView;
    private SearchApiStructure<Model> mApi;

    private final SearchListAdapter<Model> mAdapter;

    private List<Model> mDefaultRow;
    private String mSearchHint;

    public SearchPresenter(@NonNull Activity activity, int resHintId, SearchMvpView searchView) {
        mContext = mActivity = activity;
        mSearchHint = activity.getString(resHintId);
        mSearchView = searchView;

        mAdapter = new SearchListAdapter<>(mActivity,this);

        new Thread( this::loadDefaultData ).start();
    }

    public void onSearch(@NonNull String input) {
        if(input.equals("") && API.isNetworkAvailable(mContext)) {
            mAdapter.updateData(mDefaultRow);
            mSearchView.showAdapter(mAdapter, mSearchHint);
        } else if (!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
        } else {
            mApi.onSearch(input);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSearchResponse(int responseCode, List<?> applications) {
        if(responseCode == 500) {
            mSearchView.showError();
            return;
        } else if (applications == null) {

            // Request model search for suggestions on the web
            if(mSearchHint.equals(mActivity.getString(R.string.request_hint_text))) {
                
                // request google play or they have some information
                // request google search machine or he found some website





                mSearchView.showRequestButton(mAdapter);
                return;
            }

        }


        mAdapter.updateData((List<Model>) applications);
        mSearchView.showAdapter(mAdapter, mSearchHint);
    }

    public void onReportAdd(SearchModel model){
        mApi.onReportAdd(model);

        // store locally you installed it
    }

    public void onReportRemove(SearchModel model){
        mApi.onReportRemove(model);

        // store locally you requested it
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
    public void onReportResponse(int responseCode, StatusModel response) {
        if(responseCode == 500) {
            mSearchView.showError();
        } else if(!API.isNetworkAvailable(mContext)) {
            mSearchView.showErrorInternet();
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

    /*private functions*/

    @SuppressWarnings("unchecked")
    private void loadDefaultData() {
        mDefaultRow = new ArrayList<>();

        if(mSearchHint.equals(mActivity.getString(R.string.search_hint))) {
            mApi = (SearchApiStructure<Model>) new SearchApiSearch(this);

            // default
            DbSearchHelper dbSearch = new DbSearchHelper(mContext, this);
            mDefaultRow = mApi.toModels(dbSearch.getRecommended(), (Model) new SearchModel());
            onSearch("");

        } else {
            mApi = (SearchApiStructure<Model>) new SearchApiRequest(this);

            // load voted title name and what you vote

            // default
            if(API.isNetworkAvailable(mContext)){
                try {
                    mDefaultRow = mApi.getPopular();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
