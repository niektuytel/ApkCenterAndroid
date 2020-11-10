package com.pukkol.apkcenter.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.remote.AboutUsModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.SearchModel;
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
    private ConstraintLayout mLayoutAbout;

    private EditText mTextSearch;

    private SearchPresenter<SearchModel> mPresenterSearch;
    private SearchPresenter<RequestModel> mPresenterRequest;

    private String[] mMenuValues;
    private String mLatestSearchValue = null;
    private int mLatestTabIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_search);

        mTextSearch = findViewById(R.id.eText_searchBox);
        ImageView buttonHome = findViewById(R.id.img_toHome);
        mLayoutSearchBar = findViewById(R.id.layout_searchBar);
        mLayoutMenu = findViewById(R.id.tabLayout);
        mLayoutSearchResults = findViewById(R.id.layout_searchResults);
        mLayoutConnection = findViewById(R.id.layout_connection);
        mLayoutError = findViewById(R.id.layout_error);
        mLayoutAbout = findViewById(R.id.layout_AboutUs);

        buttonHome.setOnClickListener(this);
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

        new Thread(
                () -> {
                    DeviceUtil.hideKeyboard(this);
                    switch (tab.getPosition()) {
                        case 0:
                            mPresenterSearch.onSearch("");
                            break;
                        case 1:
                            mPresenterRequest.onSearch("");
                            break;
                        case 2:
                            mPresenterRequest.onAboutUs();
                            break;
                        default:
                            showError();
                    }
                }
        ).start();

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    public void onClick(@NonNull final View view) {
        if (view.getId() == R.id.img_toHome) {
            if(mTextSearch.getText().toString().length() > 0) {
                mTextSearch.setText("");
            } else {
                DeviceUtil.hideKeyboard(this);
                finish();
            }
        } else {
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
    public String currentInput() {
        return mTextSearch.getText().toString();
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
    public <T> void showAdapter(ListItemsAdapter<T> adapter, String searchHint) {
        // check
        if(mLatestTabIndex != 0 && mLatestTabIndex != 1) {
            return;
        } else if(mLayoutSearchResults.getAdapter() != null && mTextSearch.getHint() == searchHint ) {
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
                    mLayoutAbout.setVisibility(View.GONE);

                }
        );
    }

    @Override
    public void showContact(AboutUsModel model) {
        // check
        if(mLatestTabIndex != 2) {
            return;
        }else if(mTextSearch.getHint() == "") {
            return;
        }

        DeviceUtil.hideKeyboard(this);
        mLatestSearchValue = "";

        TextView phoneNumber = findViewById(R.id.text_about_phoneNumber);
        TextView emailAddress = findViewById(R.id.text_about_email);
        TextView website = findViewById(R.id.text_about_website);

        runOnUiThread(
                () -> {
                    mTextSearch.setHint("");
                    mLayoutSearchBar.setVisibility(View.GONE);
                    mLayoutSearchResults.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutAbout.setVisibility(View.VISIBLE);

                    phoneNumber.setText(model.getPhoneNumber());
                    emailAddress.setText(model.getEmail());
                    website.setText(model.getWebsite());
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
                    mLayoutAbout.setVisibility(View.GONE);
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
                    mLayoutAbout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }

    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(this, throwable);
        showError();
    }

}
