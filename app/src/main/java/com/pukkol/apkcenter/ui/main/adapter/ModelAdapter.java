package com.pukkol.apkcenter.ui.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.AppSmallModel;

public class ModelAdapter extends RecyclerView.ViewHolder
        implements
        View.OnClickListener,
        Thread.UncaughtExceptionHandler {
    private ProgressBar mProgressbar;
    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextStar;

    private final Activity mActivity;
    private final SectionAdapter.onActionListener mCallback;

    private AppSmallModel mModel;
    private int mIconWidth;
    private int mIconHeight;

    public ModelAdapter(Activity activity, View itemView, int width, int height, SectionAdapter.onActionListener callback) {
        super(itemView);
        mActivity = activity;
        mIconWidth = width;
        mIconHeight = height;
        mCallback = callback;

        mProgressbar = itemView.findViewById(R.id.progress_iconApp);
        mImageIcon = itemView.findViewById(R.id.icon_appItem_small);
        mTextTitle = itemView.findViewById(R.id.title_appItem_small);
        mTextStar = itemView.findViewById(R.id.star_appItem_small);

        itemView.setOnClickListener(this);

        ViewGroup.LayoutParams layoutParams = mImageIcon.getLayoutParams();
        layoutParams.height = height; //this is in pixels
        layoutParams.width = width; //this is in pixels
        mImageIcon.setLayoutParams(layoutParams);

        mTextTitle.setWidth(width);
    }

    @SuppressLint("SetTextI18n")
    public void onCreate(@NonNull AppSmallModel model) {
        mModel = model;

        String title = Uri.decode(model.getTitle());
        String star = String.valueOf(model.getStar());

        mActivity.runOnUiThread(
                () -> {
                    mTextTitle.setText(title);
                    mTextStar.setText(star);
                    mProgressbar.setVisibility(View.VISIBLE);
                }
        );
        Glide.with(mActivity)
                .asBitmap()
                .load(model.getIcon())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mActivity.runOnUiThread(
                                () -> {
                                    mImageIcon.setImageBitmap(Bitmap.createScaledBitmap(resource, mIconWidth, mIconHeight, false));
                                    mProgressbar.setVisibility(View.GONE);
                                }
                        );
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }


    @Override
    public void onClick(View view) {
        mCallback.onClickItem(mModel);
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

    public AppSmallModel getModel() {
        return mModel;
    }

    public void setModel(AppSmallModel mModel) {
        this.mModel = mModel;
    }
}
