package com.pukkol.apkcenter.ui.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppSmallSectionModel;

import java.util.List;

public class SectionListAdapter extends RecyclerView.Adapter<SectionAdapter>{

    private final Activity mActivity;
    private final SectionAdapter.onActionListener mCallback;

    private List<AppSmallSectionModel> mSections;

    public SectionListAdapter(Activity activity, SectionAdapter.onActionListener callback) {
        mActivity = activity;
        mCallback = callback;
    }

    @NonNull
    @Override
    public SectionAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_section, null);

        return new SectionAdapter(mActivity, view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionAdapter holder, int position) {
        holder.onCreate(mSections.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != mSections ? mSections.size() : 0);
    }

    public void newData(List<AppSmallSectionModel> sections) {
        mSections = sections;
    }

    public void addData(List<AppSmallSectionModel> sections) {
        if(mSections == null || mSections.size() == 0) {
            mSections = sections;
            return;
        }
        mSections.addAll(sections);
    }

    public void updateData(List<AppSmallSectionModel> sections) {
        if(mSections == null || mSections.size() == 0) {
            mSections = sections;
            return;
        }

        for(int i = 0; i < sections.size(); i++) {
            // add
            if(mSections.size() < i) {
                mSections.add(sections.get(i));
                continue;
            }

            // update
            List<AppSmallModel> models = mSections.get(i).getModels();
            int j = 0;
            for(; j < sections.get(i).getModels().size(); j++) {
                AppSmallModel model = sections.get(i).getModels().get(j);

                if(models.size() < j) {
                    models.add(model);
                }
                else if(!models.get(j).getTitle().equals(model.getTitle())) {
                    models.set(j, model);
                }
            }
            if(j < models.size()) models = models.subList(0, j);
            AppSmallSectionModel sectionModel = new AppSmallSectionModel(sections.get(i).getTitle(), models);
            mSections.set(i, sectionModel);
        }

    }

}
