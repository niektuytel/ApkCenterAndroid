package com.pukkol.apkcenter.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class AppSmallSectionAdapter extends RecyclerView.Adapter<AppSmallSectionAdapter.SectionHolder> implements
        AppSmallAdapter.onItemClickListener,
        Thread.UncaughtExceptionHandler
{

    private final Context mContext;
    private final ArrayList<AppSmallSectionModel> mDataList;
    private final int mLayoutID;
    private final int mAppsIndex;
    private final onClickListener mCallback;

    public AppSmallSectionAdapter(Context context, ArrayList<AppSmallSectionModel> sections, int layoutID, int maxIndex, onClickListener callback)
    {
        mContext = context;
        mDataList = sections;
        mLayoutID = layoutID;
        mAppsIndex = maxIndex;
        mCallback = callback;
    }

    @NonNull
    @SuppressLint("InflateParams")
    @Override
    public AppSmallSectionAdapter.SectionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutID, null);
        return new AppSmallSectionAdapter.SectionHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppSmallSectionAdapter.SectionHolder holder, int idx)
    {
        AppSmallSectionModel section = mDataList.get(idx);
        String sectionTitle = section.getTitle();
        List<AppSmallModel> apps = section.getApps();
        Integer sectionID = section.getSectionID();

        int displayWidth = DeviceUtil.displaySize(((Activity)mContext).getWindow()).x;
        AppSmallAdapter appsDataAdapter = new AppSmallAdapter(mContext, apps, sectionID, displayWidth, mAppsIndex, this);

        holder.mRecyclerList.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        );
        holder.mRecyclerList.setAdapter(appsDataAdapter);


        if(sectionTitle != null)
        {
            holder.mSectionTitle.setText(sectionTitle);
            holder.mSectionHeader.setOnClickListener( view -> mCallback.onSectionClicked(view, sectionTitle) );
        }

    }

    public static class SectionHolder extends RecyclerView.ViewHolder {

        protected RelativeLayout mSectionHeader;
        protected TextView mSectionTitle;
        protected ImageView mImageMore;
        protected RecyclerView mRecyclerList;

        public SectionHolder(View view) {
            super(view);

            mSectionHeader = view.findViewById(R.id.button_appInfo);
            mSectionTitle = view.findViewById(R.id.itemTitle);
            mImageMore = view.findViewById(R.id.specificBack);
            mRecyclerList = view.findViewById(R.id.recycler_view_list);
        }
    }

    public interface onClickListener extends ExceptionCallback.onExceptionListener {
        void onSectionClicked(View view, String sectionTitle);
        void onItemClicked(View view, AppSmallModel app);
    }

    @Override
    public void onItemClicked(View view, AppSmallModel app) {
        mCallback.onItemClicked(view, app);
    }

    @Override
    public int getItemCount() {
        return (null != mDataList ? mDataList.size() : 0);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

}
