package com.pukkol.apkcenter.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;
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
        TextWatcher,
        Thread.UncaughtExceptionHandler,
        ExceptionCallback.onExceptionListener
{

    private LinearLayout mLayoutSearchBar;
    private TabLayout mLayoutMenu;
    private RecyclerView mLayoutSearchResults;
    private ConstraintLayout mLayoutConnection;
    private ConstraintLayout mLayoutError;

    private EditText mTextSearch;
    private Button mButtonRequest;

    private SearchPresenter<SearchModel> mPresenterSearch;
    private SearchPresenter<RequestModel> mPresenterRequest;

    private String[] mMenuValues;
    private String mLatestSearchValue = null;
    private int mLatestTabIndex = 0;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_search);

        mTextSearch = findViewById(R.id.eText_searchBox);
        mButtonRequest = findViewById(R.id.btn_request_app);
        ImageView buttonHome = findViewById(R.id.img_toHome);
        mLayoutSearchBar = findViewById(R.id.layout_searchBar);
        mLayoutMenu = findViewById(R.id.tabLayout);
        mLayoutSearchResults = findViewById(R.id.layout_searchResults);
        mLayoutConnection = findViewById(R.id.layout_connection);
        mLayoutError = findViewById(R.id.layout_error);

        buttonHome.setOnClickListener(this);
        mButtonRequest.setOnClickListener(this);
        mLayoutMenu.addOnTabSelectedListener(this);
        mTextSearch.addTextChangedListener(this);

        mLayoutSearchResults.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        mMenuValues = this.getResources().getStringArray(R.array.default_menu);

        new Thread(
                () -> {
                    mPresenterSearch = new SearchPresenter<>(this, R.string.search_hint, this);
                    mPresenterRequest = new SearchPresenter<>(this, R.string.request_hint_text, this);
                }
        ).start();

        showMenu(true);
        mTextSearch.requestFocus();
    }

    @Override
    public void onTabSelected(@NonNull TabLayout.Tab tab) {
        if(mLatestTabIndex == tab.getPosition()) return;
        mLatestTabIndex = tab.getPosition();

        DeviceUtil.hideKeyboard(this);

        switch (tab.getPosition()) {
            case 0:
                new Thread(
                        () -> mPresenterSearch.onSearch("")
                ).start();
                break;
            case 1:
                new Thread(
                        () -> mPresenterRequest.onSearch("")
                ).start();
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
            case R.id.btn_request_app:
                String input = mTextSearch.getText().toString();
                SearchModel model = new SearchModel(input, "", "");
                new Thread( () -> mPresenterRequest.onReportAdd(model) ).start();
                runOnUiThread( () -> mTextSearch.setText("") );
                break;
            case R.id.img_toHome:
                DeviceUtil.hideKeyboard(this);
                finish();
                break;
            default:
                showError();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String input = mTextSearch.getText().toString();
        String hint = mTextSearch.getHint().toString();

        if(mLatestSearchValue == null) {
            mLatestSearchValue = input;
        }

        if(mLatestSearchValue.equals(input)) return;
        mLatestSearchValue = input;

        new Thread(
                () -> {

                    showMenu(false);

                    if(hint.equals(this.getString(R.string.search_hint))) {
                        mPresenterSearch.onSearch(input);
                    } else if (hint.equals(this.getString(R.string.request_hint_text))){
                        // search what user mean
                        mPresenterRequest.onSearch(input);
                    }
                }
        ).start();
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
    public void showMenu(boolean updateData) {
        if(mTextSearch.length() > 0) {
            runOnUiThread(
                    () -> mLayoutMenu.setVisibility(View.GONE)
            );
            return;
        }

        if(mLayoutMenu.getVisibility() != View.VISIBLE) {
            runOnUiThread(
                    () -> mLayoutMenu.setVisibility(View.VISIBLE)
            );
        }

        if(updateData){
            for (String menu_value : mMenuValues) {
                runOnUiThread( () -> mLayoutMenu.addTab(mLayoutMenu.newTab().setText(menu_value)) );
            }
        }
    }

    @Override
    public <T> void showAdapter(SearchListAdapter<T> adapter, String searchHint) {
        runOnUiThread( () -> mButtonRequest.setVisibility(View.GONE) );

        if(mTextSearch.getHint() == searchHint ) {
            runOnUiThread(adapter::notifyDataSetChanged);
            return;
        }

        runOnUiThread( () -> mLayoutSearchResults.setAdapter(adapter) );

        DeviceUtil.hideKeyboard(this);
        DeviceUtil.showKeyboard(this);

        runOnUiThread(
                () -> {
                    mTextSearch.requestFocus();
                    mTextSearch.setHint(searchHint);
                    mLayoutMenu.setVisibility(View.VISIBLE);
                    mLayoutSearchBar.setVisibility(View.VISIBLE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutSearchResults.setVisibility(View.VISIBLE);

                }
        );
    }

    @Override
    public void showContact() {
        runOnUiThread( () -> mButtonRequest.setVisibility(View.GONE) );

        if(mTextSearch.getHint() == "") {
            return;
        }

        DeviceUtil.hideKeyboard(this);
        mLatestSearchValue = "";

        runOnUiThread(
                () -> {
                    mTextSearch.setHint("");
                    mLayoutSearchBar.setVisibility(View.GONE);
                    mLayoutSearchResults.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutError.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public <T> void showRequestButton(SearchListAdapter<T> adapter) {
        runOnUiThread(adapter::notifyDataSetChanged);

        if(mButtonRequest.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
            () -> {
                mLayoutSearchBar.setVisibility(View.VISIBLE);
                mLayoutSearchResults.setVisibility(View.VISIBLE);
                mButtonRequest.setVisibility(View.VISIBLE);
            }
        );

    }

    @Override
    public void showError() {
        runOnUiThread(
                ()->{
                    mLayoutSearchResults.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutError.setVisibility(View.VISIBLE);
                    mLayoutMenu.setVisibility(View.GONE);
                    mLayoutSearchBar.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        runOnUiThread(
                ()->{
                    mLayoutSearchResults.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.VISIBLE);
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutMenu.setVisibility(View.VISIBLE);
                    mLayoutSearchBar.setVisibility(View.VISIBLE);
                }
        );
    }




}
