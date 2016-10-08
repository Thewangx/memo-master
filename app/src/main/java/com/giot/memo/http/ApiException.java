package com.giot.memo.http;

/**
 * 返回码出错
 * Created by reed on 16/8/10.
 */
public class ApiException extends RuntimeException {

    public ApiException(int code, String message) {
        this(getApiExceptionMessage(code, message));
    }

    public ApiException(String message) {
        super(message);
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * @param code 错误码
     * @param message 错误信息
     * @return 错误信息
     */
    private static String getApiExceptionMessage(int code, String message){
        //if you want to deal the error with code, to do here
        // TODO: 16/8/29
        return message;
    }
}
