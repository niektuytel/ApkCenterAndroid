package com.pukkol.apkcenter.data.model.remote;

import com.google.gson.annotations.SerializedName;

public class StatusModel {

    @SerializedName("status")
    private String status;

    public StatusModel(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "status='" + status + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
