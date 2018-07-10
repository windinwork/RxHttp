package com.windin.rxhttp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
 * created on: 18-6-25 下午4:36
 * email: windinwork@gmail.com
 */
public class HttpObsevable extends Observable<Response<ResponseBody>> {
    private Request request;

    private RxHttp rxHttp;

    HttpObsevable(RxHttp rxHttp, Request request) {
        this.rxHttp = rxHttp;
        this.request = request;
    }

    @Override
    protected void subscribeActual(Observer<? super Response<ResponseBody>> observer) {

        Request request = this.request;

        Call call = request.newCall();

        CallDisposable disposable = new CallDisposable(call);
        observer.onSubscribe(disposable);

        boolean terminated = false;

        try {
            okhttp3.Response response = call.execute();
            if (!disposable.isDisposed()) {
                ResponseBody responseBody = Utils.createResponseBody(rxHttp, request, response);
                observer.onNext(new Response<>(responseBody, false));
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

    public <T> Observable<Response<T>> fromJson(final Class<T> clz) {
        return string().map(new Function<Response<String>, Response<T>>() {
            @Override
            public Response<T> apply(Response<String> response) throws Exception {

                String string = response.body();
                Gson gson = rxHttp.json();
                T t = gson.fromJson(string, new TypeToken<T>() {
                }.getType());

                return new Response<>(t, response.isCache());
            }
        });
    }

    public Observable<Response<String>> string() {
        return map(new Function<Response<ResponseBody>, Response<String>>() {
            @Override
            public Response<String> apply(Response<ResponseBody> response) throws Exception {
                ResponseBody body = response.body();
                String string = body.string();
                return new Response<>(string, response.isCache());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
