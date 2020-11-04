package com.pukkol.apkcenter.ui.search;

public interface SearchMvpView {
    void showError();
    void showErrorInternet();
    void showApplications(SearchAdapter adapter, boolean hideMenu);
    void showMenus();
    void showContact();
    void showRequest();
    void updateMessage(String title);
}
