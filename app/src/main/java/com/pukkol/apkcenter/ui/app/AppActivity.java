package com.pukkol.apkcenter.ui.app;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.model.application.ReviewModel;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class AppActivity extends AppCompatActivity
    implements
        AppMvpViewMy,
        View.OnClickListener,
        NestedScrollView.OnScrollChangeListener,
        Thread.UncaughtExceptionHandler
{
    private static final String TAG = AppActivity.class.getSimpleName();

    private NestedScrollView mVerticalScroll;
    private ConstraintLayout mConnectionLayout;
    private ConstraintLayout mErrorLayout;
    private AppBarLayout mLayoutBar;
    private LinearLayout mLayoutApp;
    private LinearLayout mLayoutWww;
    private CardView mCardLimitWww;
    private CardView mCardLimitApp;


    private Toolbar mBar;
    private ImageView mPreviousButtonBar;
    private ImageView mReloadButtonBar;
    private TextView mTitleTextBar;
    private ImageView mLogoImageApp;
    private TextView mTitleTextApp;
    private TextView mReviewsStarTextApp;
    private TextView mReviewsTextApp;
    private TextView mDownloadTextApp;
    private ImageView mPegiImageApp;
    private Button mInstallButtonApp;
    private TextView mLimitTextApp;
    private RecyclerView mImagesApp;
    private TextView mAboutTextApp;
    private TextView mTitleTextWww;
    private TextView mLimitTextWww;
    private ImageView mReloadButtonWww;
    private ImageView mPreviousButtonWww;
    private WebView mWebViewWww;

    private AppPresenter mAppPresenter;

    private String mLatestUrl;
    private int mDisplayHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_app);
        mDisplayHeight = DeviceUtil.displaySize(this.getWindow()).y;

        mVerticalScroll = findViewById(R.id.nestedScrollV_app);
        mConnectionLayout = findViewById(R.id.connection_layout);
        mErrorLayout = findViewById(R.id.error_layout);
        mLayoutBar = findViewById(R.id.layout_bar);
        mLayoutApp = findViewById(R.id.layout_app);
        mLayoutWww = findViewById(R.id.layout_website);
        mCardLimitWww = findViewById(R.id.cView_websiteLimit);
        mCardLimitApp = findViewById(R.id.cView_appLimit);

        mBar = findViewById(R.id.tBar_app);
        ImageView homeButtonBar = findViewById(R.id.iView_barHome);
        mLogoImageApp = findViewById(R.id.ic_appLogo);
        mReviewsStarTextApp = findViewById(R.id.txt_appReviewsStar);
        mReviewsTextApp = findViewById(R.id.txt_appReviews);
        mDownloadTextApp = findViewById(R.id.txt_appDownloads);
        mPegiImageApp = findViewById(R.id.img_appPegi);
        mInstallButtonApp = findViewById(R.id.btn_appInstall);
        mImagesApp = findViewById(R.id.recycler_view_appImages);
        mAboutTextApp = findViewById(R.id.txt_appPottedAbout);
        RelativeLayout aboutButtonApp = findViewById(R.id.rl_appAbout);
        mWebViewWww = findViewById(R.id.wView_websiteBrowser);
        Button reloadButton = findViewById(R.id.connection_retry_button);
        mLimitTextApp = findViewById(R.id.txt_appLimit);
        mLimitTextWww = findViewById(R.id.txt_websiteLimit);
        mTitleTextBar = findViewById(R.id.txt_barTitle);
        mTitleTextApp = findViewById(R.id.txt_appTitle);
        mTitleTextWww = findViewById(R.id.txt_websiteTitle);
        mPreviousButtonBar = findViewById(R.id.iView_barPrevious);
        mPreviousButtonWww = findViewById(R.id.iView_websitePrevious);
        mReloadButtonBar = findViewById(R.id.iView_barReload);
        mReloadButtonWww = findViewById(R.id.iView_websiteReload);

        reloadButton.setOnClickListener(this);
        homeButtonBar.setOnClickListener(this);
        mInstallButtonApp.setOnClickListener(this);
        aboutButtonApp.setOnClickListener(this);
        mTitleTextBar.setOnClickListener(this);
        mTitleTextWww.setOnClickListener(this);
        mPreviousButtonBar.setOnClickListener(this);
        mPreviousButtonWww.setOnClickListener(this);
        mReloadButtonBar.setOnClickListener(this);
        mReloadButtonWww.setOnClickListener(this);
        mVerticalScroll.setOnScrollChangeListener(this);

        new Thread( ()-> mAppPresenter = new AppPresenter(this, this)).start();

        setSupportActionBar(mBar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mAppPresenter.onCloseDataBases();
    }

    @Override
    public void onClick(@NonNull View view) {
        switch (view.getId())
        {
            case R.id.iView_barHome:
                finish();
                break;
            case R.id.txt_barTitle:
            case R.id.txt_websiteTitle:
                onWwwInteraction();
                break;
            case R.id.iView_barPrevious:
            case R.id.iView_websitePrevious:
                mWebViewWww.goBack();
                break;
            case R.id.iView_barReload:
            case R.id.iView_websiteReload:
                mWebViewWww.reload();
                break;
            case R.id.btn_appInstall:
                final String state = mInstallButtonApp.getText().toString();
                new Thread(
                        () -> mAppPresenter.onAppButtonClicked(state)
                ).start();
                break;
            case R.id.rl_appAbout:
                mAppPresenter.onAboutClicked();
                break;
            case R.id.connection_retry_button:
                mAppPresenter.onReload();
                break;
            default:
                showError();
        }
    }

    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        showBar(scrollY);

        final int borderValue = mBar.getHeight() + (mReloadButtonBar.getHeight() * 2);
        if(scrollY > borderValue) {
            mAppPresenter.onLimitCalled(true);
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mAppPresenter.onException(throwable);
    }



    @Override
    public void showBar(final int scrollPosition) {
        final int usedHeight = mBar.getHeight() + mLayoutApp.getHeight() + mReloadButtonBar.getHeight();

        runOnUiThread(
                () -> {

                    if(scrollPosition >= usedHeight && mReloadButtonBar.getVisibility() == View.GONE) {
                        mReloadButtonBar.setVisibility(View.VISIBLE);
                        mTitleTextBar.setText(mTitleTextWww.getText());

                        if(mPreviousButtonWww.getVisibility() == View.VISIBLE) {
                            mPreviousButtonBar.setVisibility(View.VISIBLE);
                        }
                    } else if(scrollPosition < usedHeight && mReloadButtonBar.getVisibility() != View.GONE) {
                        mReloadButtonBar.setVisibility(View.GONE);
                        mTitleTextBar.setText("");
                        mPreviousButtonBar.setVisibility(View.GONE);
                    }


                }
        );
    }

    @Override
    public void showError() {
        if(mErrorLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                ()-> {
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    mVerticalScroll.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        if(mConnectionLayout.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                ()->{
                    mConnectionLayout.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.GONE);
                    mVerticalScroll.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showAppLayout() {
        if(mLayoutApp.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    if(mVerticalScroll.getVisibility() == View.GONE) {
                        mVerticalScroll.setVisibility(View.VISIBLE);
                        mConnectionLayout.setVisibility(View.GONE);
                        mErrorLayout.setVisibility(View.GONE);
                    }

                    mLayoutApp.setVisibility(View.VISIBLE);
                }
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showAppText(@NonNull AppModel app) {

        runOnUiThread( () -> {
            try {
                mTitleTextApp.setText(app.getDecodedTitle());
            } catch (UnsupportedEncodingException e) {
                mAppPresenter.onException(e);
            }
        });

        final ReviewModel review = app.getApk().getReviews();
        runOnUiThread(
                ()->{
                    mReviewsStarTextApp.setText(review.getStar());
                    mReviewsTextApp.setText(review.getAmountString());
                    mDownloadTextApp.setText(app.getApk().getDownloadString());
                }
        );

        String about = app.getApk().getAbout();
        int maxLength = 350;
        if(about.length() > maxLength) {
            runOnUiThread( () -> mAboutTextApp.setText(about.substring( 0, maxLength ) + "...") );
        } else {
            runOnUiThread( () -> mAboutTextApp.setText(about));
        }
    }

    @Override
    public void showAppIcon(String url) {
        runOnUiThread(
                () ->  Glide.with(this)
                        .load(url)
                        .into(mLogoImageApp)
        );
    }

    @Override
    public void showAppImages(@NonNull ImagesAdapter adapter, boolean containsWww) {

        if(adapter.getImageUrls().length > 0 ) {
            mImagesApp.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            );

            runOnUiThread( () -> mImagesApp.setAdapter(adapter) );

            if(containsWww) {
                updatePositionWww(adapter.getMaxHeight());
            }
        } else {
            mImagesApp.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showAppPegi(int age) {

        runOnUiThread(
                () -> {
                    if(age < 7) {
                        mPegiImageApp.setImageResource(R.drawable.pegi_3);
                    } else if (age < 12) {
                        mPegiImageApp.setImageResource(R.drawable.pegi_7);
                    } else if (age < 16) {
                        mPegiImageApp.setImageResource(R.drawable.pegi_12);
                    } else if (age < 18) {
                        mPegiImageApp.setImageResource(R.drawable.pegi_16);
                    } else {
                        mPegiImageApp.setImageResource(R.drawable.pegi_18);
                    }
                }
        );
    }

    @Override
    public void showWebsiteLayout() {
        if(mLayoutWww.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    if(mVerticalScroll.getVisibility() == View.GONE) {
                        mVerticalScroll.setVisibility(View.VISIBLE);
                        mConnectionLayout.setVisibility(View.GONE);
                        mErrorLayout.setVisibility(View.GONE);
                    }

                    mLayoutWww.setVisibility(View.VISIBLE);
                }
        );
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void showWebsite(String url, String requiredDomain) {

        runOnUiThread(
                () -> {
                    mTitleTextWww.setText(url);

                    // mWebViewWww.getSettings().setSupportZoom(true);
                    // mWebViewWww.getSettings().setSupportZoom(false);
                    // mWebViewWww.getSettings().setSupportMultipleWindows(false);
                    // mWebViewWww.getSettings().setBuiltInZoomControls(true);
                    // mWebViewWww.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                    // mWebViewWww.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    // mWebViewWww.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                    // mWebViewWww.getSettings().setDomStorageEnabled(true);

                    mWebViewWww.loadUrl(url);
                    mWebViewWww.getSettings().setJavaScriptEnabled(true);
                    mWebViewWww.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            try {
                                mLatestUrl = request.getUrl().toString();
                                String clickedHost =  new URI(mLatestUrl).getHost();

                                // current domain (request url)
                                if (clickedHost != null && clickedHost.endsWith(requiredDomain)) {
                                    return false;
                                }

                            } catch (URISyntaxException e) {
                                mAppPresenter.onException(e);
                            }

                            // other domain (ignored)
                            return true;
                        }

                        @Override
                        public void onPageCommitVisible(WebView view, String url) {
                            super.onPageCommitVisible(view, url);
                            Log.i(TAG, "Clicked: " + url);

                            boolean displayPrevBtn = !Objects.equals(mAppPresenter.getDefaultUrl(), url);
                            onWwwInteraction(displayPrevBtn);
                        }
                    });
                }
        );
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void showInstallState(int responseCode) {
        runOnUiThread(
                () -> {
                    //installed
                    if(responseCode == 200) {
                        mInstallButtonApp.setText(this.getString(R.string.install_done_text));
                        mInstallButtonApp.setTextColor(R.color.black);
                        mInstallButtonApp.setBackgroundResource(R.drawable.button_background_green_light);
                    }
                    //installing
                    else if(responseCode == 201) {
                        mInstallButtonApp.setText(R.string.installing_text);
                    }
                    //something wrong
                    else if (responseCode == 404) {
                        mInstallButtonApp.setText(R.string.somethingWrong);
                        mInstallButtonApp.setBackgroundResource(R.drawable.button_background_orange);
                    }
                    //internal server error
                    else if (responseCode == 500) {
                        mInstallButtonApp.setText(R.string.internalError);
                        mInstallButtonApp.setBackgroundResource(R.drawable.button_background_red);
                    }
                }
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void showLimit(int limit, int argb, boolean display, String dayLimit) {

        runOnUiThread(
                () -> {
                    if(limit < 0) {
                        mCardLimitWww.setVisibility(View.GONE);
                        if(limit == -2) mLimitTextWww.setText("Error");

                        return;
                    } else {
                        mCardLimitApp.setVisibility(View.VISIBLE);
                        mLimitTextApp.setText(dayLimit);
                    }

                    if(!display) {
                        mWebViewWww.setVisibility(View.GONE);
                        mReloadButtonWww.setVisibility(View.INVISIBLE);
                        mReloadButtonWww.setOnClickListener(null);
                    }

                    // display limit
                    mLimitTextWww.setText(String.valueOf(limit));

                    mLimitTextWww.getBackground().setColorFilter(argb, PorterDuff.Mode.SRC_IN);
                    mCardLimitWww.setVisibility(View.VISIBLE);

                }
        );

    }


    private void onWwwInteraction() {
        // skip the adding or removing of button previous
        boolean skippingValue = mPreviousButtonWww.getVisibility() == View.VISIBLE;

        onWwwInteraction(skippingValue);
    }

    private void onWwwInteraction(boolean displayPrev) {

        if(mAppPresenter.isLimitPaid()) {
            final int usedHeight = mBar.getHeight() + mLayoutApp.getHeight() + mReloadButtonWww.getHeight();
            mLayoutBar.setExpanded(false);
            mVerticalScroll.smoothScrollTo(mVerticalScroll.getScrollY(), usedHeight);
        }

        if(displayPrev && mPreviousButtonWww.getVisibility() == View.GONE) {
            if(mReloadButtonBar.getVisibility() == View.VISIBLE) {
                mPreviousButtonBar.setVisibility(View.VISIBLE);
            }
            mPreviousButtonWww.setVisibility(View.VISIBLE);
        } else if( !displayPrev && mPreviousButtonWww.getVisibility() == View.VISIBLE){
            mPreviousButtonBar.setVisibility(View.GONE);
            mPreviousButtonWww.setVisibility(View.GONE);
        }


    }

    private void updatePositionWww(int imagesHeight) {
        int navHeight = DeviceUtil.navigationBarSize(this).y;
        int barHeight = mLayoutBar.getHeight();
        int appHeight = mLayoutApp.getHeight() + imagesHeight;

        int usedHeight = barHeight + appHeight + mReloadButtonBar.getHeight() + navHeight;
        int freeSpace = mDisplayHeight - usedHeight;

        ViewGroup.LayoutParams params = mLayoutApp.getLayoutParams();
        params.height = freeSpace + appHeight;
        runOnUiThread( () ->mLayoutApp.setLayoutParams(params) );
    }
}
