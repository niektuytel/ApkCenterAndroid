package com.pukkol.apkcenter.ui.app;

import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.ui.base.BaseMvpView;

public interface AppMvpView extends BaseMvpView {
    void showText(AppModel model);

    void showIcon(String url);

    void showPegi(int age);

    void showImages(ImagesAdapter adapter);

    void showInstall(InstallState state);

    void showProgress(String value);
}
