package com.pukkol.apkcenter.data.remote.api.search;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiReport extends SearchApiStructure<RequestModel> implements Thread.UncaughtExceptionHandler {

    private final ApiReportService mApi;
    private final ApiSearchService mApiSearch;
    private final onDataResponseListener mCallback;

    public ApiReport(onDataResponseListener callback) {
        super();
        mCallback = callback;
        mApi = RetroClient.getRequestService();
        mApiSearch = RetroClient.getSearchService();
    }

    public void onSearch(String input) {
        if(mApi == null || mApiSearch == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        final String onInput = Uri.encode(input.toLowerCase());

        // search on own server for data
        mApi.onSearchTitle(onInput).enqueue(new Callback<List<RequestModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<RequestModel>> call, @NonNull Response<List<RequestModel>> response) {
                mCallback.onSearchResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<RequestModel>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });

        mApiSearch.onSearchTitle(onInput).enqueue(new Callback<ArrayList<SearchModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<SearchModel>> call, @NonNull Response<ArrayList<SearchModel>> response) {
                mCallback.onSearchResponseCallback(response.code(), response.body(), input);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SearchModel>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    @Override
    public void onReportAdd(RequestModel model) {
        if(mApi == null) {
            mCallback.onReportResponse(500, null);
            return;
        }

        String title = Uri.encode(model.getTitle());
        String icon = Uri.encode(model.getIcon());
        String websiteUrl = Uri.encode(model.getWebsiteUrl());

        mApi.reportAdd(title, icon, websiteUrl).enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(@NonNull Call<StatusModel> call, @NonNull Response<StatusModel> response) {
                mCallback.onReportResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<StatusModel> call, @NonNull Throwable throwable) {
                mCallback.onReportResponse(404, null);
            }
        });
    }

    @Override
    public void onReportRemove(RequestModel model) {
        if(mApi == null) {
            mCallback.onReportResponse(500, null);
            return;
        }

        String title = Uri.encode(model.getTitle());
        String icon = Uri.encode(model.getIcon());
        String websiteUrl = Uri.encode(model.getWebsiteUrl());

        mApi.reportRemove(title, icon, websiteUrl).enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(@NonNull Call<StatusModel> call, @NonNull Response<StatusModel> response) {
                mCallback.onReportResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<StatusModel> call, @NonNull Throwable throwable) {
                mCallback.onReportResponse(404, null);
            }
        });
    }


    public List<RequestModel> getPopular() throws IOException {
        if(mApi == null) {
            return new ArrayList<>();
        }

        return  mApi.getPopulars().execute().body();
    }

    @Override
    public List<RequestModel> toModels(@NonNull List<AppSmallModel> models, RequestModel instance) { return null; }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }
}
