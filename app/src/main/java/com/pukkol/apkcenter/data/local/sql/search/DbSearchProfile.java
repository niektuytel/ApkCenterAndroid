package com.pukkol.apkcenter.data.local.sql.search;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.AppSmallModel;

public class DbSearchProfile {

    public DbSearchProfile () { }

    public abstract static class Table {
        public static final String TABLE_NAME = "SEARCH_TABLE";

        public static final String COLUMN_TITLE = "SEARCH_TITLE";
        public static final String COLUMN_ICON = "SEARCH_ICON";
        public static final String COLUMN_WEBSITE_URL = "SEARCH_WEBSITE_URL";
        public static final String COLUMN_STAR = "SEARCH_STAR";
        public static final String COLUMN_USED = "SEARCH_USED";
        public static final String COLUMN_LIMIT = "SEARCH_LIMIT";
        public static final String COLUMN_LATEST_UPDATE = "SEARCH_LATEST_UPDATE";
        public static final String COLUMN_ID = "ID";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_ICON + " TEXT, " +
                    COLUMN_WEBSITE_URL + " TEXT, " +
                    COLUMN_STAR + " REAL, " +
                    COLUMN_USED + " INTEGER, " +
                    COLUMN_LIMIT + " TEXT, " +
                    COLUMN_LATEST_UPDATE + " INTEGER" +
                " ); ";

    }

    public abstract static class Limit {
        public static final int STORAGE_INDEX = 20;
        public static final int RECOMMENDED_INDEX = 20;
    }


    @NonNull
    public static ContentValues toContentValues(AppSmallModel model) {
        ContentValues values = new ContentValues();
        if(model == null) return values;

        values.put(Table.COLUMN_TITLE, model.getTitle());
        values.put(Table.COLUMN_ICON, model.getIcon());
        values.put(Table.COLUMN_WEBSITE_URL, model.getWebsiteUrl());
        values.put(Table.COLUMN_STAR, model.getStar());
        values.put(Table.COLUMN_USED, model.getUsed());
        values.put(Table.COLUMN_LIMIT, model.getLimit());
        values.put(Table.COLUMN_LATEST_UPDATE, model.getLatestUpdate());

        return values;
    }




}
