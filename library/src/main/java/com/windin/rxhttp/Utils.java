package com.windin.rxhttp;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * author: windin
 * created on: 18-7-10 上午11:33
 * email: windinwork@gmail.com
 */
public class Utils {

    public static ResponseBody createResponseBody(RxHttp rxHttp, Request request, okhttp3.Response response) throws IOException {
        ResponseBody body = response.body();

        Buffer buffer = new Buffer();
        body.source().readAll(buffer);

        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    public static ResponseBody createCacheBody(RxHttp rxHttp, Request request, okhttp3.Response response) throws IOException {
        ResponseBody body = response.body();

        Buffer buffer = new Buffer();
        body.source().readAll(buffer);

        if (request.cacheable()) {
            Cache cache = rxHttp.cache();
            if (cache != null) {
                cache.put(request.cacheKey(), ResponseBody.create(body.contentType(), buffer.size(), buffer.clone()));
            }
        }

        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    public static String addPathParam(String relativeUrl, Map<String, Object> paths) {
        if (paths== null || paths.isEmpty()) {
            return relativeUrl;
        }
        Set<Map.Entry<String, Object>> entrySet = paths.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            relativeUrl = addPathParam(relativeUrl, entry.getKey(), entry.getValue().toString());
        }
        return relativeUrl;
    }

    private static String addPathParam(String relativeUrl, String name, String value) {
        if (relativeUrl == null) {
            return null;
        }
        relativeUrl = relativeUrl.replace("{" + name + "}", value);
        return relativeUrl;
    }
}
