package com.pukkol.apkcenter.data.model.local;

public class ReportModel {
    private String title;
    private boolean reportAdd;
    private boolean reportRemove;

    public ReportModel(String title, boolean reportAdd, boolean reportRemove) {
        this.title = title;
        this.reportAdd = reportAdd;
        this.reportRemove = reportRemove;
    }

    @Override
    public String toString() {
        return "ReportModel{" +
                "title='" + title + '\'' +
                ", reportAdd=" + reportAdd +
                ", reportRemove=" + reportRemove +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isReportAdd() {
        return reportAdd;
    }

    public void setReportAdd(boolean reportAdd) {
        this.reportAdd = reportAdd;
    }

    public boolean isReportRemove() {
        return reportRemove;
    }

    public void setReportRemove(boolean reportRemove) {
        this.reportRemove = reportRemove;
    }
}
