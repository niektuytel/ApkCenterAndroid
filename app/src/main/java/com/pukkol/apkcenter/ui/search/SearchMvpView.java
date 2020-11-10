package com.pukkol.apkcenter.ui.search;

import com.pukkol.apkcenter.data.model.remote.AboutUsModel;

public interface SearchMvpView {
    void showError();
    void showErrorInternet();
    void showMenu(boolean updateData);
    <T> void showAdapter(ListItemsAdapter<T> adapter, String searchHint);
    void showContact(AboutUsModel model);
    String currentInput();
}
