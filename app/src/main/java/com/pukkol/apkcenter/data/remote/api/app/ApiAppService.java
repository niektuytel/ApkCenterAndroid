package com.pukkol.apkcenter.data.remote.api.app;

import com.pukkol.apkcenter.data.model.application.AppModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiAppService {

    @GET("apkcenter/app/{country}/{title}")
    Call<AppModel> app(@Path(value = "country", encoded = true) String country, @Path(value = "title", encoded = true) String title);

}
