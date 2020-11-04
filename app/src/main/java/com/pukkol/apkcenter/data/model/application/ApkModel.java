package com.pukkol.apkcenter.data.model.application;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class ApkModel {

    @SerializedName("url")
    private String url;

    @SerializedName("icon")
    private String icon;

    @SerializedName("reviews")
    private ReviewModel reviews;

    @SerializedName("downloads")
    private long downloads;

    @SerializedName("pegi")
    private int pegi;

    @SerializedName("images")
    private String[] images;

    @SerializedName("about")
    private String about;

    public ApkModel(String url, String icon, ReviewModel reviews, long downloads, int pegi, String[] images, String about) {
        this.url = url;
        this.icon = icon;
        this.reviews = reviews;
        this.downloads = downloads;
        this.pegi = pegi;
        this.images = images;
        this.about = about;
    }

    @Override
    public String toString() {
        return "ApkModel{" +
                ", url='" + url + '\'' +
                ", icon='" + icon + '\'' +
                ", reviews=" + reviews +
                ", downloads=" + downloads +
                ", pegi=" + pegi +
                ", images=" + Arrays.toString(images) +
                ", about='" + about + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ReviewModel getReviews() {
        return reviews;
    }

    public void setReviews(ReviewModel reviews) {
        this.reviews = reviews;
    }

    public String getDownloads() {
        return String.valueOf(downloads);
    }

    @NonNull
    public String getDownloadString() {
        String newDownloads;

        if (downloads >= 1000000000)
        {
            downloads = (downloads / 1000000000);
            newDownloads = downloads + "B+";
        }
        else if(downloads >= 1000000)
        {
            downloads = (downloads / 1000000);
            newDownloads = downloads + "M+";
        }
        else if(downloads >= 1000)
        {
            downloads = (downloads / 1000);
            newDownloads = downloads + "K+";
        }
        else
        {
            newDownloads = String.valueOf(downloads);
        }

        return newDownloads;
    }

    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    public int getPegi() {
        return pegi;
    }

    public void setPegi(int pegi) {
        this.pegi = pegi;
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
