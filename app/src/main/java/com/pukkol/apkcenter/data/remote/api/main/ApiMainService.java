package com.pukkol.apkcenter.data.remote.api.main;

import com.pukkol.apkcenter.data.model.application.AppSmallModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiMainService {

    @GET("apkcenter/category/{country}/categories")
    Call<Map<String, String>> categoryNames(@Path(value = "country", encoded = true) String country);

    @GET("apkcenter/category/{category_name}/apps")
    Call<List<AppSmallModel>> categoryApps(@Path(value = "category_name", encoded = true) String categoryName);

}
