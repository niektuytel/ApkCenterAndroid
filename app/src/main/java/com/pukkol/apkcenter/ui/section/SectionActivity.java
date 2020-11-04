package com.pukkol.apkcenter.ui.section;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.ui.search.SearchActivity;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.ui.AppSmallSectionAdapter;
import com.pukkol.apkcenter.util.DeviceUtil;


// intent string "section"
// intent boolean "isSql"
public class SectionActivity
    extends
        AppCompatActivity
    implements
        SectionMvpViewMy,
        View.OnClickListener,
        Thread.UncaughtExceptionHandler
{
    private RecyclerView mAppsLayout;
    private ConstraintLayout mConnectionLayout;
    private ConstraintLayout mErrorLayout;

    private SectionPresenter mSectionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_section);

        mAppsLayout = findViewById(R.id.apps_layout);
        mConnectionLayout = findViewById(R.id.connection_layout);
        mErrorLayout = findViewById(R.id.error_layout);
        CardView searchBox = findViewById(R.id.button_Search);
        TextView titleText = findViewById(R.id.txt_barTitle);
        ImageView backButton = findViewById(R.id.iView_barHome);
        Button connectionButton = findViewById(R.id.connection_retry_button);

        backButton.setOnClickListener(this);
        searchBox.setOnClickListener(this);
        connectionButton.setOnClickListener(this);

        Intent intent = getIntent();
        final String currentSection = intent.getStringExtra("section");
        final boolean inLocalStorage = intent.getBooleanExtra("isSql", true);

        titleText.setText(currentSection);

        // run section activity
        new Thread(
                () -> {
                    mSectionPresenter = new SectionPresenter(this, this, currentSection, inLocalStorage);
                    runOnUiThread(this::onStart);
                }
        ).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mSectionPresenter == null) return;
        mSectionPresenter.onStart();
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId()) {
            case R.id.button_Search:
                Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra("search", "");
                startActivity(intent);
                break;
            case R.id.connection_retry_button:
                mSectionPresenter.refreshConnection(true);
                break;
            case R.id.iView_barHome:
                finish();
                break;
            default:
                showError();
        }
    }

    @Override
    public void showError() {
        if(mErrorLayout.getVisibility() == View.VISIBLE) return;

        runOnUiThread(
                () -> {
                    mAppsLayout.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        if(mConnectionLayout.getVisibility() == View.VISIBLE) return;

        runOnUiThread(
                () -> {
                    mAppsLayout.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showApplications(AppSmallSectionAdapter adapter) {
        if(mAppsLayout.getVisibility() == View.VISIBLE) return;

        runOnUiThread(
                () -> {
                    mAppsLayout.setLayoutManager(
                            new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    );
                    mAppsLayout.setAdapter(adapter);

                    mAppsLayout.setVisibility(View.VISIBLE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mSectionPresenter.onException(throwable);
    }
}