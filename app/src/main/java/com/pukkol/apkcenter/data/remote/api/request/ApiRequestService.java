package com.pukkol.apkcenter.data.remote.api.request;

import com.pukkol.apkcenter.data.model.StatusModel;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequestService {

    @POST("apkcenter/request/{title}")
    Call<StatusModel> requestApp(@Path( value = "title", encoded = true) String title);
}
