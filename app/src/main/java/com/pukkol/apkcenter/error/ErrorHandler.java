package com.pukkol.apkcenter.error;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.error.DbErrorHelper;
import com.pukkol.apkcenter.data.remote.api.error.ApiError;
import com.pukkol.apkcenter.util.API;

import java.util.Arrays;
import java.util.Base64;

public class ErrorHandler
    implements
        ApiError.onErrorResponseListener,
        Thread.UncaughtExceptionHandler
{
    private static final String TAG = "ErrorHandler";

    private final Context mContext;
    private final ApiError mApiError;
    private final DbErrorHelper mDbHelper;

    public ErrorHandler(Context context, Throwable throwable)
    {
        mContext = context;
        mApiError = new ApiError(this);
        mDbHelper = new DbErrorHelper(context);

        // prepare/report errors
        if(throwable == null) {
            sendLocalToApi();
        } else {
            onException(throwable);
        }

        //mDbHelper.close();
    }

    @Override
    public void onApiResponse(int responseCode, String message) {
        if(responseCode != 404) {
            Log.i(TAG, "Add error remotely: " + (responseCode == 200));
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        String message = createErrorMessage(throwable);
        if(API.isNetworkAvailable(mContext)) {
            mApiError.reportError(message);
        } else {
            Log.i(TAG, "Add error locally: " + mDbHelper.addError(message));
        }
    }

    public void sendLocalToApi() {
        if(!API.isNetworkAvailable(mContext)) return;

        for(String errorMessage : mDbHelper.getAllErrors()) {
            mApiError.reportError(errorMessage);
            Log.i(TAG, "Remove error locally: " + mDbHelper.removeError(errorMessage));
        }
    }

    private String createErrorMessage(Throwable throwable)
    {
        if(throwable == null) return "";

        String errorMessage =
                "Uncaught Exception thread:" + Thread.currentThread().getName() + "\\n\\n" +
                "StackTrace: " + Arrays.toString(throwable.getStackTrace())     + "\\n\\n" +
                "Throwable Message: " + throwable.getMessage();

        // add more logs information
        return Base64.getEncoder().encodeToString(errorMessage.getBytes());
    }
}
