package com.pukkol.apkcenter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.application.AppSmallModel;
import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class AppSmallAdapter extends RecyclerView.Adapter<AppSmallAdapter.AppRowHolder> {

    private List<AppSmallModel> mApps;
    private onItemClickListener mCallback;
    private Integer mLayoutID;

    private Context mContext;
    private Integer mDisplayWidth;
    private Integer mDisplayHeight;
    private Integer mRowIndex;

    public AppSmallAdapter(Context context, List<AppSmallModel> itemsList, Integer layout_id, Integer display_weight, Integer app_row_index, onItemClickListener callback) {
        mContext = context;
        mApps = itemsList;
        mLayoutID = layout_id;
        mDisplayWidth = display_weight;
        mRowIndex = app_row_index;
        mCallback = callback;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AppRowHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View v = LayoutInflater.from(
                viewGroup.getContext()
        ).inflate(mLayoutID, null);

        v.setElevation(0);
        return new AppRowHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final AppRowHolder holder, int position) {
        holder.app = mApps.get(position);


        //change icon size
        if(mLayoutID == R.layout.element_app_medium) {
            int newSize = (mDisplayWidth / mRowIndex) - Math.round( DeviceUtil.pxFromDp(mContext, 21) );

            Glide.with(holder.itemView)
                .asBitmap()
                .load(holder.app.getIcon())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.app_icon_image.setImageBitmap(Bitmap.createScaledBitmap(resource, newSize, newSize, false));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

            holder.app_icon_image.setMaxWidth(newSize);
            holder.app_icon_image.setMaxHeight(newSize);
            holder.app_title_text.setWidth(newSize);
        } else {
            Glide.with(holder.itemView)
                .load(holder.app.getIcon())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.app_icon_image);

        }

        // title
        String title = null;
        try {
            title = URLDecoder.decode( holder.app.getTitle(), "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            new ErrorHandler(mContext,e);
        }

        int maxLen = 35;

        assert title != null;
        if(title.length() > maxLen)
        {
            holder.app_title_text.setText(title.substring(0, maxLen - 3) + "...");
        } else {
            holder.app_title_text.setText(title);
        }

        // star
        String star_text = String.valueOf(holder.app.getStar());
        holder.app_star_text.setText(star_text);

    }

    @Override
    public int getItemCount()
    {
        return (null != mApps ? mApps.size() : 0);
    }

    public class AppRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView app_icon_image;
        protected TextView app_title_text;
        protected TextView app_star_text;
        protected CardView app_icon_card;
        protected LinearLayout app_layout;

        protected AppSmallModel app;

        public AppRowHolder(View view) {
            super(view);

            this.app_icon_image = view.findViewById(R.id.icon_appItem_small);
            this.app_title_text = view.findViewById(R.id.title_appItem_small);
            this.app_star_text = view.findViewById(R.id.star_appItem_small);
            this.app_icon_card = view.findViewById(R.id.cardview_icon_);
            this.app_layout = view.findViewById(R.id.layout_app_);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mCallback != null)
            {
                mCallback.onItemClicked(view, app);
            }
        }
    }

    public interface onItemClickListener
    {
        void onItemClicked(View view, AppSmallModel app);
    }




















}














