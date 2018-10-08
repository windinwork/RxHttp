package com.windin.rxhttp;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Call;

public class HttpBuilder {
    protected Method method;
    protected String baseUrl;
    protected String path;
    protected boolean cache;
    protected Map<String, Object> paths;
    protected Map<String, Object> params;
    protected Map<String, String> headers;

    protected RxHttp rxHttp;

    private HttpBuilder() {
    }

    HttpBuilder(RxHttp rxHttp, String baseUrl) {
        this.baseUrl = baseUrl;
        this.rxHttp = rxHttp;
    }

    HttpBuilder get(@NonNull String path) {
        method = Method.GET;
        this.path = path;
        return this;
    }

    HttpBuilder post(@NonNull String path) {
        method = Method.POST;
        this.path = path;
        return this;
    }

    public HttpBuilder appendPath(@NonNull String name, @NonNull Object value) {
        if (paths == null) {
            paths = new HashMap<>();
        }
        paths.put(name, value);
        return this;
    }

    public HttpBuilder appendParam(@NonNull String key, @NonNull Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public HttpBuilder params(@NonNull Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public HttpBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public Call raw() {
        return build().newCall();
    }

    public HttpObsevable rawRx() {
        return new HttpObsevable(rxHttp, build());
    }

    public Observable<Response<String>> string() {
        return rx().string();
    }

    public <T> Observable<Response<T>> json(@NonNull Class<T> clz) {
        return rx().fromJson(clz);
    }

    private GeneralObservable rx() {
        return new GeneralObservable(rxHttp, build());
    }

    protected Request build() {
        return Request.create(method, baseUrl, path, cache, paths, params, headers, rxHttp);
    }

    enum Method {
        GET,
        POST,
        HEAD,
        PUT,
        PATCH,
        DELETE
    }
}
