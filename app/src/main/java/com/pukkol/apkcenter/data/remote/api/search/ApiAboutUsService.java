package com.pukkol.apkcenter.data.remote.api.search;

import com.pukkol.apkcenter.data.model.remote.AboutUsModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiAboutUsService {

    @GET("apkcenter/AboutUs")
    Call<AboutUsModel> getAboutUs();
}
