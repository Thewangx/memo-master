package com.giot.memo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.giot.memo.login.LoginActivity;
import com.giot.memo.util.LogUtil;
import com.giot.memo.util.SysConstants;
import com.giot.memo.util.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信回调接口
 * Created by reed on 16/7/22.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, WeChatUtil.APP_ID, false);
        api.handleIntent(getIntent(), this);
        if (code != null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SysConstants.CODE, code);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (baseResp.getType() == 1) {//微信登录
                    //用户同意
                    code = ((SendAuth.Resp) baseResp).code;
                } else if (baseResp.getType() == 2) {//分享到微信
                    ToastUtil.show("分享成功");
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝
                if (baseResp.getType() == 1) {//微信登录
                    ToastUtil.show("授权取消");
                } else if (baseResp.getType() == 2) {//分享到微信
                    ToastUtil.show("分享失败");
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (baseResp.getType() == 1) {//微信登录
                    ToastUtil.show("授权取消");
                } else if (baseResp.getType() == 2) {//分享到微信
                    ToastUtil.show("分享失败");
                }
                break;
        }

    }
}
