package com.windin.rxhttp;

import android.net.Uri;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * author: windin
 * created on: 18-6-28 上午9:52
 * email: windinwork@gmail.com
 */
public class Request {

    public static Request create(HttpBuilder.Method method, String baseUrl, String path, boolean cache,
                                 Map<String, Object> paths,
                                 Map<String, Object> params,
                                 Map<String, String> headers,
                                 RxHttp rxHttp) {
        return new Request(method, baseUrl, path, cache, paths, params, headers, rxHttp);
    }

    HttpBuilder.Method method;
    String url;
    boolean cache;
    Map<String, Object> paths;
    Map<String, Object> params;
    Map<String, String> headers;

    RxHttp rxHttp;

    String cacheKey;

    Request(HttpBuilder.Method method, String baseUrl, String path,
            boolean cache, Map<String, Object> paths, Map<String, Object> params,
            Map<String, String> headers,
            RxHttp rxHttp) {
        this.method = method;
        this.cache = cache;
        this.paths = paths;
        this.params = params;
        this.headers = headers;
        this.rxHttp = rxHttp;

        String url = baseUrl;
        if (!TextUtils.isEmpty(path)) {
            path = Utils.addPathParam(path, paths);
            url = new Uri.Builder().path(url).appendPath(path).build().getPath();
        }
        this.url = url;
    }

    final Call newCall() {
        OkHttpClient c = rxHttp.client();
        RequestProcessor p = rxHttp.requestProcessor();

        okhttp3.Request.Builder builder = generateBuilder();
        Map<String, Object> params = p.paramsFilter(this.params);

        return newCallInternal(c, p, builder, params);
    }

    protected Call newCallInternal(OkHttpClient c, RequestProcessor p, okhttp3.Request.Builder builder, Map<String, Object> params) {

        switch (method) {
            case GET:
                String getUrl = addParamsToUrl(url, params);
                builder.url(getUrl).get();
                break;
            case POST:
                // TODO: 18-6-27  only support form
                builder.post(createFormBody(params));
                break;
            case HEAD:
                // TODO: 18-6-27  builder.head();
                break;
            case PUT:
                // TODO: 18-6-27  builder.put();
                break;
            case PATCH:
                // TODO: 18-6-27  builder.patch();
                break;
            case DELETE:
                // TODO: 18-6-27  builder.delete();
                break;
        }

        okhttp3.Request request = builder.build();

        return c.newCall(request);
    }

    okhttp3.Request.Builder generateBuilder() {
        return new okhttp3.Request.Builder()
                .url(url);
    }

    String cacheKey() {
        if (TextUtils.isEmpty(cacheKey)) {
            // TODO: 18-6-28  and header
            RequestProcessor p = rxHttp.requestProcessor();
            Map<String, Object> params = p.paramsFilter(this.params);
            cacheKey = addParamsToUrl(url, params);
        }
        return cacheKey;
    }

    boolean cacheable() {
        // TODO: 18-6-28  POST method & MediaType is FORM
        return cache && (method == HttpBuilder.Method.GET || method == HttpBuilder.Method.POST);
    }

    private String addParamsToUrl(String url, Map<String, Object> params) {
        if (TextUtils.isEmpty(url) | params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, string(params.get(key)));
        }
        return builder.build().toString();
    }

    private RequestBody createFormBody(Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder();

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                {
                    builder.add(key, string(params.get(key)));
                }
            }
        }
        return builder.build();
    }

    private String string(Object object) {
        return String.valueOf(object);
    }
}
