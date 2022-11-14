package com.aikucun.akapp.fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.AccountSettingActivity;
import com.aikucun.akapp.activity.AfterSaleActivity;
import com.aikucun.akapp.activity.AfterSaleListActivity;
import com.aikucun.akapp.activity.MyOrderActivity;
import com.aikucun.akapp.activity.ScanResultActivity;
import com.aikucun.akapp.activity.aftersale.ApplyAfterSaleActivity;
import com.aikucun.akapp.activity.bill.BillListActivity;
import com.aikucun.akapp.activity.invitation.InvitationFriendActivity1;
import com.aikucun.akapp.activity.reconciliation.CustomerReconActivity;
import com.aikucun.akapp.activity.team.MyTeamActivity1;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.BarcodeSearchCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.UserInfoCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.GuideUtils;
import com.aikucun.akapp.utils.JxChatUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.PullScrollView;
import com.aikucun.akapp.widget.RoundImageView;
import com.aikucun.akapp.widget.ScanResultPopWindow;
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.alibaba.fastjson.JSONObject;
import com.jxccp.im.callback.JXEventListner;
import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.manager.JXEventNotifier;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.ui.JXUiHelper;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * 我的 订单页面
 * Created by jarry on 2017/6/1.
 */

public class MyInfoFragment extends BaseFragment implements ScanResultPopWindow.OnWindowItemListener {
    @BindView(R.id.head_image)
    RoundImageView userImage;

    @BindView(R.id.user_name_text)
    TextView userNameText;
//今日、本月、上月、

    @BindView(R.id.user_weixin_text)
    TextView userWeixinText;

    @BindView(R.id.user_vip_text)
    TextView userVipText;


    @BindView(R.id.order_daizhifu_count)
    TextView daizhifuCount;

    @BindView(R.id.order_yizhifu_count)
    TextView yizhifuCount;

    @BindView(R.id.order_jianhuozhong_count)
    TextView jianhuozhongCount;

    @BindView(R.id.order_yifahuo_count)
    TextView yifahuoCount;

    @BindView(R.id.order_cancel_count)
    TextView canceledCount;

    @BindView(R.id.aftersale_quehuo_count)
    TextView quehuoCount;

    @BindView(R.id.aftersale_cancel_count)
    TextView afterCancelCount;

    @BindView(R.id.aftersale_tuihuo_count)
    TextView tuihuoCount;

    @BindView(R.id.aftersale_yituihuo_count)
    TextView yituihuoCount;

    @BindView(R.id.aftersale_yiloufa_count)
    TextView yiloufaCount;
    //未读消息数量
    @BindView(R.id.unread_count)
    TextView unread_count;

    @BindView(R.id.inviting_friends_layout)
    LinearLayout inviting_friends_layout;

