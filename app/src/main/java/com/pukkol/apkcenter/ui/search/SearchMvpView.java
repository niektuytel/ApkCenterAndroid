package com.pukkol.apkcenter.ui.search;

import com.pukkol.apkcenter.data.model.remote.AboutUsModel;
import com.pukkol.apkcenter.ui.base.BaseMvpView;

public interface SearchMvpView extends BaseMvpView {
    void showMenu(boolean updateData);

    <T> void showAdapter(ListItemsAdapter<T> adapter);

    void showContact(AboutUsModel model);

    String currentInput();
}
