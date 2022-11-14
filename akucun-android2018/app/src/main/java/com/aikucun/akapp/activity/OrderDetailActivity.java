package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.logistics.QueryLogisticsActivity;
import com.aikucun.akapp.adapter.OrderProductAdapter;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.BarcodeSearchCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.DeliverApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.response.OrderDetailResp;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.ScanResultPopWindow;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.Call;

import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ADORDER;
import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ORDER;

/**
 * Created by jarry on 2017/6/11.
 */

public class OrderDetailActivity extends OrderBaseActivity implements OrderProductAdapter
        .OnItemEventListener, ScanResultPopWindow.OnWindowItemListener {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.total_text)
    TextView totalText;

    @BindView(R.id.amount_text)
    TextView amountText;

    @BindView(R.id.pay_btn)
    TextView payButton;

    @BindView(R.id.btn_right)
    TextView btn_right;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;


    @BindView(R.id.btn_tip)
    ImageButton btnTip;

    @BindView(R.id.rl_tip)
    RelativeLayout rlTip;


    private String wuliuDanHao;

    ScanResultPopWindow popWindow;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);

        payButton.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        //        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        //        TextView textView =  emptyView.findViewById(R.id.empty_text);
        //        textView.setText("暂无相关订单");
        //        recyclerView.setEmptyView(emptyView);

        productAdapter = new OrderProductAdapter(this);
        productAdapter.setOnItemEventListener(this);

        recyclerView.setAdapter(productAdapter);

        productAdapter.addHeader(new OrderHeaderView());

    }

    @Override
    public void initData() {
        productAdapter.clear();
        orderId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_ORDER_ID);
        type = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 0);

        if (type == TYPE_ORDER) {
            mTitleText.setText(R.string.order_detail);
            getOrderDetail(orderId);

        } else if (type == TYPE_ADORDER) {
            mTitleText.setText(R.string.invoice_detail);
            getDeliverDetail(orderId);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_detail;
    }

    @Override
    @OnClick({R.id.pay_btn, R.id.btn_tip, R.id.btn_right})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pay_btn: {
                String orderIds = "[\"" + order.getOrderid() + "\"]";
                Intent intent = new Intent(this, PayOrderActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_IDS, orderIds);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, order.getShangpinjine());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, order.getDikoujine());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, order.getYunfei());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, order.getZongjine());
                //设置收货地址
                intent.putExtra(AddressUtils.orderReceiptName, logistics.getShouhuoren());
                intent.putExtra(AddressUtils.orderAddressPhone, logistics.getLianxidianhua());
                intent.putExtra(AddressUtils.orderAddress, logistics.getShouhuodizhi());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
            }
            break;

            case R.id.btn_tip: {
                AppContext.set("didShowOrderTip", true);
                rlTip.setVisibility(View.GONE);
            }
            break;

            case R.id.btn_right: {
                if (order != null) {
                    goToDuiZhangDan(order.getDownloadurl());
                } else if (mAdOrder != null) {
                    showDuiZhangDanDialog();
                }
            }
            break;
            default:
                break;
        }
    }


    private void showPreview(String downloadURL) {

        if (!StringUtils.isEmpty(downloadURL)) {
            Intent intent = new Intent(this, StateMentActivity.class);
            intent.putExtra("url", downloadURL);
            startActivity(intent);
        }
    }

    private void showDuiZhangDanDialog() {
        final String[] items = new String[]{getString(R.string.update_the_bill), getString(R.string.download_the_bill)};
        if (items.length == 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    onApplyDoc(mAdOrder.getAdorderid());
                } else {
                    goToDuiZhangDan(mAdOrder.getDownloadurl());
                }
            }
        });
        builder.show();
    }

    private void onApplyDoc(String adorderId) {
        DeliverApiManager.applyDeliverDetail(this, adorderId, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);

                String msg = "对账单申请已提交，生成对账单过程需要几分钟时间，请稍候在发货单详情中查看";
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
                builder.setTitle("申请已提交").setMessage(msg).setPositiveButton(R.string.sure, null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

    }

    private void goToDuiZhangDan(String downloadURL) {

        if (!StringUtils.isEmpty(downloadURL)) {
            Intent intent = new Intent(this, StateMentActivity.class);
            intent.putExtra("url", downloadURL);
            startActivity(intent);
        } else {
            MToaster.showShort(this, "对账单还未生成，请稍候再试!", MToaster.IMG_INFO);
        }
    }

    private String getDownloadURL() {
        String downloadURL = null;

        //        if (type == TYPE_ORDER) {
        //            if (order != null && !StringUtils.isEmpty(order.getDownloadurl())) {
        //                downloadURL = order.getDownloadurl();
        //            }
        //        } else
        //非发货单取消对账单功能
        if (type == TYPE_ADORDER) {
            if (mAdOrder != null && !StringUtils.isEmpty(mAdOrder.getDownloadurl())) {
                downloadURL = mAdOrder.getDownloadurl();
            }
        }

        return downloadURL;
    }


    protected void doUpdateOrder(OrderDetailResp orderDetailResp) {
        super.doUpdateOrder(orderDetailResp);

        payButton.setVisibility((order.getStatus() == 0) ? View.VISIBLE : View.GONE);

        //
        totalText.setText("合计： " + order.getShangpinjianshu() + "件");
        amountText.setText("结算金额： " + StringUtils.getPriceString(order.getZongjine()));


        {
            boolean didShowTip = AppContext.get("didShowOrderTip", false);
            //            rlTip.setVisibility(didShowTip ? View.GONE : View.VISIBLE);
        }
        //对帐单
        {
            if (2 == order.getStatus()) {
                btn_right.setText("对帐单");
                btn_right.setVisibility(!StringUtils.isEmpty(getDownloadURL()) ? View.VISIBLE :
                        View.GONE);
            }
        }
    }


    protected void updateOrder(AdOrder adOrder, Boolean toSave) {

        super.updateOrder(adOrder, toSave);

        totalText.setTextColor(Color.RED);
        totalText.setText("缺货：-" + adOrder.getLacknum() + " 件");
        if (adOrder.getStatu() < 4) {
            amountText.setText("待发件数： " + adOrder.daifahuoNum() + " 件");
        } else {
            amountText.setText("实发件数： " + adOrder.getPnum() + " 件");
        }

        //对帐单
        {
            btn_right.setVisibility(!StringUtils.isEmpty(getDownloadURL()) ? View.VISIBLE : View
                    .GONE);
        }
    }

    private void requestBarcodeSearch(final String barcode) {

        ProductApiManager.barcodeSearch(this, barcode, this.mAdOrder.getLiveid(), new BarcodeSearchCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);

                if (cartProducts.size() == 0) {
                    new AlertDialog.Builder(OrderDetailActivity.this).
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
                        popWindow = new ScanResultPopWindow(OrderDetailActivity.this);
                        popWindow.setOnWindowItemListener(OrderDetailActivity.this);
                        popWindow.setCartProduct(product);
                        popWindow.showAtLocation(OrderDetailActivity.this.getRootView(), Gravity.BOTTOM, 0, 0);
                    } else {
                        new AlertDialog.Builder(OrderDetailActivity.this).
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
    protected void handleScanResult(String resultBarCode) {
        SCLog.debug("scan barcode result is = " + resultBarCode);

        //        barCode = "OUT1TPT01APUR";
        if (StringUtils.isEmpty(resultBarCode)) {
            return;
        }

        requestBarcodeSearch(resultBarCode);
    }


    private void searchAction() {

        Intent intent = new Intent(OrderDetailActivity.this, OrderSearchActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
        intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, type);
        if (type == TYPE_ORDER) {
            intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DETAIL_RESP, orderDetailResp);
        } else {
            intent.putExtra(AppConfig.BUNDLE_KEY_ADORDER_DETAIL_RESP, mAdOrder);
        }
        startActivityForResult(intent, SEARCH_PRODUCT_REQUEST);
    }


    private void gotoWuliu() {
        if (mAdOrder != null) {
            if (mAdOrder.getWuliuhao().indexOf(",") > -1) {
                String[] strings = mAdOrder.getWuliuhao().split(",");
                ArrayList<String> strings1 = new ArrayList<>();
                for (int i = 0, size = strings.length; i < size; i++) {
                    strings1.add(strings[i]);
                }
                strings1.add(0, getResources().getString(R.string.choose_logistics_num));
                showDialog(strings1, 1);
            } else {

                gotoWuliuByAdOrder(mAdOrder.getWuliuhao());
            }

//            Intent intent = new Intent(OrderDetailActivity.this, WuLiuDetailActivity.class);
//            intent.putExtra("title", "快递查询");
//            intent.putExtra("wuliu", mAdOrder.getWuliuhao());
//            startActivity(intent);
        } else if (order != null) {
            if (logistics != null && logistics.getWuliuhao().indexOf(",") > -1) {
                String[] strings = logistics.getWuliuhao().split(",");
                ArrayList<String> strings1 = new ArrayList<>();
                for (int i = 0, size = strings.length; i < size; i++) {
                    strings1.add(strings[i]);
                }
                strings1.add(0, getResources().getString(R.string.choose_logistics_num));
                showDialog(strings1, 2);
            } else {
                gotoWuliuByOrderModel(logistics.getWuliuhao());
            }

        }
    }

    private void showDialog(ArrayList<String> arrayList, final int type) {

        BottomDialog.showIndefiniteDialog(OrderDetailActivity.this, arrayList, true, new BottomDialog.IClickListener() {
            @Override
            public void onClick(String content, int index) {
                if (type == 1) {
                    gotoWuliuByAdOrder(content);
                } else gotoWuliuByOrderModel(content);
            }
        });
    }

    private void gotoWuliuByAdOrder(String deliver_no) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("order_info", mAdOrder);
        bundle.putString("deliver_no", deliver_no);
        bundle.putString("type", "adorder");
        ActivityUtils.startActivity(OrderDetailActivity.this, QueryLogisticsActivity.class
                , bundle);
    }

    private void gotoWuliuByOrderModel(String deliver_no) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("order_info", order);
        bundle.putSerializable("logistics", logistics);
        bundle.putString("deliver_no", deliver_no);
        bundle.putString("type", "order");
        ActivityUtils.startActivity(OrderDetailActivity.this, QueryLogisticsActivity.class
                , bundle);
    }

    private void gotoEditOrderAddress() {
        if (mAdOrder != null) {
            Intent intent = new Intent(this, AddressEditActivity.class);
            intent.putExtra("adOrder", mAdOrder);
            startActivityForResult(intent, 12);
            ;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (12 == requestCode) {

            getDeliverDetail(orderId);
        }

    }

    @Override
    public void onEvent(ScanResultPopWindow popWindow, int event) {

        if (0 == event) {
            CartProduct product = popWindow.getCartProduct();
            ProductApiManager.updateScanProduct(this, product.getCartproductid(),
                    mAdOrder.getAdorderid(), product.getSku().getBarcode(), 1, new
                            ApiBaseCallback() {
                                @Override
                                public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(o, call, jsonResponse);
                                }
                            });
            productAdapter.notifyDataSetChanged();
            MToaster.showShort(this, "操作成功!", MToaster.IMG_INFO);
        }
    }

    /**
     * Header View
     */
    public class OrderHeaderView implements RecyclerArrayAdapter.ItemView, View.OnClickListener {
        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.header_order_detail, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            TextView orderNoText =  headerView.findViewById(R.id.order_no_text);
            TextView orderTimeText =  headerView.findViewById(R.id.order_time_text);
            TextView orderStatusText =  headerView.findViewById(R.id.order_status_text);
            //
            ImageView pinpaiImage = (ImageView) headerView.findViewById(R.id.pinpai_icon);
            TextView pinpaiNameText =  headerView.findViewById(R.id.pinpai_name_text);

            TextView scanView =  headerView.findViewById(R.id.tv_scan);
            ImageView searchImageView = (ImageView) headerView.findViewById(R.id.iv_search);

            TextView orderCancelBtn =  headerView.findViewById(R.id.order_cancel_btn);
            orderCancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCancelOrder(order.getOrderid());
                }
            });

            TextView orderAmountTitle =  headerView.findViewById(R.id.order_title_amount);
            TextView orderAmountText =  headerView.findViewById(R.id.order_value_amount);
            TextView orderDikouText =  headerView.findViewById(R.id.order_value_dikou);
            TextView orderYunfeiText =  headerView.findViewById(R.id.order_value_yunfei);

            View wuliuView = headerView.findViewById(R.id.order_wuliu);
            wuliuView.setOnClickListener(this);
            TextView wuliuText =  headerView.findViewById(R.id.order_wuliu_text);

            RelativeLayout ll_countdown = (RelativeLayout) headerView.findViewById(R.id
                    .ll_countdown);
            final CountdownView coutdownView = (CountdownView) headerView.findViewById(R.id.countdown);

            View addressEdit = headerView.findViewById(R.id.address_edit);
            addressEdit.setVisibility(View.GONE);
            TextView addrNameText =  headerView.findViewById(R.id.address_text_name);
            TextView addrMobileText =  headerView.findViewById(R.id.address_text_mobile);
            TextView addressText =  headerView.findViewById(R.id.address_text_address);

            TextView order_wuliu_text_copy =  headerView.findViewById(R.id
                    .order_wuliu_text_copy);
            order_wuliu_text_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoWuliu();
