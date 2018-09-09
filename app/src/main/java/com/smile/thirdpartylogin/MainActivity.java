package com.smile.thirdpartylogin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_PHONE_STATE"};
    private RecyclerView rvLogin;
    private List<Login> logins = new ArrayList<Login>();

    private MyAdapter myAdapter;

    private Tencent mTencent;
    /**
     * 应用需要获得哪些接口的权限，由“，”分隔  所有权限用“all”
     */
    private String Scope = "get_user_info,add_t,qrcode";
    private BaseUiListener listener;
    private String QQAppID = "1106937633";
    private String wechatAppID = "∂";
    private IWXAPI mIwxapi;

    private CallbackManager callbackManager;
    private List<String> permissions = Arrays.asList("email", "user_likes",
            "user_status", "user_photos", "user_birthday", "public_profile", "user_friends");

    private static String TWITTER_KEY = "gPVLDPBr49IysOiEKjFHJJc26";
    private static String TWITTER_SECRET = "Z5cMY4rAiRpwEoa24AUfncAPefr8gHLRW40ZBF6q1TlOGNFm1q";
    private TwitterAuthClient twitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(this,
                "android.permission.READ_PHONE_STATE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            try {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_main);
        initData();
        rvLogin = (RecyclerView) findViewById(R.id.rvLogin);
        rvLogin.setLayoutManager(new GridLayoutManager(this, 2));
        rvLogin.addItemDecoration(new DividerGridItemDecoration(this));
        rvLogin.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData() {
        String[] names = getResources().getStringArray(R.array.login_name);
        Login loginQQ = new Login(names[0], R.mipmap.user_login_qq);
        Login loginWechat = new Login(names[1], R.mipmap.user_login_wechat);
        Login loginFacebook = new Login(names[2], R.mipmap.user_login_facebook);
        Login loginTwitter = new Login(names[3], R.mipmap.user_login_twitter);
        logins.add(loginQQ);
        logins.add(loginWechat);
        logins.add(loginFacebook);
        logins.add(loginTwitter);
        // 启动QQ登录SDK
        mTencent = Tencent.createInstance(QQAppID, getApplicationContext());
        // 启动QQ登录SDK
        listener = new BaseUiListener() {
            @Override
            protected void doComplete(JSONObject values) {
                super.doComplete(values);
                Toast.makeText(MainActivity.this, "QQ 登录成功-----" + values, Toast.LENGTH_SHORT).show();
            }
        };
        // 获取IWXAPI 实例
        mIwxapi = WXAPIFactory.createWXAPI(MainActivity.this, wechatAppID, false);
        // 将应用的APPID 注册到微信
        mIwxapi.registerApp(wechatAppID);
        // 创建 callbackManager，以便处理登录响应
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(MainActivity.this, "Facebook 登录成功-----" + loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        // 初始化Twitter
        TwitterConfig config = new TwitterConfig.Builder(MainActivity.this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
        // 获取twitter 的客户端
        twitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //View绘制区域
        Rect outRect = new Rect();
        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        int height = outRect.height() / 2;
        myAdapter = new MyAdapter(this, logins, height);
        rvLogin.setAdapter(myAdapter);
        myAdapter.setLoginClickListener(new LoginClickListener() {
            @Override
            public void onItemLoginClick(int position, View view) {
                switch (position) {
                    case 0:
                        // 执行登录方法
                        mTencent.login(MainActivity.this, Scope, listener);
                        break;
                    case 1:
                        SendAuth.Req req = new SendAuth.Req();
                        req.scope = "snsapi_userinfo";
                        req.state = "wechat_sdk_test";
                        mIwxapi.sendReq(req);
                        break;
                    case 2:
//                        LoginManager.getInstance().logOut();
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, permissions);
                        break;
                    case 3:
                        twitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {
                            @Override
                            public void success(Result<TwitterSession> result) {
                                TwitterAuthToken authToken = result.data.getAuthToken();
                                Toast.makeText(MainActivity.this, "Twitter 登录成功-----" + authToken, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(TwitterException exception) {

                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, listener);
        }
        if (requestCode == 64206) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

}
