package com.pukkol.apkcenter.ui.app;

import com.pukkol.apkcenter.data.model.application.AppModel;

public interface AppMvpViewMy {
    void showError();
    void showErrorInternet();
    void showAppLayout();
    void showAppText(AppModel model);
    void showAppIcon(String url);
    void showAppImages(ImagesAdapter adapter, boolean containsWww);
    void showAppPegi(int age);
    void showWebsiteLayout();
    void showWebsite(String url, String requiredDomain);
    void showBar(final int scrollPosition);
    void showInstallState(int responseCode);
    void showLimit(int limit, int maxLimit, boolean display, String dayLimit);
}
