package com.pukkol.apkcenter.data.remote.api;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.remote.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.SearchModel;
import com.pukkol.apkcenter.data.model.remote.StatusModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

import java.io.IOException;
import java.util.List;

public abstract class SearchApiStructure<Model>
{
    public SearchApiStructure() { }

    public abstract void onSearch(String input);
    public abstract void onReportAdd(SearchModel model);
    public abstract void onReportRemove(SearchModel model);
    public abstract List<Model> getPopular() throws IOException;
    public abstract List<Model> toModels(@NonNull List<AppSmallModel> models, Model instance);

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
         void onReportResponse(int responseCode, StatusModel response);
         void onSearchResponse(int responseCode, List<?> applications);
    }

}
