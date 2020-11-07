package com.pukkol.apkcenter.data.remote.api.search;

import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.remote.StatusModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiRequestService {

    @GET("apkcenter/request")
    Call<List<RequestModel>> onSearchTitle(@Query(value = "title", encoded = true) String title);

    @GET("apkcenter/request/popular")
    Call<List<RequestModel>> getPopulars();

    @POST("apkcenter/request")
    Call<StatusModel> reportAdd(@Query(value = "title", encoded = true) String title, @Query(value = "icon", encoded = true) String icon, @Query(value = "url", encoded = true) String url);

    @DELETE("apkcenter/request")
    Call<StatusModel> reportRemove(@Query(value = "title", encoded = true) String title);
}
