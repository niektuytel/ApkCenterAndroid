package com.pukkol.apkcenter.data.remote.api.request;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRequest implements Thread.UncaughtExceptionHandler {

    private final ApiRequestService mApi;
    private final onDataResponseListener mCallback;

    public ApiRequest(onDataResponseListener callback) {
        mCallback = callback;
        mApi = RetroClient.getRequestService();
    }

    public void requestApp(String title) {
        if(mApi == null) {
            mCallback.onRequestResponse(500, null);
            return;
        }

        mApi.requestApp(title).enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(@NonNull Call<StatusModel> call, @NonNull Response<StatusModel> response) {
                mCallback.onRequestResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<StatusModel> call, @NonNull Throwable throwable) {
                mCallback.onRequestResponse(404, null);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onRequestResponse(int responseCode, StatusModel response);
    }
}
