package com.windin.rxhttp;

/**
 * author: windin
 * created on: 18-6-25 下午12:47
 * email: windinwork@gmail.com
 */
public class Response<T> {

    private boolean cache;
    private T t;


    public Response(T t, boolean cache) {
        this.t = t;
        this.cache = cache;
    }

    public boolean isCache() {
        return cache;
    }

    public T body() {
        return t;
    }
}
