package com.pukkol.apkcenter.ui.main.section;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.ui.main.adapter.SectionAdapter;
import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;
import com.pukkol.apkcenter.util.API;

import java.util.ArrayList;
import java.util.List;

public class SectionPresenter
    implements
        SectionAdapter.onActionListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{
    private static final String TAG = SectionActivity.class.getSimpleName();

    private final Activity mActivity;
    private final SectionMvpViewMy mSectionView;
    private SectionListAdapter mSectionAdapter;

    private List<AppSmallModel> mApplications;

    public SectionPresenter(Activity activity, SectionMvpViewMy sectionView, String currentSection) {
        mActivity = activity;
        mSectionView = sectionView;

        // load local Storage
        DbSearchHelper dbSearch = new DbSearchHelper(mActivity, this);
        mApplications = dbSearch.getRecommended();

        mSectionAdapter = new SectionListAdapter(mActivity, this);
    }

    public void onStart() {
        refreshConnection(false);
    }

    public void refreshConnection(boolean refreshData) {
        if(mSectionView == null) return;

        if(API.isNetworkAvailable(mActivity)) {
            // load data + display [api/storage]
            Log.i(TAG, "refresh data remotely: " + refreshData);
            onAppsUpdate(mApplications);
        } else {
            mSectionView.showErrorInternet();
        }
    }

    private void onAppsUpdate(List<AppSmallModel> applications) {
        if(applications == null) return;
        mApplications = applications;

        // app sections
        ArrayList<AppSmallSectionModel> sections = new ArrayList<>();
        ArrayList<AppSmallModel> rowApps = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            rowApps.add(applications.get(i));

            int maxIndex = SectionListAdapter.MAX_ROW_INDEX - 1;
            if (i > 0 && i % maxIndex == 0 || i == (applications.size() - 1)) {
                AppSmallSectionModel section = new AppSmallSectionModel(rowApps);
                sections.add(section);
                rowApps = new ArrayList<>();
            }
        }

        mSectionAdapter.newData(sections);
        mSectionView.showApplications(mSectionAdapter);
    }


    @Override
    public void onClickItem(AppSmallModel model) {
        if (model == null) return;

        Intent intent = new Intent(mActivity, AppActivity.class);
        intent.putExtra("title", model.getTitle());
        intent.putExtra("icon", model.getIcon());
        intent.putExtra("star", model.getStar());
        mActivity.startActivity(intent);
    }

    @Override
    public void onClickSection(String title) {
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mActivity, throwable);
        mSectionView.showError();
    }
}
