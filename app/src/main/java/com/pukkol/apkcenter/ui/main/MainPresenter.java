package com.pukkol.apkcenter.ui.main;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.ui.app.AppActivity;
import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;
import com.pukkol.apkcenter.ui.section.SectionActivity;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledHelper;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchHelper;
import com.pukkol.apkcenter.data.model.Categorymodel;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.data.remote.api.main.ApiMain;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainPresenter
    implements
        ExceptionCallback.onExceptionListener,
        ApiMain.onDataResponseListener,
        AppSmallSectionAdapter.onClickListener,
        Thread.UncaughtExceptionHandler
{
    private static final Integer APPS_ROW_INDEX = 4;

    private final Context mContext;
    private final MainMvpView mMainView;
    private final DbInstalledHelper mDbInstalled;
    private final DbSearchHelper mDbSearch;
    private final ApiMain mApiMain;
    private AppSmallSectionAdapter mAppSmallSectionAdapter;

    private final String mHomeKey;
    private final String mRecommendedTitle;
    private List<AppSmallModel> mRecommended;
    private HashMap<String, List<AppSmallModel>> mApplications;
    private List<Categorymodel> mCategories;
    private int mSectionLayoutID;
    private int mLatestTabIndex = 0;


    public MainPresenter(Context context, MainMvpView mainView) {
        mContext = context;
        mMainView = mainView;
        mDbInstalled = new DbInstalledHelper(mContext, this);
        mDbSearch = new DbSearchHelper(mContext, this);
        mApiMain = new ApiMain(this);

        // set default values
        mHomeKey = mContext.getString(R.string.home);
        mRecommendedTitle = mContext.getString(R.string.recommended_text);
        mRecommended = mDbSearch.getRecommended();
        mApplications = new HashMap<>();

        onCategoriesUpdate(new ArrayList<>());
        onAppsUpdate(new ArrayList<>(), mHomeKey);

        //remove installed apks from sdcard
        new Thread(
                () -> {
                    mDbInstalled.cleanSdCard();

                    boolean needReload = mDbSearch.cleanDeletedData();
                    if(needReload) onReload(true);
                }
        ).start();
    }

    public void onStart() {
        mRecommended = mDbSearch.getRecommended();
        onReload(true);
    }

    public void onReload(boolean refreshData) {
        if(mMainView == null) return;

        if(API.isNetworkAvailable(mContext)) {
            mApiMain.getCategoryNames();
            onTabSelected(0, refreshData);
        } else {
            mMainView.showErrorInternet();
        }
    }

    public void onTabSelected(int position, boolean refreshData) {
        if(mMainView == null || (mLatestTabIndex == position && !refreshData)) return;

        String category = mCategories.get(position).getKey();
        mLatestTabIndex = position;

        if(API.isNetworkAvailable(mContext)) {
            mApiMain.getCategoryApps(category,0, 10);
        } else {
            mMainView.showErrorInternet();
        }

    }


    @Override
    public void onSectionClicked(View view, String title) {
        if(title == null) return;

        boolean fromSql = title.equals(mRecommendedTitle);

        Intent intent = new Intent(mContext, SectionActivity.class);
        intent.putExtra("isSql", fromSql);
        intent.putExtra("section", title);
        mContext.startActivity(intent);
    }

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
    public void onCategoriesResponse(int responseCode, ArrayList<Categorymodel> categories) {
        if(responseCode == 500) {
            return;
        }
        onCategoriesUpdate(categories);
    }

    @Override
    public void onAppsResponse(int responseCode, List<AppSmallModel> applications, String categoryName) {
        if( responseCode == 500 ) {
            return;
        }
        onAppsUpdate(applications, categoryName);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mContext, throwable);
        mMainView.showError();
    }


    private void onCategoriesUpdate(ArrayList<Categorymodel> categories) {
        if(categories == null) return;

        mCategories = categories;
        mCategories.add(0, new Categorymodel("Home", mHomeKey));

        String[] values = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++)
        {
            Categorymodel model = categories.get(i);
            values[i] = model.getValue();
        }

        mMainView.showCategoryTabs(values);
    }

    private void onAppsUpdate(List<AppSmallModel> applications, String categoryName) {
        if(applications == null) return;

        ArrayList<AppSmallSectionModel> sections = new ArrayList<>();

        if(mLatestTabIndex == 0) {
            mSectionLayoutID = R.layout.element_section_medium;

            // add sections home
            if(mRecommended.size() > 0)
            {
                String titleSection = mContext.getResources().getString(R.string.recommended_text);
                AppSmallSectionModel section = new AppSmallSectionModel(titleSection, mRecommended, R.layout.element_app_small, true);
                sections.add(section);
            }

        } else {
            mApplications.put(categoryName, applications);
            mSectionLayoutID = R.layout.element_section_small;

            // add app sections
            ArrayList<AppSmallModel> rowApps = new ArrayList<>();
            for(int i=0; i < applications.size(); i++)
            {
                rowApps.add(applications.get(i));

                if(i > 0 && i % APPS_ROW_INDEX == 0 || i == (applications.size() - 1))
                {
                    AppSmallSectionModel section = new AppSmallSectionModel(rowApps, R.layout.element_app_medium, false);
                    sections.add(section);
                    rowApps = new ArrayList<>();
                }
            }

        }

        mAppSmallSectionAdapter = new AppSmallSectionAdapter(mContext, sections, mSectionLayoutID, APPS_ROW_INDEX, this);
        mMainView.showApplications(mAppSmallSectionAdapter);
    }
}