//                    if (!StringUtils.isEmpty(wuliuDanHao))
//                    {
//                        ClipboardManager cm = (ClipboardManager) OrderDetailActivity.this
//                                .getSystemService(Context.CLIPBOARD_SERVICE);
//                        cm.setPrimaryClip(ClipData.newPlainText("wuliuhao", wuliuDanHao));
//                        MToaster.showShort(OrderDetailActivity.this, "已经将物流单号拷贝到剪切板\n" +
//                                wuliuDanHao, MToaster.IMG_INFO);
//                    }
                }
            });

            TextView modifyAddress =  headerView.findViewById(R.id.address_modify_text);
            modifyAddress.setOnClickListener(this);

            scanView.setOnClickListener(this);
            searchImageView.setOnClickListener(this);

            //售后
            TextView timeTips =  headerView.findViewById(R.id.time_tips);
            final TextView rl_outsaletime_status_tv =  headerView.findViewById(R.id
                    .rl_outsaletime_status_tv);
            final TextView time_tips = headerView.findViewById(R.id.time_tips);

            if (order != null) {

                timeTips.setText("订单支付剩余时间：");
                ll_countdown.setVisibility(View.GONE);
                if (order.getStatus() == 0) {
                    orderCancelBtn.setVisibility(View.VISIBLE);
                    ll_countdown.setVisibility(View.VISIBLE);
                    Date date = new Date();
                    coutdownView.start(order.getOvertimeshuzi() * 1000 - date.getTime());
                    coutdownView.restart();
                    coutdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
                        @Override
                        public void onEnd(CountdownView cv) {
                            // TODO: 2018/2/7 倒计时结束
                            rl_outsaletime_status_tv.setText(R.string.unpay_cancel_order);
                            rl_outsaletime_status_tv.setVisibility(View.VISIBLE);
                            coutdownView.setVisibility(View.GONE);
                            time_tips.setVisibility(View.GONE);
                            payButton.setVisibility(View.GONE);
                        }
                    });
                } else if (order.getStatus() == 2) {
                    wuliuView.setVisibility(View.VISIBLE);
                } else {
                    orderCancelBtn.setVisibility(View.GONE);
                }
                orderNoText.setText("订单编号：" + order.displayOrderId());
                orderTimeText.setText("下单时间：" + order.getDingdanshijian());
                orderStatusText.setText(order.orderStatusText());
                Glide.with(OrderDetailActivity.this).load(order.getPinpaiURL()).diskCacheStrategy
                        (DiskCacheStrategy.ALL).into(pinpaiImage);
                pinpaiNameText.setText(order.getPinpai());

                orderAmountTitle.setText("商品金额（ " + order.getShangpinjianshu() + "件 ）");
                orderAmountText.setText(StringUtils.getPriceString(order.getShangpinjine()));
                orderDikouText.setText("- " + StringUtils.getPriceString(order.getDikoujine()));
                orderYunfeiText.setText("+ " + StringUtils.getPriceString(order.getYunfei()));

                if (logistics != null) {
//                    wuliuText.setText(logistics.getWuliugongsi() + "\n单号：" + logistics
//                            .getWuliuhao());

                    wuliuText.setText(logistics.displayWuliuInfo());

                    wuliuDanHao = logistics.getWuliuhao();
                    addrNameText.setText(logistics.getShouhuoren());
                    addrMobileText.setText(logistics.getLianxidianhua());
                    addressText.setText(logistics.getShouhuodizhi());
                }
                if (order.getIsvirtual() == 1) {
                    wuliuView.setVisibility(View.GONE);
                }
            } else if (mAdOrder != null) {
                orderNoText.setText("发货单号：" + mAdOrder.getOdorderstr());

                orderAmountTitle.setText("商品金额（ " + mAdOrder.getNum() + "件 ）");
                orderAmountText.setText(StringUtils.getPriceString(mAdOrder.getShangpinjine()));
                orderDikouText.setText("- " + StringUtils.getPriceString(mAdOrder.getDikoujine()));
                orderYunfeiText.setText("+ " + StringUtils.getPriceString(mAdOrder.getYunfeijine
                        ()));

                Glide.with(OrderDetailActivity.this).load(mAdOrder.getPinpaiURL())
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(pinpaiImage);
                pinpaiNameText.setText(mAdOrder.getPinpai());

                addrNameText.setText(mAdOrder.getShouhuoren());
                addrMobileText.setText(mAdOrder.getLianxidianhua());
                addressText.setText(mAdOrder.getShouhuodizhi());

                View dingdanView = headerView.findViewById(R.id.order_dingdanjine);
                TextView dingdanValue =  headerView.findViewById(R.id
                        .order_value_dingdan);
                View tuikuanView = headerView.findViewById(R.id.order_tuikuanjine);
                TextView tuikuanValue =  headerView.findViewById(R.id
                        .order_value_tuikuanjine);
                TextView tuikuanTitle =  headerView.findViewById(R.id
                        .order_title_tuikuanjine);


                dingdanView.setVisibility(View.VISIBLE);
                dingdanValue.setText(StringUtils.getPriceString(mAdOrder.getZongjine()));

                if (mAdOrder.getTuikuanjine() > 0) {
                    tuikuanView.setVisibility(View.VISIBLE);
                    tuikuanValue.setText("- " + StringUtils.getPriceString(mAdOrder
                            .getTuikuanjine()));

                    StringBuilder title = new StringBuilder("退款金额（ ");
                    if (mAdOrder.getLacknum() > 0) {
                        title.append("缺货" + mAdOrder.getLacknum() + "件 ");
                    }
                    if (mAdOrder.getCancelnum() > 0) {
                        title.append(R.string.cancel + mAdOrder.getCancelnum() + "件 ");
                    }
                    title.append("）");
                    tuikuanTitle.setText(title);
                }

                if (mAdOrder.getStatu() == 4) {
                    orderTimeText.setText("发货时间：" + mAdOrder.getOptime());
                    wuliuView.setVisibility(View.VISIBLE);
                    if (mAdOrder.getIsvirtual() == 0)
                        scanView.setVisibility(View.VISIBLE);
                    else scanView.setVisibility(View.GONE);

                    //                    wuliuText.setText(mAdOrder.getWuliugongsi() + "  单号：" +
                    // mAdOrder.getWuliuhao());
                    wuliuText.setText(mAdOrder.displayWuliuInfo());
                    wuliuDanHao = mAdOrder.getWuliuhao();

                    {
                        ll_countdown.setVisibility(View.VISIBLE);
                        Date date = new Date();
                        long diff = mAdOrder.getAftersaletimenum() - date.getTime() / 1000;
                        if (diff > 86400) {
                            coutdownView.setVisibility(View.GONE);
                            rl_outsaletime_status_tv.setVisibility(View.VISIBLE);
                            timeTips.setText("售后截止时间： ");
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = formatter.format(new Date(mAdOrder
                                    .getAftersaletimenum() * 1000));
                            rl_outsaletime_status_tv.setText(dateString);

                        } else if (diff > 0) {
                            coutdownView.setVisibility(View.VISIBLE);
                            rl_outsaletime_status_tv.setVisibility(View.GONE);
                            timeTips.setText("售后剩余时间：");
                            coutdownView.start(diff * 1000);
                            coutdownView.restart();
//                            coutdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
//                                @Override
//                                public void onEnd(CountdownView cv) {
//                                    rl_outsaletime_status_tv.setText(R.string.unpay_cancel_order);
//                                    rl_outsaletime_status_tv.setVisibility(View.
// VISIBLE);
//                                    coutdownView.setVisibility(View.GONE);
//                                    time_tips.setVisibility(View.GONE);
//                                    payButton.setVisibility(View.GONE);
//                                }
//                            });
                        } else {
                            timeTips.setText("售后已结束");
                            coutdownView.setVisibility(View.GONE);
                            rl_outsaletime_status_tv.setVisibility(View.GONE);
                        }
                    }
                } else {
                    ll_countdown.setVisibility(View.GONE);
                    String timeText = "下单时间：" + mAdOrder.getCreatetime();
                    if ((1 == mAdOrder.getStatu() || 2 == mAdOrder.getStatu()) && !TextUtils
                            .isEmpty(mAdOrder.getExpectdelivertime())) {
                        timeText = "发货时间：" + mAdOrder.getExpectdelivertime();
                    }
                    orderTimeText.setText(timeText);

                    if (0 == mAdOrder.getStatu()) {
                        orderStatusText.setText("待发货");
                        modifyAddress.setVisibility(View.VISIBLE);
                        scanView.setVisibility(View.GONE);
                    } else {
                        orderStatusText.setText("拣货中");
                        if (mAdOrder.getIsvirtual() == 0)
                            scanView.setVisibility(View.VISIBLE);
                        else scanView.setVisibility(View.GONE);
                    }
                }
                if (mAdOrder.getIsvirtual() == 1) {
                    wuliuView.setVisibility(View.GONE);
                }
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_scan: {
                    captureAction();
                }
                break;
                case R.id.iv_search: {

                    searchAction();
                }
                break;
                case R.id.order_wuliu: {
                    gotoWuliu();
                }
                break;
                case R.id.address_modify_text: {
                    gotoEditOrderAddress();
                }
                break;
            }
        }

    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS)) {
            getOrderDetail(orderId);
        }else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENET_CLOSE_ORDER_DETIAL)){
            finish();
        }
    }

}