    @BindView(R.id.order_daizhifu_item)
    LinearLayout order_daizhifu_item;
    @BindView(R.id.my_team_layout)
    LinearLayout my_team_layout;

    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.pull_scrllview)
    PullScrollView pull_scrllview;
    @BindView(R.id.update_text)
    TextView update_text;
    @BindView(R.id.sales_purchase_layout)
    LinearLayout sales_purchase_layout;
    @BindView(R.id.progress)
    RoundCornerProgressBar progress;

    private int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0;
    private int acount1 = 0, acount2 = 0, acount3 = 0, acount4 = 0, acount5 = 0;
    private ScanResultPopWindow popWindow;
    private View view;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        this.view = view;
    }

    @Override
    public void initData() {
        super.initData();
        //监听客服新消息
        JXImManager.Message.getInstance().registerEventListener(messageEventListner);

        int messageCount = AppContext.get("unread_msg_count", 0);
        Message message = MyHandler.obtainMessage();
        message.arg1 = messageCount;
        message.what = 1;
        MyHandler.sendMessage(message);
        pull_scrllview.setOnRefreshListener(new PullScrollView.onRefreshListener() {
            @Override
            public void refresh() {
                getUserInfo();
            }
        });

        progress.setProgress(80);
        progress.setProgressColor(R.color.color_accent);
    }

    /**
     * 新消息接收到时的监听器
     */
    JXEventListner messageEventListner = new JXEventListner() {
        @Override
        public void onEvent(final JXEventNotifier eventNotifier) {
//            JXMessage jxMessage = (JXMessage) eventNotifier.getData();
//            TextMessage textMessage = (TextMessage)jxMessage;
//            String content = textMessage.getContent();
//            MyNotificationUtils.showNotification(getActivity(),getString(R.string.customer_msg)+content);
            //NotificationUtils.showMessagePushNotify(getContext(), jxMessage);
            int messageCount = JXImManager.Conversation.getInstance().getAllUnreadCount();
            ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn.getClassName().equals("com.jxccp.ui.view.JXChatUIActivity")
                    || cn.getClassName()
                    .equals("com.jxccp.ui.view.JXInitActivity")) {
                messageCount = 0;
            }
            AppContext.set("unread_msg_count", messageCount);
            Message message = MyHandler.obtainMessage();
            message.arg1 = messageCount;
            message.what = 1;
            MyHandler.sendMessage(message);
        }
    };

    private android.os.Handler MyHandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int messageCount = msg.arg1;
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_JX_NEW_MSG_COUNT, messageCount));
            setNewMsgCount(messageCount);
            return false;
        }
    });

    @Override
    public void onResume() {
        super.onResume();
        requestOrderCount();
        updateUserData();
    }

    private void updateUserData() {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (null == userInfo)
            return;
        userNameText.setText(userInfo.getName());
        userWeixinText.setText("代购号： " + userInfo.getYonghubianhao());
        // FIXME: 2018/1/4
        if (userInfo.getViplevel() > 0) {
            userVipText.setText("VIP" + userInfo.getViplevel());
            userVipText.setBackground(getContext().getResources().getDrawable(R.drawable.b_vip_bg));
        } else {
            userVipText.setText("VIP");
            userVipText.setBackground(getContext().getResources().getDrawable(R.drawable.b_vip_disable_bg));
        }
        //设置销售信息
        String lastMonthAmount = StringUtils.getNum2Decimal((float) userInfo.getLastmonthstat() / 100 + "");
        String monthAmount = StringUtils.getNum2Decimal((float) userInfo.getMonthstat() / 100 + "");
        String dayAmount = StringUtils.getNum2Decimal((float) userInfo.getTodaystat() / 100 + "");
        String todayfee = StringUtils.getNum2Decimal((float) userInfo.getTodayfee() / 100 + "");
        String monthfee = StringUtils.getNum2Decimal((float) userInfo.getMonthfee() / 100 + "");
        String lastmonthfee = StringUtils.getNum2Decimal((float) userInfo.getLastmonthfee() / 100 + "");

        sales_purchase_layout.removeAllViews();
        //今日销售额
        sales_purchase_layout.addView(getSalesAndPurchaseView(dayAmount, getString(R.string.today_sales)));
        //本日代购费
        sales_purchase_layout.addView(getSalesAndPurchaseView(todayfee, getString(R.string.today_purchase)));
        //本月代购费
        sales_purchase_layout.addView(getSalesAndPurchaseView(monthfee, getString(R.string.month_purchase)));
        //本月销售额
        sales_purchase_layout.addView(getSalesAndPurchaseView(monthAmount, getString(R.string.month_sales)));
        //上月销售额
        sales_purchase_layout.addView(getSalesAndPurchaseView(lastMonthAmount, getString(R.string.last_month_sales)));
        //上月代购费
        sales_purchase_layout.addView(getSalesAndPurchaseView(lastmonthfee, getString(R.string.last_month_purchase)));
        if (userInfo.getPrcstatu() == 1) {
            inviting_friends_layout.setVisibility(View.VISIBLE);
        } else {
            inviting_friends_layout.setVisibility(View.GONE);
        }
        if (userInfo.getMembers() > 0) {
            my_team_layout.setVisibility(View.VISIBLE);
        } else my_team_layout.setVisibility(View.GONE);

        userImage.SetUrl(userInfo.getAvator(), true);
        if (userInfo.getPrcstatu() == 1) {
            //邀请码可用
            boolean showMyTeamGuideView = AppContext.get(GuideUtils.showGuidePicActivation, true);
            if (showMyTeamGuideView) {
                scrollToEnd(inviting_friends_layout, GuideUtils.showPrcActivation);
                return;
            }
        }

        if (userInfo.getWaitApproveMembers() > 0) {
            //有待批准的成员
            boolean showMyTeamGuideView = AppContext.get(GuideUtils.showGuideNewFriendApproval, true);
            if (showMyTeamGuideView) {
                scrollToEnd(inviting_friends_layout, GuideUtils.showNewFriendCanApproval);
                return;
            }
        }
        if (userInfo.getMembers() > 0) {
            //是否显示我的团队
            boolean showMyTeamGuideView = AppContext.get(GuideUtils.showGuideMyTeam, true);
            if (showMyTeamGuideView) {
                scrollToEnd(my_team_layout, GuideUtils.showMyTeam);
                return;
            }
        }

    }

    @Override
    @OnClick({R.id.user_account_layout, R.id.my_order_all, R.id.order_daizhifu_item, R.id
            .order_daifahuo_item, R.id.order_jianhuozhong_item, R.id.order_yifahuo_item, R.id.order_cancel_item, R.id
            .my_aftersale_all, R.id.aftersale_quehuo_item, R.id.aftersale_cancel_item, R.id
            .aftersale_tuihuo_item, R.id.aftersale_yituihuo_item, R.id.aftersale_yiloufa_item, R.id.my_team_layout, R.id.transaction_detail_layout, R.id.scan_code_sorting_layout, R.id.apply_after_sale_layout, R.id.sale_contact_customer_service_layout, R.id.inviting_friends_layout, R.id.customer_reconciliation_layout})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.user_account_layout:
                ActivityUtils.startActivity(getActivity(), AccountSettingActivity.class);
                break;
            case R.id.my_order_all: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, -1);
                startActivity(intent);
            }
            break;

            case R.id.order_daizhifu_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 0);
                startActivity(intent);
            }
            break;
            case R.id.order_daifahuo_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 1);
                startActivity(intent);
            }
            break;

            case R.id.order_yifahuo_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 2);
                startActivity(intent);
            }
            break;
            case R.id.order_jianhuozhong_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 3);
                startActivity(intent);
            }
            break;

            case R.id.order_cancel_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), MyOrderActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 4);
                startActivity(intent);
            }
            break;

            case R.id.my_aftersale_all: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, -1);
                startActivity(intent);

            }
            break;

            case R.id.aftersale_quehuo_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, 0);
                startActivity(intent);
            }
            break;

            case R.id.aftersale_cancel_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, 1);
                startActivity(intent);
            }
            break;

            case R.id.aftersale_tuihuo_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, 2);
                startActivity(intent);
            }
            break;

            case R.id.aftersale_yituihuo_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleActivity
                        .class);
                intent.putExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, 3);
                startActivity(intent);
            }
            break;

            case R.id.aftersale_yiloufa_item: {
                Intent intent = new Intent(MyInfoFragment.this.getActivity(), AfterSaleListActivity
                        .class);
                startActivity(intent);
            }
            break;
            case R.id.transaction_detail_layout:
                // TODO: 2018/1/7 交易明细
                ActivityUtils.startActivity(getActivity(), BillListActivity.class);
                break;
            case R.id.scan_code_sorting_layout:
                // TODO: 2018/1/7 扫码分拣·
                captureAction(1000);
                break;
            case R.id.apply_after_sale_layout:
                // TODO: 2018/1/7 申请售后
