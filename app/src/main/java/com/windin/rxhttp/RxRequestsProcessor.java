package com.windin.rxhttp;

import android.support.annotation.Nullable;

import java.util.Map;

public class RxRequestsProcessor extends RequestProcessor {
    @Nullable
    @Override
    public Map<String, Object> paramsFilter(@Nullable Map<String, Object> params) {
        // 用于处理请求参数
        return super.paramsFilter(params);
    }
}
