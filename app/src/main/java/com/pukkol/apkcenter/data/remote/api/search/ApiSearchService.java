package com.pukkol.apkcenter.data.remote.api.search;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiSearchService {

    @GET("apkcenter/search")
    Call<List<String>> onSearchTitle(@Query(value = "title", encoded = true) String input);

}
