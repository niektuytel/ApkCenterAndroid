package com.pukkol.apkcenter.ui.section;

import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;

public interface SectionMvpViewMy{
    void showApplications(AppSmallSectionAdapter adapter);
    void showError();
    void showErrorInternet();
}
