package com.pukkol.apkcenter.ui.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.local.sql.report.DbReportHelper;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.ui.search.listItem.ItemAdapter;

import java.util.ArrayList;

public class ListItemsAdapter<T> extends RecyclerView.Adapter<ItemAdapter>
{
    private Activity mActivity;
    private ItemAdapter.onListItemClickListener mCallback;
    private DbReportHelper mDbReport = null;

    private ArrayList<T> mModels;

    public ListItemsAdapter(Activity activity, DbReportHelper dbReport, ItemAdapter.onListItemClickListener callback) {
        mActivity = activity;
        mDbReport = dbReport;
        mCallback = callback;
        mModels = new ArrayList<>();
    }

    public ListItemsAdapter(Activity activity, ItemAdapter.onListItemClickListener callback) {
        mActivity = activity;
        mCallback = callback;
        mModels = new ArrayList<>();
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public ItemAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_app_search, null);
        return new ItemAdapter(mActivity, mDbReport, v, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter holder, int position) {
        if(!holder.equals(mModels.get(position))) {
            holder.onCreate(mModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (null != mModels ? mModels.size() : 0);
    }

    public void updateData(ArrayList<T> models) {
        mModels = models;
    }

    public void addData(ArrayList<T> models) {
        if(mModels == null) {
            mModels = models;
            return;
        }

        boolean isRequestModel = false;
        ArrayList<T> currentModels = mModels;
        for(T model : models) {
            boolean founded = false;
            for( T storedModel : currentModels) {
                if(!isRequestModel) isRequestModel = model instanceof RequestModel && storedModel instanceof RequestModel;
                if(!isRequestModel) return;

                String storedTitle = ((RequestModel) storedModel).getTitle();
                String title = ((RequestModel) model).getTitle();

                if(storedTitle.equals(title)){
                    founded = true;
                    break;
                }
            }

            // add new application
            if(!founded) mModels.add(model);
        }
    }

}


