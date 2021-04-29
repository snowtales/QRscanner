package com.kashpirovich.qrscanner;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.loaders.EventsLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<EventClass>> {
    GatesClass getter;
    LinearLayoutManager lLM;
    RecyclerView recyclerView;
    RelativeLayout line;
    int idVenue;
    int gatesId;
    private EventsAdapter eventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_layout);

        Bundle bungle = getIntent().getExtras();
        recyclerView = findViewById(R.id.events_rec);
        line = findViewById(R.id.relative_event);
        lLM = new LinearLayoutManager(this);
        eventsAdapter = new EventsAdapter(this);
        recyclerView.setLayoutManager(lLM);
        recyclerView.setAdapter(eventsAdapter);
        Button newbut = findViewById(R.id.get_events);
        if (bungle != null) {
            getter = getIntent().getParcelableExtra("gates");
        }
        gatesId = getter.getId();
        idVenue = getter.getIdVenue();

        LoaderManager.getInstance(this).initLoader(1, null, this);
        newbut.setOnClickListener(v -> {
            newbut.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
        Log.v("idVuenue", idVenue + "");
    }

    @NonNull
    @Override
    public Loader<ArrayList<EventClass>> onCreateLoader(int id, @Nullable Bundle args) {
        return new EventsLoader(this, BuildConfig.EVENT_URL, BuildConfig.FILM_URL, idVenue, gatesId);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<EventClass>> loader, ArrayList<EventClass> data) {
        lLM.removeAllViews();
        eventsAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<EventClass>> loader) {
        lLM.removeAllViews();
        eventsAdapter.setData(new ArrayList<>());
    }
}
