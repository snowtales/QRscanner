package com.kashpirovich.qrscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.ScanMode;
import com.google.android.material.snackbar.Snackbar;
import com.kashpirovich.qrscanner.databinding.ThirdWindowBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ThirdActivity extends AppCompatActivity {
    private static final int CODE_CAM = 101;
    private static final String TAG = "makeMeReal";
    private CodeScanner codeScanner;
    EventClass getter;
    private ThirdWindowBinding binding;
    private String tail, eventId, gatesId;
    private Vibrator vibrator;
    private String total;
    private String concat = "";
    LinearLayout llm;
    CompositeDisposable compositeDisposable;



    // @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ThirdWindowBinding.inflate(getLayoutInflater());
        Bundle bungle = getIntent().getExtras();
        llm = findViewById(R.id.linearLY);
        View v = binding.getRoot();
        compositeDisposable = new CompositeDisposable();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(v);
        if (bungle != null) {
            getter = getIntent().getParcelableExtra("event");
        }
        eventId = getter.getId() + "/";
        gatesId = getter.getGatesId() + "/";
        tail = eventId + gatesId;
        EditText edit = binding.editText;

        edit.requestFocus();

        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 1) {
                    //concat = BuildConfig.TICKET_URL + tail + s.toString();
                    //Log.i("Request", concat);
                    // parseExampleOfJsonObject(concat);

                    compositeDisposable.add(Observable.just(s)
                            //.map(finalTicket -> concat = BuildConfig.TICKET_URL + tail + finalTicket.toString().trim())
                            .throttleLatest(4, TimeUnit.SECONDS)
                            .observeOn(Schedulers.io())
                            .subscribe(obg -> {
                                        concat = BuildConfig.TICKET_URL + tail + obg.toString().trim();
                                        Log.i("Request", concat);
                                        parseExampleOfJsonObject(concat);
                                    },
                                    throwable -> Log.e("TAG", "onCreate: ", throwable)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        codeScan();
        binding.refresh.setOnClickListener(v1 -> recreate());
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
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);
        codeScanner.setDecodeCallback(result
                -> {
            runOnUiThread(()
                    -> {
                String concat = BuildConfig.TICKET_URL + tail + result.getText();
                Log.i("Request", concat);
                parseExampleOfJsonObject(concat);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            });
        });

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
                .subscribe(jsonString ->
                        {
                            Log.v("jsonString", jsonString);
                            parseJsonObject(jsonString);
                        },
                        throwable -> Log.e(TAG, "onCreate: ", throwable));
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
            runOnUiThread(() -> {
                binding.internetearn.setVisibility(View.VISIBLE);
                binding.info.setVisibility(View.GONE);
                Snackbar.make(llm, "Проверьте интернет соединение и обновите страницу", Snackbar.LENGTH_LONG).setBackgroundTint(
                        ResourcesCompat.getColor(getResources(), R.color.red, null)).show();
                //binding.internetearn.setText("Проверьте интернет соединение и обновите страницу");
                //binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
                //binding.infoBlock.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
            });
            Log.e("MainActivity", "downloadJson: ", exception);
            return "";
        }
    }

    private void parseJsonObject(String json) {
        try {
            JSONObject rootObj = new JSONObject(json);
            JSONObject data = rootObj.getJSONObject("data");
            String event = data.getString("event");
            String hall = data.getString("hall");
            String row = data.getString("row");
            String column = data.getString("col");
            total = event + '\n' + hall + '\n' + "Ряд: " + row + '\n' + "Место: " + column;
            runOnUiThread(() ->
            {
                Snackbar.make(llm, total, Snackbar.LENGTH_LONG).setBackgroundTint(
                        ResourcesCompat.getColor(getResources(), R.color.green, null)
                ).show();
                //binding.info.setText(total);
                //binding.infoBlock.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                //binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
            });

        } catch (Exception e) {
            try {
                JSONObject root = new JSONObject(json);
                String message = root.getString("message");
                runOnUiThread(() ->
                {
//                    binding.infoBlock.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
//                    binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
//                    binding.info.setText(message);
                    Snackbar.make(binding.infoBlock, message, Snackbar.LENGTH_LONG).setBackgroundTint(
                            ResourcesCompat.getColor(getResources(), R.color.red, null)
                    ).show();
                });
            } catch (Exception n) {
                Log.e("THAT IS MISTAKE", e.toString());
                runOnUiThread(() ->
                {
//                    binding.infoBlock.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
//                    binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
//                    binding.info.setText("Ошибка, такого мероприятия не существует");
                    Snackbar.make(llm, "Ошибка, такого мероприятия не существует", Snackbar.LENGTH_LONG).setBackgroundTint(
                            ResourcesCompat.getColor(getResources(), R.color.red, null)
                    ).show();
                });
            }
        }
    }
}
