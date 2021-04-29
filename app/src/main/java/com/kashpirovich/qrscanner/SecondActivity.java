package com.kashpirovich.qrscanner;

import android.os.Bundle;

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

import com.kashpirovich.qrscanner.loaders.GatesLoader;

import java.util.ArrayList;


public class SecondActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<GatesClass>> {
    CinemasClass getter;
    LinearLayoutManager lLM;
    RecyclerView recyclerView;
    RelativeLayout line;
    private GatesRecycleAdapter gatesRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_window);
        Bundle bungle = getIntent().getExtras();
        recyclerView = findViewById(R.id.listOfGates);
        line = findViewById(R.id.gatesLayout);
        lLM = new LinearLayoutManager(this);
        gatesRecycleAdapter = new GatesRecycleAdapter(this);
        recyclerView.setLayoutManager(lLM);
        recyclerView.setAdapter(gatesRecycleAdapter);


        if (bungle != null) {
            getter = getIntent().getParcelableExtra("id");
        }
        Log.e("finalUrl is: ", BuildConfig.GATES_URL + getter.getId());
        Button b = findViewById(R.id.get_gate);

        LoaderManager.getInstance(this).initLoader(1, null, this);

        b.setOnClickListener(v ->
        {
            b.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    @NonNull
    @Override
    public Loader<ArrayList<GatesClass>> onCreateLoader(int id, @Nullable Bundle args) {
        return new GatesLoader(this, BuildConfig.GATES_URL + getter.getId());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<GatesClass>> loader, ArrayList<GatesClass> data) {
        lLM.removeAllViews();
        gatesRecycleAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<GatesClass>> loader) {
        lLM.removeAllViews();
        gatesRecycleAdapter.setData(new ArrayList<>());
    }
}
