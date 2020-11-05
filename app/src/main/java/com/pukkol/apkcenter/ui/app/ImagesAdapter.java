package com.pukkol.apkcenter.ui.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pukkol.apkcenter.R;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.CustomTarget;
//import com.bumptech.glide.request.transition.Transition;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.RowHolder> {

    private Activity mContext;
    private String[] mImagesUrls;
    private int mMaxHeight;

    public ImagesAdapter(Activity context, String[] images, int maxHeight)
    {
        mContext = context;
        mImagesUrls = images;
        mMaxHeight = maxHeight;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_app_info, null);
        v.setElevation(0);
        v.setMinimumHeight(mMaxHeight);

        return new RowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position)
    {
        String url = mImagesUrls[position];

        Glide.with(holder.itemView)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        double diff = (double) mMaxHeight / (double) resource.getHeight();
                        int newWidth = (int)Math.round((double) resource.getWidth() * diff);
                        holder.app_view.setImageBitmap(Bitmap.createScaledBitmap(resource, newWidth, mMaxHeight, false));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }

    @Override
    public int getItemCount()
    {
        return (null != mImagesUrls ? mImagesUrls.length : 0);
    }

    public static class RowHolder extends RecyclerView.ViewHolder{
        protected ImageView app_view;

        public RowHolder(View view)
        {
            super(view);
            this.app_view = view.findViewById(R.id.srcInfoImage);
        }
    }

    public int getMaxHeight() {
        return mMaxHeight;
    }

    public String[] getImageUrls() {
        return mImagesUrls;
    }
}
