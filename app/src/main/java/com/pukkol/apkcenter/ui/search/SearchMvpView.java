package com.pukkol.apkcenter.ui.search;

public interface SearchMvpView {
    void showError();
    void showErrorInternet();
    void showMenu(boolean updateData);
    <T> void showAdapter(SearchListAdapter<T> adapter, String searchHint);
    void showContact();
    <T> void showRequestButton(SearchListAdapter<T> adapter);
}
