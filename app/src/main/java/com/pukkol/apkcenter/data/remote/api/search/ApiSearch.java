package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiSearch implements Thread.UncaughtExceptionHandler {

    private final onDataResponseListener mCallback;
    private final ApiSearchService mApi;

    public ApiSearch(onDataResponseListener callback) {
        mCallback = callback;
        mApi = RetroClient.getSearchService();
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        input = input.toLowerCase();

        mApi.onSearchTitle(input).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                mCallback.onSearchResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onSearchResponse(int responseCode, List<String> titles);
    }
}

