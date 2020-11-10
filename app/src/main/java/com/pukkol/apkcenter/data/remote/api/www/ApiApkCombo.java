package com.pukkol.apkcenter.data.remote.api.www;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.data.remote.api.search.SearchApiStructure;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiApkCombo {
    private static final Integer sMaxContent = 3000;
    private static final String[] sNeededSection = new String[]{"<div class=\"columns lapps is-multiline\">", "<div class=\"column is-nav\">"};
    private static final String[] sGetNextModel = new String[]{"</a>"};
    private static final String[] sSplitTitle = new String[]{"<strong>","</strong>"};
    private static final String[] sSplitIcon = new String[]{"\" data-src=\"", "\" alt="};

    private final ApiApkComboService mApi;
    private final SearchApiStructure.onDataResponseListener mCallback;

    public ApiApkCombo(SearchApiStructure.onDataResponseListener callback) {
        mApi = RetroClient.getApkComboService();
        mCallback = callback;
    }

    public void onSearch(String input) {
        if(mApi == null) {
            mCallback.onSearchResponse(500, new ArrayList<>());
            return;
        }
        String onInput = input.toLowerCase();
        mApi.onSearchTitleNL(onInput).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(mCallback.isCurrentInput(input)) {
                    assert response.body() != null;
                    toRequestModels(response.code(), response.body(), input);
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                mCallback.onException(throwable);
            }
        });
    }

    private void toRequestModels(int responseCode, @NonNull String html, String onInput){
        List<RequestModel> models = new ArrayList<>();

        if(!html.contains(sNeededSection[0])){
            mCallback.onSearchResponseCallback(responseCode, models, onInput);
            return;
        }


        String infoSection = html.split(sNeededSection[0],2)[1].split(sNeededSection[1],2)[0];
        if(infoSection.length() > sMaxContent) {
            infoSection = infoSection.substring(0,sMaxContent);
        }

        // get models
        while(infoSection.contains(sGetNextModel[0])) {
            if(!infoSection.contains(sGetNextModel[0])){
                break;
            }

            String strModel = infoSection.split(sGetNextModel[0],2)[0];
            int newPos = strModel.length() + sGetNextModel[0].length();

            //cut model out of html
            if(newPos <= infoSection.length()) {
                infoSection = infoSection.substring(newPos);
            } else {
                infoSection = "";
            }

            models.add(stringToModel(strModel));
        }

        mCallback.onSearchResponseCallback(responseCode, models, onInput);
    }

    @NonNull
    private RequestModel stringToModel(@NonNull String htmlText) {
        String title = htmlText.split(sSplitTitle[0],2)[1].split(sSplitTitle[1],2)[0];
        String icon = htmlText.split(sSplitIcon[0],2)[1].split(sSplitIcon[1],2)[0];

        SearchModel search = new SearchModel(title, "", icon);
        return new RequestModel(search);
    }

}

