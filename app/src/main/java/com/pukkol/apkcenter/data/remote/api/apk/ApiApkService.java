package com.pukkol.apkcenter.data.remote.api.apk;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiApkService {

    @GET("apkcenter/apk/{title}")
    Call<ResponseBody> downloadApk(@Path(value = "title", encoded = true) String title);
}
