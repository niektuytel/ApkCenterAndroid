package com.pukkol.apkcenter.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pukkol.apkcenter.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private List<String> mModels;

    public SearchAdapter(Context context, List<String> models) {
        this.context = context;
        this.mModels = models;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.element_app_search, viewGroup, false);

        TextView titleView = view.findViewById(R.id.view_title);
        String title = Uri.decode(mModels.get(position));
        titleView.setText(title);

        return view;
    }

}


