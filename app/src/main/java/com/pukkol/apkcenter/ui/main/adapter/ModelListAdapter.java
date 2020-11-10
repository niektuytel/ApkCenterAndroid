package com.pukkol.apkcenter.ui.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.data.model.AppSmallModel;

import java.util.List;

public class ModelListAdapter extends RecyclerView.Adapter<ModelAdapter> {

    private final Activity mActivity;
    private final SectionAdapter.onActionListener mCallback;

    private final Integer mModelResourceId;
    private List<AppSmallModel> mModels;

    public ModelListAdapter(Activity activity, List<AppSmallModel> models, Integer modelResourceId, SectionAdapter.onActionListener callback) {
        mActivity = activity;
        mModels = models;
        mModelResourceId = modelResourceId;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ModelAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mModelResourceId, null);
        view.setElevation(0);

        return new ModelAdapter(mActivity, view, mModelResourceId, Math.max(4,mModels.size()), mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ModelAdapter holder, int position) {
        if(holder.getModel() == null || !holder.getModel().equals(mModels.get(position))) {
            holder.onCreate(mModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (null != mModels ? mModels.size() : 0);
    }

    public void newData(List<AppSmallModel> models) {
        mModels = models;
    }

}