//                captureAction(1010);
                ActivityUtils.startActivity(getActivity(), ApplyAfterSaleActivity.class);
                // startChat("10004");
                break;
            case R.id.sale_contact_customer_service_layout:
                // TODO: 2018/1/7 联系客服
                startChat(JxChatUtils.jx_channel_num_customer);
                break;
            case R.id.inviting_friends_layout:
                // TODO: 2018/1/7 邀请好友
                ActivityUtils.startActivity(getActivity(), InvitationFriendActivity1.class);
                break;
            case R.id.customer_reconciliation_layout:
                // TODO: 2018/1/7 客户对账
                ActivityUtils.startActivity(getActivity(), CustomerReconActivity.class);
                break;
            case R.id.my_team_layout:
                // TODO: 2018/1/7 我的团队
                ActivityUtils.startActivity(getActivity(), MyTeamActivity1.class);
                break;
            default:
                break;
        }
    }

    private void updateOrderCount() {
        daizhifuCount.setText(String.valueOf(count1));
        yizhifuCount.setText(String.valueOf(count2));
        jianhuozhongCount.setText(String.valueOf(count3));
        yifahuoCount.setText(String.valueOf(count4));
        canceledCount.setText(String.valueOf(count5));
        if (count1 > 0) {
            daizhifuCount.setVisibility(View.VISIBLE);
            boolean bl = AppContext.get(GuideUtils.showGuidePaidOrder, true);
            if (bl) {
                //未支付提示
                GuideUtils.showGuideView(getActivity(), order_daizhifu_item, GuideUtils.showPaidOrder);
            }
        } else daizhifuCount.setVisibility(View.GONE);
        if (count2 > 0) {
            yizhifuCount.setVisibility(View.VISIBLE);
        } else yizhifuCount.setVisibility(View.GONE);
        if (count3 > 0) {
            jianhuozhongCount.setVisibility(View.VISIBLE);
        } else jianhuozhongCount.setVisibility(View.GONE);
        if (count4 > 0) {
            yifahuoCount.setVisibility(View.VISIBLE);
        } else yifahuoCount.setVisibility(View.GONE);
        if (count5 > 0) {
            canceledCount.setVisibility(View.VISIBLE);
        } else canceledCount.setVisibility(View.GONE);

        quehuoCount.setText(String.valueOf(acount1));
        afterCancelCount.setText(String.valueOf(acount2));
        tuihuoCount.setText(String.valueOf(acount3));
        yituihuoCount.setText(String.valueOf(acount4));
        yiloufaCount.setText(String.valueOf(acount5));

        if (acount1 > 0) {
            quehuoCount.setTextColor(Color.RED);
        }
        if (acount2 > 0) {
            afterCancelCount.setTextColor(Color.RED);
        }
        if (acount3 > 0) {
            tuihuoCount.setTextColor(Color.RED);
        }
        if (acount4 > 0) {
            yituihuoCount.setTextColor(Color.RED);
        }
        if (acount5 > 0) {
            yiloufaCount.setTextColor(Color.RED);
        }
    }

    private void dealWithJson(JSONObject jsonObject) {
        JSONObject statObj = jsonObject.getJSONObject("stat");
        count1 = statObj.getIntValue("0");
        count2 = statObj.getIntValue("1");
        count3 = statObj.getIntValue("3");
        count4 = statObj.getIntValue("2");
        count5 = statObj.getIntValue("4");

        JSONObject aftersale = jsonObject.getJSONObject("aftersalestat");
        acount1 = aftersale.getIntValue("0");
        acount2 = aftersale.getIntValue("1");
        acount3 = aftersale.getIntValue("2");
        acount4 = aftersale.getIntValue("3");
//        acount5 = aftersale.getIntValue("4");
        acount5 = aftersale.getIntValue("5");

        updateOrderCount();
    }

    private void requestOrderCount() {
        OrderApiManager.orderCount(getActivity(), new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                dealWithJson(jsonObject);
            }

            @Override
            public void onCacheSuccess(JSONObject jsonObject, Call call) {
                super.onCacheSuccess(jsonObject, call);
                dealWithJson(jsonObject);
            }
        });
    }

    //
