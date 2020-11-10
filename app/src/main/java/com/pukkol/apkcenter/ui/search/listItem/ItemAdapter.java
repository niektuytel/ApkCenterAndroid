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
import com.pukkol.apkcenter.data.model.local.ReportModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.SearchModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

public class ItemAdapter extends RecyclerView.ViewHolder
    implements
        ItemMvpView,
        Button.OnClickListener
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

    public <T> void onCreate(final T model) {
        new Thread(
                () -> {
                    if (mDbReport == null) {
                        new ItemPresenter(this, (SearchModel) model);
                        return;
                    }

                    String title;
                    if (model instanceof SearchModel) {
                        title = ((SearchModel) model).getTitle();
                    } else {
                        title = ((RequestModel) model).getTitle();

                        // display loader
                        if(title == null) {
                            mLayoutApp.setVisibility(View.GONE);
                            mLayoutLoader.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    mReport = mDbReport.getReport(title);


                    if (model instanceof SearchModel) {
                        mRequestModel = new RequestModel((SearchModel) model);
                    } else {
                        mRequestModel = (RequestModel) model;
                    }
                    new ItemPresenter(this, mRequestModel);
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
                onAdd(mRequestModel);
                break;
            case R.id.btn_app_remove:
                mClickedRemove = !mClickedRemove;
                onRemove(mRequestModel);
                break;
            case R.id.layout_searchItem:
                mCallback.onItemClicked(model);
                break;
        }
    }

    private void onAdd(@NonNull RequestModel model) {
        mClickedRemove = false;
        int add = mClickedAdd ? 1 : 0;
        int newValue = mCounting + add;

        mReport = new ReportModel(model.getTitle(), mClickedAdd, false);

        if(mClickedAdd) {
            mCallback.onReportAdd(model);
            mButtonAdd.setImageResource(R.drawable.ic_arrow_voted);
        } else {
            mCallback.onReportRemove(model);
            mButtonAdd.setImageResource(R.drawable.ic_arrow_vote);
        }

        mButtonRemove.setImageResource(R.drawable.ic_arrow_vote);
        mDbReport.editReport(mReport);

        mActivity.runOnUiThread(
                () -> mTextCounting.setText(String.valueOf(newValue))
        );
    }

    private void onRemove(@NonNull RequestModel model) {
        mClickedAdd = false;
        int remove = mClickedRemove ? -1 : 0;
        int newValue = mCounting + remove;

        mReport = new ReportModel(model.getTitle(), false, mClickedRemove);

        if(mClickedRemove) {
            mCallback.onReportRemove(model);
            mButtonRemove.setImageResource(R.drawable.ic_arrow_voted);
        } else {
            mCallback.onReportAdd(model);
            mButtonRemove.setImageResource(R.drawable.ic_arrow_vote);
        }

        mButtonAdd.setImageResource(R.drawable.ic_arrow_vote);
        mDbReport.editReport(mReport);

        mActivity.runOnUiThread(
                () -> mTextCounting.setText(String.valueOf(newValue))
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

        final int drawCount = value;
        mActivity.runOnUiThread(
                () -> {
                    mLayoutReport.setVisibility(View.VISIBLE);
                    mTextCounting.setText(String.valueOf(drawCount));
                }
        );
    }

    public interface onListItemClickListener extends ExceptionCallback.onExceptionListener {
        void onReportAdd(RequestModel model);
        void onReportRemove(RequestModel model);
        void onItemClicked(SearchModel model);
    }

}