package com.windin.rxhttp;

import io.reactivex.Observable;
import okhttp3.Response;

public class RxResponseProcessor extends ResponseProcessor {
    @Override
    public boolean responseSuccessful(Response response) {
        // 可加入自己的成功请求判断逻辑，可用于网络请求框架对于成功或失败请求和缓存的处理
        return super.responseSuccessful(response);
    }

    @Override
    public boolean cacheFilter(String cacheString) {
        // 用于命中缓存后是否进行请求回调的判断
        return super.cacheFilter(cacheString);
    }

    @Override
    public Observable<com.windin.rxhttp.Response<String>> string(Observable<com.windin.rxhttp.Response<String>> observable) {
        // 处理string的响应结果，一般无需操作
        return super.string(observable);
    }

    @Override
    public <T> Observable<com.windin.rxhttp.Response<T>> toJson(RxHttp rxHttp, Observable<com.windin.rxhttp.Response<String>> observable, Class<T> clz) {
        // 将string转化为json的过程，可在此处进行一些自定义操作
        return super.toJson(rxHttp, observable, clz);
    }
}
