package com.pukkol.apkcenter.data.local.sql.error;

import android.content.ContentValues;
import android.content.Context;

import com.pukkol.apkcenter.data.local.sql.DbOpenHelper;

import java.util.List;

public class DbErrorHelper {
    private static final String sTableName = DbErrorProfile.Table.TABLE_NAME;
    private static final String sColumnErrorName = DbErrorProfile.Table.COLUMN_MESSAGE;

    private DbOpenHelper mDb;

    public DbErrorHelper(Context context) {
        mDb = new DbOpenHelper(context);
    }

    public void close() {
        mDb.close();
    }

    public boolean addError(String error)
    {
        ContentValues cv = DbErrorProfile.toContentValues(error);
        return mDb.insert(sTableName, cv);
    }

    public List<String> getAllErrors() {
        return mDb.getValuesOnName(sTableName, sColumnErrorName);
    }

    public boolean removeError(String message) {
        return mDb.deleteOnName(sTableName, sColumnErrorName, message);
    }

}
