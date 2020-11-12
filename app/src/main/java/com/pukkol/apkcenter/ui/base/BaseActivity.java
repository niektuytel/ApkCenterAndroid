package com.pukkol.apkcenter.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.util.DeviceUtil;

public abstract class BaseActivity
        extends
        AppCompatActivity
        implements
        BaseMvpView,
        View.OnClickListener,
        Thread.UncaughtExceptionHandler {
    public static final String TAG = BaseActivity.class.getSimpleName();

    private ConstraintLayout mLayoutConnection;
    private ConstraintLayout mLayoutError;
    private Object mLayoutContent;

    private BasePresenter mPresenter;

    protected abstract int getLayoutResourceId();

    protected abstract Object getLayoutContent();

    protected abstract void onContentCalled();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, getLayoutResourceId());

        mLayoutConnection = findViewById(R.id.connection_layout);
        mLayoutError = findViewById(R.id.error_layout);
        mLayoutContent = getLayoutContent();

        Button buttonReload = findViewById(R.id.connection_retry_button);
        buttonReload.setOnClickListener(this);

        mPresenter = new BasePresenter(this, this);
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
        if (mLayoutContent == null || mLayoutError.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.VISIBLE);
                    mLayoutConnection.setVisibility(View.GONE);
                    setVisibilityContent(View.GONE);
                }
        );
    }

    @Override
    public void showErrorInternet() {
        if (mLayoutContent == null || mLayoutConnection.getVisibility() == View.VISIBLE) {
            return;
        }

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.VISIBLE);
                    setVisibilityContent(View.GONE);
                }
        );
    }

    @Override
    public void showContentLayout() {
        if (mLayoutContent == null) return;

        onContentCalled();

        // display content
        if (isVisible()) return;

        runOnUiThread(
                () -> {
                    mLayoutError.setVisibility(View.GONE);
                    mLayoutConnection.setVisibility(View.GONE);
                    setVisibilityContent(View.VISIBLE);
                }
        );
    }


    private void setVisibilityContent(final int visibilityCode) {
        if (mLayoutContent instanceof RecyclerView) {
            runOnUiThread(() -> ((RecyclerView) mLayoutContent).setVisibility(visibilityCode));
            return;
        } else if (mLayoutContent instanceof NestedScrollView) {
            runOnUiThread(() -> ((NestedScrollView) mLayoutContent).setVisibility(visibilityCode));
            return;
        }

        Log.e(TAG, "You give a unknown layout, BaseActivity::setVisibilityContent()");
    }

    private boolean isVisible() {
        if (mLayoutContent instanceof RecyclerView) {
            return ((RecyclerView) mLayoutContent).getVisibility() == View.VISIBLE;
        } else if (mLayoutContent instanceof NestedScrollView) {
            return ((NestedScrollView) mLayoutContent).getVisibility() == View.VISIBLE;
        }

        Log.e(TAG, "You give a unknown layout, BaseActivity::isVisible()");
        return false;
    }

}
