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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.model.remote.AboutUsModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.ui.base.BaseActivity;
import com.pukkol.apkcenter.util.DeviceUtil;


public class SearchActivity
    extends
        BaseActivity
    implements
        SearchMvpView,
        TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        TextWatcher
{

    private LinearLayout mLayoutSearchBar;
    private TabLayout mLayoutMenu;
    private RecyclerView mLayoutContent;
    private ConstraintLayout mLayoutAbout;

    private EditText mTextSearch;

    private SearchPresenter<SearchModel> mPresenterSearch;
    private SearchPresenter<RequestModel> mPresenterRequest;

    private String[] mMenuValues;
    private String mLatestSearchValue = null;
    private int mLatestTabIndex = 0;
    private String mLatestHint;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search;
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
        if (mTextSearch == null) return;

        new Thread(
                () -> {
                    if (mTextSearch.getHint() == getString(R.string.search_hint)) {
                        if (mPresenterSearch == null) return;
                        mPresenterSearch.onSearch(currentInput());
                    } else if (mTextSearch.getHint() == getString(R.string.request_hint_text)) {
                        if (mPresenterRequest == null) return;
                        mPresenterRequest.onSearch(currentInput());
                    } else if (mTextSearch.getHint() == "") {
                        if (mPresenterSearch == null) return;
                        mPresenterSearch.onAboutUs();
                    }
                }
        ).start();

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextSearch = findViewById(R.id.eText_searchBox);
        mLayoutSearchBar = findViewById(R.id.layout_searchBar);
        mLayoutMenu = findViewById(R.id.tabLayout);
        mLayoutAbout = findViewById(R.id.layout_AboutUs);
        ImageView buttonPrevious = findViewById(R.id.img_toHome);

        buttonPrevious.setOnClickListener(this);
        mLayoutMenu.addOnTabSelectedListener(this);
        mTextSearch.addTextChangedListener(this);

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
        if (mLatestTabIndex == tab.getPosition()) return;
        mLatestTabIndex = tab.getPosition();
        DeviceUtil.hideKeyboard(this);

        switch (tab.getPosition()) {
            case 0:
                mTextSearch.setHint(R.string.search_hint);
                break;
            case 1:
                mTextSearch.setHint(R.string.request_hint_text);
                break;
            case 2:
                mTextSearch.setHint("");
                break;
            default:
                showError();
                return;
        }

        onContentCalled();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) { }

    @Override
    public void onTabReselected(TabLayout.Tab tab) { }

    @Override
    public void onClick(@NonNull final View view) {
        super.onClick(view);
        if (view.getId() == R.id.img_toHome) {
            if(mTextSearch.getText().toString().length() > 0) {
                mTextSearch.setText("");
            } else {
                DeviceUtil.hideKeyboard(this);
                finish();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String input = mTextSearch.getText().toString();

        if (mLatestSearchValue == null) {
            mLatestSearchValue = input;
        }

        if (mLatestSearchValue.equals(input)) return;

        mLatestSearchValue = input;
        showMenu(false);
        onContentCalled();
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
    public <T> void showAdapter(ListItemsAdapter<T> adapter) {
        // check
        if (mLatestTabIndex > 1) {
            return;
        } else if (mLayoutContent.getAdapter() != null && mTextSearch.getHint() == mLatestHint) {
            runOnUiThread(adapter::notifyDataSetChanged);
            return;
        }

        DeviceUtil.hideKeyboard(this);
        DeviceUtil.showKeyboard(this);

        mLatestSearchValue = "";
        mLatestHint = mTextSearch.getHint().toString();

        runOnUiThread(
                () -> {
                    mLayoutContent.setAdapter(adapter);
                    mTextSearch.requestFocus();
                    mLayoutMenu.setVisibility(View.VISIBLE);
                    mLayoutSearchBar.setVisibility(View.VISIBLE);
                    mLayoutAbout.setVisibility(View.GONE);
                    mLayoutContent.setVisibility(View.VISIBLE);
                }
        );
    }

    @Override
    public void showContact(AboutUsModel model) {
        // check
        if(mLatestTabIndex != 2) {
            return;
        }

        DeviceUtil.hideKeyboard(this);

        mLatestSearchValue = "";
        mLatestHint = "";

        TextView phoneNumber = findViewById(R.id.text_about_phoneNumber);
        TextView emailAddress = findViewById(R.id.text_about_email);
        TextView website = findViewById(R.id.text_about_website);

        runOnUiThread(
                () -> {
                    mLayoutSearchBar.setVisibility(View.GONE);
                    mLayoutAbout.setVisibility(View.VISIBLE);
                    mLayoutContent.setVisibility(View.GONE);

                    phoneNumber.setText(model.getPhoneNumber());
                    emailAddress.setText(model.getEmail());
                    website.setText(model.getWebsite());
                }
        );

    }


    @Override
    public void showError() {
        super.showError();
        if (mLayoutMenu == null) return;

        runOnUiThread(
                () -> {
                    mLayoutMenu.setVisibility(View.GONE);
                    mLayoutSearchBar.setVisibility(View.GONE);
                    mLayoutAbout.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        super.showErrorInternet();
        if (mLayoutMenu == null) return;

        runOnUiThread(
                () -> {
                    mLayoutMenu.setVisibility(View.VISIBLE);
                    mLayoutSearchBar.setVisibility(View.VISIBLE);
                    mLayoutAbout.setVisibility(View.GONE);
                }
        );
    }
}
