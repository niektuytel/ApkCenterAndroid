package com.pukkol.apkcenter.ui.main.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

public class SectionAdapter extends RecyclerView.ViewHolder
    implements
        View.OnClickListener,
        Thread.UncaughtExceptionHandler
{
    private RelativeLayout mSectionHeader;
    private TextView mTextSectionTitle;
    private RecyclerView mLayoutModels;

    private final Activity mActivity;
    private final onActionListener mCallback;
    private ModelListAdapter mModelsAdapter;

    private String mTitle;


    public SectionAdapter(Activity activity, View itemView, onActionListener callback) {
        super(itemView);
        mActivity = activity;
        mCallback = callback;

        mSectionHeader = itemView.findViewById(R.id.layout_sectionTitle);
        mTextSectionTitle = itemView.findViewById(R.id.text_sectionTitle);
        mLayoutModels = itemView.findViewById(R.id.recycler_view_list);

        mLayoutModels.setLayoutManager(
                new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        );

    }

    public void onCreate(AppSmallSectionModel section) {
        mTitle = section.getTitle();
        mTextSectionTitle.setText(mTitle);

        int modelResourceId = R.layout.element_app_medium;
        if(section.getTitle() != null) {
            modelResourceId = R.layout.element_app_small;
            mSectionHeader.setVisibility(View.VISIBLE);
            mSectionHeader.setOnClickListener(this);
        }

        // create
        if(mModelsAdapter == null) {
            mModelsAdapter = new ModelListAdapter(mActivity, section.getModels(), modelResourceId, mCallback);
            mActivity.runOnUiThread( () -> mLayoutModels.setAdapter(mModelsAdapter) );
        } else {
            mModelsAdapter.newData(section.getModels());
        }
    }

    public void onUpdate(final ModelListAdapter adapter) {
        if(mLayoutModels.getAdapter() == null) {
            // create
            mModelsAdapter = adapter;
            mActivity.runOnUiThread( () -> mLayoutModels.setAdapter(mModelsAdapter) );
        } else {
            // update
            mActivity.runOnUiThread(adapter::notifyDataSetChanged);
        }
    }

    @Override
    public void onClick(View view) {
        mCallback.onClickSection(mTitle);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public interface onActionListener extends ExceptionCallback.onExceptionListener {
        void onClickItem(AppSmallModel model);
        void onClickSection(String title);
    }

}
