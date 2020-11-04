package com.pukkol.apkcenter.data.model.application;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

    public AppModel(String title, String websiteUrl, ApkModel apk, boolean popular, String limit) {
        this.title = title;
        this.websiteUrl = websiteUrl;
        this.apk = apk;
        this.popular = popular;
        this.limit = limit;
    }

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

    public String getDecodedTitle() throws UnsupportedEncodingException {
        return URLDecoder.decode(title, "UTF-8");
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
}
