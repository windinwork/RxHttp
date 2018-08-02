package com.windin.rxhttp;

import android.content.Context;

import com.readystatesoftware.chuck.ChuckInterceptor;

import java.io.File;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * author: windin
 * created on: 18-7-12 上午11:26
 * email: windinwork@gmail.com
 */
public class RxHttpService {

    static RxHttp defaultRxHttp;

    public static void init(Context context) {
        context = context.getApplicationContext();

        defaultRxHttp = new RxHttp.Builder()
                .url("http://www.xxx.com")
                .client(new OkHttpClient.Builder()
                        // Add a ChuckInterceptor instance to your OkHttp client
                        .addInterceptor(new ChuckInterceptor(context))
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .cache(new Cache(new File(context.getCacheDir(), "rxhttp"), 2 * 1024 * 1024))
                .build();
    }

    public static HttpBuilder get(String path) {
        return defaultRxHttp.get(path);
    }

    public static HttpBuilder post(String path) {
        return defaultRxHttp.post(path);
    }

    public static HttpBuilder postJson(String path) {
        return defaultRxHttp.postJson(path);
    }
}
