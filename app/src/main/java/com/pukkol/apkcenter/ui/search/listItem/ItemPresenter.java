package com.pukkol.apkcenter.ui.search.listItem;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;

public class ItemPresenter
        implements
        Thread.UncaughtExceptionHandler {

    private ItemMvpView mElementView;
    private ItemAdapter.onListItemClickListener mCallback;
    private int mIconResID;

    public ItemPresenter(ItemMvpView itemMvpView, @NonNull RequestModel model, ItemAdapter.onListItemClickListener callback) {
        mElementView = itemMvpView;
        mCallback = callback;
        mIconResID = R.drawable.ic_persons;
        String url = "";

        if (model.getWebsiteUrls().length > 0) {
            url = model.getWebsiteUrls()[0];
        }

        displayIcon(model.getIcon());
        displayTitle(model.getTitle());
        displayWebsite(url);
        displayReports(model.getWantAdding(), model.getWantDeletion());
    }

    public ItemPresenter(ItemMvpView itemMvpView, @NonNull SearchModel model) {
        mElementView = itemMvpView;
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
        if (websiteUrl == null) websiteUrl = "";
        mElementView.showUrl(websiteUrl);
    }

    private void displayReports(int wantAdding, int wantDeletion) {
        int currentCount = wantAdding - wantDeletion;
        mElementView.showReports(currentCount);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }
}
