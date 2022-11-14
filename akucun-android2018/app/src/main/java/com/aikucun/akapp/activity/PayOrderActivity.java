package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.OrderPayCallback;
import com.aikucun.akapp.api.callback.PayTypesCallback;
import com.aikucun.akapp.api.entity.PayType;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.SystemApiManager;
import com.aikucun.akapp.api.response.OrderPayResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.UMFPayUtils;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umf.pay.sdk.UmfPay;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 订单支付界面
 * Created by jarry on 2017/6/9.
 */

public class PayOrderActivity extends BaseActivity {
    private static final int ALIPAY_PAY_FLAG = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.address_edit)
    View addrEditView;

    @BindView(R.id.address_text_name)
    TextView addrNameText;

    @BindView(R.id.address_text_mobile)
    TextView addrMobileText;

    @BindView(R.id.address_text_address)
    TextView addressText;

    @BindView(R.id.order_value_amount)
    TextView amountValueText;

    @BindView(R.id.order_value_dikou)
    TextView dikouValueText;

    @BindView(R.id.order_value_yunfei)
    TextView yunfeiValueText;

    @BindView(R.id.order_value_total)
    TextView totalValueText;

    @BindView(R.id.pay_layout)
    LinearLayout pay_layout;
    @BindView(R.id.sure_pay_btn)
    Button sure_pay_btn;

    private int orderAmount;

    private List<PayType> mPayTypes;

    private String orderIds;

    private IWXAPI wxApi;
    private PayType selectedPayType;
    private String paymentId;
    private boolean isFinished = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_payorder;
    }

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.pay_order);

        addrEditView.setVisibility(View.GONE);

        wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID);
        wxApi.registerApp(WXEntryActivity.WX_APP_ID);

    }

    @Override
    public void initData() {
        // FIXME: 2018/1/4
        //获取收货人信息
        String address = getIntent().getStringExtra(AddressUtils.orderAddress);
        String orderReceiptName = getIntent().getStringExtra(AddressUtils.orderReceiptName);
        String orderAddressPhone = getIntent().getStringExtra(AddressUtils.orderAddressPhone);
        addrNameText.setText(orderReceiptName);
        addrMobileText.setText(orderAddressPhone);
        addressText.setText(address);
        //        updateAccount();
        int amount = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, 0);
        int dikou = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, 0);
        int yunfei = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, 0);
        int total = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, 0);
        amountValueText.setText(StringUtils.getPriceString(amount));
        dikouValueText.setText("- " + StringUtils.getPriceString(dikou));
        yunfeiValueText.setText("+ " + StringUtils.getPriceString(yunfei));
        totalValueText.setText(StringUtils.getPriceString(total));
        orderAmount = total;

        orderIds = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_ORDER_IDS);
        if (orderIds != null && orderIds.length() > 0) {
            showProgress("");
            // requestUserAccount();
            requestPayTypes();
        } else {
            showMessage("订单号不合法");
        }
        sure_pay_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPayType != null) {
                    requestPayOrder(orderIds, selectedPayType.getPaytype(), true);
                    sure_pay_btn.setEnabled(false);
                } else
                    MToaster.showLong(PayOrderActivity.this, R.string.choose_pay_type, MToaster.IMG_INFO);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "请在我的订单中继续完成支付";
        builder.setTitle("订单未支付").setMessage(msg).setPositiveButton(R.string.sure, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isFinished = true;
                finish();
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void requestPayTypes() {
        SystemApiManager.getPayTypes(this, new PayTypesCallback() {
            @Override
            public void onApiSuccess(List<PayType> payTypes, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(payTypes, call, jsonResponse);

                cancelProgress();
                mPayTypes = payTypes;
                initPayType(mPayTypes);
            }
        });
    }

    private void requestPayOrder(String orderIds, int type, final boolean pay) {
        showProgress("");
        OrderApiManager.payOrder(this, orderIds, type, new OrderPayCallback() {
            @Override
            public void onApiSuccess(OrderPayResp orderPayResp, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(orderPayResp, call, jsonResponse);
                Object payInfo = orderPayResp.getPayinfo();
                doPay(payInfo, orderPayResp.getPaytype(), orderPayResp.getPayment_id());
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message))MToaster.showShort(PayOrderActivity.this,message,MToaster.IMG_INFO);
                sure_pay_btn.setEnabled(true);
            }
        });
    }


    private void doPay(Object payInfo, int paytype, String payment_id) {
        Log.e("支付类型：", paytype + "");
        String channel = UmfPay.CHANNEL_ALIPAY;
        if (paytype == AppConfig.PAY_TYPE_WXPAY) {
            doWeixinPay((JSONObject) payInfo);
        } else if (paytype == AppConfig.PAY_TYPE_ALIPAY) {
            doAliPay((String) payInfo);
        } else {
            JSONObject jsonObject = (JSONObject) payInfo;
            String merId = jsonObject.getString("merIds");
            String merCustId = jsonObject.getString("merCustId");
            String tradeNo = jsonObject.getString("tradeNo");
            String amount = jsonObject.getString("orderAmount");
            String sign = jsonObject.getString("sign");
            Log.e("支付信息：", jsonObject.toString());
            if (paytype == UMFPayUtils.ALI_PAY) {
                channel = UmfPay.CHANNEL_ALIPAY;
            } else if (paytype == UMFPayUtils.WEXIN_PAY) channel = UmfPay.CHANNEL_WECHAT;
            else if (paytype == UMFPayUtils.UNION_PAY) channel = UmfPay.CHANNEL_UPAY;
            paymentId = payment_id;
            UMFPayUtils.toPay(PayOrderActivity.this, channel, merId, merCustId, tradeNo, amount, sign);
        }
        sure_pay_btn.setEnabled(true);
        cancelProgress();
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


        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PayOrderActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = ALIPAY_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void initPayType(List<PayType> list) {
        pay_layout.removeAllViews();
        for (int i = 0, size = list.size(); i < size; i++) {
            View view = LayoutInflater.from(PayOrderActivity.this).inflate(R.layout.item_pay_layout, null);
            TextView payName = view.findViewById(R.id.pay_name_tv);
            TextView payContent = view.findViewById(R.id.pay_content_tv);
            ImageView iconIv = view.findViewById(R.id.icon_pay_logo);
            ImageView selectedIv = view.findViewById(R.id.icon_right_iv);
            final PayType payType = list.get(i);
            payName.setText(payType.getName());
            payContent.setText(payType.getContent());
            if (payType.getPaytype() == 11 || payType.getPaytype() == AppConfig.PAY_TYPE_ALIPAY) {
                //支付宝
                iconIv.setImageResource(R.drawable.logo_alipay);
            } else if (payType.getPaytype() == 12 || payType.getPaytype() == AppConfig.PAY_TYPE_WXPAY) {
                iconIv.setImageResource(R.drawable.logo_wxpay);//微信支付
            } else if (payType.getPaytype() == 13) {
                iconIv.setImageResource(R.drawable.icon_union_pay);//银联支付
            } else {
                iconIv.setImageResource(R.drawable.icon_unknow_pay_type);//APP未知支付
            }
            if (payType.getFlag() == 1) {
                selectedIv.setVisibility(View.VISIBLE);
            } else selectedIv.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelectedPayType(payType);
                }
            });
            pay_layout.addView(view);
        }
    }

    private void updateSelectedPayType(PayType payType) {
        selectedPayType = payType;
        for (int i = 0, size = mPayTypes.size(); i < size; i++) {
            if (mPayTypes.get(i).getPaytype() == payType.getPaytype()) {
                mPayTypes.get(i).setFlag(1);
            } else mPayTypes.get(i).setFlag(0);
        }
        initPayType(mPayTypes);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UmfPay.REQUEST_CODE) {
            showProgress("");
            getPayOrderResult();
        }
    }

    int requestCount = 0;

    /**
     * 查询联动支付返回状态
     */
    private void getPayOrderResult() {
        if (TextUtils.isEmpty(paymentId)){
            cancelProgress();
            return;
        }
        requestCount++;
        OrderApiManager.getPayResult(this, paymentId, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                if (jsonObject != null) {
                    int paystatu = jsonObject.getIntValue("paystatu");
                    // 0：待支付2：支付处理中3：支付成功4：支付失败
                    if (paystatu == 3) {
                        cancelProgress();
                        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_ORDER_STATUS));
                        MToaster.showShort(PayOrderActivity.this, R.string.pay_success, MToaster.IMG_INFO);
                        gotoOrderDetail();
                    } else if (paystatu == 4) {
                        cancelProgress();
                        MToaster.showShort(PayOrderActivity.this, R.string.pay_failed, MToaster.IMG_INFO);
                    } else if (paystatu == 2) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isFinished) return;
                                if (requestCount < 3) {
                                    getPayOrderResult();
                                } else {
                                    cancelProgress();
                                    MToaster.showShort(PayOrderActivity.this, R.string.pay_order_processing, MToaster.IMG_INFO);
                                    PayOrderActivity.this.finish();
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
                                }
                            }
                        }, 1000 * 2);
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (requestCount < 3) {
                            getPayOrderResult();
                        } else {
                            cancelProgress();
                            MToaster.showShort(PayOrderActivity.this, R.string.pay_order_processing, MToaster.IMG_INFO);
                            PayOrderActivity.this.finish();
                        }
                    }
                }, 1000 * 2);
            }
        });
    }

    /**
     * 跳转到订单详情页面
     */
    private void gotoOrderDetail(){
        List<String> orderIdsArr = JSONObject.parseArray(orderIds,String.class);
        Intent intent = new Intent(PayOrderActivity.this, OrderDetailActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 1);
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderIdsArr.get(0));
        startActivity(intent);
        PayOrderActivity.this.finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == ALIPAY_PAY_FLAG) {
                Map<String, String> result = (Map<String, String>) msg.obj;
                SCLog.logi("--> Alipay result : " + result.toString());

                final String resultStatus = result.get("resultStatus");
                final String memo = result.get("memo");

                String resultMsg = "";
                if (TextUtils.equals(resultStatus, "9000")) {
                    resultMsg = "订单支付成功 ！";
                } else {
                    cancelProgress();
                    resultMsg = (memo != null) ? memo : "订单支付失败 ！";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(PayOrderActivity.this);
                builder.setTitle(resultMsg).setMessage(null).setPositiveButton(R.string.sure, new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.equals(resultStatus, "9000")) {
                                    //支付成功刷新订单列表数据
                                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS));
                                    gotoOrderDetail();
                                }else{
                                    cancelProgress();
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

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS)){
            //微信支付回调查询
//            showProgress("");
//            getPayOrderResult();
            gotoOrderDetail();
        }
    }
}
