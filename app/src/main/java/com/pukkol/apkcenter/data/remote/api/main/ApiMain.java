package com.pukkol.apkcenter.data.remote.api.main;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.Categorymodel;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.main.MainActivity;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiMain implements Thread.UncaughtExceptionHandler {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String COUNTRY = DeviceUtil.getCountry();

    private final ApiMainService mApi;
    private final onDataResponseListener mCallback;

    private final HashMap<String, Boolean> mEndLoading;
    private final HashMap<String, List<AppSmallModel>> mAllApps;

    public ApiMain(onDataResponseListener callback) {
        mCallback = callback;

        mApi = RetroClient.getMainApiService();
        mAllApps = new HashMap<>();
        mEndLoading = new HashMap<>();

        getCategoryNames();
    }

    public void getCategoryNames() {
        if(mApi == null){
            mCallback.onCategoriesResponse(500, new ArrayList<>());
            return;
        }

        mApi.categoryNames(COUNTRY).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, String>> call, @NonNull Response<Map<String, String>> response) {
                ArrayList<Categorymodel> categories = mapToModels(response.body());
                mCallback.onCategoriesResponse(response.code(), categories);
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>> call, @NonNull Throwable t) {
                mCallback.onCategoriesResponse(404, new ArrayList<>());
            }
        });
    }

    public void getCategoryApps(final String categoryName, int startIndex, int endIndex) {
        if(mAllApps == null || mApi == null || categoryName.equals("")){
            mCallback.onAppsResponse(500, new ArrayList<>(), categoryName);
            return;
        }

        checkIndexes(startIndex, endIndex);

        if(mAllApps.containsKey(categoryName)) {
            List<AppSmallModel> apps = mAllApps.get(categoryName);
            int appsSize = apps != null ? apps.size() : 0;
            endIndex = Math.min(appsSize, endIndex);

            // load history
            if(appsSize >= endIndex || mEndLoading.containsKey(categoryName)){
                assert apps != null;
                apps = new ArrayList<>(apps.subList(startIndex, endIndex));
                mCallback.onAppsResponse(200, apps, categoryName);
                return;
            }

        }

        mApi.categoryApps(categoryName).enqueue(new Callback<List<AppSmallModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<AppSmallModel>> call, @NonNull Response<List<AppSmallModel>> response) {
                List<AppSmallModel> apps = new ArrayList<>();

                if(response.body() == null || response.body().size() == 0){
                    mEndLoading.put(categoryName, true);
                } else {
                    apps = response.body();
                }
                if(mAllApps.containsKey(categoryName)) {
                    apps.addAll(0, Objects.requireNonNull(mAllApps.get(categoryName)));
                }

                mAllApps.put(categoryName, apps);
                mCallback.onAppsResponse(response.code(), apps, categoryName);
            }

            @Override
            public void onFailure(@NonNull Call<List<AppSmallModel>> call, @NonNull Throwable t) {
                List<AppSmallModel> apps = new ArrayList<>();
                if(mAllApps.containsKey(categoryName)) {
                    apps.addAll(0, Objects.requireNonNull(mAllApps.get(categoryName)));
                }

                mCallback.onAppsResponse(404, apps, categoryName);
            }
        });

    }


    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    // private functions

    @NonNull
    private ArrayList<Categorymodel> mapToModels(Map<String, String> values) {
        ArrayList<Categorymodel> models = new ArrayList<>();

        if(values != null) {
            for(Map.Entry<String, String> entry : values.entrySet()) {
                Categorymodel category = new Categorymodel(entry.getKey(),entry.getValue());
                models.add(category);
            }
        }

        return models;
    }

    private void checkIndexes(int indexBegin, int indexEnd) {
        if(indexBegin > indexEnd) {
            indexBegin = indexEnd - 10;
        }
        indexBegin = Math.max(0,indexBegin);
        indexEnd = Math.max(indexBegin + 1, indexEnd);

        Log.i(TAG, "start index: " + indexBegin + "\t|\tending index: " + indexEnd);
    }




    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener{
        void onCategoriesResponse(int responseCode, ArrayList<Categorymodel> categories);
        void onAppsResponse(int responseCode, List<AppSmallModel> applications, String categoryName);
    }
}
