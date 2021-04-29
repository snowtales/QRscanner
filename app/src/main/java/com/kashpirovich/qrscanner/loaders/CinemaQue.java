package com.kashpirovich.qrscanner.loaders;

import android.text.TextUtils;
import android.util.Log;

import com.kashpirovich.qrscanner.BuildConfig;
import com.kashpirovich.qrscanner.CinemasClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CinemaQue {
    private CinemaQue() {
    }

    public static List<CinemasClass> fetchData(String requestUrl) {
        String jsonResponse;
        jsonResponse = makeHttpRequest(requestUrl);

        List<CinemasClass> listOfCinemas = extractFeatureFromJson(jsonResponse);

        return listOfCinemas;
    }

    public static String makeHttpRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + BuildConfig.MAIN_TOKEN)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException exception) {
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private static List<CinemasClass> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<CinemasClass> cinLi = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(newsJSON);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                String id = currentItem.getString("id");
                String name = currentItem.getString("name");
                String address = currentItem.getString("address");
                CinemasClass currentGate = new CinemasClass(id, name, address);
                cinLi.add(currentGate);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing news JSON results", e);
        }
        return cinLi;
    }
}
