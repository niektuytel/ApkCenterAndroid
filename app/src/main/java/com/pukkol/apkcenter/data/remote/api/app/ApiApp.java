package com.pukkol.apkcenter.data.remote.api.app;

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

    public void getApp(String title) {
        if(mApi == null){
            mCallback.onAppResponse(500, null);
            return;
        }

        mApi.app(COUNTRY, title).enqueue(new Callback<AppModel>() {
            @Override
            public void onResponse(@NonNull Call<AppModel> call, @NonNull Response<AppModel> response) {
                mCallback.onAppResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<AppModel> call, @NonNull Throwable t) {
                mCallback.onAppResponse(404, null);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onAppResponse(int responseCode, AppModel application);
    }
}
