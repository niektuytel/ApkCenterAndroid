package com.pukkol.apkcenter.ui.section;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;
import com.pukkol.apkcenter.util.API;

import java.util.ArrayList;
import java.util.List;

public class SectionPresenter
    implements
        ExceptionCallback.onExceptionListener,
        AppSmallSectionAdapter.onClickListener,
        Thread.UncaughtExceptionHandler
{
    private static final String TAG = SectionActivity.class.getSimpleName();
    private static final Integer APPS_ROW_INDEX = 4;

    private final Context mContext;
    private final SectionMvpViewMy mSectionView;

    private List<AppSmallModel> mApplications;

    public SectionPresenter(Context context, SectionMvpViewMy sectionView, String currentSection, boolean inLocalStorage) {
        mContext = context;
        mSectionView = sectionView;

        // load local Storage
        if(inLocalStorage)
        {
            DbSearchHelper dbSearch = new DbSearchHelper(mContext, this);
            mApplications = dbSearch.getRecommended();
//            dbSearch.close();
        }
        //else {
        //  // load api storage
        //  // based on current section
        //}

        Log.i(TAG, "current section: " + currentSection);
        Log.i(TAG, "use local storage: " + inLocalStorage);
    }

    public void onStart() {
        refreshConnection(false);
    }

    public void refreshConnection(boolean refreshData) {
        if(mSectionView == null) return;

        if(API.isNetworkAvailable(mContext)) {
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
        for(int i=0; i < applications.size(); i++)
        {
            rowApps.add(applications.get(i));

            if(i > 0 && i % APPS_ROW_INDEX == 0 || i == (applications.size() - 1))
            {
                AppSmallSectionModel section = new AppSmallSectionModel(rowApps, R.layout.element_app_medium);
                sections.add(section);
                rowApps = new ArrayList<>();
            }
        }

        AppSmallSectionAdapter mAppSmallSectionAdapter = new AppSmallSectionAdapter(mContext, sections, R.layout.element_section_small, APPS_ROW_INDEX, this);
        mSectionView.showApplications(mAppSmallSectionAdapter);
    }

    @Override
    public void onSectionClicked(View view, String sectionTitle) { }

    @Override
    public void onItemClicked(View view, AppSmallModel app) {
        if(app == null) return;

        Intent intent = new Intent(mContext, AppActivity.class);
        intent.putExtra("title", app.getTitle());
        intent.putExtra("icon", app.getIcon());
        intent.putExtra("star", app.getStar());
        mContext.startActivity(intent);
    }
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mContext, throwable);
        mSectionView.showError();
    }

}
