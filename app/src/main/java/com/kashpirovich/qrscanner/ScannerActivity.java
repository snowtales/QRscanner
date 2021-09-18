package com.kashpirovich.qrscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.kashpirovich.qrscanner.databinding.ActivityScannerBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScannerActivity extends AppCompatActivity {
    private static final String TAG = "makeMe2Real";
    EventClass getter;
    CompositeDisposable compositeDisposable;
    String concat = "";
    LinearLayout llm;
    private ActivityScannerBinding binding;
    private String tail, eventId, gatesId;
    private String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        Bundle bundle = getIntent().getExtras();
        llm = findViewById(R.id.linearLY);
        setContentView(R.layout.activity_scanner);
        View v = binding.getRoot();
        compositeDisposable = new CompositeDisposable();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setContentView(v);
        if (bundle != null) {
            getter = getIntent().getParcelableExtra("Event");
        }
        eventId = getter.getId() + "/";
        gatesId = getter.getGatesId() + "/";
        tail = eventId + gatesId;

        EditText edit = binding.editText;

        edit.requestFocus();
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);

        compositeDisposable.add(RxTextView.textChanges(edit)
                .throttleLast(4, TimeUnit.SECONDS)
                .filter(f -> f.length() > 0)
                .subscribe(obg -> {
                    concat = BuildConfig.TICKET_URL + tail + obg.toString().trim();
                    Log.i("Request", concat);
                    parseExampleOfJsonObject(concat);
                }));

        binding.sendreq.setOnClickListener(v1 -> {
            concat = BuildConfig.TICKET_URL + tail + edit.getText().toString().trim();
            Log.i("Request2", concat);
            parseExampleOfJsonObject(concat);
        });

        binding.refresh.setOnClickListener(v1 ->
        {
            recreate();
            edit.setText(null);
        });
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
                Snackbar.make(binding.scannerLayout, "Проверьте интернет соединение и обновите страницу", Snackbar.LENGTH_LONG).setBackgroundTint(
                        ResourcesCompat.getColor(getResources(), R.color.red, null)).show();
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
            String sector = data.getString("sector");
            total = event + '\n' + hall + '\n' + "Ряд: " + row + '\n' + "Место: " + column + '\n' + sector;
            runOnUiThread(() ->
            {
                binding.info.setText(total);
                binding.scannerLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.green));
                binding.editText.setVisibility(View.GONE);
                binding.sendreq.setVisibility(View.GONE);
            });

        } catch (Exception e) {
            try {
                JSONObject root = new JSONObject(json);
                String message = root.getString("message");
                runOnUiThread(() ->
                {
                    binding.scannerLayout.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
                    binding.refresh.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red));
                    binding.info.setText(message);
                    binding.editText.setVisibility(View.GONE);
                    binding.sendreq.setVisibility(View.GONE);
                });
            } catch (Exception n) {
                Log.e("THAT IS MISTAKE", e.toString());
                runOnUiThread(() ->
                {
                    Snackbar.make(binding.scannerLayout, "Ошибка, такого мероприятия не существует", Snackbar.LENGTH_LONG).setBackgroundTint(
                            ResourcesCompat.getColor(getResources(), R.color.red, null)
                    ).show();
                });
            }
        }
    }
}