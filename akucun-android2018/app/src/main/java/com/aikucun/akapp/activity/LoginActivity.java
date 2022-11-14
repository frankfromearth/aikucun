package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.api.callback.AddressListCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.UserInfoCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.ContentWithSpaceEditText;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_terms)
    TextView tvTerms;
    @BindView(R.id.get_verification_code_btn)
    TextView get_verification_code_btn;
    @BindView(R.id.phone_edit)
    ContentWithSpaceEditText phone_edit;
    @BindView(R.id.verification_code_edit)
    EditText verification_code_edit;
    private String phone, authcode;

    private static boolean versionCheck = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onBeforeSetContentLayout() {

    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        configTerms();
        get_verification_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/1/3
                getAuthCodeAction();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    @OnClick({R.id.btn_login, R.id.btn_phone_login})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                weixinLoginAction();
                break;
            case R.id.btn_phone_login:
                loginAction();
                break;
            default:
                break;
        }
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
                    showApplicationSettingDetails("存储");
                }
            }
        });

        if (!versionCheck && AppContext.getInstance().shouldCheckUpgrade()) {
            versionCheck = true;
            checkVersionUpdate(false);
        }
    }

    private void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                Intent intent = new Intent(LoginActivity.this, WebViewActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_TITLE, "爱库存APP使用(服务)条款");
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_URL, url);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.overridePendingTransition(R.anim.anim_bottom_in, R.anim
                        .anim_fade_out);
            }
        });
    }

    private void weixinLoginAction() {
        Intent intent = new Intent(LoginActivity.this, WXEntryActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 获取用户基本信息
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
     * 获取短信验证码
     */
    protected void getAuthCodeAction() {
        phone = phone_edit.getText().toString().replaceAll(" ", "");

        if (!StringUtils.isMobile(phone)) {
            MToaster.showShort(this, getString(R.string.phone_error), MToaster.IMG_INFO);
            return;
        }

        cdt.cancel();
        get_verification_code_btn.setEnabled(false);
        get_verification_code_btn.requestFocus();
        cdt.start();
        verification_code_edit.requestFocus();
        verification_code_edit.setSelection(0);
        UsersApiManager.authCode(this, phone,
                AppContext.getInstance().getUserId(), 5, new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (code == 40011) {
                            MToaster.showShort(LoginActivity.this, "客户端版本过低，请升级后重试", MToaster.IMG_INFO);
                        } else
                            MToaster.showShort(LoginActivity.this, "获取验证码失败，请稍后重试", MToaster.IMG_INFO);
                    }
                });
    }

    CountDownTimer cdt = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            get_verification_code_btn.setText(millisUntilFinished / 1000 + "秒");
            get_verification_code_btn.setTextColor(getResources().getColor(R.color.gray));
        }

        @Override
        public void onFinish() {
            get_verification_code_btn.setText(R.string.get_verf_code);
            get_verification_code_btn.setEnabled(true);
            get_verification_code_btn.setTextColor(getResources().getColor(R.color.color_accent));
        }
    };

    /**
     * 手机号直接登录
     */
    private void loginAction() {

        if (figureContent()) {
            showProgress("登录中...");
            UsersApiManager.phoneLogin(this, phone, authcode, new JsonDataCallback() {

                @Override
                public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                    super.onApiSuccess(jsonObject, call, jsonResponse);
                    Log.e("登录返回的新：", jsonObject.toString());
                    //获取子账号信息
                    JSONArray jsonArray = jsonObject.getJSONArray("subuserinfos");
                    if (jsonArray != null) {
                        if (jsonArray.size() == 1) {
                            //fixme 该用户只存在一个子账户直接掉登录接口
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String userid = jsonObject1.getString("userid");
                            String subuserid = jsonObject1.getString("subUserId");
                            String token = jsonObject1.getString("tmptoken");
                            subuserlogin(userid, subuserid, token);
                        } else {
                            // TODO: 2018/1/4 该账户存在多个子账号 选择子账户在登录
                            ArrayList<SubAccountInfo> accountInfos = new ArrayList<>();
                            for (int i = 0, size = jsonArray.size(); i < size; i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SubAccountInfo subAccountInfo = new SubAccountInfo();
                                subAccountInfo.subuserid = jsonObject1.getString("subUserId");
                                subAccountInfo.subusername = jsonObject1.getString("subusername");
                                subAccountInfo.tmptoken = jsonObject1.getString("tmptoken");
                                subAccountInfo.userid = jsonObject1.getString("userid");
                                subAccountInfo.avatar = jsonObject1.getString("avatar");
                                subAccountInfo.shoujihao = jsonObject1.getString("shoujihao");
                                subAccountInfo.devicename = jsonObject1.getString("devicename");
                                subAccountInfo.islogin = jsonObject1.getIntValue("islogin");
                                accountInfos.add(subAccountInfo);
                            }
                            showChooseSubAccountDialog(accountInfos);
                        }
                    }
                }

                @Override
                public void onApiFailed(String message, int code) {
                    super.onApiFailed(message, code);
                    cancelProgress();
                }
            });

        }
    }

    /**
     * 选择子账户
     *
     * @param accountInfos
     */
    private void showChooseSubAccountDialog(final ArrayList<SubAccountInfo> accountInfos) {
        cancelProgress();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0, size = accountInfos.size(); i < size; i++) {
            arrayList.add(accountInfos.get(i).subusername);
        }
        BottomDialog.showChooseSubAccountDialog(this, getString(R.string.choose_sub_account), accountInfos, new BottomDialog.ISuAccountLisenter() {
            @Override
            public void onClick(SubAccountInfo mSubAccountInfo) {
                showSureLoginSubAccountDialog(mSubAccountInfo);
            }

        });
    }

    /**
     * 确定登录
     *
     * @param subAccountInfo
     */
    private void showSureLoginSubAccountDialog(final SubAccountInfo subAccountInfo) {
        if (subAccountInfo.islogin == 1) {
            MyDialogUtils.showV7NormalDialog(this, R.string.other_device_login_title, R.string.other_device_login_content, R.string.login_continue, new MyDialogUtils.IDialogListenter() {
                @Override
                public void onClick() {
                    subuserlogin(subAccountInfo.userid, subAccountInfo.subuserid, subAccountInfo.tmptoken);
                }
            });
        } else
            subuserlogin(subAccountInfo.userid, subAccountInfo.subuserid, subAccountInfo.tmptoken);

    }

    /**
     * 子账户登录
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

    protected Boolean figureContent() {
        phone = phone_edit.getText().toString().replaceAll(" ", "");
        authcode = verification_code_edit.getText().toString();

        if (!StringUtils.isMobile(phone)) {
            MToaster.showShort(this, "请输入有效的手机号", MToaster.IMG_INFO);
            return false;
        }
        if (authcode.length() == 0) {
            MToaster.showShort(this, "请输入验证码", MToaster.IMG_INFO);
            return false;
        }
        return true;
    }

    /**
     * 子账户信息
     */
    public static class SubAccountInfo {
        public String subusername;
        public String subuserid;
        public String tmptoken;
        public String userid;
        public String avatar;
        public String shoujihao;
        public String devicename;
        public int islogin;
    }

}