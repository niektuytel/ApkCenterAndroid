package com.pukkol.apkcenter.data.local.sql.search;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.local.sql.DatabaseHelper;
import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.application.AppModel;
import com.pukkol.apkcenter.data.remote.api.RetroClient;
import com.pukkol.apkcenter.data.remote.api.app.ApiAppService;
import com.pukkol.apkcenter.error.ExceptionCallback.onExceptionListener;
import com.pukkol.apkcenter.util.API;
import com.pukkol.apkcenter.util.DeviceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Response;

//import retrofit2.Response;

public class DbSearchHelper implements Thread.UncaughtExceptionHandler {
    private static final String COUNTRY = DeviceUtil.getCountry();
    private static final String TAG = "DbSearchHelper";
    private static final String sTableName = DbSearchProfile.Table.TABLE_NAME;
    private static final String sColumnTitleName = DbSearchProfile.Table.COLUMN_TITLE;
    private static final String sColumnIconName = DbSearchProfile.Table.COLUMN_ICON;
    private static final String sColumnStarName = DbSearchProfile.Table.COLUMN_STAR;
    private static final String sColumnWebsiteUrlName = DbSearchProfile.Table.COLUMN_WEBSITE_URL;
    private static final String sColumnIdName = DbSearchProfile.Table.COLUMN_ID;
    private static final String sColumnUsedName = DbSearchProfile.Table.COLUMN_USED;
    private static final String sColumnLimitName = DbSearchProfile.Table.COLUMN_LIMIT;
    private static final String sColumnLatestUpdateName = DbSearchProfile.Table.COLUMN_LATEST_UPDATE;

    private static final int iStorageLimit = DbSearchProfile.Limit.STORAGE_INDEX;
    private static final int iRecommendedLimit = DbSearchProfile.Limit.RECOMMENDED_INDEX;

    private final Context mContext;
    private final DatabaseHelper mDb;
    private final onExceptionListener mCallback;

    public DbSearchHelper(Context context, onExceptionListener exceptionCallback) {
        mContext = context;
        mDb = DatabaseHelper.getInstance(context);
        mCallback = exceptionCallback;
        onUpdate();
    }

    public void close() {
        if(mDb != null) {
            mDb.close();
        }
    }

    // db related only
    public String getLimit(String title) {
        return mDb.getStringOnName(sTableName, sColumnTitleName, title, sColumnLimitName);
    }

    public boolean updateLimit(String title, String newValue) {
        return mDb.updateStringOnName(sTableName, sColumnLimitName, newValue, sColumnTitleName, title);
    }

    public void updateTime(String title, String maxLimit){
        long lastUpdate = mDb.getValueOnName(sTableName, sColumnTitleName, title, sColumnLatestUpdateName, (long) 0);
        long oneDay = 1000 * 60 * 60 * 24;
        long currentTime = System.currentTimeMillis();

        if( (lastUpdate + oneDay) < currentTime ) {
            boolean updated = mDb.updateStringOnName(sTableName, sColumnLimitName, maxLimit, sColumnTitleName, title);
            if(updated) {
                mDb.updateLongOnName(sTableName, sColumnLatestUpdateName, currentTime, sColumnTitleName, title);
            }
        }
    }

    public boolean deleteOnTitle(String title) {
        return mDb.deleteOnName(sTableName, sColumnTitleName, title);
    }

    public void onUpdate() {
        List<Integer> ids = mDb.getIdsOrderedBy(sTableName, sColumnUsedName, sColumnIdName, iStorageLimit);
        for(Integer id : ids) {
            mDb.deleteOnName(sTableName, sColumnIdName, String.valueOf(id));
        }
    }

    public List<String> getTitles() {
        return mDb.getValuesOnName(sTableName, sColumnTitleName);
    }


    // response to app
    public boolean cleanDeletedData() {
        if (!API.isNetworkAvailable(mContext)) return false;

        List<String> titles = getTitles();
        ApiAppService service = RetroClient.getAppService();
        boolean removedSome = false;

        for (String title : titles) {

            boolean stillExists = false;
            try {
                Response<AppModel> response = service.app(COUNTRY, title).execute();
                stillExists = response.isSuccessful();
            } catch (IOException ignored) {
                // this exception is been called as well when the response data is not valid
                // to fit into appModel, this is however correct.
                // when the response is 404 that means the app does not exists anymore so need deletion
                // so stillExists to false is good like that
            }

            if(!stillExists) {
                boolean deleted = deleteOnTitle(title);
                Log.i(TAG, title + " does not exists -> deletion : " + deleted);
                if(deleted) removedSome = true;
            }
        }

        return removedSome;
    }

    public List<AppSmallModel> getRecommended() {
        String query = "SELECT * FROM " + sTableName + " ORDER BY " + sColumnUsedName + " DESC LIMIT " + iRecommendedLimit;
        List<String> columnNames = new ArrayList<>();
        columnNames.add(sColumnTitleName);
        columnNames.add(sColumnIconName);
        columnNames.add(sColumnWebsiteUrlName);
        columnNames.add(sColumnStarName);
        columnNames.add(sColumnUsedName);
        columnNames.add(sColumnLimitName);
        columnNames.add(sColumnLatestUpdateName);

        return mDb.getAppSmallModelsOnQuery(query, columnNames);
    }

    public boolean updateSearch(AppSmallModel model) {
        if(model == null) return false;

        boolean found = mDb.find(sTableName, sColumnTitleName, model.getTitle());
        if(found) {
            Pair<Integer, Integer> pair =
                    mDb.getPairValueOnName(
                            sTableName, sColumnTitleName, model.getTitle(), new Pair<>(sColumnIdName, sColumnUsedName)
                    );

            int id = pair.first;
            int used = pair.second;
            updateTime(model.getTitle(), model.getLimit());

            return mDb.updateStringOnName(sTableName, sColumnUsedName, String.valueOf(used+1), sColumnIdName, String.valueOf(id));
        }

        ContentValues cv = DbSearchProfile.toContentValues(model);
        return mDb.insert(sTableName, cv);
    }

    // private function
    @NonNull
    private List<AppSmallModel> removeDuplicates(List<AppSmallModel> models) {
        List<AppSmallModel> results = new ArrayList<>();
        if(models == null) return results;

        HashSet<String> set = new HashSet<>();
        for (AppSmallModel model : models) {
            if (!set.contains(model.getTitle())) {
                results.add(model);
                set.add(model.getTitle());
            }
        }

        return results;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        mCallback.onException(throwable);
    }

}

