package com.pukkol.apkcenter.data.model;

import com.google.gson.annotations.SerializedName;

public class ErrorModel {

    @SerializedName("ErrorType")
    private String errorType;

    @SerializedName("Error")
    private String error;

    public ErrorModel(String errorType, String error) {
        this.errorType = errorType;
        this.error = error;
    }

    public ErrorModel(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "DbErrorModel{" +
                "errorType='" + errorType + '\'' +
                ", error='" + error + '\'' +
                '}';
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
