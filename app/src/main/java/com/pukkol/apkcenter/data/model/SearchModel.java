package com.pukkol.apkcenter.data.model;

import com.google.gson.annotations.SerializedName;

public class SearchModel {
    @SerializedName("title")
    private String title;

    @SerializedName("websiteUrl")
    private String websiteUrl;

    @SerializedName("icon")
    private String icon;

    public SearchModel() { }

    public SearchModel(String title, String websiteUrl, String icon) {
        this.title = title;
        this.websiteUrl = websiteUrl;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "SearchModel{" +
                "title='" + title + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
