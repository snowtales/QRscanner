package com.kashpirovich.qrscanner;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

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

public class EventActivity extends AppCompatActivity {
    private final ArrayList<EventClass> maydo = new ArrayList<>();
    GatesClass getter;
    LinearLayoutManager lLM;
    RecyclerView recyclerView;
    RelativeLayout line;
    int idVenue;
    int gatesId;
    Map<Integer, String> mapFilm = new HashMap();
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
        newbut.setOnClickListener(v -> {
            eventsAdapter.setData(maydo);
            newbut.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });

        if (bungle != null) {
            getter = getIntent().getParcelableExtra("gates");
        }
        gatesId = getter.getId();
        idVenue = getter.getIdVenue();
        Log.v("idVuenue", idVenue + "");
        parseExampleOfJsonObject(BuildConfig.EVENT_URL);
        parseFilms(BuildConfig.FILM_URL);
    }

    private void parseExampleOfJsonObject(String url) {
        Disposable d = Observable.just(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(jsonString -> parseJsonObject(jsonString),
                        throwable -> Log.e("TAG", "onCreate: ", throwable));

    }

    private void parseFilms(String url) {
        Disposable d = Observable.just(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(jsonString ->
                        {
                            parseFilmsJson(jsonString);
                        },
                        throwable -> Log.e("TAG", "onCreate: ", throwable));
    }

    private void parseFilmsJson(String s) {
        try {
            JSONObject rootObj = new JSONObject(s);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                int id = currentItem.getInt("id");
                String name = currentItem.getString("nameRus");
                mapFilm.put(id, name);
            }
        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
            runOnUiThread(() ->
                    Snackbar.make(this, line, "что-то не так", BaseTransientBottomBar.LENGTH_LONG).show());
        }
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
            int count = 0;
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                JSONObject hallObj = currentItem.getJSONObject("hall");
                JSONObject venue = hallObj.getJSONObject("venue");
                int idVen = venue.getInt("id");
                if (idVen == idVenue) {
                    int id = currentItem.getInt("id");
                    JSONArray films = currentItem.getJSONArray("films");
                    JSONObject ne = films.getJSONObject(0);
                    int filmId = ne.getInt("film");
                    String nameFilm = mapFilm.get(filmId);
                    //Log.v("FILMS", mapFilm.get(filmId));
                    String date = currentItem.getString("dateStart").replace("-", " ");
                    String day = date.substring(8, 10);
                    String time = date.substring(11);
                    String finalDate = day + " апреля " + time;

                    EventClass pussy = new EventClass(id, idVen, nameFilm, finalDate, gatesId);
                    maydo.add(pussy);
                }
            }

        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
            runOnUiThread(() ->
                    Snackbar.make(this, line, "ошибка приложения, попробуйте еще раз", BaseTransientBottomBar.LENGTH_LONG).show());
        }
    }
}
