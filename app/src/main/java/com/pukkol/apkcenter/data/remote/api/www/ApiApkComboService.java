package com.pukkol.apkcenter.data.remote.api.www;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ApiApkComboService {

    @GET("https://apkcombo.com/nl-nl/search")
    @Headers({
            "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:81.0) Gecko/20100101 Firefox/81.0",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"
    })
    Call<String> onSearchTitleNL(@Query(value = "q", encoded = true) String title);
}
