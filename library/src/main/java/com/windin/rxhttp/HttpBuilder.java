package com.windin.rxhttp;

import java.util.HashMap;
import java.util.Map;

public class HttpBuilder {
    private Method method;
    private String baseUrl;
    private String path;
    private boolean cache;
    private Map<String, Object> params;
    private Map<String, String> headers;

    private RxHttp rxHttp;

    private HttpBuilder() {
    }

    HttpBuilder(RxHttp rxHttp, String baseUrl) {
        this.baseUrl = baseUrl;
        this.rxHttp = rxHttp;
    }

    HttpBuilder get(String path) {
        method = Method.GET;
        this.path = path;
        return this;
    }

    HttpBuilder post(String path) {
        method = Method.POST;
        this.path = path;
        return this;
    }

    public HttpBuilder appendParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public HttpBuilder params(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public HttpBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    public GeneralObservable rx() {
        return new GeneralObservable(rxHttp, build());
    }

    public HttpObsevable raw() {
        return new HttpObsevable(rxHttp, build());
    }

    private Request build() {
        return Request.create(method, baseUrl, path, cache, params, headers, rxHttp);
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
