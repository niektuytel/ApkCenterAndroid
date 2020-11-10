package com.pukkol.apkcenter.data.remote.api.search;

import com.pukkol.apkcenter.data.model.SearchModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSearchService {

    @GET("apkcenter/search")
    Call<ArrayList<SearchModel>> onSearchTitle(@Query(value = "title", encoded = true) String title);

}
