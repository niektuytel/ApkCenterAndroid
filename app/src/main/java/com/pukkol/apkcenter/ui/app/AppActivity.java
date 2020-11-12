package com.pukkol.apkcenter.ui.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.service.InstallApkService;
import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.model.application.ReviewModel;
import com.pukkol.apkcenter.ui.base.BaseActivity;

public class AppActivity extends BaseActivity
        implements
        AppMvpView,
        View.OnClickListener {
    private static final String TAG = AppActivity.class.getSimpleName();

    private NestedScrollView mLayoutScroll;
    private Toolbar mLayoutBar;
    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextReviewStar;
    private TextView mTextReview;
    private TextView mTextDownloads;
    private ImageView mImagePegi;
    private Button mButtonInstall;
    private RecyclerView mImages;
    private TextView mTextAbout;
    private CardView mLayoutLimit;
    private TextView mTextLimit;
    private TextView mTextCategory;

    private AppPresenter mAppPresenter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_app;
    }

    @Override
    protected Object getLayoutContent() {
        mLayoutScroll = findViewById(R.id.nestedScrollV_app);
        return mLayoutScroll;
    }

    @Override
    public void onContentCalled() {
        if (mAppPresenter == null) return;

        Intent intent = AppActivity.this.getIntent();
        String title = intent.getStringExtra("title");
        mAppPresenter.onReload(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayoutBar = findViewById(R.id.layout_toolBar);
        ImageView buttonBack = findViewById(R.id.image_bar_arrow_back);
        mImageIcon = findViewById(R.id.image_app_icon);
        mTextTitle = findViewById(R.id.text_app_title);
        mTextReviewStar = findViewById(R.id.text_app_review_star);
        mTextReview = findViewById(R.id.text_app_review);
        mTextDownloads = findViewById(R.id.text_app_downloads);
        mImagePegi = findViewById(R.id.image_app_pegi);
        mButtonInstall = findViewById(R.id.button_app_install);
        mImages = findViewById(R.id.recycler_app_images);
        RelativeLayout layoutAbout = findViewById(R.id.layout_app_about);
        mTextAbout = findViewById(R.id.text_app_potted_about);
        mLayoutLimit = findViewById(R.id.layout_app_limit);
        mTextLimit = findViewById(R.id.text_app_limit);
        mTextCategory = findViewById(R.id.text_app_category);

        buttonBack.setOnClickListener(this);
        mButtonInstall.setOnClickListener(this);
        layoutAbout.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImages.setLayoutManager(layoutManager);

        new Thread(() -> mAppPresenter = new AppPresenter(this, this)).start();

        setSupportActionBar(mLayoutBar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAppPresenter != null) {
            new Thread(() -> mAppPresenter.onBindService()).start();
        }
    }

    @Override
    public void onClick(@NonNull View view) {
        super.onClick(view);
        new Thread(
                () -> {
                    switch (view.getId()) {
                        case R.id.image_bar_arrow_back:
                            finish();
                            break;
                        case R.id.button_app_install:
                            final String btnText = mButtonInstall.getText().toString();
                            mAppPresenter.onAppButtonClicked(btnText);
                            break;
                        case R.id.layout_app_about:
                            mAppPresenter.onAboutClicked();
                            break;
                    }
                }
        ).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (InstallApkService.isServiceRunning()) {
            unbindService(mAppPresenter);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showText(@NonNull AppModel model) {
        ReviewModel review = model.getApk().getReviews();

        runOnUiThread(
                () -> {
                    mTextTitle.setText(model.getDecodedTitle());
                    mTextReviewStar.setText(review.getStar());
                    mTextReview.setText(review.getAmountString());
                    mTextDownloads.setText(model.getApk().getDownloadString());
                    mTextAbout.setText(model.getApk().getAbout());
                    mTextCategory.setText(model.getCategory());

                    if (model.getLimit() != null && !model.getLimit().equals("None")) {
                        mLayoutLimit.setVisibility(View.VISIBLE);
                        mTextLimit.setText(model.getLimit());
                    }

                }
        );
    }

    @Override
    public void showIcon(String url) {
        runOnUiThread(
                () -> Glide.with(this)
                        .load(url)
                        .into(mImageIcon)
        );
    }

    @Override
    public void showImages(@NonNull ImagesAdapter adapter) {
        if (adapter.getImageUrls().length > 0) {
            runOnUiThread(() -> mImages.setAdapter(adapter));
        } else {
            mImages.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void showPegi(int age) {
        runOnUiThread(() -> {
            if (mImagePegi.getVisibility() != View.VISIBLE) {
                mImagePegi.setVisibility(View.VISIBLE);
            }

            if (age < 7) {
                mImagePegi.setImageResource(R.drawable.pegi_3);
            } else if (age < 12) {
                mImagePegi.setImageResource(R.drawable.pegi_7);
            } else if (age < 16) {
                mImagePegi.setImageResource(R.drawable.pegi_12);
            } else if (age < 18) {
                mImagePegi.setImageResource(R.drawable.pegi_16);
            } else {
                mImagePegi.setImageResource(R.drawable.pegi_18);
            }
        });
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void showInstall(InstallState state) {
        if (mButtonInstall.getVisibility() != View.VISIBLE) {
            runOnUiThread(() -> mButtonInstall.setVisibility(View.VISIBLE));
        }

        switch (state) {
            case INSTALLABLE:
                mButtonInstall.setText(R.string.button_install);
                mButtonInstall.setTextColor(getColor(R.color.colorBackground));
                mButtonInstall.setBackgroundResource(R.drawable.button_background_green);
                return;
            case INSTALLED:
                mButtonInstall.setText(this.getString(R.string.install_done_text));
                break;
            case OCCUPIED:
                mButtonInstall.setText("busy downloading 0%");
                break;
            case SOMETHING_WRONG:
                mButtonInstall.setText(R.string.somethingWrong);
                break;
            case INTERNAL_ERROR:
            default:
                mButtonInstall.setText(R.string.internalError);
                break;
        }

        mButtonInstall.setTextColor(getColor(R.color.black));
        mButtonInstall.setBackgroundResource(R.drawable.button_background_green_light);
    }

    @Override
    public void showProgress(String value) {
        runOnUiThread(() -> mButtonInstall.setText(value));
    }

}