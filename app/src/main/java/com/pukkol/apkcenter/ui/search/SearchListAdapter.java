package com.pukkol.apkcenter.ui.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.ui.search.listElement.ElementAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter<T> extends RecyclerView.Adapter<ElementAdapter>
{

    private Activity mActivity;
    private ElementAdapter.onListItemClickListener mCallback;

    private List<T> mModels;

    public SearchListAdapter(Activity activity, ElementAdapter.onListItemClickListener callback) {
        mActivity = activity;
        mCallback = callback;
        mModels = new ArrayList<>();

        // load storage which you selected already



    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public ElementAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_app_search, null);



        return new ElementAdapter(mActivity, v, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementAdapter holder, int position) {
        if(!holder.equals(mModels.get(position))) {
            holder.onCreate(mModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (null != mModels ? mModels.size() : 0);
    }

    public void updateData(List<T> models) {
        mModels = models;
    }
}


