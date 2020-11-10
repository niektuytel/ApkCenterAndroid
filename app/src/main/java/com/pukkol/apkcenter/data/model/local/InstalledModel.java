package com.pukkol.apkcenter.data.model.local;

public class InstalledModel {
    private String title;
    private String filename;

    public InstalledModel(String title, String filename) {
        this.title = title;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "InstalledModel{" +
                "title='" + title + '\'' +
                ", filename='" + filename + '\'' +
                '}';
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
