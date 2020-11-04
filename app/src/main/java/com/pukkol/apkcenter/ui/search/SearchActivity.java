package com.pukkol.apkcenter.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.tabs.TabLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.DeviceUtil;


public class SearchActivity
    extends
        AppCompatActivity
    implements
        SearchMvpView,
        TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        ListView.OnItemClickListener,
        TextWatcher,
        Thread.UncaughtExceptionHandler,
        ExceptionCallback.onExceptionListener
{

    private EditText mEditText;
    private Button mRequestAppButton;
    private TextView mStatusRequestText;
    private ListView mSearchList;
    private TabLayout mMenuLayout;
    private LinearLayout mTopBarLayout;
    private ConstraintLayout mConnectionLayout;
    private ConstraintLayout mErrorLayout;

    private SearchPresenter mSearchPresenter;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_search);
        DeviceUtil.showKeyboard(this);

        mEditText = findViewById(R.id.edit_text_box);
        mRequestAppButton = findViewById(R.id.btn_app_request);
        mStatusRequestText = findViewById(R.id.status_text);
        ImageView homeButton = findViewById(R.id.toHome_image);
        ImageView menuButton = findViewById(R.id.menu_button);
        mTopBarLayout = findViewById(R.id.topSearchBar_layout);
        mMenuLayout = findViewById(R.id.tabLayout);
        mSearchList = findViewById(R.id.lv_suggestions);
        mConnectionLayout = findViewById(R.id.connection_layout);
        mErrorLayout = findViewById(R.id.error_layout);

        showMenus();

        mSearchList.setOnItemClickListener(this);
        mMenuLayout.addOnTabSelectedListener(this);
        mRequestAppButton.setOnClickListener(this);
        mEditText.addTextChangedListener(this);
        homeButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);

        mEditText.requestFocus();

        new Thread(() -> mSearchPresenter = new SearchPresenter(this, this)).start();
    }

    @Override
    public void onTabSelected(@NonNull TabLayout.Tab tab) {
        DeviceUtil.hideKeyboard(this);

        switch (tab.getPosition()) {
            case 0:
                new Thread( () -> mSearchPresenter.onAppsUpdate("")).start();
                break;
            case 1:
                showRequest();
                break;
            case 2:
                showContact();
                break;
            default:
                showError();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    public void onClick(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.btn_app_request:
                String input = mEditText.getText().toString();
                new Thread( () -> mSearchPresenter.requestApp(input) ).start();
                runOnUiThread( () -> mEditText.setText("") );
                break;
            case R.id.toHome_image:
                DeviceUtil.hideKeyboard(this);
                finish();
                break;
            case R.id.menu_button:
                int visible = View.GONE;
                if(mMenuLayout.getVisibility() == visible) {
                    visible = View.VISIBLE;
                }

                mMenuLayout.setVisibility(visible);
                break;
            default:
                showError();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mSearchPresenter.onAppClicked(position);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String input = mEditText.getText().toString();
        String hint = mEditText.getHint().toString();

        if(hint.equals(this.getString(R.string.search_hint))) {
            mSearchPresenter.onAppsUpdate(input);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) { }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(this, throwable);
        showError();
    }




    @Override
    public void showMenus() {
        String[] menu_values = this.getResources().getStringArray(R.array.default_menu);
        for (String menu_value : menu_values) {
            runOnUiThread( () -> mMenuLayout.addTab(mMenuLayout.newTab().setText(menu_value)) );
        }
    }

    @Override
    public void showApplications(SearchAdapter adapter, boolean hideMenu) {
        runOnUiThread(
                ()->{
                    mSearchList.setAdapter(adapter);
                    mMenuLayout.setVisibility(hideMenu ? View.GONE : View.VISIBLE);
                }
        );

        if(mSearchList.getVisibility() == View.VISIBLE) {
            return;
        }

        DeviceUtil.hideKeyboard(this);
        DeviceUtil.showKeyboard(this);

        runOnUiThread(
                ()->{
                    mEditText.requestFocus();
                    mEditText.setText("");
                    mEditText.setHint(this.getString(R.string.search_hint));
                    mSearchList.setVisibility(View.VISIBLE);
                    mTopBarLayout.setVisibility(View.VISIBLE);
                    mRequestAppButton.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mStatusRequestText.setVisibility(View.GONE);
                }
        );

    }

    @Override
    public void showRequest() {
        if(mRequestAppButton.getVisibility() == View.VISIBLE) {
            return;
        }

        DeviceUtil.hideKeyboard(this);
        DeviceUtil.showKeyboard(this);

        runOnUiThread(
                () -> {
                    mEditText.requestFocus();
                    mEditText.setHint(this.getString(R.string.request_text));
                    mEditText.setText("");
                    mSearchList.setVisibility(View.GONE);
                    mTopBarLayout.setVisibility(View.VISIBLE);
                    mRequestAppButton.setVisibility(View.VISIBLE);
                    mStatusRequestText.setVisibility(View.VISIBLE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showContact() {
        //TO DO create display with contact information about us

        if(mTopBarLayout.getVisibility() == View.GONE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mSearchList.setVisibility(View.GONE);
                    mTopBarLayout.setVisibility(View.GONE);
                    mRequestAppButton.setVisibility(View.GONE);
                    mStatusRequestText.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showError() {
        runOnUiThread(
                ()->{
                    mSearchList.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    mMenuLayout.setVisibility(View.GONE);
                    mTopBarLayout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        runOnUiThread(
                ()->{
                    mSearchList.setVisibility(View.GONE);
                    mConnectionLayout.setVisibility(View.VISIBLE);
                    mErrorLayout.setVisibility(View.GONE);
                    mMenuLayout.setVisibility(View.VISIBLE);
                    mTopBarLayout.setVisibility(View.VISIBLE);
                }
        );
    }

    @Override
    public void updateMessage(String message) {
        runOnUiThread( () -> mStatusRequestText.setText(message) );
    }
}
