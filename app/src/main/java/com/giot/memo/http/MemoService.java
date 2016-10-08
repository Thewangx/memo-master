package com.giot.memo.http;

import com.giot.memo.data.entity.Bill;
import com.giot.memo.data.entity.ResponseEntity;
import com.giot.memo.data.entity.User;
import com.giot.memo.util.SysConstants;

import org.json.JSONArray;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

/**
 * retrofit service interface
 * Created by reed on 16/7/22.
 */
public interface MemoService {

    @GET("user/wechat")//微信登录
    Observable<ResponseEntity<User>> loginWithWeChat(@Query(SysConstants.VER) String ver,
                                                     @Query(SysConstants.DEV) String dev,
                                                     @Query(SysConstants.SIG) String sig,
                                                     @Query(SysConstants.CODE) String code);

    @GET("bill/down")//下载账单
    Observable<ResponseEntity<List<Bill>>> downloadBill(@Query(SysConstants.USER_ID) String userId);

    @GET("bill/export")//账单导出
    Observable<ResponseEntity<String>> exportBill(@Query(SysConstants.EMAIL) String email,
                                          @Query(SysConstants.USER_ID) String userId,
                                          @Query(SysConstants.START_TIME) String startTime,
                                          @Query(SysConstants.END_TIME) String endTime);

    @FormUrlEncoded
    @POST("bill/up")//上传账单
    Observable<ResponseEntity<String>> upBill(@Field(SysConstants.DATA) JSONArray data);

    @FormUrlEncoded
    @POST("user/opinion")//意见反馈
    Observable<ResponseEntity<String>> feedback(@Field(SysConstants.VER) String ver,
                                                @Field(SysConstants.DEV) String dev,
                                                @Field(SysConstants.SIG) String sig,
                                                @Field(SysConstants.EMAIL) String email,
                                                @Field(SysConstants.FEEDBACK) String feedback);

    @GET("application/update")//检查更新
    Observable<ResponseEntity<Integer>> checkUpdate(@Query(SysConstants.VER) String ver,
                                                   @Query(SysConstants.DEV) String dev);

    @Streaming
    @GET("../download/apk/memo.apk")
    Observable<ResponseBody> downloadApk();



}
