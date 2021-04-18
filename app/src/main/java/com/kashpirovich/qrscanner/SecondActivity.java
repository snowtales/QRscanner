package com.kashpirovich.qrscanner;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SecondActivity extends AppCompatActivity {
    private final ArrayList<GatesClass> todo = new ArrayList<>();
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
        //this.setTitle(getter.getName());
        Log.e("finalUrl is: ", BuildConfig.GATES_URL + getter.getId());
        Button b = findViewById(R.id.get_gate);
        parseExampleOfJsonObject(BuildConfig.GATES_URL + getter.getId());
        b.setOnClickListener(v ->
        {
            b.setVisibility(View.GONE);
            gatesRecycleAdapter.setData(todo);
            recyclerView.setVisibility(View.VISIBLE);
        });
        Observable.just(todo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> gatesRecycleAdapter.setData(todo),
                        throwable -> Log.e("TAG", "onCreate: ", throwable));

    }

    private void parseExampleOfJsonObject(String url) {
        Disposable d = Observable.just(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(this::parseJsonObject,
                        throwable -> Log.e("TAG", "onCreate: ", throwable));

    }

    private String downloadJson(String s) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(s)
                .header("Authorization", "Bearer " + BuildConfig.MAIN_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException exception) {
            runOnUiThread(() -> Snackbar.make(this, line, "Проверьте интернет-соединение", BaseTransientBottomBar.LENGTH_LONG).show());
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private void parseJsonObject(String json) {
        //ArrayList<GatesClass> gatesClasses = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(json);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                int id = currentItem.getInt("id");
                String name = currentItem.getString("name");
                int address = currentItem.getInt("id_venue");
                GatesClass currentGate = new GatesClass(id, name, address);
                todo.add(currentGate);
            }
        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
            runOnUiThread(() ->
                    Snackbar.make(this, line, "ошибка приложения, попробуйте еще раз", BaseTransientBottomBar.LENGTH_LONG).show());
        }
    }
}
