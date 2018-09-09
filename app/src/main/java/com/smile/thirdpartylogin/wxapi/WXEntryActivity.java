package com.smile.thirdpartylogin.wxapi;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";
    private String wechatAppID = "wxd38bb2e01fee8e55";
    private String wechatAppSecret = "wxd38bb2e01fee8e55";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IWXAPI api = WXAPIFactory.createWXAPI(this, wechatAppID, true);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.i(TAG, "onReq...");
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i(TAG, "onResp: " + resp);
        String code = null;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:// 用户同意,只有这种情况的时候code是有效的
                code = ((SendAuth.Resp) resp).code;
                Log.i("Apptest", code);
                try {
                    requesUserInfo(code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:// 用户拒绝授权
                Log.i("Apptest", "用户拒绝授权");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:// 用户取消
                Log.i("Apptest", "用户取消");
                break;

            default:// 发送返回

                break;
        }
        finish();
    }

    public void requesUserInfo(final String code) {
        final String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + wechatAppID
                + "&secret=" + wechatAppSecret + "&code=" + code
                + "&grant_type=authorization_code";
        final android.os.Handler handler = new android.os.Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                String newsTemp = b.getString("msg");
                Toast.makeText(WXEntryActivity.this, "微信登录成功-----" + newsTemp, Toast.LENGTH_SHORT).show();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                try {
                    URL url = new URL(path);
                    HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
                    httpconn.setRequestProperty("accept", "*/*");
                    httpconn.setDoInput(true);
                    httpconn.setDoOutput(true);
                    httpconn.setConnectTimeout(5000);
                    httpconn.connect();
                    int stat = 200;
                    String msg = "";
                    if (stat == 200) {
                        br = new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
                        msg = br.readLine();
                        Bundle b = new Bundle();
                        b.putString("msg", msg);
                        Message m = new Message();
                        m.setData(b);
                        handler.sendMessage(m);
                    } else {
                        msg = "请求失败";
                        Log.i(TAG, msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

}

