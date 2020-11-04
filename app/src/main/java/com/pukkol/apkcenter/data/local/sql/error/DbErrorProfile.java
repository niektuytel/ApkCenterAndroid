package com.pukkol.apkcenter.data.local.sql.error;

import android.content.ContentValues;

import androidx.annotation.NonNull;

public class DbErrorProfile {

    public abstract static class Table {
        public static final String TABLE_NAME = "ERROR_TABLE";

        public static final String COLUMN_MESSAGE = "ERROR_MESSAGE";
        public static final String COLUMN_ID = "ID";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGE + " TEXT" +
                " ); ";
    }

    @NonNull
    public static ContentValues toContentValues(String error) {
        ContentValues values = new ContentValues();

        if(error != null) {
            values.put(Table.COLUMN_MESSAGE, error);
        }

        return values;
    }
}
