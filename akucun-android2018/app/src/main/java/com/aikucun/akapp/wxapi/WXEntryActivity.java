package com.aikucun.akapp.wxapi;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.BindPhoneActivity;
import com.aikucun.akapp.activity.LoginActivity;
import com.aikucun.akapp.activity.MainActivity;
import com.aikucun.akapp.activity.WebViewActivity;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.api.callback.AddressListCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.UserInfoCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.widget.ContentWithSpaceEditText;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.lzy.okgo.OkGo;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    // Weixin App ID
//    public static final String WX_APP_ID = "wxe70358a259804642";
//    public static final String WX_APP_SECRET = "ee068f44b2c41c43d82d539c49741b39";
    public static final String WX_APP_ID = "wxb140ca2d12d4425a";
    public static final String WX_APP_SECRET = "88eacde204275bd006b24913e98a2c09";

    @BindView(R.id.tv_terms)
    TextView tvTerms;
    @BindView(R.id.get_verification_code_btn)
    TextView get_verification_code_btn;
    @BindView(R.id.phone_edit)
    ContentWithSpaceEditText phone_edit;
    @BindView(R.id.verification_code_edit)
    EditText verification_code_edit;
    private IWXAPI wxApi;

    private static boolean versionCheck = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wxentry_layout;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        //
        //        StatusBarUtils.MIUISetStatusBarLightMode(getWindow(), true);
    }

    @Override
    public void initView() {
        wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID);
        wxApi.registerApp(WX_APP_ID);
        wxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void initData() {
        configTerms();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        wxApi.handleIntent(intent, this);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    @OnClick({R.id.btn_login, R.id.ll_phone_login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                goToPhoneLogin();
            break;
            case R.id.ll_phone_login:
                weixinLoginAction();
                break;
            default:
                break;
        }
    }

    private void goToPhoneLogin() {
        Intent intent = new Intent(WXEntryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                .WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (AppContext.getInstance().shouldCheckUpgrade()) {
                        checkVersionUpdate(false);
                    }
                } else {
                    showApplicationSettingDetails("??????");
                }
            }
        });

        if (!versionCheck && AppContext.getInstance().shouldCheckUpgrade()) {
            versionCheck = true;
            checkVersionUpdate(false);
        }
    }

    private void loginSuccess() {
        Intent intent = new Intent(WXEntryActivity.this, MainActivity.class);
        startActivity(intent);
        cancelProgress();
        // LiveManager.getInstance().setLiveLiveIds(ProductManager.getInstance().getPinpaiString());
        finish();
    }

    private void configTerms() {
        String content = tvTerms.getText().toString();
        if (content.length() < 4) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(40);
        spannableStringBuilder.setSpan(absoluteSizeSpan, content.length() - 4, content.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableStringBuilder.setSpan(underlineSpan, content.length() - 4, content.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvTerms.setText(spannableStringBuilder);

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(WXEntryActivity.this, TermActivity.class);
                String url = HttpConfig.APP_TERMS_URL + System.currentTimeMillis();
                Intent intent = new Intent(WXEntryActivity.this, WebViewActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_TITLE, "?????????APP??????(??????)??????");
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_URL, url);
                WXEntryActivity.this.startActivity(intent);
                WXEntryActivity.this.overridePendingTransition(R.anim.anim_bottom_in, R.anim
                        .anim_fade_out);
            }
        });
    }

    private void weixinLoginAction() {
        showProgress("?????????...");
//        if (HttpConfig.isOnline || !HttpConfig.isAccountTest) {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_message,snsapi_userinfo";
        req.state = "akucun";
        wxApi.sendReq(req);
            /*
        } else if (HttpConfig.isAccountTest) {
            //TODO TEST
            //            String openId = "oVx8yvykXng_nEpRrk8g05Nu7WEY";
            //            String unionId = "oRuFRt0daNJ77FBZ0wNOBuIPtIt8";
            String openId = "oVx8yvw3MgLAbf8jk1E1xqEd6wLs";
            String unionId = "oRuFRtyFRlKByxqVHTm8RDIgEbK0";
            loginRequest(openId, unionId, "?????????????????????", "http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJlXMqDz35qFniaFOSibz9DTLGQhTP1sgPB2baXBibgE8Uiab3TnhNibDuibxHDynRb5l5BKmW94L5d9xsA/0");
        }*/
    }

    /**
     * ?????????????????????????????????????????????
     */
    @Override
    public void onReq(BaseReq baseReq) {
        SCLog.debug("--> Weixin Auth onReq ");
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK: {
                showProgress("?????????...");
                SendAuth.Resp resp = (SendAuth.Resp) baseResp;
                SCLog.debug("--> Weixin Auth code : " + resp.code);
                requestAccessToken(resp.code);
            }
            break;

            case BaseResp.ErrCode.ERR_USER_CANCEL: {
                SCLog.debug("--> Weixin Auth Canceled ");
                cancelProgress();
                showMessage("?????????????????? ???");
            }
            break;

            case BaseResp.ErrCode.ERR_AUTH_DENIED: {
                SCLog.debug("--> Weixin Auth Denied  ");
                cancelProgress();
                showMessage("?????????????????? ???");
            }
            break;

            default: {
                SCLog.debug("--> Weixin Auth Failed  ");
                cancelProgress();
                showMessage("???????????? ???");
            }
            break;
        }
    }

    private void requestAccessToken(String code) {
        String urlPath = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID +
                "&secret=" + WX_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";

        OkGo.get(urlPath).tag(this).execute(new JsonCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);

                String openId = jsonObject.getString("openid");
                String token = jsonObject.getString("access_token");
                SCLog.debug("--> Weixin Auth openId : " + openId);
                SCLog.debug("--> Weixin Auth token : " + token);

                requestUserInfo(openId, token);
            }
        });
    }

    private void requestUserInfo(String openid, String access_token) {
        String urlPath = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token +
                "&openid=" + openid;

        OkGo.get(urlPath).tag(this).execute(new JsonCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);

                String openId = jsonObject.getString("openid");
                String unionId = jsonObject.getString("unionid");
                SCLog.debug("--> Weixin Auth openId : " + openId);
                SCLog.debug("--> Weixin Auth unionId : " + unionId);

                String name = jsonObject.getString("nickname");
                String avatar = jsonObject.getString("headimgurl");

                loginRequest(openId, unionId, name, avatar);
            }
        });
    }

    /**
     * ??????????????????
     *
     * @param openId  ???????????? Open ID
     * @param unionId ???????????? Union ID
     * @param name    ??????????????????
     * @param avatar  ????????????????????????
     */
    private void loginRequest(String openId, String unionId, String name, String avatar) {
        UsersApiManager.thirdlogin(this, 2, openId, unionId, name, avatar, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                JSONObject subJson = jsonObject.getJSONObject("subuserinfo");
                int validflag = subJson.getIntValue("validflag");
                if (validflag == 0) {
                    cancelProgress();
                    // fixme ???????????????????????????
                    String tmptoken = subJson.getString("tmptoken");
                    String subuserid = subJson.getString("subUserId");
                    if (!TextUtils.isEmpty(tmptoken) && !TextUtils.isEmpty(subuserid)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("subuserid", subuserid);
                        bundle.putString("token", tmptoken);
                        ActivityUtils.startActivity(WXEntryActivity.this, BindPhoneActivity.class, bundle);
                        finish();
                    }
                } else {
                    // TODO: 2018/1/4 ???????????????????????????
                    String tmptoken = subJson.getString("tmptoken");
                    String userid = subJson.getString("userid");
                    String subuserid = subJson.getString("subUserId");
                    subuserlogin(userid, subuserid, tmptoken);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });

//        UsersApiManager.weixinLogin(this, openId, unionId, name, avatar, new JsonDataCallback() {
//            @Override
//            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
//                super.onApiSuccess(jsonObject, call, jsonResponse);
//
////                String userId = jsonObject.getString("userid");
////                String token = jsonObject.getString("token");
////                AppContext.saveUserToken(userId, token);
////
////                requestUserInfo();
//            }
//        });
    }

    /**
     * ????????????????????????
     */
    private void requestUserInfo() {
        UsersApiManager.userGetInfo(this, new UserInfoCallback() {
            @Override
            public void onApiSuccess(UserInfo userInfo, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(userInfo, call, jsonResponse);
                requestUserAddress();
                PushManager.getInstance().bindAlias(getApplicationContext(), userInfo.getUserid());
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    private void requestUserAddress() {
        UsersApiManager.userAddressList(this, new AddressListCallback() {
            @Override
            public void onApiSuccess(List<Address> addresses, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(addresses, call, jsonResponse);
                AddressUtils.setAddresses(addresses);
                loginSuccess();
            }
        });
    }

    /**
     * ???????????????
     *
     * @param userid
     * @param subuserid
     * @param token
     */
    private void subuserlogin(String userid, String subuserid, String token) {
        UsersApiManager.subuserlogin(this, userid, subuserid, token, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);

                String userId = jsonObject.getString("userid");
                String subUserId = jsonObject.getString("subuserid");
                String token = jsonObject.getString("token");
                AppContext.saveUserToken(userId, subUserId, token);
                requestUserInfo();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }
}