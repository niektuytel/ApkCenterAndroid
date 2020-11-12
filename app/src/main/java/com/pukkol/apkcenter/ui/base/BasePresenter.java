package com.pukkol.apkcenter.ui.base;

import android.content.Context;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.error.ErrorHandler;
import com.pukkol.apkcenter.error.ExceptionCallback;
import com.pukkol.apkcenter.util.API;

public class BasePresenter
    implements
        ExceptionCallback.onExceptionListener,
        Thread.UncaughtExceptionHandler
{

    private final Context mContext;
    private final BaseMvpView mMvpView;

    public BasePresenter(Context context, BaseMvpView mvpView) {
        mMvpView = mvpView;
        mContext = context;

        onReloadConnection();
    }

    public void onReloadConnection() {
        if(mMvpView == null) return;

        if(API.isNetworkAvailable(mContext)) {
            mMvpView.showContentLayout();
        } else {
            mMvpView.showErrorInternet();
        }
    }


    @Override
    public void onException(Throwable throwable) {
        new ErrorHandler(mContext, throwable);
        mMvpView.showError();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        onException(throwable);
    }
}
