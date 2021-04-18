package com.kashpirovich.qrscanner.retrofit;

import com.kashpirovich.qrscanner.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit ourInstance;

    private RetrofitClient() {
    }

    public static Retrofit getInstance() {
        if (ourInstance == null) {
            OkHttpClient.Builder httpclient = new OkHttpClient.Builder();
            httpclient.addInterceptor(chain -> {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + BuildConfig.MAIN_TOKEN)
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            });

            OkHttpClient newBie = httpclient.build();
            ourInstance = new Retrofit.Builder().baseUrl(BuildConfig.CINEMAS_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(newBie)
                    .build();
        }
        return ourInstance;
    }
}
