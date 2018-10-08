package com.windin.rxhttp;

import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * author: windin
 * created on: 18-8-2 下午5:20
 * email: windinwork@gmail.com
 */
public class PostJsonBuilder extends HttpBuilder {
    PostJsonBuilder(RxHttp rxHttp, String baseUrl) {
        super(rxHttp, baseUrl);
    }

    @Override
    protected Request build() {
        return PostJsonRequest.create(method, baseUrl, path, cache, paths, params, headers, rxHttp);
    }

    static class PostJsonRequest extends Request {

        public static PostJsonRequest create(HttpBuilder.Method method, String baseUrl, String path, boolean cache,
                                             Map<String, Object> paths,
                                             Map<String, Object> params,
                                             Map<String, String> headers,
                                             RxHttp rxHttp) {
            return new PostJsonRequest(method, baseUrl, path, cache, paths, params, headers, rxHttp);
        }

        private PostJsonRequest(Method method, String baseUrl, String path, boolean cache, Map<String, Object> paths, Map<String, Object> params, Map<String, String> headers, RxHttp rxHttp) {
            super(method, baseUrl, path, cache,paths,  params, headers, rxHttp);
        }

        @Override
        protected Call newCallInternal(OkHttpClient c, RequestProcessor p, okhttp3.Request.Builder builder, Map<String, Object> params) {

            String content = rxHttp.json().toJson(params);
            builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content));

            okhttp3.Request request = builder.build();

            return c.newCall(request);
        }
    }
}
