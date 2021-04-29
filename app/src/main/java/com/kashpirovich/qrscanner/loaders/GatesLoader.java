package com.kashpirovich.qrscanner.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.kashpirovich.qrscanner.GatesClass;

import java.util.List;

public class GatesLoader extends AsyncTaskLoader {
    private String mUrl;

    public GatesLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        return GatesQue.fetchData(mUrl);
    }
}
