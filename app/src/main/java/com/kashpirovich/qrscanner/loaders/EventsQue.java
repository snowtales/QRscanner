package com.kashpirovich.qrscanner.loaders;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.BuildConfig;
import com.kashpirovich.qrscanner.CinemasClass;
import com.kashpirovich.qrscanner.EventClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventsQue {
    private static Map<Integer, String> mapFilm;

    private EventsQue() {
    }

    public static List<EventClass> fetchData(String requestUrl, int idVenue, int gatesId) {
        String jsonResponse;
        jsonResponse = CinemaQue.makeHttpRequest(requestUrl);
        return extractFeatureFromJson(jsonResponse, idVenue, gatesId);
    }

    public static Map<Integer, String> fetchFilm(String requestUrl) {
        String jsonResponse;
        jsonResponse = CinemaQue.makeHttpRequest(requestUrl);

        mapFilm = parseFilmsJson(jsonResponse);

        return mapFilm;
    }

    private static Map<Integer, String> parseFilmsJson(String s) {
        Map<Integer, String> listOfNames = new HashMap<>();

        try {
            JSONObject rootObj = new JSONObject(s);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                int id = currentItem.getInt("id");
                String name = currentItem.getString("nameRus");
                listOfNames.put(id, name);
            }
        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
        }

        return listOfNames;
    }

    private static List<EventClass> extractFeatureFromJson(String newsJSON, int idVenue, int gatesId) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<EventClass> eventClasses = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(newsJSON);
            JSONArray data = rootObj.getJSONArray("data");
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
                    eventClasses.add(pussy);
                }
            }

        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
        }
        return eventClasses;
    }

}
