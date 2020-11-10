package com.pukkol.apkcenter.ui.main.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.util.DeviceUtil;

public class ModelAdapter extends RecyclerView.ViewHolder
    implements
        View.OnClickListener,
        Thread.UncaughtExceptionHandler
{
    private ImageView mImageIcon;
    private TextView mTextTitle;
    private TextView mTextStar;

    private final Activity mActivity;
    private final Integer mResourceId;
    private final SectionAdapter.onActionListener mCallback;

    private AppSmallModel mModel;
    private int mDeviceWidth;
    private int mRowAmount;

    public ModelAdapter(Activity activity, View itemView, Integer modelResourceId, Integer rowAmount, SectionAdapter.onActionListener callback) {
        super(itemView);
        mActivity = activity;
        mResourceId = modelResourceId;
        mRowAmount = rowAmount;
        mCallback = callback;

        mImageIcon = itemView.findViewById(R.id.icon_appItem_small);
        mTextTitle = itemView.findViewById(R.id.title_appItem_small);
        mTextStar = itemView.findViewById(R.id.star_appItem_small);

        itemView.setOnClickListener(this);

        mDeviceWidth = DeviceUtil.displaySize(mActivity.getWindow()).x;
    }

    @SuppressLint("SetTextI18n")
    public void onCreate(AppSmallModel model){
        mModel = model;

        if(mResourceId == R.layout.element_app_medium) {
            int newSize = (mDeviceWidth / mRowAmount) - Math.round( DeviceUtil.pxFromDp(mActivity, 21) );

            mActivity.runOnUiThread(
                    () -> {
                        Glide.with(mActivity)
                                .asBitmap()
                                .load(model.getIcon())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        mImageIcon.setImageBitmap(Bitmap.createScaledBitmap(resource, newSize, newSize, false));
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                                });

                        mImageIcon.setMaxWidth(newSize);
                        mImageIcon.setMaxHeight(newSize);
                        mTextTitle.setWidth(newSize);
                    }
            );
        }
        else if (mResourceId == R.layout.element_app_small) {
            mActivity.runOnUiThread(
                    ()-> Glide.with(mActivity)
                            .load(model.getIcon())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mImageIcon)
            );
        }


        int maxLen = 35;
        String title = Uri.decode(model.getTitle());
        if(title.length() > maxLen)
        {
            mTextTitle.setText(title.substring(0, maxLen - 3) + "...");
        } else {
            mTextTitle.setText(title);
        }

        // star
        String star = String.valueOf(model.getStar());
        mTextStar.setText(star);

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
