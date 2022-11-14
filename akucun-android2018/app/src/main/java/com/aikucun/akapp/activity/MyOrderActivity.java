package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.OrderAdapter;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.BarcodeSearchCallback;
import com.aikucun.akapp.api.callback.DeliverDetailCallback;
import com.aikucun.akapp.api.callback.DeliverListCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.OrderDetailCallback;
import com.aikucun.akapp.api.callback.OrderListCallback;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.OrderModel;
import com.aikucun.akapp.api.manager.DeliverApiManager;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.response.OrderDetailResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.YiFaAdOrderManager;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.ScanResultPopWindow;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import rx.functions.Action1;

import static com.aikucun.akapp.adapter.OrderAdapter.ORDER_EVENT_PAY;
import static com.aikucun.akapp.adapter.OrderAdapter.ORDER_EVENT_SCAN;
import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ADORDER;
import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ORDER;

/**
 * Created by jarry on 2017/6/21.
 */

public class MyOrderActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, OrderAdapter
        .OnItemEventListener, ScanResultPopWindow.OnWindowItemListener {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.order_type_all)
    TextView typeAllText;

    @BindView(R.id.order_type_daizhifu)
    TextView typeDaizhifuText;

    @BindView(R.id.order_type_yizhifu)
    TextView typeYiZhiFuText;

    @BindView(R.id.order_type_yifahuo)
    TextView typeYifahuoText;

    @BindView(R.id.order_type_jianhuozhong)
    TextView typeJianhuoText;

    @BindView(R.id.order_type_canceled)
    TextView typeCancelText;

    private OrderAdapter orderAdapter;

    private int page = 0;

    private int orderType = -2;
    private TextView selectTypeText;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.my_orders);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);
        textView.setText(R.string.no_orders);
        recyclerView.setEmptyView(emptyView);


        orderAdapter = new OrderAdapter(this);
        orderAdapter.setOnItemEventListener(this);

        orderAdapter.setMore(R.layout.view_load_more, this);
        orderAdapter.setNoMore(R.layout.view_nomore);

        recyclerView.setAdapter(orderAdapter);
        recyclerView.setRefreshListener(this);

        orderAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int type = orderAdapter.getViewType(position);
                Intent intent = new Intent(MyOrderActivity.this, OrderDetailActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, type);
                if (type == TYPE_ORDER) {
                    OrderModel order = (OrderModel) orderAdapter.getItem(position);
                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, order.getOrderid());
                } else if (type == TYPE_ADORDER) {
                    AdOrder order = (AdOrder) orderAdapter.getItem(position);
                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, order.getAdorderid());
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        int type = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, -1);
        setSelectedType(type);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myorder;
    }

    @Override
    public void onResume() {
        super.onResume();

        orderAdapter.notifyDataSetChanged();
    }

    @Override
    @OnClick({R.id.order_type_all, R.id.order_type_daizhifu, R.id.order_type_yizhifu, R.id.order_type_jianhuozhong, R.id
            .order_type_yifahuo, R.id.order_type_canceled})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.order_type_all: {
                setSelectedType(-1);
            }
            break;
            case R.id.order_type_daizhifu: {
                setSelectedType(0);
            }
            break;
            case R.id.order_type_yizhifu: {
                // TODO: 2018/1/10  
                setSelectedType(1);
            }
            break;
            case R.id.order_type_jianhuozhong: {
                setSelectedType(3);
            }
            break;
            case R.id.order_type_yifahuo: {
                setSelectedType(2);
            }
            break;
            case R.id.order_type_canceled: {
                setSelectedType(4);
            }
            break;
        }
    }

    @Override
    public void onLoadMore() {
        doLoadData();
    }

    @Override
    public void onRefresh() {
        orderAdapter.clear();
        recyclerView.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshOrderList();
            }
        }, 500);
    }

    @Override
    public void onEvent(int event, OrderModel order, int position) {
        switch (event) {
            case ORDER_EVENT_PAY: {
                getLogis(order);
            }
            break;
        }
    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_REFRESH_ORDER_STATUS) || event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENET_ORDER_PAY_SUCCESS)) {
            refreshOrderList();
        }
    }

    /**
     * 获取物流信息
     *
     * @param order
     */
    private void getLogis(final OrderModel order) {
        showProgress("");
        OrderApiManager.detailOrder(this, order.getOrderid(), new OrderDetailCallback() {
            @Override
            public void onApiSuccess(OrderDetailResp orderDetailResp, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(orderDetailResp, call, jsonResponse);

                cancelProgress();
                String orderIds = "[\"" + order.getOrderid() + "\"]";
                Intent intent = new Intent(MyOrderActivity.this, PayOrderActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_IDS, orderIds);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, order.getShangpinjine());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, order.getDikoujine());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, order.getYunfei());
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, order.getZongjine());
                //设置地址信息
                intent.putExtra(AddressUtils.orderReceiptName, orderDetailResp.getLogistics().getShouhuoren());
                intent.putExtra(AddressUtils.orderAddressPhone, orderDetailResp.getLogistics().getLianxidianhua());
                intent.putExtra(AddressUtils.orderAddress, orderDetailResp.getLogistics().getShouhuodizhi());

                startActivity(intent);
                overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);

            }
        });
