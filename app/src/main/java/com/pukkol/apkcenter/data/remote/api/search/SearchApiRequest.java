package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.remote.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;
import com.pukkol.apkcenter.data.model.remote.StatusModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.data.remote.api.SearchApiStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchApiRequest extends SearchApiStructure<RequestModel> {

    private final ApiRequestService mApi;
    private final onDataResponseListener mCallback;

    public SearchApiRequest(onDataResponseListener callback) {
        super();
        mCallback = callback;
        mApi = RetroClient.getRequestService();
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        input = input.toLowerCase();

        mApi.onSearchTitle(input).enqueue(new Callback<List<RequestModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<RequestModel>> call, @NonNull Response<List<RequestModel>> response) {
                mCallback.onSearchResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<RequestModel>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    @Override
    public void onReportAdd(SearchModel model) {
        if(mApi == null) {
            mCallback.onReportResponse(500, null);
            return;
        }

        mApi.reportAdd(model.getTitle(), model.getIcon(), model.getWebsiteUrl()).enqueue(new Callback<StatusModel>() {
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
    public void onReportRemove(SearchModel model) {
        if(mApi == null) {
            mCallback.onReportResponse(500, null);
            return;
        }

        mApi.reportRemove(model.getTitle()).enqueue(new Callback<StatusModel>() {
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
}
