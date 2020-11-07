package com.pukkol.apkcenter.ui.search.listElement;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;

public class ElementPresenter {

    private ElementMvpView mElementView;
    private int mIconResID;

    public ElementPresenter(ElementMvpView elementMvpView, @NonNull RequestModel model) {
        mElementView = elementMvpView;
        mIconResID = R.drawable.ic_persons;
        String url = "";

        if(model.getWebsiteUrls().length > 0) {
            url = model.getWebsiteUrls()[0];
        }

        displayIcon(model.getIcon());
        displayTitle(model.getTitle());
        displayWebsite(url);
        displayReports(model.getWantAdding(), model.getWantDeletion());
    }

    public ElementPresenter(ElementMvpView elementMvpView, @NonNull SearchModel model) {
        mElementView = elementMvpView;
        mIconResID = R.drawable.icon_clock;

        displayIcon(model.getIcon());
        displayTitle(model.getTitle());
        displayWebsite(model.getWebsiteUrl());
    }

    private void displayIcon(String url) {
        if(url != null && !url.equals("") && url.contains("http")) {
            mElementView.showIcon(url);
        } else {
            mElementView.showIcon(mIconResID);
        }
    }

    private void displayTitle(String title) {
        if(title != null && !title.equals("")) {
            title = Uri.decode(title);
            mElementView.showTitle(title);
        }
    }

    private void displayWebsite(String websiteUrl) {
        if(websiteUrl != null && !websiteUrl.equals("")) {
            mElementView.showUrl(websiteUrl);
        }
    }

    private void displayReports(int wantAdding, int wantDeletion) {
        int currentCount = wantAdding - wantDeletion;
        mElementView.showReports(currentCount);
    }

}