//        DeliverApiManager.deliverDetail(this, order.getOrderid(), new DeliverDetailCallback() {
//            @Override
//            public void onApiSuccess(AdOrder adOrder, Call call, ApiResponse jsonResponse) {
//                super.onApiSuccess(adOrder, call, jsonResponse);
//                cancelProgress();
//                if (adOrder != null) {
//                    String orderIds = "[\"" + order.getOrderid() + "\"]";
//                    Intent intent = new Intent(MyOrderActivity.this, PayOrderActivity.class);
//                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_IDS, orderIds);
//                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, order.getShangpinjine());
//                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, order.getDikoujine());
//                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, order.getYunfei());
//                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, order.getZongjine());
//                    //设置地址信息
//                    intent.putExtra(AddressUtils.orderReceiptName, adOrder.getShouhuoren());
//                    intent.putExtra(AddressUtils.orderAddressPhone, adOrder.getLianxidianhua());
//                    intent.putExtra(AddressUtils.orderAddress, adOrder.getShouhuodizhi());
//
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
//                }
//            }
//        });
    }

    @Override
    public void onEvent(int event, AdOrder order, int position) {
        mAdOrder = order;

        switch (event) {
            case ORDER_EVENT_PAY: {
                if (order != null) {
                    if (0 != order.getStatu()) {
                        showDuiZhangDanDialog();
                    } else {
                        //只有0 是需要申请
                        onApplyDoc(order.getAdorderid());
                    }
                }
            }
            break;
            case ORDER_EVENT_SCAN: {
                AdOrder scanOrder = YiFaAdOrderManager.getInstance().getByAdOrderId(order.getAdorderid());

                if (null != scanOrder) {
                    captureAction();
                } else {
                    doDownloadAdorderDetail(order.getAdorderid());
                }
            }
            break;
        }
    }

    private void doDownloadAdorderDetail(String orderId) {
        showProgress("");
        DeliverApiManager.deliverDetail(this, orderId, new DeliverDetailCallback() {
            @Override
            public void onApiSuccess(AdOrder adOrder, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(adOrder, call, jsonResponse);
                cancelProgress();
                if (4 == adOrder.getStatu()) {
                    //保存发货单详情
                    YiFaAdOrderManager.getInstance().save(adOrder);
                }
                captureAction();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
            }
        });
    }

    private void showDuiZhangDanDialog() {
        final String[] items = new String[]{getString(R.string.update_account_statement), getString(R.string.download_the_bill)};
        if (items.length == 0) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    onApplyDoc(mAdOrder.getAdorderid());
                } else {
                    goToDuiZhangDan();
                }
            }
        });
        builder.show();
    }

    private void goToDuiZhangDan() {
        String downloadURL = mAdOrder.getDownloadurl();

        if (!StringUtils.isEmpty(downloadURL)) {
            Intent intent = new Intent(this, StateMentActivity.class);
            intent.putExtra("url", downloadURL);
            startActivity(intent);
        } else {
            MToaster.showShort(this, "对账单还未生成，请稍候再试!", MToaster.IMG_INFO);
        }
    }

    protected AdOrder mAdOrder;
    ScanResultPopWindow popWindow;

    @Override
    public void onEvent(ScanResultPopWindow popWindow, int event) {

        if (0 == event) {
            MToaster.showShort(this, "操作成功!", MToaster.IMG_INFO);
            CartProduct product = popWindow.getCartProduct();
            ProductApiManager.updateScanProduct(this, product.getCartproductid(),
                    mAdOrder.getAdorderid(), product.getSku().getBarcode(), 1, new
                            ApiBaseCallback() {
                                @Override
                                public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(o, call, jsonResponse);
                                }
                            });
        }
    }

    private void requestBarcodeSearch(final String barcode) {

        ProductApiManager.barcodeSearch(this, barcode, this.mAdOrder.getLiveid(), new BarcodeSearchCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);

                if (cartProducts.size() == 0) {
                    new AlertDialog.Builder(MyOrderActivity.this).
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
                        popWindow = new ScanResultPopWindow(MyOrderActivity.this);
                        popWindow.setOnWindowItemListener(MyOrderActivity.this);
                        popWindow.setCartProduct(product);
                        popWindow.showAtLocation(MyOrderActivity.this.getRootView(), Gravity.BOTTOM, 0, 0);
                    } else {
                        new AlertDialog.Builder(MyOrderActivity.this).
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

    protected void handleScanResult(String resultBarCode) {
        SCLog.debug("scan barcode result is = " + resultBarCode);

//        barCode = "OUT1TPT01APUR";
        if (StringUtils.isEmpty(resultBarCode))
            return;

        requestBarcodeSearch(resultBarCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1010 == requestCode) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                handleScanResult(scanResult);
            }
        }
    }

    protected void captureAction() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (TDevice.isCameraAvailable()) {
                        // 开启扫描
                        startActivityForResult(new Intent(MyOrderActivity.this, CaptureActivity.class), 1010);
                    } else {
                        showMessage(R.string.camera_is_not_available);
                    }
                } else {
                    showApplicationSettingDetails(getString(R.string.camera));
                }
            }
        });
    }

    private void onApplyDoc(String adorderId) {
        DeliverApiManager.applyDeliverDetail(this, adorderId, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);

                String msg = "对账单申请已提交，生成对账单过程需要几分钟时间，请稍候在发货单详情中查看";
                AlertDialog.Builder builder = new AlertDialog.Builder(MyOrderActivity.this);
                builder.setTitle("申请已提交").setMessage(msg).setPositiveButton(R.string.sure, null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

    }

    private void setSelectedType(int type) {
        if (orderType == type) {
            return;
        }

        if (selectTypeText != null) {
            selectTypeText.setSelected(false);
            selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        if (type == -1) {
            selectTypeText = typeAllText;
        } else if (type == 0) {
            selectTypeText = typeDaizhifuText;
        } else if (type == 1) {
            selectTypeText = typeYiZhiFuText;
        } else if (type == 2) {
            selectTypeText = typeYifahuoText;
        } else if (type == 3) {
            selectTypeText = typeJianhuoText;
        } else if (type == 4) {
            selectTypeText = typeCancelText;
        }

        selectTypeText.setSelected(true);
        selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        orderType = type;

        refreshOrderList();
    }

    private void refreshOrderList() {
        recyclerView.setRefreshing(true);
        page = 0;
        orderAdapter.clear();
        doLoadData();
    }

    private void doLoadData() {
        if (orderType == 1 || orderType == 2 || orderType == 3) {
            requestDeliverList(page + 1, orderType);
        } else {
            requestOrderList(page + 1);
        }
    }


    private void requestOrderList(int index) {
        OrderApiManager.pageOrder2(this, index, 20, orderType, new OrderListCallback() {
            @Override
            public void onApiSuccess(List<OrderModel> orderModels, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(orderModels, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (orderModels.size() > 0) {
                    page++;
                }
                orderAdapter.addAll(orderModels);

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    private void requestDeliverList(int index, int status) {
        DeliverApiManager.deliverList(this, index, 20, status, new DeliverListCallback() {
            @Override
            public void onApiSuccess(List<AdOrder> adOrders, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(adOrders, call, jsonResponse);

                recyclerView.setRefreshing(false);

                if (adOrders.size() > 0) {
                    page++;
                }
                orderAdapter.addAll(adOrders);

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }
}
