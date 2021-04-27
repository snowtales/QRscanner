package com.kashpirovich.qrscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.RelativeLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.loaders.CinemaLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<CinemasClass>> {
    private final ArrayList<CinemasClass> todo = new ArrayList<>();
    RecyclerView recyclerView;
    RelativeLayout line;
    private LinearLayoutManager linearLayoutManager;
    private PlatformRecycleAdapter platformRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.listOfCinemas);

//        Button b = findViewById(R.id.button);
//        ImageView vv = findViewById(R.id.logo);
        line = findViewById(R.id.terr);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        platformRecycleAdapter = new PlatformRecycleAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(platformRecycleAdapter);

        LoaderManager.getInstance(this).initLoader(1, null, this);
//        parseExampleOfJsonObject(BuildConfig.CINEMAS_URL);
//        b.setOnClickListener(vi ->
//        {
//            platformRecycleAdapter.setData(todo);
//            recyclerView.setVisibility(View.VISIBLE);
//            b.setVisibility(View.GONE);
//            vv.setVisibility(View.GONE);
//        });
//
    }

    @NonNull
    @Override
    public Loader<ArrayList<CinemasClass>> onCreateLoader(int id, @Nullable Bundle args) {
        return new CinemaLoader(this, BuildConfig.CINEMAS_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<CinemasClass>> loader, ArrayList<CinemasClass> data) {
        linearLayoutManager.removeAllViews();
        platformRecycleAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<CinemasClass>> loader) {
        linearLayoutManager.removeAllViews();
        platformRecycleAdapter.setData(new ArrayList<>());
    }
}