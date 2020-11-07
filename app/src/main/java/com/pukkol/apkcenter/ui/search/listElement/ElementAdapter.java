package com.pukkol.apkcenter.ui.search.listElement;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;

public class ElementAdapter extends RecyclerView.ViewHolder
    implements
        ElementMvpView,
        Button.OnClickListener
{
    private TextView mTextTitle;
    private TextView mTextWebsiteUrl;
    private ImageView mImageIcon;
    private LinearLayout mLayoutReport;
    private TextView mTextCounting;
    private ImageView mButtonAdd;
    private ImageView mButtonRemove;

    private Activity mActivity;
    private onListItemClickListener mCallback;

    private int mCounting = 0;
    private boolean mClickedAdd = false;
    private boolean mClickedRemove = false;
    private String mIconUrl;

    public ElementAdapter(Activity activity, View itemView, onListItemClickListener callback) {
        super(itemView);
        mActivity = activity;
        mCallback = callback;

        mTextTitle = itemView.findViewById(R.id.text_searchTitle);
        mTextWebsiteUrl = itemView.findViewById(R.id.text_searchWebsiteUrl);
        mImageIcon = itemView.findViewById(R.id.img_search);
        mLayoutReport = itemView.findViewById(R.id.layout_request_counter);
        mTextCounting = itemView.findViewById(R.id.txt_app_counter);
        LinearLayout layoutItem = itemView.findViewById(R.id.layout_searchItem);
        mButtonAdd = itemView.findViewById(R.id.btn_app_add);
        mButtonRemove = itemView.findViewById(R.id.btn_app_remove);

        layoutItem.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
    }

    public <T> void onCreate(final T model) {
        new Thread(
                () -> {
                    if (model instanceof SearchModel) {
                        new ElementPresenter(this, (SearchModel) model);
                    } else if(model instanceof RequestModel) {
                        new ElementPresenter(this, (RequestModel) model);
                    }
                }
        ).start();
    }

    @Override
    public void onClick(@NonNull View view) {
        String title = mTextTitle.getText().toString();
        String url = mTextWebsiteUrl != null ? mTextWebsiteUrl.getText().toString() : "";
        SearchModel model = new SearchModel(title, url, mIconUrl);

        switch (view.getId()) {
            case R.id.btn_app_add:
                mClickedAdd = !mClickedAdd;
                onButtonClicked(model, mClickedAdd);
                if(mClickedAdd) {
                    mButtonAdd.setImageResource(R.drawable.ic_arrow_voted);
                } else {
                    mButtonAdd.setImageResource(R.drawable.ic_arrow_vote);
                }
                break;
            case R.id.btn_app_remove:
                mClickedRemove = !mClickedRemove;
                onButtonClicked(model, !mClickedRemove);
                if(mClickedRemove) {
                    mButtonRemove.setImageResource(R.drawable.ic_arrow_voted);
                } else {
                    mButtonRemove.setImageResource(R.drawable.ic_arrow_vote);
                }
                break;
            case R.id.layout_searchItem:
                mCallback.onItemClicked(model);
                break;
        }
    }

    private void onButtonClicked(SearchModel model, boolean add) {

        if(add){
            mCounting += 1;
            mCallback.onReportAdd(model);
        } else {
            mCounting -= 1;
            mCallback.onReportRemove(model);
        }

        mActivity.runOnUiThread(
                () -> mTextCounting.setText(String.valueOf(mCounting))
        );
    }


    @Override
    public void showIcon(String url) {
        mIconUrl = url;

        mActivity.runOnUiThread(
                () -> Glide.with(mActivity)
                        .load(url)
                        .into(mImageIcon)
        );
    }

    @Override
    public void showIcon(int resourceId) {
        mActivity.runOnUiThread(
                () -> mImageIcon.setImageResource(resourceId)
        );
    }

    @Override
    public void showTitle(String title) {
        mActivity.runOnUiThread(
                () -> {
                    mTextTitle.setVisibility(View.VISIBLE);
                    mTextTitle.setText(title);
                }
        );
    }

    @Override
    public void showUrl(String url) {
        mActivity.runOnUiThread(
                () -> {
                    mTextWebsiteUrl.setVisibility(View.VISIBLE);
                    mTextWebsiteUrl.setText(url);
                }
        );
    }

    @Override
    public void showReports(int counting) {
        mCounting = counting;
        mActivity.runOnUiThread(
                () -> {
                    mLayoutReport.setVisibility(View.VISIBLE);
                    mTextCounting.setText(String.valueOf(mCounting));

                }
        );
    }

    public interface onListItemClickListener {
        void onReportAdd(SearchModel model);
        void onReportRemove(SearchModel model);
        void onItemClicked(SearchModel model);
    }

}