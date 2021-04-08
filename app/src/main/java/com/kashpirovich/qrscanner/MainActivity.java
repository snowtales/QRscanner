package com.kashpirovich.qrscanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.kashpirovich.qrscanner.databinding.ActivityMainBinding;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    private static final int CODE_CAM = 101;
    private static final String TAG = "makeMeReal";
    private static final String TICK_URL = "https://43.arcomp.ru/api/check/ticket/584/1/";
    private CodeScanner codeScanner;
    private ActivityMainBinding binding;
    private String tail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        codeScan();
        setupPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    private void codeScan() {
        codeScanner = new CodeScanner(this, binding.qrScanner);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setScanMode(ScanMode.CONTINUOUS);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);

        codeScanner.setDecodeCallback(result
                -> runOnUiThread(()
                -> {
            tail = result.getText();
            parseExampleOfJsonObject(TICK_URL /*https://43.arcomp.ru/api/check/ticket/584/1/ */ + result.getText());
            binding.info.setText(TICK_URL + result.getText());
        }));

        binding.qrScanner.setOnClickListener(view -> codeScanner.startPreview());

    }

    private void setupPermission() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) makeRequest();
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_CAM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODE_CAM)
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Для работы приложения, нужно разрешение на использование камеры", Toast.LENGTH_LONG).show();
    }

    private void parseExampleOfJsonObject(String url) {
        Disposable d = Single.just(url)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(this::downloadJson)
                .subscribe(jsonString -> {
                            Log.w(TAG, "parseExampleOfJsonObject: " + jsonString);
                        },
                        throwable -> Log.e(TAG, "onCreate: ", throwable));
    }

    private String downloadJson(String s) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(s)
                .header("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzIiwianRpIjoiZDBlMWExZjEzMGM4OWUyMGFmODZjNzVlYTYwMzEwYWQ3NmNjNThiOGJlZTkyYmQ0MmFmYzg4MTU5MTVkNDcyOWUxNWNmYzUzMzMxYTY1NDciLCJpYXQiOjE2MTQ4OTkyODgsIm5iZiI6MTYxNDg5OTI4OCwiZXhwIjoxNjQ2NDM1Mjg4LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.jsNJEEkHzaordNWRl4sSImycaL3XeVJZhK4oPxL3Yn8iD8HbMDk6SY7IBI-ie-WqP-cTY9zetMsW2fSXrINfoG1EOXcgOZrp4Wlzr26LwIGzNljrki4AlW5eapfx5ZxvYI6RBc-BsTpZXvtrvkIrvbpwAlYo1s20MjEfJu6lREk7EZsnL9HxKw4obNGhoqdlnWMG1be6iMZAOeg_Ew2vxn1XcbulCNVeVkDGALWfcMReDAMt07Z7cfdN_zpmGFVOzgJlQerMdeYTS0DShQk2fb0HvimVnlA4lytOPTsIbmCeVI37qFtsFN-zMI9W8iedPJZ0ETT7WlYZYXiIDLsljsDsOgdz8s2sUPdzCo6ELKr8SxRdrZGT2DzLml9_peRiIS6siTXkmKL2Mg2zH2-pQOlM6bpWGkQ7-aE19uj0YtudtZz1keOeCtPcbmJgUKmIWF0G3dbjIHuzsxgPQqPTabJdwOaISFBsHgskRVgdNubFTZ7ztNcXc0AySZ7X9meeF6wYwU_gFR5b3xw2Vf2v0_LA4fZ_fetZGaYqhvDRTHtzBDf92FJ_NAdqqJLWdlApZ6vL4S-TCcHtppvMaqK32vpUBXaQbO1_jlRziM11S-P5uth1nlKKPqBIMfsZItQn390pneiP2gdNNjyHPjQeUfqbHK0QAJaVwTdJ5XVySus")
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException exception) {
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private void parseJsonObject(String json) {
        Gson gson = new Gson();
        Todo todo = gson.fromJson(json, Todo.class);
        Log.e(TAG, "parseJsonObject: " + todo);
    }

    public class Todo {
        public int userId;
        public int id;
        public String title;
        public boolean completed;

        @Override
        public String toString() {
            return "Todo{" +
                    "userId=" + userId +
                    ", id=" + id +
                    ", title='" + title + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }
}