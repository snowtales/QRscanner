package com.kashpirovich.qrscanner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.internal.operators.observable.ObservableAny;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventActivity extends AppCompatActivity {
    private final ArrayList<EventClass> maydo = new ArrayList<>();
    GatesClass getter;
    LinearLayoutManager lLM;
    RecyclerView recyclerView;
    SwipeRefreshLayout line;
    int idVenue;
    int gatesId;
    Map<Integer, String> mapFilm = new HashMap<>();
    Map<String, String> calendar = new HashMap<>();
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

        line.setOnRefreshListener(() -> {
            maydo.clear();
            parseFilms();
            parseExampleOfJsonObject();
            line.setRefreshing(false);
        });
        calendar.put("01", "????????????");
        calendar.put("02", "??????????????");
        calendar.put("03", "????????");
        calendar.put("04", "????????????");
        calendar.put("05", "??????");
        calendar.put("06", "????????");
        calendar.put("07", "????????");
        calendar.put("08", "????????????");
        calendar.put("09", "????????????????");
        calendar.put("10", "??????????????");
        calendar.put("11", "????????????");
        calendar.put("12", "??????????????");

        parseFilms();

        if (bungle != null) {
            getter = getIntent().getParcelableExtra("gates");
        }

        gatesId = getter.getId();
        idVenue = getter.getIdVenue();
        Log.v("idVuenue", idVenue + "");
    }

    private void updateList() {
        eventsAdapter.setData(maydo);
        findViewById(R.id.vProgress).setVisibility(View.GONE);
    }

    private void parseExampleOfJsonObject() {
        Observable.just(BuildConfig.EVENT_URL)
                .observeOn(Schedulers.io())
                .map(this::downloadJson)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonString -> {
                            parseJsonObject(jsonString);
                            updateList();
                        },
                        throwable -> Log.e("TAG", "onCreate: ", throwable));

    }

    private void parseFilms() {
        Observable.just(BuildConfig.FILM_URL)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(jsonString -> {
                            parseFilmsJson(jsonString);
                            parseExampleOfJsonObject();
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
                    Snackbar.make(this, line, "??????-???? ???? ??????", BaseTransientBottomBar.LENGTH_LONG).show());
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
            runOnUiThread(() -> Snackbar.make(this, line, "?????????????????? ????????????????-????????????????????", BaseTransientBottomBar.LENGTH_LONG).show());
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private void parseJsonObject(String json) {
        try {
            JSONObject rootObj = new JSONObject(json);
            JSONArray data = rootObj.getJSONArray("data");
            String nameFilm;
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                JSONObject hallObj = currentItem.getJSONObject("hall");
                JSONObject venue = hallObj.getJSONObject("venue");
                int idVen = venue.getInt("id");
                if (idVen == idVenue) {
                    int id = currentItem.getInt("id");
                    JSONArray films = currentItem.getJSONArray("films");
                    if (films.length() > 0) {
                        JSONObject ne = films.getJSONObject(0);
                        int filmId = ne.getInt("film");
                        nameFilm = mapFilm.get(filmId);
                    } else {
                        nameFilm = currentItem.getString("name");
                    }
                    String date = currentItem.getString("dateStart").replace("-", " ");
                    String month = calendar.get(date.substring(5, 7)); //???????????????? ????????????, ?????????? ???????????????? ??????????
                    String day = date.substring(8, 10).replace("0", "");
                    String time = date.substring(11, 16);
                    String finalDate = day + " " + month + " " + time;

                    EventClass pussy = new EventClass(id, idVen, nameFilm, finalDate, gatesId);
                    maydo.add(pussy);
                }
            }

        } catch (Exception e) {
            Log.e("THAT IS MISTAKE EV", e.toString());
            runOnUiThread(() ->
                    Snackbar.make(this, line, "???????????? ????????????????????, ???????????????????? ?????? ??????", BaseTransientBottomBar.LENGTH_LONG).show());
        }
    }
}
