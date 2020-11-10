package com.pukkol.apkcenter.data.remote.api.search;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiSearch extends SearchApiStructure<SearchModel> implements Thread.UncaughtExceptionHandler {

    private final onDataResponseListener mCallback;
    private final ApiSearchService mApi;

    public ApiSearch(onDataResponseListener callback) {
        super();
        mCallback = callback;
        mApi = RetroClient.getSearchService();
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        String search = Uri.encode(input.toLowerCase());

        mApi.onSearchTitle(search).enqueue(new Callback<ArrayList<SearchModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<SearchModel>> call, @NonNull Response<ArrayList<SearchModel>> response) {
                mCallback.onSearchResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<SearchModel>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    @Override
    public void onReportAdd(RequestModel model) { }

    @Override
    public void onReportRemove(RequestModel model) { }

    @Override
    public List<SearchModel> getPopular() throws IOException { return null; }

    @Override
    public List<SearchModel> toModels(@NonNull List<AppSmallModel> models, SearchModel instance) {
        ArrayList<SearchModel> newModels = new ArrayList<>();

        for( AppSmallModel model : models) {
            String title = model.getTitle();
            String websiteUrl = model.getWebsiteUrl();
            String icon = model.getIcon();

            newModels.add(new SearchModel(title, websiteUrl, icon));

        }

        return newModels;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }
}

