package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.MemberAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.MemberCallback;
import com.aikucun.akapp.api.callback.MemberOrderPayCallback;
import com.aikucun.akapp.api.entity.MemberInfo;
import com.aikucun.akapp.api.entity.PayType;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.api.response.MemberOrderPayResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.utils.UMFPayUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umf.pay.sdk.UmfPay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by micker on 2017/7/12.
 */

public class MemberActivity extends BaseActivity implements MemberAdapter.OnItemEventListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.btn_action)
    Button actionButton;

    @BindView(R.id.btn_code)
    Button btnCode;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    private MemberAdapter memberAdapter;

    private MemberInfo memberInfo;

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
            mTitleText.setText(userInfo.getViplevel() > 0 ? "会员状态" : "开通会员");
            actionButton.setText(userInfo.getViplevel() > 0 ? "会员续费" : "开通会员");
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        memberAdapter = new MemberAdapter(this);
        recyclerView.setAdapter(memberAdapter);

        memberAdapter.setOnItemEventListener(this);
        memberAdapter.addHeader(new MemberHeaderView());
        memberAdapter.addFooter(new MemberFooterView());

        getMemberInfo();

        wxApi = WXAPIFactory.createWXAPI(this, WXEntryActivity.WX_APP_ID);
        wxApi.registerApp(WXEntryActivity.WX_APP_ID);

        {
            // FIXME: 2018/1/4 
            btnCode.setVisibility((0 == userInfo.getViplevel()) ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_member;
    }


    @Override
    @OnClick({R.id.btn_action, R.id.btn_code})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_action: {

                if (this.memberInfo == null) {
                    MToaster.showShort(MemberActivity.this, "请选择会员套餐", MToaster.IMG_INFO);
                    return;
                }

                buyMemberInfo();
            }
            break;
            case R.id.btn_code: {
                showInviteCode();
            }
            break;
        }
    }

    private void showInviteCode() {

        final EditText remarkEt = new EditText(this);
        remarkEt.setHint(R.string.input_invitation_code);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.invitation_code).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TDevice.hideSoftKeyboard(remarkEt);
                String remark = remarkEt.getText().toString();
                if (!TextUtils.isEmpty(remark)) {
                    remark =remark.replaceAll("\\n","");
                    UsersApiManager.sendInviteCode(MemberActivity.this, remark, new JsonDataCallback() {
                        @Override
                        public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                            super.onSuccess(jsonObject, call, response);
                            if (jsonObject != null) {
                                int code = jsonObject.getIntValue("code");
                                if (code == 0) {
                                    UsersApiManager.userGetInfo(MemberActivity.this, null);
                                    MyDialogUtils.showV7NormalDialog(MemberActivity.this, R.string.alread_opend_vip_title, R.string.already_opened_vip, null);
                                } else {
                                    String message = jsonObject.getString("message");
                                    if (!TextUtils.isEmpty(message)) {
                                        MToaster.showShort(MemberActivity.this, message, MToaster.IMG_INFO);
                                    } else
                                        MToaster.showShort(MemberActivity.this, R.string.invite_code_invalid, MToaster.IMG_INFO);
                                }
                            }
                        }

                        @Override
                        public void onApiFailed(String message, int code) {
                            super.onApiFailed(message, code);
                            if (!TextUtils.isEmpty(message)) {
                                MToaster.showShort(MemberActivity.this, message, MToaster.IMG_INFO);
                            }
                        }
                    });
                } else {
                    MToaster.showShort(MemberActivity.this, R.string.invite_code_invalid, MToaster.IMG_INFO);
                }

            }
        });
        builder.show();
    }


    @Override
    public void onEvent(int event, MemberInfo memberInfo, Object object, int position) {
        this.memberInfo = memberInfo;
    }


    private void getMemberInfo() {

        UsersApiManager.getPurchased(this, new MemberCallback() {
            @Override
            public void onApiSuccess(List<MemberInfo> memberInfos, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(memberInfos, call, jsonResponse);

                memberAdapter.clear();
                memberAdapter.addAll(memberInfos);
                memberAdapter.notifyDataSetChanged();

                if (memberInfos.size() > 0) {
                    MemberActivity.this.memberInfo = memberInfos.get(0);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });

    }

    private void buyMemberInfo() {
        requestPayTypes();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("选择支付方式");
//        String[] items = new String[]{"支付宝", "微信"};
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (1 == which) {
//                    if (!wxApi.isWXAppInstalled()) {
//                        MToaster.showShort(MemberActivity.this, "微信未安装", MToaster.IMG_ALERT);
//                        return;
//                    }
//                }
//                payType = (which + 1) + "";
//                doBuy();
//            }
//        });
//        builder.show();
    }

    private void doBuy() {
        UsersApiManager.memberPurchased(this, this.memberInfo.getId(), this.payType, new MemberOrderPayCallback() {
            @Override
            public void onApiSuccess(MemberOrderPayResp memberOrderPayResp, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(memberOrderPayResp, call, jsonResponse);
                cancelProgress();

                if (memberOrderPayResp.getPaytype() == AppConfig.PAY_TYPE_WXPAY) {
                    JSONObject payInfo = (JSONObject) memberOrderPayResp.getPayinfo();
                    doWeixinPay(payInfo);
//                    finish();
                } else if (memberOrderPayResp.getPaytype() == AppConfig.PAY_TYPE_ALIPAY) {
                    String orderInfo = (String) memberOrderPayResp.getPayinfo();   // 订单信息
                    doAliPay(orderInfo);
                }
//                String orderInfo = (String) memberOrderPayResp.getPayinfo();
////                Log.e("返回支付信息：",orderInfo);
//                JSONObject payInfo = (JSONObject) memberOrderPayResp.getPayinfo();
//                doPay(payInfo, memberOrderPayResp.getPaytype());

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }


    private void doPay(JSONObject jsonObject, int paytype) {
        String merId = jsonObject.getString("merIds");
        String merCustId = jsonObject.getString("merCustId");
        String tradeNo = jsonObject.getString("tradeNo");
        String amount = jsonObject.getString("orderAmount");
        String sign = jsonObject.getString("sign");
        Log.e("支付信息：", jsonObject.toString());
        Log.e("支付类型：", paytype + "");
        String channel = UmfPay.CHANNEL_ALIPAY;
        if (paytype == AppConfig.PAY_TYPE_WXPAY) {
            doWeixinPay(jsonObject);
        } else if (paytype == AppConfig.PAY_TYPE_ALIPAY) {
            doAliPay(jsonObject.toJSONString());
        } else {
            if (paytype == UMFPayUtils.ALI_PAY) {
                channel = UmfPay.CHANNEL_ALIPAY;
            } else if (paytype == UMFPayUtils.WEXIN_PAY) channel = UmfPay.CHANNEL_WECHAT;
            else if (paytype == UMFPayUtils.UNION_PAY) channel = UmfPay.CHANNEL_UPAY;
            UMFPayUtils.toPay(MemberActivity.this, channel, merId, merCustId, tradeNo, amount, sign);
        }

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
                PayTask alipay = new PayTask(MemberActivity.this);
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
                    cancelProgress();
                    resultMsg = (memo != null) ? memo : "订单支付失败 ！";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberActivity.this);
                builder.setTitle(resultMsg).setMessage(null).setPositiveButton(R.string.sure, new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.equals(resultStatus, "9000")) {
                                    setResult(RESULT_OK);
                                    finish();
                                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
                                } else {
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

    public class MemberHeaderView implements RecyclerArrayAdapter.ItemView {

        ImageView userImage;
        TextView userNameText, userVipText, member_state_tv;
        RatingBar ratingBar;

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.layout_member_header, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            userNameText = headerView.findViewById(R.id.user_name_text);
            userVipText = headerView.findViewById(R.id.user_vip_text);
            member_state_tv = headerView.findViewById(R.id.member_state_tv);

            userImage = headerView.findViewById(R.id.user_image);
            ratingBar = headerView.findViewById(R.id.ratingbar_rr);

            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            if (userInfo != null) {

                userNameText.setText(userInfo.getName());
                // FIXME: 2018/1/4 
                if (userInfo.getViplevel() > 0) {
                    userVipText.setText("VIP" + userInfo.getViplevel());
                    userVipText.setBackground(getResources().getDrawable(R.drawable.b_vip_bg));
                } else {
                    userVipText.setText("VIP");
                    userVipText.setBackground(getResources().getDrawable(R.drawable.b_vip_disable_bg));
                }

                ratingBar.setRating(userInfo.getViplevel());

                member_state_tv.setText(userInfo.getViplevel() > 0 ? "会员有效期至" + userInfo.getVipendtime() : "您还不是会员");
                // FIXME: 2018/1/4
                if (!TextUtils.isEmpty(userInfo.getAvator())) {
                    Glide.with(MemberActivity.this).load(userInfo.getAvator()).diskCacheStrategy(DiskCacheStrategy
                            .ALL).into(userImage);
                }
            }

        }

    }

    public class MemberFooterView implements RecyclerArrayAdapter.ItemView {
        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.layout_member_footer, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            LinearLayout vip_info_layout = headerView.findViewById(R.id.vip_info_layout);
            if (vip_info_layout != null)
                vip_info_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConfig.BUNDLE_KEY_WEB_URL, "http://api.aikucun.com/teaminviterule.do?action=viplevel");
                        bundle.putString(AppConfig.BUNDLE_KEY_WEB_TITLE, getString(R.string.vip_level_rule));
                        ActivityUtils.startActivity(MemberActivity.this, WebViewActivity.class, bundle);
                    }
                });
        }

    }


    private void requestPayTypes() {
//        showProgress("");
//        SystemApiManager.getPayTypes(this, new PayTypesCallback() {
//            @Override
//            public void onApiSuccess(List<PayType> payTypes, Call call, ApiResponse jsonResponse) {
//                super.onApiSuccess(payTypes, call, jsonResponse);
//                cancelProgress();
//                showSelectPayTypeDialog(payTypes);
//            }
//
//            @Override
//            public void onApiFailed(String message, int code) {
//                super.onApiFailed(message, code);
//                if (!TextUtils.isEmpty(message))
//                    MToaster.showShort(MemberActivity.this, message, MToaster.IMG_INFO);
//            }
//        });

        List<PayType> list = new ArrayList<>();
        PayType payType1 = new PayType();
        payType1.setPaytype(AppConfig.PAY_TYPE_WXPAY);
        payType1.setName(getString(R.string.wechat_pay));
        payType1.setContent(getString(R.string.wechat_pay_info));
        list.add(payType1);
        PayType payType2 = new PayType();
        payType2.setPaytype(AppConfig.PAY_TYPE_ALIPAY);
        payType2.setName(getString(R.string.alipay));
        payType2.setContent(getString(R.string.alipay_pay_info));
        list.add(payType2);
        showSelectPayTypeDialog(list);
    }

    private void showSelectPayTypeDialog(final List<PayType> payTypes) {
        BottomDialog.showPayTypeDialog(this, payTypes, new BottomDialog.ISelectedPayTypeListener() {
            @Override
            public void onResult(PayType mPayType) {
                payType = mPayType.getPaytype() + "";
                doBuy();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UmfPay.REQUEST_CODE) {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
        }
    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS)) {
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
        }
    }
}
