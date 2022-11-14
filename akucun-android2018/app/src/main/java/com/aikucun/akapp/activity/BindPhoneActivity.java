package com.aikucun.akapp.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.aikucun.akapp.utils.StatusBarUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.ContentWithSpaceEditText;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.PushManager;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录界面
 * Created by jarry on 16/3/11.
 */
public class BindPhoneActivity extends BaseActivity {

    public static int AUTH_CODE_LOGIN = 5;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    //手机号输入框
    @BindView(R.id.phone_edit)
    ContentWithSpaceEditText phoneEdit;
    //验证码输入框
    @BindView(R.id.verification_code_edit)
    EditText authcodeEdit;

    //获取验证码按钮
    @BindView(R.id.get_verification_code_btn)
    TextView get_verification_code_btn;

    @BindView(R.id.tv_terms)
    TextView tvTerms;

    private String phone, authcode;
    //子账户信息
    private String subuserid, token;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        StatusBarUtils.MIUISetStatusBarLightMode(getWindow(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.bind_mobile);
        tvTerms.setVisibility(View.GONE);

        configTerms();
    }

    @Override
    public void initData() {
        subuserid = getIntent().getStringExtra("subuserid");
        token = getIntent().getStringExtra("token");
        get_verification_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthCodeAction();
            }
        });
    }

    @Override
    @OnClick({R.id.btn_phone_login})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_phone_login: {
                bindPhone();
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        phoneEdit.requestFocus();
    }

    protected void loginSuccess() {
        Intent intent = new Intent(BindPhoneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 绑定手机号
     */
    private void bindPhone() {
        if (figureContent()) {
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(subuserid)) {
                showProgress(getString(R.string.msg_text_loading));
                UsersApiManager.bindphonenum(this, phone, authcode, subuserid, token, new JsonDataCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                        cancelProgress();
                        super.onSuccess(jsonObject, call, response);
                        Log.e("绑定手机号返回：", jsonObject.toString());
                        String subuserid = jsonObject.getString("subuserid");
                        String token = jsonObject.getString("token");
                        String userid = jsonObject.getString("userid");
                        AppContext.saveUserToken(userid,subuserid, token);
                        requestUserInfo();
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        cancelProgress();
                        super.onApiFailed(message, code);
                        if (!TextUtils.isEmpty(message))
                            MToaster.showLong(BindPhoneActivity.this, message, MToaster.IMG_INFO);
                    }
                });
            }
        }
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
                // FIXME: 2018/1/4
                PushManager.getInstance().bindAlias(getApplicationContext(), userInfo.getUserid());
            }
        });
    }


    /**
     * 获取用户地址
     */
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

    private void configTerms() {
        String content = tvTerms.getText().toString();
        if (content.length() < 4) return;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(40);
        spannableStringBuilder.setSpan(absoluteSizeSpan, content.length() - 4, content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableStringBuilder.setSpan(underlineSpan, content.length() - 4, content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvTerms.setText(spannableStringBuilder);

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(BindPhoneActivity.this, TermActivity.class);
                String url = HttpConfig.APP_TERMS_URL + System.currentTimeMillis();
                Intent intent = new Intent(BindPhoneActivity.this, WebViewActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_TITLE, "爱库存APP使用(服务)条款");
                intent.putExtra(AppConfig.BUNDLE_KEY_WEB_URL, url);
                BindPhoneActivity.this.startActivity(intent);
                BindPhoneActivity.this.overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
            }
        });
        tvTerms.setVisibility(View.VISIBLE);
    }

    protected Boolean figureContent() {
        phone = phoneEdit.getText().toString().replaceAll(" ","");
        authcode = authcodeEdit.getText().toString();

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


    @Override
    public void onBackPressed() {
        showExitBialog();
    }


    private void showExitBialog() {
        MyDialogUtils.showV7NormalDialog(this, R.string.exit_bind_phone, new MyDialogUtils.IDialogListenter() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    /**
     * 获取短信验证码
     */
    protected void getAuthCodeAction() {
        phone = phoneEdit.getText().toString().replaceAll(" ", "");

        if (!StringUtils.isMobile(phone)) {
            MToaster.showShort(this, getString(R.string.phone_error), MToaster.IMG_INFO);
            return;
        }

        cdt.cancel();
        get_verification_code_btn.setEnabled(false);
        get_verification_code_btn.requestFocus();
        cdt.start();

        UsersApiManager.authCode(this, phone,
                AppContext.getInstance().getUserId(), 3, new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (code == 40011) {
                            MToaster.showShort(BindPhoneActivity.this, "客户端版本过低，请升级后重试", MToaster.IMG_INFO);
                        } else
                            MToaster.showShort(BindPhoneActivity.this, "获取验证码失败，请稍后重试", MToaster.IMG_INFO);
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
}
