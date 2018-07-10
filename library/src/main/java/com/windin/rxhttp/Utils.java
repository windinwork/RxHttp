package com.windin.rxhttp;

import java.io.IOException;

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

        if (response.isSuccessful() && request.cacheable()) {
            Cache cache = rxHttp.cache();
            if (cache != null) {
                cache.put(request.cacheKey(), ResponseBody.create(body.contentType(), buffer.size(), buffer.clone()));
            }
        }

        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }
}
