package com.kashpirovich.qrscanner.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.kashpirovich.qrscanner.CinemasClass;

import java.util.List;

public class CinemaLoader extends AsyncTaskLoader {
    private String mUrl;

    public CinemaLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<CinemasClass> loadInBackground() {
        List<CinemasClass> cinemaList = QueUtils.fetchData(mUrl);
        return cinemaList;
    }
}
