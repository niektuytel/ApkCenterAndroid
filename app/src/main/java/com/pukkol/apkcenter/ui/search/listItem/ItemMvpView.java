package com.pukkol.apkcenter.ui.search.listItem;

public interface ItemMvpView {
    void showIcon(String url);
    void showIcon(int resourceId);
    void showTitle(String title);
    void showUrl(String url);
    void showReports(int counting);
}
