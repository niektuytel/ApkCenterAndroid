package com.pukkol.apkcenter.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;
import com.pukkol.apkcenter.ui.base.BaseActivity;
import com.pukkol.apkcenter.ui.search.SearchActivity;

public class MainActivity
    extends
        BaseActivity
    implements
        MainMvpView,
        TabLayout.OnTabSelectedListener,
        BaseActivity.OnBaseReloadListener
{
    private TabLayout mLayoutTab;
    private RecyclerView mLayoutActivity;

    private MainPresenter mMainPresenter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected Object getActivityLayout() {
        mLayoutActivity = findViewById(R.id.apps_layout);
        return mLayoutActivity;
    }

    @Override
    protected BaseActivity.OnBaseReloadListener getCallback() {
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutTab = findViewById(R.id.tabLayout);
        CardView buttonSearch = findViewById(R.id.button_Search);

        mLayoutTab.addOnTabSelectedListener(this);
        buttonSearch.setOnClickListener(this);

        // send local stored errors to api
        new Thread(
                () -> new ErrorHandler(this, null)
        ).start();


        // run main activity
        new Thread (
                () -> {
                    mMainPresenter = new MainPresenter(this,this);
                    runOnUiThread(this::onStart);
                }
        ).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMainPresenter == null) return;

        new Thread(
                () -> mMainPresenter.onStart()
        ).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mMainPresenter.onCloseDataBases();
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        if (view.getId() == R.id.button_Search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("search", "");
            startActivity(intent);
        }
    }

    @Override
    public void onBaseReload() {
        new Thread(
                ()->mMainPresenter.onReload(true)
        ).start();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(mMainPresenter == null) return;
        mMainPresenter.onTabSelected(tab.getPosition(), false);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}

    @Override
    public void onTabReselected(TabLayout.Tab tab) {}


    @Override
    public void showApplications(AppSmallSectionAdapter adapter) {
        runOnUiThread(
                () -> {
                    mLayoutActivity.setLayoutManager(
                            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    );
                    mLayoutActivity.setAdapter(adapter);
                }
        );
    }

    @Override
    public void showCategoryTabs(@NonNull String[] categories) {
        if(categories.length == 0) return;
        runOnUiThread(
                () -> {
                    mLayoutTab.removeAllTabs();
                    for(final String category : categories) {
                        mLayoutTab.addTab( mLayoutTab.newTab().setText(category) );
                    }
                }
        );
    }

}