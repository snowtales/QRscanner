package com.kashpirovich.qrscanner.loaders;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.BuildConfig;
import com.kashpirovich.qrscanner.CinemasClass;
import com.kashpirovich.qrscanner.GatesClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GatesQue {
    private GatesQue() {
    }

    public static List<GatesClass> fetchData(String mUrl) {
        String jsonResponse;
        jsonResponse = CinemaQue.makeHttpRequest(mUrl);

        return extractFeatureFromJson(jsonResponse);
    }

    private static List<GatesClass> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<GatesClass> gatesClasses = new ArrayList<>();
        try {
            JSONObject rootObj = new JSONObject(newsJSON);
            JSONArray data = rootObj.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject currentItem = data.getJSONObject(i);
                int id = currentItem.getInt("id");
                String name = currentItem.getString("name");
                int address = currentItem.getInt("id_venue");
                GatesClass currentGate = new GatesClass(id, name, address);
                gatesClasses.add(currentGate);
            }
        } catch (Exception e) {
            Log.e("THAT IS MISTAKE", e.toString());
        }

        return gatesClasses;
    }
}
