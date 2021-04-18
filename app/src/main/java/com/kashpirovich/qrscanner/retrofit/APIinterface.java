package com.kashpirovich.qrscanner.retrofit;

import com.kashpirovich.qrscanner.Cinema2Class;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface APIinterface {
    @GET("venues")
    Observable<Cinema2Class.Root> getData();
}
