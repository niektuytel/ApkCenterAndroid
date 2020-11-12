package com.pukkol.apkcenter.ui.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.AppSmallModel;

import java.util.List;

public class ModelListAdapter extends RecyclerView.Adapter<ModelAdapter>
        implements
        Thread.UncaughtExceptionHandler {

    private final Activity mActivity;
    private final SectionAdapter.onActionListener mCallback;

    private List<AppSmallModel> mModels;
    private int mWidth;
    private int mHeight;

    public ModelListAdapter(Activity activity, List<AppSmallModel> models, int width, int height, SectionAdapter.onActionListener callback) {
        mActivity = activity;
        mModels = models;
        mWidth = width;
        mHeight = height;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ModelAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_app_card, parent, false);

        return new ModelAdapter(mActivity, view, mWidth, mHeight, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelAdapter holder, int position) {
        if(holder.getModel() == null || !holder.getModel().equals(mModels.get(position))) {
            new Thread(() -> holder.onCreate(mModels.get(position))).start();
        }
    }

    @Override
    public int getItemCount() {
        return (null != mModels ? mModels.size() : 0);
    }

    public void newData(List<AppSmallModel> models) {
        mModels = models;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }
}
