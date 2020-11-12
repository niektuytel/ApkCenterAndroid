package com.pukkol.apkcenter.data.local.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.pukkol.apkcenter.data.local.sql.error.DbErrorProfile;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledProfile;
import com.pukkol.apkcenter.data.local.sql.report.DbReportProfile;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchProfile;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.local.ReportModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper mInstance = null;
    public static final String DATABASE_NAME = "ApkCenter.db";
    public static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase mDb;

    private static final String TAG = DatabaseHelper.class.getSimpleName();


    public static DatabaseHelper getInstance(Context ctx) {


        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDb = this.getWritableDatabase();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //Uncomment line below if you want to enable foreign keys
        //db.execSQL("PRAGMA foreign_keys");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(db == null) return;

        db.beginTransaction();
        try {
            db.execSQL(DbErrorProfile.Table.CREATE);
            db.execSQL(DbInstalledProfile.Table.CREATE);
            db.execSQL(DbSearchProfile.Table.CREATE);
            db.execSQL(DbReportProfile.Table.CREATE);
            //Add other tables here
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }

    public void close() {
        if(mDb != null) {
            mDb.close();
            super.close();
        }
    }

    public boolean insert(String tableName, ContentValues cv) {
        long response = mDb.insert(tableName, null, cv);
        return response != -1;// -1 = error
    }

    public boolean find(String tableName, String columnName, String columnValue) {
        if(tableName == null || tableName.length() == 0) return false;

        String query = "SELECT * FROM " + tableName;
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);

        // find it
        boolean result = false;
        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                String value = cursor.getString(cursor.getColumnIndexOrThrow(columnName));

                if(columnValue.equals(value)) {
                    result = true;
                    break;
                }
            } while(cursor.moveToNext());

            //clean
            cursor.close();
        }

        return result;
    }

    public boolean deleteOnName(String tableName, String columnName, String nameValue) {
        int result = mDb.delete(tableName, columnName + " = ? ", new String[]{nameValue});
        return (result != 0);
    }


    public List<String> getValuesOnName(String tableName, String columnName) {

        String query = "SELECT * FROM " + tableName;
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);

        List<String> values = new ArrayList<>();

        // delete already installed applications
        if(cursor.moveToFirst())
        {
            do {
                String value = cursor.getString(cursor.getColumnIndexOrThrow(columnName));
                assert value != null;
                values.add(value);
            } while(cursor.moveToNext());
        }

        //clean
        cursor.close();
        return values;
    }

    public List<ReportModel> getReports(String tableName, String columnTitle, String columnReportAdd, String columnReportRemove) {

        String query = "SELECT * FROM " + tableName;
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);

        List<ReportModel> models = new ArrayList<>();

        // delete already installed applications
        if(cursor.moveToFirst())
        {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(columnTitle));
                boolean reportAdd = cursor.getInt(cursor.getColumnIndexOrThrow(columnReportAdd)) == 1;
                boolean reportRemove = cursor.getInt(cursor.getColumnIndexOrThrow(columnReportRemove)) == 1;

                ReportModel model = new ReportModel(title, reportAdd, reportRemove);
                models.add(model);
            } while(cursor.moveToNext());
        }

        //clean
        cursor.close();
        return models;
    }

    public ReportModel getReport(String tableName, String whereName, String isValue, String columnTitle, String columnAdd, String columnRemove) {

        String query = "SELECT * FROM " + tableName + " WHERE " + whereName + " = '" + isValue + "'";
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);

        ReportModel report = null;
        if(cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(columnTitle));
            boolean add = cursor.getInt(cursor.getColumnIndexOrThrow(columnAdd)) == 1;
            boolean remove = cursor.getInt(cursor.getColumnIndexOrThrow(columnRemove)) == 1;

            report = new ReportModel(title, add, remove);
        }

        //clean
        cursor.close();
        return report;
    }





    public boolean updateContent(String tableName, ContentValues contentValues, String whereName, String isValue) {
        int result = mDb.update(tableName, contentValues, whereName + " = ? ", new String[]{isValue});
        return (result != 0);
    }


    public String getStringOnName(String tableName, String columnName, String columnValue, String columnReturnValue) {

        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = '" + columnValue + "'";
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);
        String value = "";

        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            value = cursor.getString(cursor.getColumnIndexOrThrow(columnReturnValue));

            //clean
            cursor.close();
        }

        return value;
    }


    public boolean updateStringOnName(String tableName, String setName, String setNewValue, String whereName, String whereValue) {
        return updateOnName(tableName, setName, setNewValue, whereName, whereValue);
    }

    public boolean updateLongOnName(String tableName, String setName, Long setNewValue, String whereName, String whereValue) {
        return updateOnName(tableName, setName, setNewValue, whereName, whereValue);
    }

    public <Value> boolean updateOnName(String tableName, String setName, Value valueOfName, String whereName, String whereValue) {
        ContentValues contentValues = new ContentValues();

        if (valueOfName instanceof String) {
            contentValues.put(setName, (String) valueOfName);
        } else if (valueOfName instanceof Integer) {
            contentValues.put(setName, (Integer) valueOfName);
        } else if (valueOfName instanceof Long) {
            contentValues.put(setName, (Long) valueOfName);
        } else if (valueOfName instanceof Boolean) {
            contentValues.put(setName, (Boolean) valueOfName);
        } else {
            Log.e(TAG, "Missing giving instance in updateOnName()");
        }

        int result = mDb.update(tableName, contentValues, whereName + " = ? ", new String[]{whereValue});
        return (result != 0);
    }


    public <Value> Value getValueOnName(String tableName, String whereName, String isValue, String returnName, Value defaultValue) {
        String query = "SELECT * FROM " + tableName + " WHERE " + whereName + " = '" + isValue + "'";


        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);
        Value value = defaultValue;

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            int index = cursor.getColumnIndexOrThrow(returnName);

            if (value instanceof String) {
                value = (Value) cursor.getString(index);
            } else if (value instanceof Long) {
                value = (Value) (Object) cursor.getLong(index);
            } else if (value instanceof Integer) {
                value = (Value) (Object) cursor.getInt(index);
            } else if (value instanceof Boolean) {
                value = (Value) (Object) (cursor.getInt(index) == 1);
            } else {
                Log.e(TAG, "Missing giving instance in getValueOnName()");
            }

            //clean
            cursor.close();
        }

        return value;
    }


    public List<AppSmallModel> getAppSmallModelsOnQuery(String query, List<String> columnNames) {
        Cursor cursor = mDb.rawQuery(query, null);
        List<AppSmallModel> models = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(
                        cursor.getColumnIndexOrThrow(columnNames.get(0))
                );

                String icon = cursor.getString(
                        cursor.getColumnIndexOrThrow(columnNames.get(1))
                );

                String websiteUrl = cursor.getString(
                        cursor.getColumnIndexOrThrow(columnNames.get(2))
                );

                double star = cursor.getDouble(
                        cursor.getColumnIndexOrThrow(columnNames.get(3))
                );

                int used = cursor.getInt(
                        cursor.getColumnIndexOrThrow(columnNames.get(4))
                );


                String limit = cursor.getString(
                        cursor.getColumnIndexOrThrow(columnNames.get(5))
                );

                long latestUpdate = cursor.getLong(
                        cursor.getColumnIndexOrThrow(columnNames.get(6))
                );

                models.add(new AppSmallModel(title, icon, websiteUrl, star, used, limit, latestUpdate));
            } while (cursor.moveToNext());
        }

        // clean
        cursor.close();
        return models;
    }

    public Pair<Integer, Integer> getPairValueOnName(String tableName, String columnName, String columnValue, Pair<String, String> columnNames) {
        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = '" + columnValue + "'";
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);
        Pair<Integer, Integer> pair = new Pair<>(0,0);

        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
        {
            int first = cursor.getInt(cursor.getColumnIndexOrThrow(columnNames.first));
            int second = cursor.getInt(cursor.getColumnIndexOrThrow(columnNames.second));

            //clean
            cursor.close();
            pair = new Pair<>(first, second);
        }

        //clean
        return pair;
    }

    public List<Integer> getIdsOrderedBy(String tableName, String orderByColumnName, String columnIdName, int startingIndex) {

        String query = "SELECT * FROM " + tableName + " ORDER BY " + orderByColumnName;
        Cursor cursor = mDb.rawQuery(query, null);
        List<Integer> values = new ArrayList<>();

        int totalIndex = cursor.getCount();
        if(totalIndex > startingIndex && cursor.moveToPosition(startingIndex)) {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(columnIdName));
                values.add(id);
            }
        }

        //clean
        cursor.close();
        return values;
    }


}
