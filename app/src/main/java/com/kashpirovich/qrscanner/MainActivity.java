package com.kashpirovich.qrscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.retrofit.APIinterface;
import com.kashpirovich.qrscanner.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {
    private final ArrayList<CinemasClass> todo = new ArrayList<>();
    APIinterface apIinterface;
    RecyclerView recyclerView;
    RelativeLayout line;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PlatformRecycleAdapter platformRecycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.listOfCinemas);


        Retrofit retrofit = RetrofitClient.getInstance();
        apIinterface = retrofit.create(APIinterface.class);

        Button b = findViewById(R.id.button);
        ImageView vv = findViewById(R.id.logo);
        line = findViewById(R.id.terr);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        platformRecycleAdapter = new PlatformRecycleAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(platformRecycleAdapter);

        //fetchData();

        parseExampleOfJsonObject(BuildConfig.CINEMAS_URL);
        b.setOnClickListener(vi ->
        {
            platformRecycleAdapter.setData(todo);
            recyclerView.setVisibility(View.VISIBLE);
            b.setVisibility(View.GONE);
            vv.setVisibility(View.GONE);
        });
    }
/*
    private void fetchData() {
        compositeDisposable.add(apIinterface.getData()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer <Cinema2Class.Root>() {
            @Override
            public void accept(Cinema2Class.Root cinema2Classes) throws Throwable {
                displayData(cinema2Classes);
            }
        }));
    }

    private void displayData(Cinema2Class.Root cinema2Classes) {
        platformRecycleAdapter = new PlatformRecycleAdapter(this, cinemasClasses);
        recyclerView.setAdapter(platformRecycleAdapter);
    }
    */

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    private void parseExampleOfJsonObject(String url) {
        Disposable d = Single.just(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(jsonString -> {
                            parseJsonObject(jsonString);
//                    Log.e("TAHHHH", jsonString);
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
        //ArrayList<CinemasClass> nw = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(json);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                String id = currentItem.getString("id");
                String name = currentItem.getString("name");
                String address = currentItem.getString("address");
                CinemasClass currentGate = new CinemasClass(id, name, address);
                todo.add(currentGate);
            }
        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
        }
    }

}