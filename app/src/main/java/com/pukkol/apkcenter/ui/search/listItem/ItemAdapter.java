package com.pukkol.apkcenter.ui.search.listItem;

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
import com.pukkol.apkcenter.data.local.sql.report.DbReportHelper;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.data.model.local.ReportModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

public class ItemAdapter extends RecyclerView.ViewHolder
        implements
        ItemMvpView,
        Button.OnClickListener,
        Thread.UncaughtExceptionHandler
{
    private LinearLayout mLayoutApp;
    private LinearLayout mLayoutLoader;
    private TextView mTextTitle;
    private TextView mTextWebsiteUrl;
    private ImageView mImageIcon;
    private LinearLayout mLayoutReport;
    private TextView mTextCounting;
    private ImageView mButtonAdd;
    private ImageView mButtonRemove;

    private final Activity mActivity;
    private DbReportHelper mDbReport;
    private final onListItemClickListener mCallback;

    private int mCounting = 0;
    private int mLatestCount;
    private boolean mClickedAdd = false;
    private boolean mClickedRemove = false;
    private String mIconUrl;
    private ReportModel mReport;
    private RequestModel mRequestModel;


    public ItemAdapter(Activity activity, DbReportHelper dbReport, View itemView, onListItemClickListener callback) {
        super(itemView);
        mActivity = activity;
        mDbReport = dbReport;
        mCallback = callback;

        mLayoutApp = itemView.findViewById(R.id.layout_app_search);
        mLayoutLoader = itemView.findViewById(R.id.layout_loader);
        mTextTitle = itemView.findViewById(R.id.text_searchTitle);
        mTextWebsiteUrl = itemView.findViewById(R.id.text_searchWebsiteUrl);
        mImageIcon = itemView.findViewById(R.id.img_search);
        mLayoutReport = itemView.findViewById(R.id.layout_request_counter);
        mTextCounting = itemView.findViewById(R.id.txt_app_counter);
        LinearLayout layoutItem = itemView.findViewById(R.id.layout_searchItem);
        mButtonAdd = itemView.findViewById(R.id.btn_app_add);
        mButtonRemove = itemView.findViewById(R.id.btn_app_remove);

        mLayoutApp.setVisibility(View.VISIBLE);
        mLayoutLoader.setVisibility(View.GONE);

        layoutItem.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mButtonRemove.setOnClickListener(this);
    }

    public void onCreate(final RequestModel model) {
        mRequestModel = model;
        String title = mRequestModel.getTitle();

        // display loader
        if (title == null) {
            mLayoutApp.setVisibility(View.GONE);
            mLayoutLoader.setVisibility(View.VISIBLE);
            return;
        }

        mReport = mDbReport.getReport(title);
        new ItemPresenter(this, mRequestModel, mCallback);
    }

    public void onCreate(final SearchModel model) {
        if (mDbReport == null) {
            new ItemPresenter(this, model);
            return;
        }

        mRequestModel = new RequestModel(model);
        String title = mRequestModel.getTitle();

        mReport = mDbReport.getReport(title);
        new ItemPresenter(this, mRequestModel, mCallback);
    }

    @Override
    public void onClick(@NonNull View view) {
        new Thread(
                () -> {
                    String title = mTextTitle.getText().toString();
                    String url = mTextWebsiteUrl != null ? mTextWebsiteUrl.getText().toString() : "";
                    SearchModel model = new SearchModel(title, url, mIconUrl);

                    switch (view.getId()) {
                        case R.id.btn_app_add:
                            onAdd(mRequestModel);
                            break;
                        case R.id.btn_app_remove:
                            onRemove(mRequestModel);
                            break;
                        case R.id.layout_searchItem:
                            mCallback.onItemClicked(model);
                            break;
                    }
                }
        ).start();
    }

    private void onAdd(@NonNull RequestModel model) {
        mClickedAdd = !mClickedAdd;
        mClickedRemove = false;

        int addCount = mClickedAdd ? 1 : 0;

        setRequest(model, mClickedAdd, addCount);
    }

    private void onRemove(@NonNull RequestModel model) {
        mClickedAdd = false;
        mClickedRemove = !mClickedRemove;

        int addCount = mClickedRemove ? -1 : 0;

        setRequest(model, !mClickedRemove, addCount);
    }

    private void setRequest(RequestModel model, boolean add, int addCount) {

        if (mClickedRemove) {
            mButtonRemove.setImageResource(R.drawable.ic_arrow_voted);
        } else {
            mButtonRemove.setImageResource(R.drawable.ic_arrow_vote);
        }

        if (mClickedAdd) {
            mButtonAdd.setImageResource(R.drawable.ic_arrow_voted);
        } else {
            mButtonAdd.setImageResource(R.drawable.ic_arrow_vote);
        }

        mReport = new ReportModel(model.getTitle(), mClickedAdd, mClickedRemove);
        mDbReport.editReport(mReport);

        //

        final int counting = mCounting + addCount;

        int amount = Math.max(mLatestCount - counting, counting - mLatestCount);
        for (int i = 0; i < amount; i++) {
            if (add) {
                mCallback.onReportAdd(model);
                mRequestModel.setWantAdding(mRequestModel.getWantAdding() + 1);
            } else {
                mCallback.onReportRemove(model);
                mRequestModel.setWantDeletion(mRequestModel.getWantDeletion() + 1);
            }
        }

        mLatestCount = counting;
        mActivity.runOnUiThread(
                () -> mTextCounting.setText(String.valueOf(counting))
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
                    if(url.equals("")) {
                        mTextWebsiteUrl.setVisibility(View.GONE);
                    } else {
                        mTextWebsiteUrl.setVisibility(View.VISIBLE);
                        mTextWebsiteUrl.setText(url);
                    }
                }
        );
    }

    @Override
    public void showReports(int counting) {
        mCounting = counting;
        int value = mCounting;

        // change button ui
        if(mReport != null) {
            if(mReport.isReportAdd()) {
                mCounting -= 1;
                mClickedAdd = true;
                mButtonAdd.setImageResource(R.drawable.ic_arrow_voted);
            } else if(mReport.isReportRemove()) {
                mCounting += 1;
                mClickedRemove = true;
                mButtonRemove.setImageResource(R.drawable.ic_arrow_voted);
            }
        } else {
            mButtonAdd.setImageResource(R.drawable.ic_arrow_vote);
            mButtonRemove.setImageResource(R.drawable.ic_arrow_vote);
        }

        mLatestCount = value;
        mActivity.runOnUiThread(
                () -> {
                    mLayoutReport.setVisibility(View.VISIBLE);
                    mTextCounting.setText(String.valueOf(mLatestCount));
                }
        );
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onListItemClickListener extends ExceptionCallback.onExceptionListener {
        void onReportAdd(RequestModel model);

        void onReportRemove(RequestModel model);

        void onItemClicked(SearchModel model);
    }

}