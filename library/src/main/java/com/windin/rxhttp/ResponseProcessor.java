package com.windin.rxhttp;

import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * author: windin
 * created on: 18-8-3 上午10:52
 * email: windinwork@gmail.com
 */
public class ResponseProcessor {

    public boolean responseSuccessful(okhttp3.Response response) {
        return response.isSuccessful();
    }

    public boolean cacheFilter(String cacheString) {
        return true;
    }

    final Observable<Response<String>> stringOriginally(Observable<Response<ResponseBody>> observable) {
        return string(observable.map(new Function<Response<ResponseBody>, Response<String>>() {
            @Override
            public Response<String> apply(Response<ResponseBody> response) throws Exception {
                ResponseBody body = response.body();
                String string = body.string();
                return new Response<>(string, response.isCache());
            }
        }));
    }

    final <T> Observable<Response<T>> toJsonOriginally(RxHttp rxHttp, Observable<Response<ResponseBody>> observable, Class<T> clz) {
        return toJson(rxHttp, stringOriginally(observable), clz);
    }

    public Observable<Response<String>> string(Observable<Response<String>> observable) {
        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<Response<T>> toJson(final RxHttp rxHttp, Observable<Response<String>> observable, final Class<T> clz) {
        return observable.map(new Function<Response<String>, Response<T>>() {
            @Override
            public Response<T> apply(Response<String> response) throws Exception {

                String string = response.body();
                Gson gson = rxHttp.json();
                T t = gson.fromJson(string, clz);

                return new Response<>(t, response.isCache());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true);
    }
}
