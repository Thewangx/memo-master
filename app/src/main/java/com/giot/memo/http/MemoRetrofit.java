package com.giot.memo.http;

import com.giot.memo.util.SysConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit
 * Created by reed on 16/7/22.
 */
public class MemoRetrofit {

    private volatile static Retrofit retrofit;
    private volatile static MemoService service;
    public static final String baseUrl = SysConstants.BASE_URL + "api/";

    private static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (MemoRetrofit.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            //解析结果为需要的类型
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static MemoService getService() {
        if (service == null) {
            synchronized (MemoRetrofit.class) {
                if (service == null) {
                    service = getInstance().create(MemoService.class);
                }
            }
        }
        return service;
    }

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

}
