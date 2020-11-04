package com.pukkol.apkcenter.data.model.application;

import com.google.gson.annotations.SerializedName;

public class AppSmallModel {
    @SerializedName("title")
    private String title;

    @SerializedName("icon")
    private String icon;

    @SerializedName("star")
    private double star;

    private int used;

    private String limit;

    private long latestUpdate;

    public AppSmallModel(String title, String icon, double star, int used, String limit, long latestUpdate) {
        this.title = title;
        this.icon = icon;
        this.star = star;
        this.used = used;
        this.limit = limit;
        this.latestUpdate = latestUpdate;
    }

    @Override
    public String toString() {
        return "AppSmallModel{" +
                "title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                ", star=" + star +
                ", used=" + used +
                ", limit='" + limit + '\'' +
                ", latestUpdate=" + latestUpdate +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public long getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(long latestUpdate) {
        this.latestUpdate = latestUpdate;
    }
}
