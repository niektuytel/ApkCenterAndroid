package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.remote.AboutUsModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiAboutUs implements Thread.UncaughtExceptionHandler{

    private ApiAboutUsService mApiAbout;
    private onDataResponseListener mCallback;


    public ApiAboutUs(onDataResponseListener callback) {
        mApiAbout = RetroClient.getAboutService();
        mCallback = callback;
    }

    public void getAboutUs() {
        mApiAbout.getAboutUs().enqueue(new Callback<AboutUsModel>() {
            @Override
            public void onResponse(@NonNull Call<AboutUsModel> call, @NonNull Response<AboutUsModel> response) {
                mCallback.onAboutResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AboutUsModel> call, @NonNull Throwable t) {
                mCallback.onException(t);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }


    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onAboutResponse(int responseCode, AboutUsModel model);
    }
}
