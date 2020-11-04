package com.pukkol.apkcenter.data.local.sql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.pukkol.apkcenter.data.local.sql.error.DbErrorProfile;
import com.pukkol.apkcenter.data.local.sql.installed.DbInstalledProfile;
import com.pukkol.apkcenter.data.local.sql.search.DbSearchProfile;
import com.pukkol.apkcenter.util.Sextet;

import java.util.ArrayList;
import java.util.List;

public class DbOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ApkCenter.db";
    public static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase mDb;

    public DbOpenHelper(@Nullable Context context) {
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
            //Add other tables here
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }

    public void close() {
        mDb.close();
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


    public boolean updateStringOnName(String tableName, String setName, String setNewValue, String whereName, String whereValue) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(setName, setNewValue);

        int result = mDb.update(tableName, contentValues, whereName + " = ? ", new String[]{whereValue});
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


    public boolean updateLongOnName(String tableName, String setName, Long setNewValue, String whereName, String whereValue) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(setName, setNewValue);

        int result = mDb.update(tableName, contentValues, whereName + " = ? ", new String[]{whereValue});
        return (result != 0);
    }

    public Long getLongOnName(String tableName, String columnName, String columnValue, String columnReturnValue) {

        String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " = '" + columnValue + "'";
        @SuppressLint("Recycle") Cursor cursor = mDb.rawQuery(query, null);
        long value = 0;

        if(cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            value = cursor.getLong(cursor.getColumnIndexOrThrow(columnReturnValue));

            //clean
            cursor.close();
        }

        return value;
    }




    public List<Sextet<String, String, Double, Integer, String, Long>> getSextetValuesOnQuery(String query, Sextet<String, String, String, String, String, String> columnNames) {
        Cursor cursor = mDb.rawQuery(query, null);
        List<Sextet<String, String, Double, Integer, String, Long>> Sextets = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String first = cursor.getString(cursor.getColumnIndexOrThrow(columnNames.first));
                String second = cursor.getString(cursor.getColumnIndexOrThrow(columnNames.second));
                double third = cursor.getDouble(cursor.getColumnIndexOrThrow(columnNames.third));
                int fourth = cursor.getInt(cursor.getColumnIndexOrThrow(columnNames.fourth));
                String fifth = cursor.getString(cursor.getColumnIndexOrThrow(columnNames.fifth));
                long sixth = cursor.getLong(cursor.getColumnIndexOrThrow(columnNames.sixth));

                Sextets.add(new Sextet<>(first, second, third, fourth, fifth, sixth));
            } while (cursor.moveToNext());
        }

        // clean
        cursor.close();
        return Sextets;
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
