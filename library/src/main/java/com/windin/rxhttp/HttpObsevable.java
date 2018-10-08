package com.windin.rxhttp;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
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
        return rxHttp.responseProcessor().toJsonOriginally(rxHttp, this, clz);
    }

    public Observable<Response<String>> string() {
        return rxHttp.responseProcessor().stringOriginally(this);
    }
}
