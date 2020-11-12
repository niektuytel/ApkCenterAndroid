package com.pukkol.apkcenter.ui.main.section;

import com.pukkol.apkcenter.ui.base.BaseMvpView;
import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;

public interface SectionMvpViewMy extends BaseMvpView {
    void showApplications(SectionListAdapter adapter);
//    void showError();
//    void showErrorInternet();
}
