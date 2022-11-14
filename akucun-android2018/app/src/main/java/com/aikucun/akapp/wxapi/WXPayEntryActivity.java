package com.aikucun.akapp.wxapi;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.SCLog;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.pay_result_text)
    TextView resultText;

    private IWXAPI wxApi;

    @Override
    protected int getLayoutId()
    {
        return R.layout.pay_result;
    }
    private boolean isSendMsg = false;
    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText("支付结果");


        wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID);
        wxApi.handleIntent(getIntent(), this);
    }

    @Override
    public void initData()
    {
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        wxApi.handleIntent(intent, this);
    }

    @Override
    @OnClick({R.id.btn_right})
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.btn_right:
            {
                if (isSendMsg){
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS));
                }
                finish();
            }
            break;
        }
    }

    @Override
    public void onReq(BaseReq baseReq)
    {
        SCLog.logi("--> Weixin Pay Req ");
    }

    @Override
    public void onResp(BaseResp baseResp)
    {
        SCLog.logi("--> Weixin Pay Resp : " + baseResp.errCode);

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX)
        {
            switch (baseResp.errCode)
            {
                case BaseResp.ErrCode.ERR_OK:
                {
                    PayResp resp = (PayResp) baseResp;
                    SCLog.logi("--> Weixin Pay Success !");
                    resultText.setTextColor(Color.GREEN);
                    resultText.setText("订单支付成功 ！");
                   isSendMsg =true;
                }
                break;

                case BaseResp.ErrCode.ERR_USER_CANCEL:
                {
                    SCLog.debug("--> Weixin Pay Canceled ");
                    resultText.setTextColor(Color.RED);
                    resultText.setText("用户取消支付 ！");
                }
                break;

                default:
                {
                    SCLog.debug("--> Weixin Pay Failed  ");
                    resultText.setTextColor(Color.RED);
                    resultText.setText("订单支付失败 ！");
                }
                break;
            }

            //			AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //			builder.setTitle(R.string.app_tip);
            //			builder.setMessage(getString(R.string.pay_result_callback_msg, String
            // .valueOf(resp.errCode)));
            //			builder.show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().finishActivity(this);
        if (isSendMsg){
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS));
        }
    }
}