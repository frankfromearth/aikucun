package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.RechargeAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.MemberOrderPayCallback;
import com.aikucun.akapp.api.callback.RechargeCallback;
import com.aikucun.akapp.api.entity.RechargeItem;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.api.response.MemberOrderPayResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by micker on 2017/7/12.
 */

public class RechargeActivity extends BaseActivity implements RechargeAdapter.OnItemEventListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.btn_action)
    Button actionButton;

    @BindView(R.id.btn_custom_action)
    Button customActionButton;

    @BindView(R.id.noticeTv)
    TextView noticeTv;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    private RechargeAdapter rechargeAdapter;

    private RechargeItem rechargeItem;

    private IWXAPI wxApi;

    private String payType;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initData() {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (userInfo != null) {
            mTitleText.setText(R.string.recharge);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.TRANSPARENT, DisplayUtils
                .dip2px(this, 10.f), 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        rechargeAdapter = new RechargeAdapter(this);
        recyclerView.setAdapter(rechargeAdapter);

        rechargeAdapter.setOnItemEventListener(this);

        getRechargeInfo();

        wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID);
        wxApi.registerApp(WXEntryActivity.WX_APP_ID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge;
    }


    @Override
    @OnClick({R.id.btn_action, R.id.btn_custom_action})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_action: {
                if (this.rechargeItem == null) {
                    MToaster.showShort(RechargeActivity.this, "请选择充值金额", MToaster.IMG_INFO);
                    return;
                }
                rechareInfo(this.rechargeItem);
            }
            break;
            case R.id.btn_custom_action: {

                showCustomRecharge();
            }
            break;
        }
    }

    private void updateAction() {
        if (this.rechargeItem != null) {
            actionButton.setText("充 值 ¥" + rechargeItem.getJine());
        }
    }

    private void showCustomRecharge() {
        final EditText remarkEt = new EditText(this);
        remarkEt.setHint("请输入充值金额（1～5000元）");
        remarkEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("自定义充值").setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TDevice.hideSoftKeyboard(remarkEt);
                String remark = remarkEt.getText().toString();

                if (StringUtils.isEmpty(remark)) {
                    RechargeActivity.this.showMessage("请输入有效的金额!");
                    return;
                }
                Pattern p = Pattern.compile("[0-9]*");
                Matcher m = p.matcher(remark);
                if (!m.matches()) {
                    RechargeActivity.this.showMessage("请输入有效的金额!");
                    return;
                }

                Integer ind = 0;
                try {
                    ind = Integer.parseInt(remark);
                } catch (Exception e) {
                    RechargeActivity.this.showMessage("请输入有效的金额!");
                    return;
                }
                if (ind < 1 || ind > 5000) {

                    RechargeActivity.this.showMessage("请输入有效的金额，1~5000元 !");
                    return;
                }
                {
                    RechargeItem item = new RechargeItem();
                    item.setPayjine(ind);
                    item.setJine(ind);
                    rechareInfo(item);
                    //                    onEvent(0,item,null,-1);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onEvent(int event, RechargeItem rechargeItem, Object object, int position) {
        this.rechargeItem = rechargeItem;
        rechargeAdapter.setSelectRechargeItem(rechargeItem);
        rechargeAdapter.notifyDataSetChanged();
        updateAction();
    }


    private void getRechargeInfo() {
        UsersApiManager.getDeltas(this, new RechargeCallback() {
            @Override
            public void onApiSuccess(List<RechargeItem> rechargeItems, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(rechargeItems, call, jsonResponse);

                rechargeAdapter.clear();
                rechargeAdapter.addAll(rechargeItems);

                if (rechargeItems.size() > 0) {
                    RechargeActivity.this.rechargeItem = rechargeItems.get(0);
                    rechargeAdapter.setSelectRechargeItem(rechargeItems.get(0));
                    updateAction();
                }
                rechargeAdapter.notifyDataSetChanged();
                if (!StringUtils.isEmpty(this.desc)) {
                    RechargeActivity.this.noticeTv.setVisibility(View.VISIBLE);
                    RechargeActivity.this.noticeTv.setText(this.desc.trim());
                } else {
                    RechargeActivity.this.noticeTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });

    }

    private void rechareInfo(final RechargeItem item) {
        if (item == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("需支付￥" + item.getPayjine());
        String[] items = new String[]{getString(R.string.alipay), getString(R.string.wechat_pay)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (1 == which) {
                    if (!wxApi.isWXAppInstalled()) {
                        MToaster.showShort(RechargeActivity.this, "微信未安装", MToaster.IMG_ALERT);
                        return;
                    }
                }
                payType = (which + 1) + "";
                doBuy(item);
            }
        });
        builder.show();
    }

    private void doBuy(RechargeItem item) {
        UsersApiManager.buyDeltas(this, item.getDeltaid(), this.payType, item.getPayjine(), new
                MemberOrderPayCallback() {
                    @Override
                    public void onApiSuccess(MemberOrderPayResp memberOrderPayResp, Call call,
                                             ApiResponse jsonResponse) {
                        super.onApiSuccess(memberOrderPayResp, call, jsonResponse);
                        cancelProgress();

                        if (memberOrderPayResp.getPaytype() == AppConfig.PAY_TYPE_WXPAY) {
                            JSONObject payInfo = (JSONObject) memberOrderPayResp.getPayinfo();
                            doWeixinPay(payInfo);
                            finish();
                        } else if (memberOrderPayResp.getPaytype() == AppConfig.PAY_TYPE_ALIPAY) {
                            String orderInfo = (String) memberOrderPayResp.getPayinfo();   // 订单信息
                            doAliPay(orderInfo);
                        }
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                    }
                });
    }

    private void doWeixinPay(JSONObject payInfo) {
        PayReq req = new PayReq();
        req.appId = payInfo.getString("appid");
        req.partnerId = payInfo.getString("partnerid");
        req.prepayId = payInfo.getString("prepayid");
        req.nonceStr = payInfo.getString("noncestr");
        req.timeStamp = payInfo.getString("timestamp");
        req.packageValue = payInfo.getString("package");
        req.sign = payInfo.getString("sign");
        wxApi.sendReq(req);
    }

    private void doAliPay(final String orderInfo) {
        showProgress("");

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Map<String, String> result = (Map<String, String>) msg.obj;
                SCLog.logi("--> Alipay result : " + result.toString());

                final String resultStatus = result.get("resultStatus");
                final String memo = result.get("memo");

                String resultMsg = "";
                if (TextUtils.equals(resultStatus, "9000")) {
                    resultMsg = "订单支付成功 ！";
                } else {
                    resultMsg = (memo != null) ? memo : "订单支付失败 ！";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(RechargeActivity.this);
                builder.setTitle(resultMsg).setMessage(null).setPositiveButton(R.string.sure, new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.equals(resultStatus, "9000")) {
                                    finish();
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                cancelProgress();
            }
        }
    };


}
