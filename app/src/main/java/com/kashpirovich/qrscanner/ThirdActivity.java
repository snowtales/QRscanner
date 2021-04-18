package com.kashpirovich.qrscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.kashpirovich.qrscanner.databinding.ActivityMainBinding;
import com.kashpirovich.qrscanner.databinding.SecondWindowBinding;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {
    private static final int CODE_CAM = 101;
    private static final String TAG = "makeMeReal";
    private CodeScanner codeScanner;
    private SecondWindowBinding binding;
    private String tail;
    private Vibrator vibrator;

    CinemasClass getter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SecondWindowBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        Bundle bungle = getIntent().getExtras();

        if (bungle != null) {
            getter = getIntent().getParcelableExtra("id");
        }
        this.setTitle(getter.getName());
        binding.info.setText(getter.getId());
        codeScan();
        setupPermission();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
                -> {
            runOnUiThread(()
                    -> {
                tail = result.getText();
                String concat = BuildConfig.TEST_TICKET_URL + result.getText();
                parseExampleOfJsonObject(concat);
                binding.info.setText(concat);
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            });
        });


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
                .subscribe(this::parseJsonObject,
                        throwable -> Log.e(TAG, "onCreate: ", throwable));
        d.dispose();
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
            runOnUiThread(() -> Snackbar.make(this, binding.getRoot(), "Проверьте интернет", BaseTransientBottomBar.LENGTH_LONG).show());
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private void parseJsonObject(String json) {
        Gson gson = new Gson();
        try {
            SecondActivity.Todo todo = gson.fromJson(json, SecondActivity.Todo.class);
            Log.e(TAG, "parseJsonObject: " + todo);
        } catch(Exception e)
        {
            Log.e("THAT IS MISTAKE", e.toString());
            runOnUiThread(()->
                    Snackbar.make(this, binding.getRoot(), "Qr код от мероприятия, которого не существует", BaseTransientBottomBar.LENGTH_LONG).show());
        }
    }

    public class Todo {
        public boolean success;
        public String message;

        @Override
        public String toString() {
            return "Todo{" +
                    "success=" + success +
                    ", message=" + message + '\'' +
                    '}';
        }
    }
}
