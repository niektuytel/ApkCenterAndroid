package com.pukkol.apkcenter.data.remote.api;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.remote.api.apk.ApiApkService;
import com.pukkol.apkcenter.data.remote.api.app.ApiAppService;
import com.pukkol.apkcenter.data.remote.api.error.ApiErrorService;
import com.pukkol.apkcenter.data.remote.api.main.ApiMainService;
import com.pukkol.apkcenter.data.remote.api.request.ApiRequestService;
import com.pukkol.apkcenter.data.remote.api.search.ApiSearchService;
import com.pukkol.apkcenter.util.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    @NonNull
    private static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(API.sIpAddress)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    /**
     * Get Api Services
     * @return API Services
     **/
    @NonNull
    public static ApiErrorService getErrorApiService() {
        return getRetrofitInstance().create(ApiErrorService.class);
    }

    @NonNull
    public static ApiMainService getMainApiService() {
        return getRetrofitInstance().create(ApiMainService.class);
    }

    @NonNull
    public static ApiSearchService getSearchService() {
        return getRetrofitInstance().create(ApiSearchService.class);
    }

    @NonNull
    public static ApiRequestService getRequestService() {
        return getRetrofitInstance().create(ApiRequestService.class);
    }

    @NonNull
    public static ApiAppService getAppService() {
        return getRetrofitInstance().create(ApiAppService.class);
    }

    @NonNull
    public static ApiApkService getApkService() {
        return getRetrofitInstance().create(ApiApkService.class);
    }

}
