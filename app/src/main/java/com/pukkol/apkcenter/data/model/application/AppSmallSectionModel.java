package com.pukkol.apkcenter.data.model.application;

import java.util.List;

public class AppSmallSectionModel {

    private String mTitle;
    private List<AppSmallModel> mApps;
    private Integer mSectionID;
    private Boolean mIsSql;

    public AppSmallSectionModel(List<AppSmallModel> apps, int layoutID) {
        mApps = apps;
        mSectionID = layoutID;
        mIsSql = false;
    }

    public AppSmallSectionModel(List<AppSmallModel> apps, int layoutID, boolean isSql) {
        mApps = apps;
        mSectionID = layoutID;
        mIsSql = isSql;
    }

    public AppSmallSectionModel(String title, List<AppSmallModel> apps, int layoutID, boolean isSql) {
        mTitle = title;
        mSectionID = layoutID;
        mIsSql = isSql;
        mApps = apps;
    }


    @Override
    public String toString() {
        return "SmallAppSectionModel{" +
                "mTitle='" + mTitle + '\'' +
                ", mApps=" + mApps +
                ", mSectionID=" + mSectionID +
                ", mIsSql=" + mIsSql +
                '}';
    }

    public Integer getSectionID() {
        return mSectionID;
    }

    public void setSectionID(Integer sectionID) {
        mSectionID = sectionID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public List<AppSmallModel> getApps() {
        return mApps;
    }

    public void setApps(List<AppSmallModel> apps) {
        mApps = apps;
    }

    public Boolean getIsSql() {
        return mIsSql;
    }

    public void setIsSql(Boolean isSql) {
        mIsSql = isSql;
    }
}