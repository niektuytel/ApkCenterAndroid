package com.pukkol.apkcenter.ui.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pukkol.apkcenter.R;
import com.pukkol.apkcenter.util.DeviceUtil;

/**
 * required data:
 * intent.getStringExtra("content");
 */
public class AboutActivity
    extends
        AppCompatActivity
    implements
        View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtil.fullDisplay(this, R.layout.activity_about);

        ImageView buttonBack = findViewById(R.id.btn_backBar);
        TextView textAbout = findViewById(R.id.txt_aboutContent);

        Intent intent = getIntent();
        String content = intent.getStringExtra("content");

        buttonBack.setOnClickListener(this);
        textAbout.setText(content);
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.btn_backBar ) {
            finish();
        }
    }

}