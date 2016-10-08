package com.giot.memo.http;

import com.giot.memo.data.entity.ResponseEntity;

import rx.functions.Func1;

/**
 * 对请求返回数据进行预处理
 * Created by reed on 16/8/10.
 */
public class ResponseEntityFunc<T> implements Func1<ResponseEntity<T>, T> {
    @Override
    public T call(ResponseEntity<T> responseEntity) {
        if (responseEntity.getCode() != 0) {
            throw new ApiException(responseEntity.getMsg());
        }
        return responseEntity.getData();
    }
}
