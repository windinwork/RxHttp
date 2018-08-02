package com.windin.rxhttp;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;

public class RxHttp {

    private RxHttp(String baseUrl, OkHttpClient client, Cache cache) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.cache = cache;
    }

    private String baseUrl;
    private OkHttpClient client;
    private Cache cache;
    private Gson gson;

    public OkHttpClient client() {
        return client;
    }

    public Gson json() {
        if (gson == null) {
            synchronized (RxHttp.class) {
                if (gson == null) {
                    gson = new GsonBuilder().create();
                }
            }
        }
        return gson;
    }

    public Cache cache() {
        return cache;
    }

    public HttpBuilder get(String path) {
        return httpBuilder().get(path);
    }

    public HttpBuilder post(String path) {
        return httpBuilder().post(path);
    }

    public HttpBuilder postJson(String path) {
        return new PostJsonBuilder(this, baseUrl).post(path);
    }

    private HttpBuilder httpBuilder() {
        return new HttpBuilder(this, baseUrl);
    }

    public static class Builder {
        String baseUrl;
        OkHttpClient client;
        Cache cache;

        public Builder url(@NonNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder client(@NonNull OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder cache(@NonNull Cache cache) {
            this.cache = cache;
            return this;
        }

        public RxHttp build() {
            if (baseUrl == null) {
                throw new NullPointerException("The base url can not be empty");
            }
            if (client == null) {
                client = new OkHttpClient.Builder().build();
            }
            return new RxHttp(baseUrl, client, cache);
        }
    }
}
