package com.pukkol.apkcenter.data.local.sql.report;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.local.ReportModel;

public class DbReportProfile {

    public DbReportProfile() {}

    public abstract static class Table {
        public static final String TABLE_NAME = "REPORT_TABLE";

        public static final String COLUMN_ID = "COLUMN_ID";
        public static final String COLUMN_TITLE = "COLUMN_TITLE";
        public static final String COLUMN_ADD = "COLUMN_ADD";
        public static final String COLUMN_REMOVE = "COLUMN_REMOVE";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_ADD + " INTEGER DEFAULT 0, " +
                        COLUMN_REMOVE + " INTEGER DEFAULT 0" +
                " ); ";
    }

    public abstract static class Limit {
        public static final Integer MAX_STORAGE = 200;
    }

    @NonNull
    public static ContentValues toContentValues(@NonNull ReportModel model) {
        ContentValues values = new ContentValues();
        if(model.getTitle() == null || model.getTitle().equals("") ) return values;

        values.put(Table.COLUMN_TITLE, model.getTitle());
        values.put(Table.COLUMN_ADD, model.isReportAdd());
        values.put(Table.COLUMN_REMOVE, model.isReportRemove());

        return values;
    }
}
