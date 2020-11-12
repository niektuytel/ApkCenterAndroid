package com.pukkol.apkcenter.data.model.local;

import androidx.annotation.NonNull;

public class InstalledModel {
    private String title;
    private String filename;
    private boolean success;

    public InstalledModel(String title, String filename, boolean success) {
        this.title = title;
        this.filename = filename;
        this.success = success;
    }

    @NonNull
    @Override
    public String toString() {
        return "InstalledModel{" +
                "title='" + title + '\'' +
                ", filename='" + filename + '\'' +
                ", succeeded=" + success +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
