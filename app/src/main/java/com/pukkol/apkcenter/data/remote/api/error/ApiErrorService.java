package com.pukkol.apkcenter.data.remote.api.error;

import com.pukkol.apkcenter.data.model.ErrorModel;
import com.pukkol.apkcenter.data.model.StatusModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiErrorService {

    @Headers({"Content-Type: application/json"})
    @POST("apkcenter/error")
    Call<StatusModel> reportError(@Body ErrorModel body);

}
