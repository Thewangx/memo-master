package com.giot.memo.wxapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.bumptech.glide.Glide;
import com.giot.memo.R;
import com.giot.memo.util.ImageLoader;
import com.giot.memo.util.SnackBarUtil;
import com.giot.memo.util.ToastUtil;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * some function about wechat
 * Created by reed on 16/7/22.
 */
public class WeChatUtil {

    //从微信官网申请的appId
    public static final String APP_ID = "wx200222a550f06a21";
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

    //IWXAPI是第三方app和微信通信的openapi接口
    private IWXAPI api;

    private Context context;

    public static WeChatUtil getInstance(Context context) {
        WeChatUtil weChatUtil = new WeChatUtil();
        weChatUtil.context = context;
        weChatUtil.regToWx();
        return weChatUtil;
    }

    //注册应用id到微信
    private void regToWx() {
        //通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);

        //将应用的appId注册到微信
        api.registerApp(APP_ID);
    }

    public boolean requestCode() {
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//授权域
        req.state = "";//用于保持请求和回调的状态，授权请求后原样带回给第三方。
        return api.sendReq(req);
    }

    //分享网页到微信
    public boolean share(String url, String title, String description, int scene) {
        if (scene == SendMessageToWX.Req.WXSceneTimeline && !isSupportWX()) {
            return false;
        }
        //初试话一个WXWebpageObject对象，填写url
        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = url;

        //用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = title;
        msg.description = description;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        msg.thumbData = bmpToByteArray(thumb, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        return api.sendReq(req);
    }

    //判断是否支持转发到朋友圈
    //微信4.2以上支持，如果需要检查微信版本支持API的情况， 可调用IWXAPI的getWXAppSupportAPI方法,0x21020001及以上支持发送朋友圈
    public boolean isSupportWX() {
        int wxSdkVersion = api.getWXAppSupportAPI();
        return wxSdkVersion >= TIMELINE_SUPPORTED_VERSION;
    }

    private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
