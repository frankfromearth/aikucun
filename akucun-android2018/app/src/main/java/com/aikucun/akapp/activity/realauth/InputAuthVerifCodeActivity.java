package com.aikucun.akapp.activity.realauth;

import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.AuthApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;

import java.text.MessageFormat;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2017/12/20.
 * 提交认证信息
 */

public class InputAuthVerifCodeActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.authcode_edit)
    EditText authcode_edit;
    @BindView(R.id.authcode_tv)
    TextView authcode_tv;
    @BindView(R.id.auth_verif_code_intro)
    TextView auth_verif_code_intro;
    private String phone, realName, idno, bankno;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.realname_auth);
        //获取上个页面输入的信息
        phone = getIntent().getStringExtra("phone_num");
        realName = getIntent().getStringExtra("real_name");
        idno = getIntent().getStringExtra("id_num");
        bankno = getIntent().getStringExtra("bank_num");
        String temp = "";
        if (phone.length() == 11) {
            temp = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
        } else temp = phone;
        auth_verif_code_intro.setText(MessageFormat.format(getString(R.string.auth_input_verif_code), temp));
    }

    @Override
    public void initData() {
        authcode_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuthCodeAction();
            }
        });
        authcode_tv.setEnabled(false);
        cdt.start();
        initLisenter();
    }

    private void initLisenter() {
        findViewById(R.id.complete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = authcode_edit.getText().toString();
                if (!TextUtils.isEmpty(code)) {
                    String positivePath = AppContext.get("idno_positive_base64", "");
                    String backPath = AppContext.get("idno_back_base64", "");
                    bankno = bankno.replaceAll(" ","");
                    addAuth(realName, idno, bankno, phone, code, positivePath, backPath);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_auth_verification_code_layout;
    }

    protected void getAuthCodeAction() {
        if (phone.length() != 11) {
            MToaster.showShort(this, getString(R.string.phone_error), MToaster.IMG_INFO);
            return;
        }
        sendSMSBankMoblie(phone);
    }

    /**
     * 发送验证码
     *
     * @param phoneNo
     */
    private void sendSMSBankMoblie(final String phoneNo) {
        showProgress(getString(R.string.loading));
        AuthApiManager.sendSMSBankMoblie(this, phoneNo, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                cancelProgress();
                super.onSuccess(jsonObject, call, response);
                cdt.cancel();
                authcode_tv.setEnabled(false);
                cdt.start();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message)) {
                    MToaster.showShort(InputAuthVerifCodeActivity.this, message, MToaster.IMG_INFO);
                }
            }
        });
    }

    CountDownTimer cdt = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            authcode_tv.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            authcode_tv.setText("获取验证码");
            authcode_tv.setEnabled(true);
        }
    };

    /**
     * 添加实名认证
     *
     * @param realname
     * @param idcard
     * @param bankcard
     * @param mobile
     * @param code
     * @param base64Img    身份证正面base64 str
     * @param base64ImgBac 身份证反面base64 str
     */
    private void addAuth(String realname, String idcard, String bankcard, String mobile, String code, String base64Img, String base64ImgBac) {
        showProgress(getString(R.string.loading));
        AuthApiManager.validUser(this, realname, idcard, bankcard, mobile, code, base64Img, base64ImgBac, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                if (jsonObject != null && !TextUtils.isEmpty(jsonObject.getString("userid"))) {
                    AppContext.set("idno_positive_base64", "");
                    AppContext.set("idno_back_base64", "");
                    MToaster.showShort(InputAuthVerifCodeActivity.this, getString(R.string.auth_success), MToaster.IMG_INFO);
                    if (InputAuthBankNumActivity.mInputAuthBankNumActivity != null)
                        InputAuthBankNumActivity.mInputAuthBankNumActivity.finish();
                    if (InputAuthRealNameActivity.mInputAuthRealNameActivity != null)
                        InputAuthRealNameActivity.mInputAuthRealNameActivity.finish();
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_USER_INFO));
                    finish();
                }else {
                    if (jsonObject != null){
                        String message = jsonObject.getString("message");
                        if (!TextUtils.isEmpty(message)) MToaster.showShort(InputAuthVerifCodeActivity.this, message, MToaster.IMG_INFO);
                    }
                }

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(InputAuthVerifCodeActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }
}
