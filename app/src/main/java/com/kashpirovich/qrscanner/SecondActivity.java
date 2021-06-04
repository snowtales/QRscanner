package com.kashpirovich.qrscanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        Log.e("finalUrl is: ", BuildConfig.GATES_URL + getter.getId());
        parseExampleOfJsonObject(BuildConfig.GATES_URL + getter.getId());
    }

    private void updateUi(){
        gatesRecycleAdapter.setData(todo);
        findViewById(R.id.progress2).setVisibility(View.GONE);
    }

    private void parseExampleOfJsonObject(String url) {
        Disposable d = Observable.just(url)
                .observeOn(Schedulers.io())
                .map(this::downloadJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonString ->
                {
                    parseJsonObject(jsonString);
                    updateUi();
                },
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
