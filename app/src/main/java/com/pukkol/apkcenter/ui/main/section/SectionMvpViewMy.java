package com.pukkol.apkcenter.ui.main.section;

import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;

public interface SectionMvpViewMy{
    void showApplications(SectionListAdapter adapter);
    void showError();
    void showErrorInternet();
}
