package com.pukkol.apkcenter.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class AboutUsModel {
    @SerializedName("tel")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("website")
    private String website;

    @SerializedName("extra")
    private String extra;


    public AboutUsModel(String phoneNumber, String email, String website, String extra) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "AboutUsModel{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", website='" + website + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
