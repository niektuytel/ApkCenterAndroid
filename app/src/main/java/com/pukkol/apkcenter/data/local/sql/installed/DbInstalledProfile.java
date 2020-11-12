package com.pukkol.apkcenter.data.local.sql.installed;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.local.InstalledModel;

public class DbInstalledProfile {

    public DbInstalledProfile() {
    }

    @NonNull
    public static ContentValues toContentValues(InstalledModel model) {
        ContentValues values = new ContentValues();
        if (model == null) return values;

        values.put(Table.COLUMN_TITLE, model.getTitle());
        values.put(Table.COLUMN_FILENAME, model.getFilename());
        values.put(Table.COLUMN_SUCCESS, model.isSuccess());

        return values;
    }

    public abstract static class Table {
        public static final String TABLE_NAME = "INSTALLED_TABLE";

        public static final String COLUMN_TITLE = "APP_TITLE";
        public static final String COLUMN_FILENAME = "APP_FILENAME";
        public static final String COLUMN_SUCCESS = "APP_SUCCESS";
        public static final String COLUMN_ID = "ID";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_FILENAME + " TEXT, " +
                        COLUMN_SUCCESS + " INTEGER " +
                        " ); ";
    }
}
