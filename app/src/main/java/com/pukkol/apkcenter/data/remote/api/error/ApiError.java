package com.pukkol.apkcenter.data.remote.api.error;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.ErrorModel;
import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiError implements Thread.UncaughtExceptionHandler{
    private static final String ERROR_TYPE = "apkcenter";

    private final onErrorResponseListener mCallback;
    private final ApiErrorService mApi;

    public ApiError(onErrorResponseListener callback) {
        mCallback = callback;
        mApi = RetroClient.getErrorApiService();
    }

    public void reportError(String errorMessage) {
        if(errorMessage == null) {
            mCallback.onApiResponse( 500, "ErrorModel is null (not called the default constructor)" );
            return;
        }

        ErrorModel model = new ErrorModel(ERROR_TYPE, errorMessage);
        mApi.reportError(model).enqueue(new Callback<StatusModel>() {
            @Override
            public void onResponse(@NonNull Call<StatusModel> call, @NonNull Response<StatusModel> response) {
                mCallback.onApiResponse(200, response.message());
            }

            @Override
            public void onFailure(@NonNull Call<StatusModel> call, @NonNull Throwable t) {
                mCallback.onApiResponse(404, "reporting error Failed, check your internet connection");
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onErrorResponseListener extends ExceptionCallback.onExceptionListener {
        void onApiResponse(int responseCode, String message);
    }
}
