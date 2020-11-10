package com.pukkol.apkcenter.data.remote.api.search;

import androidx.annotation.NonNull;

import com.pukkol.apkcenter.data.model.AppSmallModel;
import com.pukkol.apkcenter.data.model.remote.RequestModel;
import com.pukkol.apkcenter.data.model.StatusModel;
import com.pukkol.apkcenter.error.ExceptionCallback;

import java.io.IOException;
import java.util.List;

public abstract class SearchApiStructure<Model>
{
    public SearchApiStructure() { }

    public abstract void onSearch(String input);
    public abstract void onReportAdd(RequestModel model);
    public abstract void onReportRemove(RequestModel model);
    public abstract List<Model> getPopular() throws IOException;
    public abstract List<Model> toModels(@NonNull List<AppSmallModel> models, Model instance);

    public interface onDataResponseListener extends ExceptionCallback.onExceptionListener {
         boolean isCurrentInput(String input);
         void onReportResponse(int responseCode, StatusModel response);
         void onSearchResponse(int responseCode, List<?> applications);
         void onSearchResponseCallback(int responseCode, List<?> applications, String onInput);
    }

}
