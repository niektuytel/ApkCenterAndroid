package com.pukkol.apkcenter.ui.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.util.DeviceUtil;

public abstract class BaseActivity
    extends
        AppCompatActivity
    implements
        BaseMvpView,
        View.OnClickListener,
        Thread.UncaughtExceptionHandler
{

    private ConstraintLayout mLayoutConnection;
    private ConstraintLayout mLayoutError;
    private RecyclerView mLayoutActivity;

    private BasePresenter mPresenter;
    private OnBaseReloadListener mCallback;

    protected abstract int getLayoutResourceId();
    protected abstract Object getActivityLayout();
    protected abstract OnBaseReloadListener getCallback();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, getLayoutResourceId());

        mLayoutConnection = findViewById(R.id.connection_layout);
        mLayoutError = findViewById(R.id.error_layout);
        mLayoutActivity = (RecyclerView) getActivityLayout();

        mCallback = getCallback();

        Button buttonReload = findViewById(R.id.connection_retry_button);
        buttonReload.setOnClickListener(this);

        new Thread(
                () -> mPresenter = new BasePresenter(this, this)
        ).start();
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.connection_retry_button) {
            mPresenter.onReloadConnection();
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mPresenter.onException(throwable);
    }

    @Override
    public void showError() {
        if(mLayoutActivity == null || mLayoutError.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.VISIBLE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutActivity.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        if(mLayoutActivity == null || mLayoutConnection.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.VISIBLE);
                    mLayoutActivity.setVisibility(View.GONE);
                }
        );
    }

    @Override
    public void showActivityLayout() {
        if(mLayoutActivity == null) {
            return;
        }

        mCallback.onBaseReload();

        if(mLayoutActivity.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.GONE);
                    mLayoutActivity.setVisibility(View.VISIBLE);
                }
        );
    }

    public interface OnBaseReloadListener {
        void onBaseReload();
    }

}
