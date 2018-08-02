package com.windin.rxhttp;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.ResponseBody;


/**
 * author: windin
 * created on: 18-7-10 上午10:54
 * email: windinwork@gmail.com
 */
public class GeneralObservable extends Observable<Response<String>> {
    private Request request;

    private RxHttp rxHttp;

    GeneralObservable(RxHttp rxHttp, Request request) {
        this.rxHttp = rxHttp;
        this.request = request;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<String>> observer) {

        Request request = this.request;

        Call call = request.newCall();

        CallDisposable disposable = new CallDisposable(call);
        observer.onSubscribe(disposable);

        boolean terminated = false;

        // Load data from cache if cache enabled
        Cache cache = rxHttp.cache();

        String cacheString = null;
        if (cache != null
                && request.cacheable()
                && !disposable.isDisposed()) {
            String key = request.cacheKey();
            ResponseBody cacheResponseBody = cache.get(key);
            if (cacheResponseBody != null) {
                try {
                    cacheString = cacheResponseBody.string();
                    observer.onNext(new Response<>(cacheString, true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            okhttp3.Response response = call.execute();
            if (!disposable.isDisposed()) {
                ResponseBody responseBody = Utils.createCacheBody(rxHttp, request, response);
                String s = responseBody.string();

                boolean sameAsCache = TextUtils.equals(cacheString, s);

                if (sameAsCache) {
                } else {
                    observer.onNext(new Response<>(s, false));
                }


            }
            if (!disposable.isDisposed()) {
                terminated = true;
                observer.onComplete();
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            if (terminated) {
                RxJavaPlugins.onError(t);
            } else if (!disposable.isDisposed()) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
            }
        }
    }

    public Observable<Response<String>> string() {
        return map(new Function<Response<String>, Response<String>>() {
            @Override
            public Response<String> apply(Response<String> response) throws Exception {
                return response;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<Response<T>> fromJson(final Class<T> clz) {
        return map(new Function<Response<String>, Response<T>>() {
            @Override
            public Response<T> apply(Response<String> response) throws Exception {

                String string = response.body();
                Gson gson = rxHttp.json();
                T t = gson.fromJson(string, clz);

                return new Response<>(t, response.isCache());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
