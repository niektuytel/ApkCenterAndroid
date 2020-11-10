package com.pukkol.apkcenter.ui.main;

import com.pukkol.apkcenter.ui.base.BaseMvpView;
import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;

public interface MainMvpView extends BaseMvpView {
    String getRecommendedKey();
    String getHomeKey();
    void showApplications(SectionListAdapter adapter);
    void showCategoryTabs(String[] categories);
}
