package com.pukkol.apkcenter.data.model.remote;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.util.API;

import java.util.Arrays;

public class RequestModel {

    @SerializedName("title")
    private String title;

    @SerializedName("websiteUrls")
    private String[] websiteUrls;

    @SerializedName("icon")
    private String icon;

    @SerializedName("wantDeletion")
    private int wantDeletion;

    @SerializedName("wantAdding")
    private int wantAdding;

    @SerializedName("versions")
    private String[] versions;

    @SerializedName("requestedTimes")
    private int requestedTimes;

    public RequestModel() { }

    public RequestModel(String title, String[] websiteUrls, String icon, int wantDeletion, int wantAdding, String[] versions, int requestedTimes) {
        this.title = title;
        this.websiteUrls = websiteUrls;
        this.icon = icon;
        this.wantDeletion = wantDeletion;
        this.wantAdding = wantAdding;
        this.versions = versions;
        this.requestedTimes = requestedTimes;
    }

    public RequestModel(@NonNull SearchModel model) {
        this.title = model.getTitle();
        this.websiteUrls = new String[]{model.getWebsiteUrl()};
        this.icon = model.getIcon();
        this.wantDeletion = 0;
        this.wantAdding = 0;
        this.versions = new String[]{API.APK_VERSION};
        this.requestedTimes = 0;
    }

    @Override
    public String toString() {
        return "RequestModel{" +
                "title='" + title + '\'' +
                ", websiteUrls=" + Arrays.toString(websiteUrls) +
                ", icon='" + icon + '\'' +
                ", wantDeletion=" + wantDeletion +
                ", wantAdding=" + wantAdding +
                ", versions=" + Arrays.toString(versions) +
                ", requestedTimes=" + requestedTimes +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getWebsiteUrls() {
        return websiteUrls;
    }

    public void setWebsiteUrls(String[] websiteUrls) {
        this.websiteUrls = websiteUrls;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getWantDeletion() {
        return wantDeletion;
    }

    public void setWantDeletion(int wantDeletion) {
        this.wantDeletion = wantDeletion;
    }

    public int getWantAdding() {
        return wantAdding;
    }

    public void setWantAdding(int wantAdding) {
        this.wantAdding = wantAdding;
    }

    public String[] getVersions() {
        return versions;
    }

    public void setVersions(String[] versions) {
        this.versions = versions;
    }

    public int getRequestedTimes() {
        return requestedTimes;
    }

    public void setRequestedTimes(int requestedTimes) {
        this.requestedTimes = requestedTimes;
    }

    public String getWebsiteUrl() {
        if(this.websiteUrls.length > 0) {
            return websiteUrls[0];
        }
        return "";
    }

}
