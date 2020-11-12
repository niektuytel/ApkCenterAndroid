package com.pukkol.apkcenter.data.remote.api.app;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.DeviceUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiApp
    implements
        Thread.UncaughtExceptionHandler
{
    private static final String COUNTRY = DeviceUtil.getCountry();

    private final onDataResponseListener mCallback;
    private final ApiAppService mApi;

    public ApiApp(onDataResponseListener callback) {
        mCallback = callback;

        mApi = RetroClient.getAppService();
    }

    public void getApp(String appTitle) {
        if(mApi == null){
            mCallback.onResponseApp(500, null);
            return;
        }

        String country = Uri.encode(COUNTRY);

        mApi.app(country, appTitle).enqueue(new Callback<AppModel>() {
            @Override
            public void onResponse(@NonNull Call<AppModel> call, @NonNull Response<AppModel> response) {
                mCallback.onResponseApp(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AppModel> call, @NonNull Throwable t) {
                mCallback.onResponseApp(404, null);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onResponseApp(int responseCode, AppModel application);
    }
}
