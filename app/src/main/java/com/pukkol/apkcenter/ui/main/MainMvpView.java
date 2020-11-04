package com.pukkol.apkcenter.ui.main;

import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;
import com.pukkol.apkcenter.ui.base.BaseMvpView;

public interface MainMvpView extends BaseMvpView {
    void showApplications(AppSmallSectionAdapter adapter);
    void showCategoryTabs(String[] categories);
}
