package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiSearch implements Thread.UncaughtExceptionHandler {

    private final onDataResponseListener mCallback;
    private final ApiSearchService mApi;

    public ApiSearch(onDataResponseListener callback) {
        mCallback = callback;
        mApi = RetroClient.getSearchService();
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        input = input.toLowerCase();

        mApi.onSearchTitle(input).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                mCallback.onSearchResponse(response.code(), response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
        void onSearchResponse(int responseCode, List<String> titles);
    }
}


/*deprecated*/
//        try {
//            JSONArray response = mRequest.apiSearch(input);
//
//            for(int i = 0; i < response.length(); i++)
//            {
//                JSONObject json_app = response.getJSONObject(i);
//
//                int id = -1;
//                String icon = json_app.getString("Icon");
//                String title = URLDecoder.decode(json_app.getString("Title"), "utf-8");
//                double star = json_app.getDouble("Star");
//                int used = -1;
//
//                DbSearchModel model = new DbSearchModel( title, icon, star, used );
//                values.add(model);
//            }
//        }
//        catch (JSONException | UnsupportedEncodingException e)
//        {
//            new ErrorHandler(mActivity, e);
//        }
//        values = sortValues(input, values);// most matching first
//
//        return values;
//    public List<DbSearchModel> sortValues(@NonNull String input, List<DbSearchModel> values) {
//        List<DbSearchModel> new_values = new ArrayList<>();
//
//        for(int i = input.length(); i > 0; i--)
//        {
//            String sub_input = input.substring(0,i).toUpperCase();
//            for(DbSearchModel model : values)
//            {
//                String model_title = model.getTitle().toUpperCase();
//                if(model_title.contains(sub_input))
//                {
//                    boolean founded = false;
//                    for(DbSearchModel model2 : new_values)
//                    {
//                        String model2_title = model2.getTitle().toUpperCase();
//
//                        if(model2_title.equals(model_title))
//                        {
//                            founded = true;
//                            break;
//                        }
//                    }
//
//                    if(!founded)
//                    {
//                        new_values.add(model);
//                    }
//                }
//            }
//        }
//
//        return new_values;
//    }

