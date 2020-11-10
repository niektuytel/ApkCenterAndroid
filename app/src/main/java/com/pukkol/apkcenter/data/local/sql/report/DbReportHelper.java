package com.pukkol.apkcenter.data.local.sql.report;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.DatabaseHelper;
import com.pukkol.apkcenter.data.model.local.ReportModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

import java.util.List;

/**
 * remove by user do not exists
 **/
public class DbReportHelper
    implements
        Thread.UncaughtExceptionHandler
{
    public static final String TAG = DbReportHelper.class.getSimpleName();
    private static final Integer sMaxStorage = DbReportProfile.Limit.MAX_STORAGE;

    private static final String sTableName = DbReportProfile.Table.TABLE_NAME;
    private static final String sTitle = DbReportProfile.Table.COLUMN_TITLE;
    private static final String sReportAdd = DbReportProfile.Table.COLUMN_ADD;
    private static final String sReportRemove = DbReportProfile.Table.COLUMN_REMOVE;

    private final DatabaseHelper mDatabaseHelper;
    private final ExceptionCallback.onExceptionListener mErrorCallback;

    public DbReportHelper(Context context, ExceptionCallback.onExceptionListener errorCallback) {
        mErrorCallback = errorCallback;
        mDatabaseHelper = DatabaseHelper.getInstance(context);

        // give service to database
        new Thread(this::service).start();
    }

    public void close(){
        if(mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mErrorCallback.onException(throwable);
    }

    public List<ReportModel> getReports() {
        return mDatabaseHelper.getReports(sTableName, sTitle, sReportAdd, sReportRemove);
    }

    public ReportModel getReport(String title) {
        return mDatabaseHelper.getReport(sTableName, sTitle, title, sTitle, sReportAdd, sReportRemove);
    }

    public boolean addReport(ReportModel model) {
        if(model == null) return false;

        //delete
        if(!model.isReportAdd() && !model.isReportRemove()) {
            return removeReport(model);
        }

        boolean found = mDatabaseHelper.find(sTableName, sTitle, model.getTitle());
        if(found) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(sReportAdd, model.isReportAdd());
            contentValues.put(sReportRemove, model.isReportRemove());

            return mDatabaseHelper.updateContent(sTableName, contentValues, sTitle, model.getTitle());
        }

        ContentValues cv = DbReportProfile.toContentValues(model);
        return mDatabaseHelper.insert(sTableName, cv);
    }

    public boolean editReport(ReportModel model) {
        return addReport(model);
    }

    public boolean removeReport(@NonNull ReportModel model) {
        String title = model.getTitle();
        return mDatabaseHelper.deleteOnName(sTableName, sTitle, title);
    }

    private void service() {
        List<ReportModel> allReports = getReports();

        if(allReports.size() > sMaxStorage) {
            for( ReportModel report : allReports) {
                boolean success = removeReport(report);
                Log.i(TAG, "(storage to big) success:" + success + " delete report: " + report.toString());
            }
        }

    }

}
