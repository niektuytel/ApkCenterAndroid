package com.pukkol.apkcenter.data.remote.api.apk;

import android.os.Environment;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiApk
    implements
        Thread.UncaughtExceptionHandler
{
    private final onDataResponseListener mCallback;
    private ApiApkService mApi;

    public ApiApk(onDataResponseListener callback) {
        mCallback = callback;
        mApi = RetroClient.getApkService();
    }

    public void downloadApk(String title, final String filename) {
        if(mApi == null) {
            return;
        }

        mApi.downloadApk(title).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    File path = Environment.getExternalStorageDirectory();
                    File file = new File(path, filename);
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    assert response.body() != null;
                    IOUtils.write(response.body().bytes(), fileOutputStream);
                    mCallback.onApkResponse(200);
                } catch (Exception ex){
                    mCallback.onException(ex.getCause());
                    mCallback.onApkResponse(500);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                mCallback.onApkResponse(404);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onApkResponse(int responseCode);
    }


}
