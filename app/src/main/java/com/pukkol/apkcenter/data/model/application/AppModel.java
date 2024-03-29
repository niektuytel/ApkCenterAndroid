package com.pukkol.apkcenter.data.model.application;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class AppModel {
    @SerializedName("title")
    private String title;

    @SerializedName("websiteUrl")
    private String websiteUrl;

    @SerializedName("apk")
    private ApkModel apk;

    @SerializedName("popular")
    private boolean popular;

    @SerializedName("limit")
    private String limit;

    @SerializedName("category")
    private String category;

    public AppModel(String title, String websiteUrl, ApkModel apk, boolean popular, String limit) {
        this.title = title;
        this.websiteUrl = websiteUrl;
        this.apk = apk;
        this.popular = popular;
        this.limit = limit;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppModel{" +
                "title='" + title + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", apk=" + apk +
                ", popular=" + popular +
                ", limit=" + limit +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public String getDecodedTitle() {
        return Uri.decode(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public ApkModel getApk() {
        return apk;
    }

    public void setApk(ApkModel apk) {
        this.apk = apk;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
