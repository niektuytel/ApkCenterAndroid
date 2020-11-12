package com.pukkol.apkcenter.ui.main.section;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.ui.base.BaseActivity;
import com.pukkol.apkcenter.ui.main.adapter.SectionListAdapter;
import com.pukkol.apkcenter.ui.search.SearchActivity;


// intent string "section"
public class SectionActivity
        extends
        BaseActivity
        implements
        SectionMvpViewMy,
        View.OnClickListener,
        Thread.UncaughtExceptionHandler {
    private RecyclerView mLayoutContent;

    private SectionPresenter mSectionPresenter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_section;
    }

    @Override
    protected Object getLayoutContent() {
        // set recyclerview
        mLayoutContent = findViewById(R.id.layout_content);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutContent.setLayoutManager(layoutManager);

        return mLayoutContent;
    }

    @Override
    protected void onContentCalled() {
        if (mSectionPresenter == null) return;

        new Thread(
                () -> mSectionPresenter.refreshConnection(true)
        ).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView titleText = findViewById(R.id.txt_barTitle);
        CardView searchBox = findViewById(R.id.button_Search);
        ImageView backButton = findViewById(R.id.image_bar_arrow_back);

        backButton.setOnClickListener(this);
        searchBox.setOnClickListener(this);

        Intent intent = getIntent();
        final String currentSection = intent.getStringExtra("section");

        titleText.setText(currentSection);

        // run section activity
        new Thread(
                () -> mSectionPresenter = new SectionPresenter(this, this, currentSection)
        ).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSectionPresenter == null) return;
        new Thread(() -> mSectionPresenter.onStart()).start();
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.button_Search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("search", "");
                startActivity(intent);
                break;
            case R.id.image_bar_arrow_back:
                finish();
                break;
        }
    }

    @Override
    public void showApplications(SectionListAdapter adapter) {
        runOnUiThread(() -> mLayoutContent.setAdapter(adapter));
    }
}