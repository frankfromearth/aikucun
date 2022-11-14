package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.AfterSaleApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.alibaba.fastjson.JSONObject;
import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * 填写快递信息
 * Created by jarry on 17/6/30.
 */
public class AfterSaleAddressActivity extends BaseActivity
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.refund_wuliu_edit)
    AppCompatEditText mEditWuliu;

    @BindView(R.id.refund_bianhao_edit)
    AppCompatEditText mEditBianhao;

    @BindView(R.id.ok_button)
    TextView okButton;

    private String cartProductId;

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.input_return_goods_info);

    }

    @Override
    public void initData()
    {
        cartProductId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);

    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_aftersale_address;
    }


    @Override
    @OnClick({R.id.ok_button,R.id.addr_copy_btn, R.id.iv_scan})
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.ok_button:
            {
                hideInputKeyboard();

                if (validate())
                {
                    String wuliu = mEditWuliu.getText().toString();
                    String bianhao = mEditBianhao.getText().toString();
                    requestAddress(wuliu, bianhao);
                }
            }
            break;

            case R.id.addr_copy_btn:
            {
                hideInputKeyboard();

                String address = getResources().getString(R.string.order_refund_addr);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(address);
                cm.setPrimaryClip(ClipData.newPlainText("akucun", address));

                showMessage(getResources().getString(R.string.address_is_copy));
            }
            break;
            case R.id.iv_scan: {
                captureAction();
            }
            break;
        }
    }

    private void requestAddress(String wuliugongsi, String wuliuhao)
    {
        showProgress("");
        AfterSaleApiManager.afterSaleApplyMore(this,cartProductId,wuliugongsi, wuliuhao, new JsonDataCallback(){

            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                cancelProgress();
                showMessage("已成功提交 ！");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });

    }

    private boolean validate()
    {
        if (mEditWuliu.length() == 0)
        {
            mEditWuliu.setError(getString(R.string.input_express_company));
            mEditWuliu.requestFocus();
            return false;
        }

        if (mEditBianhao.length() == 0)
        {
            mEditBianhao.setError(getString(R.string.input_courier_number));
            mEditBianhao.requestFocus();
            return false;
        }
        return true;
    }

    private void hideInputKeyboard()
    {
        if (mEditWuliu.isFocused())
        {
            InputMethodUtils.hideInputKeyboard(this, mEditWuliu);
        }
        else if (mEditBianhao.isFocused())
        {
            InputMethodUtils.hideInputKeyboard(this, mEditBianhao);
        }
    }

    //扫描快递单号

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (1010 == requestCode) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK)
            {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                handleScanResult(scanResult);
            }
        }
    }

    protected  void handleScanResult(String barCode) {

        if (!StringUtils.isEmpty(barCode)) {
            mEditBianhao.setText(barCode);
            mEditBianhao.setSelection(barCode.length());
            mEditBianhao.requestFocus();
        }
    }

    protected void captureAction() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>()
        {
            @Override
            public void call(Boolean granted)
            {
                if (granted)
                {
                    if (TDevice.isCameraAvailable())
                    {
                        // 开启扫描
                        startActivityForResult(new Intent(AfterSaleAddressActivity.this, CaptureActivity.class), 1010);
                    }
                    else
                    {
                        showMessage(R.string.camera_is_not_available);
                    }
                }
                else
                {
                    showMessage("摄像头已被禁用 !");
                }
            }
        });
    }
}
