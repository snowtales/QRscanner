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
            parseExampleOfJsonObject(BuildConfig.TEST_TICKET_URL /*https://task.arcomp.ru/api/check/ticket/584/1/ */ + result.getText());
            String concat = BuildConfig.TEST_TICKET_URL + result.getText();
            binding.info.setText(concat);
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
                .header("Authorization", "Bearer " + BuildConfig.TEST_TOKEN)
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