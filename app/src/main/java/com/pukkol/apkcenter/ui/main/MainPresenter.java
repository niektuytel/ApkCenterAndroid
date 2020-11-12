package com.pukkol.apkcenter.ui.main;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledHelper;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.data.model.local.Categorymodel;
import com.pukkol.apkcenter.data.remote.api.main.ApiMain;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.ui.main.adapter.SectionAdapter;
import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;
import com.pukkol.apkcenter.ui.main.section.SectionActivity;
import com.pukkol.apkcenter.util.API;

import java.util.ArrayList;
import java.util.List;

public class MainPresenter
    implements
        ApiMain.onDataResponseListener,
        SectionAdapter.onActionListener,
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{

    private final Activity mActivity;
    private final MainMvpView mView;

    private final DbInstalledHelper mDbInstalled;
    private final DbSearchHelper mDbSearch;
    private final ApiMain mApiMain;

    private SectionListAdapter mSectionAdapter;
    private List<AppSmallModel> mRecommended;
    private List<Categorymodel> mCategories;
    private int mLatestTabIndex = 0;


    public MainPresenter(Activity activity, MainMvpView mainView) {
        mActivity = activity;
        mView = mainView;

        mDbInstalled = new DbInstalledHelper(mActivity, this);
        mDbSearch = new DbSearchHelper(mActivity, this);
        mApiMain = new ApiMain(this);
        mSectionAdapter = new SectionListAdapter(mActivity, this);

        new Thread(this::loadDefaultData).start();
    }

    private void onCreateCategories(ArrayList<Categorymodel> categories) {
        if(categories == null) return;

        mCategories = categories;
        mCategories.add(0, new Categorymodel("Home", mView.getHomeKey()));

        String[] values = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++)
        {
            Categorymodel model = categories.get(i);
            values[i] = model.getValue();
        }

        mView.showCategoryTabs(values);
    }

    private void onCreateApplications(List<AppSmallModel> applications, String category, String sectionTitle) {
        if(applications == null) return;

        ArrayList<AppSmallSectionModel> sections = new ArrayList<>();

        if(sectionTitle != null) {
            if (applications.size() == 0) return;
            AppSmallSectionModel section = new AppSmallSectionModel(sectionTitle, applications);
            sections.add(section);
        }
        else if(category != null) {
            // add app sections
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
        }

        mSectionAdapter.newData(sections);
        mView.showApplications(mSectionAdapter);
    }


    @Override
    public void onResponseCategories(int responseCode, ArrayList<Categorymodel> categories) {
        if(responseCode == 500) return;

        onCreateCategories(categories);
    }

    @Override
    public void onResponseApplications(final int responseCode, final List<AppSmallModel> applications, final String category) {
        if( responseCode == 500 ) return;

        new Thread(
                ()->{
                    if(category.equals(mView.getHomeKey())) {
                        onCreateApplications(new ArrayList<>(mRecommended), null, mView.getRecommendedKey());
                    } else {
                        onCreateApplications(applications, category, null);
                    }
                }
        ).start();

    }


//    public void onStart() {
//        mRecommended = mDbSearch.getRecommended();
//        onReload(true);
//    }

    public void onReload(boolean refreshData) {
        if (mView == null) return;

        if (API.isNetworkAvailable(mActivity)) {
            mApiMain.getCategoryNames();
            onTabSelected(0, refreshData);
        } else {
            mView.showErrorInternet();
        }
    }

    public void onTabSelected(int position, boolean refreshData) {
        if(mView == null || (mLatestTabIndex == position && !refreshData)) return;

        String category = mCategories.get(position).getKey();
        mLatestTabIndex = position;

        if(API.isNetworkAvailable(mActivity)) {
            mApiMain.getCategoryApps(category,0, 10);
        } else {
            mView.showErrorInternet();
        }

    }

    @Override
    public void onClickItem(@NonNull AppSmallModel model) {
        Intent intent = new Intent(mActivity, AppActivity.class);
        intent.putExtra("title", model.getTitle());
        intent.putExtra("icon", model.getIcon());
        intent.putExtra("star", model.getStar());
        mActivity.startActivity(intent);
    }

    @Override
    public void onClickSection(String title) {
        Intent intent = new Intent(mActivity, SectionActivity.class);
        intent.putExtra("section", title);
        mActivity.startActivity(intent);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mActivity, throwable);
        mView.showError();
    }

    private void loadDefaultData() {
        mRecommended = mDbSearch.getRecommended();
        mDbInstalled.cleanSdCard();

        boolean needReload = mDbSearch.cleanDeletedData();
        if(needReload) onReload(true);

        onResponseApplications(200, new ArrayList<>(mRecommended), mView.getHomeKey());
    }


}

