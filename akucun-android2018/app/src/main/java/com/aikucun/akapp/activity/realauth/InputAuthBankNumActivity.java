package com.aikucun.akapp.activity.realauth;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.AuthApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.BankInfoUtil;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.widget.ContentWithSpaceEditText;
import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2017/12/20.
 * 实名认证输入银行卡号
 */

public class InputAuthBankNumActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.bank_no_et)
    ContentWithSpaceEditText bank_no_et;
    @BindView(R.id.phone_edit)
    ContentWithSpaceEditText phone_edit;
    @BindView(R.id.bank_card_type_tv)
    TextView bank_card_type_tv;
    @BindView(R.id.bank_name_tv)
    TextView bank_name_tv;
    @BindView(R.id.bank_info_layout)
    LinearLayout bank_info_layout;
    @BindView(R.id.name_et)
    EditText name_et;
    private String realName, idno;
    public static InputAuthBankNumActivity mInputAuthBankNumActivity;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.realname_auth);
        realName = getIntent().getStringExtra("real_name");
        idno = getIntent().getStringExtra("id_num");
        mInputAuthBankNumActivity = this;
        name_et.setText(realName);
    }

    @Override
    public void initData() {
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bankno = bank_no_et.getText().toString();
                String phoneno = phone_edit.getText().toString().replaceAll(" ", "");
                if (!TextUtils.isEmpty(bankno) && !TextUtils.isEmpty(phoneno)) {
                    // TODO: 2017/12/20
                    if (phoneno.length() != 11) {
                        MToaster.showShort(InputAuthBankNumActivity.this, getString(R.string.phone_error), MToaster.IMG_INFO);
                        return;
                    } else sendSMSBankMoblie(phoneno, bankno);
                }
            }
        });
        bank_no_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onCheckCard();
            }
        });
    }

    private void onCheckCard() {
        String cardNum = bank_no_et.getText().toString().replaceAll(" ", "");
        if (!TextUtils.isEmpty(cardNum) && BankInfoUtil.checkBankCard(cardNum)) {
            BankInfoUtil mInfoUtil = new BankInfoUtil(cardNum);
            bank_name_tv.setText(mInfoUtil.getBankName());
            bank_card_type_tv.setText(mInfoUtil.getCardType());
            if (!mInfoUtil.getBankName().equals("未知"))
                bank_info_layout.setVisibility(View.VISIBLE);
        } else {
            bank_info_layout.setVisibility(View.GONE);
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phoneNo
     * @param bankno
     */
    private void sendSMSBankMoblie(final String phoneNo, final String bankno) {
        showProgress(getString(R.string.loading));
        AuthApiManager.sendSMSBankMoblie(this, phoneNo, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                Bundle bundle = new Bundle();
                bundle.putString("real_name", realName);
                bundle.putString("id_num", idno);
                bundle.putString("bank_num", bankno);
                bundle.putString("phone_num", phoneNo);
                ActivityUtils.startActivity(InputAuthBankNumActivity.this, InputAuthVerifCodeActivity.class, bundle);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message)) {
                    MToaster.showShort(InputAuthBankNumActivity.this, message, MToaster.IMG_INFO);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_auth_bankno_layout;
    }
}