//    private void checkMessageList() {
//        KefuApiManager.checkMsgs(getActivity(), new JsonDataCallback() {
//            @Override
//            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
//                super.onApiSuccess(jsonObject, call, jsonResponse);
//                int cnt = jsonObject.getInteger("cnt");
//                if (cnt > 0) {
//                    user_contact_value.setText("您有" + cnt + "条未读消息");
//                    user_contact_value.setVisibility(View.VISIBLE);
//                } else {
//                    user_contact_value.setVisibility(View.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onApiFailed(String message, int code) {
//                super.onApiFailed(message, code);
//            }
//        });
//
//    }
//
    private void setNewMsgCount(int messageCount) {
        if (messageCount > 99) {
            unread_count.setText("99+");
            unread_count.setVisibility(View.VISIBLE);
        } else if (messageCount > 0 && messageCount <= 99) {
            unread_count.setText(messageCount + "");
            unread_count.setVisibility(View.VISIBLE);
        } else {
            unread_count.setVisibility(View.GONE);
        }
    }

    private void startChat(String channelNumber) {
        //设置消息为已读
        List<JXConversation> arr = JXImManager.Conversation.getInstance().getAllConversations();
        for (int i = 0, size = arr.size(); i < size; i++) {
            arr.get(i).setAsRead();
        }
        AppContext.set("unread_msg_count", 0);
        setNewMsgCount(0);
        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_JX_NEW_MSG_COUNT, 0));
        JXUiHelper.getInstance().setJxCommodity(null);
        JxChatUtils.startChat(getActivity(), channelNumber);
    }


    protected void captureAction(final int requestCode) {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (TDevice.isCameraAvailable()) {
                        // 开启扫描
                        startActivityForResult(new Intent(getActivity(), CaptureActivity
                                .class), requestCode);
                    } else {
                        MToaster.showShort(getActivity(), R.string.camera_is_not_available, MToaster.IMG_INFO);
                    }
                } else {
                    MyDialogUtils.showPermissionsDialog(getActivity(), getString(R.string.camera));
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                if (!TextUtils.isEmpty(scanResult)) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("barcode", scanResult);
                    ActivityUtils.startActivity(getActivity(), ScanResultActivity.class, bundle);
                }
            }
        } else if (requestCode == 1000) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                if (!TextUtils.isEmpty(scanResult)) {
                    requestBarcodeSearch(scanResult);
                }
            }
        }
    }


    private void requestBarcodeSearch(final String barcode) {

        ProductApiManager.barcodeSearch(getActivity(), barcode, "", new BarcodeSearchCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);

                if (cartProducts.size() == 0) {
                    new AlertDialog.Builder(getActivity()).
                            setTitle(barcode).
                            setMessage("请确认商品条码是否正确！\n您可以尝试扫一下另一个条码 ！\n").setPositiveButton(R.string.sure,
                            null).show();
                } else {
                    CartProduct product = null;
                    for (CartProduct p : cartProducts) {
                        if (p.scanstatu == 0) {
                            product = p;
                            break;
                        }
                    }
                    if (product != null) {
                        popWindow = new ScanResultPopWindow(getActivity());
                        popWindow.setOnWindowItemListener(MyInfoFragment.this);
                        popWindow.setCartProduct(product);
                        popWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    } else {
                        new AlertDialog.Builder(getActivity()).
                                setTitle(barcode).
                                setMessage("该商品已拣货，请勿重复操作").setPositiveButton(R.string.sure, null).show();
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }


    @Override
    public void onEvent(ScanResultPopWindow popWindow, int event) {

        if (0 == event) {
            CartProduct product = popWindow.getCartProduct();
            ProductApiManager.updateScanProduct(getActivity(), product.getCartproductid(), "", product.getSku().getBarcode(), 1, new
                    ApiBaseCallback() {
                        @Override
                        public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                            super.onApiSuccess(o, call, jsonResponse);
                        }
                    });
            MToaster.showShort(getActivity(), "操作成功!", MToaster.IMG_INFO);
        }
    }

    private void scrollToEnd(final View inView, final int type) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GuideUtils.showGuideView(getActivity(), inView, type);
                    }
                }, 100);
            }
        });

    }

    private View getSalesAndPurchaseView(String amount, String typeTv) {
        View salesAndPurchaseView = LayoutInflater.from(getActivity()).inflate(R.layout.item_sales_purchase_layout, null);
        LinearLayout linearLayout = salesAndPurchaseView.findViewById(R.id.parent_layout);
        float w = TDevice.getScreenWidth();
        int tempw = (int) w / 3;
        ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
        layoutParams.width = tempw;
        linearLayout.setLayoutParams(layoutParams);
        TextView amountTv = salesAndPurchaseView.findViewById(R.id.amount_tv);
        TextView type_text = salesAndPurchaseView.findViewById(R.id.type_text);
        amountTv.setText("¥" + amount);
        type_text.setText(typeTv);
        return salesAndPurchaseView;
    }


    private void getUserInfo() {
        UsersApiManager.userGetInfo(getActivity(), new UserInfoCallback() {
            @Override
            public void onApiSuccess(UserInfo userInfo, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(userInfo, call, jsonResponse);
                AppContext.getInstance().setUserInfo(userInfo);
                updateUserData();
                update_text.setText(R.string.updated_user_info);
                stopRefreshScrollview();
            }

            @Override
            public void onCacheSuccess(UserInfo userInfo, Call call) {
                super.onCacheSuccess(userInfo, call);
                AppContext.getInstance().setUserInfo(userInfo);
                updateUserData();
                update_text.setText(R.string.updated_user_info);
                stopRefreshScrollview();
            }

            @Override
            public void onApiFailed(String message, int code) {
                update_text.setText(R.string.updated_user_info);
                stopRefreshScrollview();
            }
        });
    }

    private void stopRefreshScrollview() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pull_scrllview.stopRefresh();
                update_text.setText(R.string.updating_user_info);
            }
        }, 500);
    }

}
