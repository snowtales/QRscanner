package com.kashpirovich.qrscanner.loaders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class EventsLoader extends AsyncTaskLoader {
    private final String mEventUrl;
    private final String mFilmUrl;
    private final int mIdVenue;
    private final int mGatesId;

    public EventsLoader(@NonNull Context context, String eventUrl, String filmUrl, int idVenue, int gatesId) {
        super(context);
        mEventUrl = eventUrl;
        mIdVenue = idVenue;
        mGatesId = gatesId;
        mFilmUrl = filmUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Object loadInBackground() {
        EventsQue.fetchFilm(mFilmUrl);
        return EventsQue.fetchData(mEventUrl, mIdVenue, mGatesId);
    }
}
