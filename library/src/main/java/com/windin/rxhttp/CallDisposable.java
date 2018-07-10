package com.windin.rxhttp;

import io.reactivex.disposables.Disposable;
import okhttp3.Call;

/**
 * author: windin
 * created on: 18-7-10 下午2:08
 * email: windinwork@gmail.com
 */
public class CallDisposable  implements Disposable {
    private final Call call;
    private volatile boolean disposed;

    CallDisposable(Call call) {
        this.call = call;
    }

    @Override
    public void dispose() {
        disposed = true;
        call.cancel();
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }
}
