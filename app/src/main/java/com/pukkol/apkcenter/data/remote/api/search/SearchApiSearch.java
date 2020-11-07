package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.remote.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.data.remote.api.SearchApiStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchApiSearch extends SearchApiStructure<SearchModel> {

    private final onDataResponseListener mCallback;
    private final ApiSearchService mApi;

    public SearchApiSearch(onDataResponseListener callback) {
        super();
        mCallback = callback;
        mApi = RetroClient.getSearchService();
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        input = input.toLowerCase();

        mApi.onSearchTitle(input).enqueue(new Callback<ArrayList<SearchModel>>() {
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
    public void onReportAdd(SearchModel model) { }

    @Override
    public void onReportRemove(SearchModel model) { }

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
}

