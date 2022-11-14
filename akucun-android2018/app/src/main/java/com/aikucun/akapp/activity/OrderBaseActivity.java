package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.OrderProductAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.DeliverDetailCallback;
import com.aikucun.akapp.api.callback.OrderDetailCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.Logistics;
import com.aikucun.akapp.api.entity.OrderModel;
import com.aikucun.akapp.api.manager.DeliverApiManager;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.response.OrderDetailResp;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.YiFaAdOrderManager;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.LouFaPopWindow;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.ScanResultPopWindow;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import rx.functions.Action1;

import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ADORDER;
import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ORDER;
import static com.aikucun.akapp.adapter.OrderProductAdapter.PRODUCT_EVENTS_REMARK;
import static com.aikucun.akapp.adapter.OrderProductAdapter.PRODUCT_EVENTS_SHOUHOU_AFTER;
import static com.aikucun.akapp.adapter.OrderProductAdapter.PRODUCT_EVENTS_SHOUHOU_APPLY;
import static com.aikucun.akapp.adapter.OrderProductAdapter.Product_EVENTS_CHANGE;
import static com.aikucun.akapp.adapter.OrderProductAdapter.Product_EVENTS_TUIKUAN;

/**
 * Created by jarry on 2017/6/11.
 */

public abstract class OrderBaseActivity extends BaseActivity implements OrderProductAdapter
        .OnItemEventListener, ScanResultPopWindow.OnWindowItemListener, LouFaPopWindow
        .OnWindowItemListener {

    public static final int AFTER_SALE_SERVICE_APPLY_REQUEST = 1012;
    public static final int SEARCH_PRODUCT_REQUEST = 1013;


    protected OrderProductAdapter productAdapter;

    protected int type;
    protected String orderId;

    protected OrderDetailResp orderDetailResp;
    protected OrderModel order;
    protected Logistics logistics;

    protected AdOrder mAdOrder;


    @Override
    public void onEvent(int event, final CartProduct product, final int position) {
        switch (event) {
            case Product_EVENTS_CHANGE: {
                if (TextUtils.isEmpty(orderId) || TextUtils.isEmpty(product.getId()) || TextUtils.isEmpty(product.getCartproductid())) {
                    MyDialogUtils.showV7NormalDialog(OrderBaseActivity.this, R.string.cannot_change_product, null);
                } else {
                    Intent intent = new Intent(this, ChangeProductActivity.class);
                    intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
                    intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product.getCartproductid());
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
                }

            }
            break;

            case Product_EVENTS_TUIKUAN: {
                String msg = "订单未发货，确定取消该商品吗 ？ 确定取消商品金额将退回原支付渠道";
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("申请退款不发货").setMessage(msg).setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestCancelProduct(product.getOrderid(), product.getCartproductid());
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
            break;

            //            case PRODUCT_EVENTS_TUIHUO:
            //            {
            //                Intent intent = new Intent(this, OrderRefundActivity.class);
            //                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
            //                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product
            // .getCartproductid());
            //                startActivityForResult(intent, 0);
            //                overridePendingTransition(R.anim.anim_bottom_in, R.anim
            // .anim_fade_out);
            //            }
            //            break;

            //            case PRODUCT_EVENTS_HUANHUO:
            //            {
            //                Intent intent = new Intent(this, OrderRefundActivity.class);
            //                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
            //                intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
            //                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product
            // .getCartproductid());
            //                startActivityForResult(intent, 0);
            //                overridePendingTransition(R.anim.anim_bottom_in, R.anim
            // .anim_fade_out);
            //            }
            //            break;

            case PRODUCT_EVENTS_REMARK: {
//                final EditText remarkEt = new EditText(this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView
//                        (remarkEt).setNegativeButton(R.string.cancel, null);
//                builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener()
//                {
//
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        String remark = remarkEt.getText().toString();
//                        if (!StringUtils.isEmpty(remark))
//                        {
//                            requestRemarkProduct(product, remark, position);
//                        }
//                    }
//                });
//                builder.show();
                MyDialogUtils.showSetRemarkDialog(this, new MyDialogUtils.ISetRemarkLisenter() {
                    @Override
                    public void onBack(String content) {
                        requestRemarkProduct(product, content, position);
                    }
                });
            }
            break;

            //            case PRODUCT_EVENTS_LOUFA: {
            //
            //                LouFaPopWindow popWindow = new LouFaPopWindow(this);
            //                popWindow.setOnWindowItemListener(this);
            //                popWindow.showAtLocation(getRootView(), Gravity.BOTTOM, 0, 0);
            //                popWindow.setCartProduct(product);
            //            }
            //            break;
            case PRODUCT_EVENTS_SHOUHOU_APPLY: {
                Intent intent = new Intent(this, AfterSaleServiceActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
                intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT, product);
                intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product.getCartproductid());
                startActivityForResult(intent, AFTER_SALE_SERVICE_APPLY_REQUEST);
                overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
            }
            break;
            case PRODUCT_EVENTS_SHOUHOU_AFTER: {
                Intent intent = new Intent(this, AfterSaleDetailActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product.getCartproductid());
                startActivityForResult(intent, 0);
            }
            break;

        }
    }

    protected void refreshOrderDetail() {
        if (type == TYPE_ORDER) {
            getOrderDetail(orderId);
        } else if (type == TYPE_ADORDER) {
            //这里是否需要更新数据库，还
            if (null != mAdOrder && mAdOrder.getStatu() == 4) {
                YiFaAdOrderManager.getInstance().deleteAdOrder(mAdOrder.getAdorderid());
            }
            getDeliverDetail(orderId);
        }
    }

    /**
     * 获取订单详细
     *
     * @param orderId 订单ID
     */
    protected void getOrderDetail(String orderId) {
        if (orderId == null || orderId.length() == 0) {
            return;
        }

        showProgress("");
        OrderApiManager.detailOrder(this, orderId, new OrderDetailCallback() {
            @Override
            public void onApiSuccess(OrderDetailResp orderDetailResp, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(orderDetailResp, call, jsonResponse);
                cancelProgress();
                doUpdateOrder(orderDetailResp);
            }
        });
    }

    protected void getDeliverDetail(String orderId) {
        if (orderId == null || orderId.length() == 0) {
            return;
        }

//        AdOrder order = YiFaAdOrderManager.getInstance().getByAdOrderId(orderId);
//        if (order != null)
//        {
//            SCLog.debug("从本地数据库中获取已发货单详情");
//            updateOrder(order, false);
//            return;
//        }

        showProgress("");
        DeliverApiManager.deliverDetail(this, orderId, new DeliverDetailCallback() {
            @Override
            public void onApiSuccess(AdOrder adOrder, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(adOrder, call, jsonResponse);
                cancelProgress();
                updateOrder(adOrder, true);

            }

            @Override
            public void onApiFailed(String message, int code) {
                cancelProgress();
                super.onApiFailed(message, code);
                if (code == 60077) {
                    // TODO: 2018/2/10 发货单已失效关闭页面
                    OrderBaseActivity.this.finish();
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENET_CLOSE_ORDER_DETIAL));
                }
            }
        });
    }

    protected void updateOrder(AdOrder adOrder, Boolean toSave) {

        doUpdateAdOrder(adOrder);

        if (4 == adOrder.getStatu()) {

            //保存发货单详情
            if (toSave) {
                YiFaAdOrderManager.getInstance().save(adOrder);
            }
        }
    }


    protected void requestRemarkProduct(final CartProduct product, final String remark, final int
            position) {
        showProgress("");
        ProductApiManager.remarkProduct(this, product.getCartproductid(), remark, new
                ResultCallback(this) {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(apiResponse, call, jsonResponse);
                        cancelProgress();
                        product.setRemark(remark);
                        productAdapter.update(product, position);
                        productAdapter.notifyDataSetChanged();
                        MToaster.showShort(OrderBaseActivity.this, "成功添加备注", MToaster.IMG_INFO);

                /*/更新本地备注
                if (null != mAdOrder && mAdOrder.getStatu() == 4)
                {
                    String uid = RSAUtils.md5String(mAdOrder.getAdorderid() + product
                            .getCartproductid());
                    YiFaAdOrderManager.getInstance().updateRemark(uid, remark);
                }*/

                        refreshOrderDetail();
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);

                    }
                });
    }

    //LouFaPopWindow popWindow,
    public void onEvent(LouFaPopWindow popWindow, int event) {

        if (1 == event) {
            showProgress("");
            CartProduct cartProduct = popWindow.getCartProduct();

            ProductApiManager.updateLackProduct(this, cartProduct.getCartproductid(), new
                    ResultCallback() {
                        @Override
                        public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse
                                jsonResponse) {
                            super.onApiSuccess(apiResponse, call, jsonResponse);
                            cancelProgress();
                            showMessage("已提交");
                            refreshOrderDetail();
                        }
                    });
        }
    }


    protected void requestCancelOrder(String orderId) {
        showProgress("");
        OrderApiManager.cancelOrder(this, orderId, new ResultCallback() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);
                cancelProgress();
                showMessage("订单已取消");
                //刷新我的订单列表
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_ORDER_STATUS));
                refreshOrderDetail();
            }
        });
    }


    protected void requestCancelProduct(String orderId, String productId) {
        showProgress("");
        OrderApiManager.cancelProduct(this, orderId, productId, new ResultCallback() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);
                cancelProgress();
                showMessage("已提交");
                //刷新我的订单列表
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_ORDER_STATUS));
                refreshOrderDetail();
            }
        });
    }

    protected void doUpdateOrder(OrderDetailResp orderDetailResp) {
        this.orderDetailResp = orderDetailResp;
        order = orderDetailResp.getOrder();
        logistics = orderDetailResp.getLogistics();

        productAdapter.outaftersale = order.getOutaftersale();
        productAdapter.clear();
        productAdapter.addAll(orderDetailResp.getProducts());
        productAdapter.notifyDataSetChanged();

    }

    protected void doUpdateAdOrder(AdOrder adOrder) {
        mAdOrder = adOrder;
        if (null != mAdOrder) {
            productAdapter.outaftersale = mAdOrder.getOutaftersale();
            productAdapter.clear();
            productAdapter.setAdOrderId(adOrder.getAdorderid());
            productAdapter.addAll(mAdOrder.getProducts());
            productAdapter.notifyDataSetChanged();
        } else {
            SCLog.debug("Nothing");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        handleActivityResult(requestCode, resultCode, data);
    }

    protected void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SEARCH_PRODUCT_REQUEST) {
            AdOrder order = YiFaAdOrderManager.getInstance().getByAdOrderId(orderId);
            if (order != null) {
                updateOrder(order, false);
                return;
            }
        } else if (resultCode == RESULT_OK) {
            refreshOrderDetail();
        }

        if (requestCode == AFTER_SALE_SERVICE_APPLY_REQUEST && resultCode == RESULT_OK) {

            final int problem = data.getIntExtra("PROBLEM_TYPE", -1);
            final String cartProductId = data.getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);

            if (!StringUtils.isEmpty(cartProductId)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (problem > 1) {
                            showTuiHuoConfirm(cartProductId);
                        } else {
                            showAfterSaleDetail(cartProductId);
                        }
                    }
                }, 600);
            }
        }

        if (1010 == requestCode) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                handleScanResult(scanResult);
            }
        }
    }

    protected abstract void handleScanResult(String barCode);


    protected void captureAction() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (TDevice.isCameraAvailable()) {
                        // 开启扫描
                        startActivityForResult(new Intent(OrderBaseActivity.this, CaptureActivity
                                .class), 1010);
                    } else {
                        showMessage(R.string.camera_is_not_available);
                    }
                } else {
                    showApplicationSettingDetails(getString(R.string.camera));
                }
            }
        });
    }

    protected void showTuiHuoConfirm(final String cartProductId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("售后申请已提交").
                setMessage("您需要将问题货品寄回爱库存，现在填写退货快递单号吗 ？").
//                setNegativeButton("暂不填写", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//
//                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderBaseActivity
//                                .this);
//                        builder.setTitle("提示信息").
//                                setMessage("您可以稍后在售后进度详情中填写退货快递单号").
//                                setPositiveButton(R.string.sure, null);
//                        AlertDialog dialog1 = builder.create();
//                        dialog1.setCanceledOnTouchOutside(false);
//                        dialog1.show();
//                    }
//                }).
        setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {

        Intent intent = new Intent(OrderBaseActivity.this,
                AfterSaleAddressActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, cartProductId);
        startActivity(intent);
    }
});
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void showAfterSaleDetail(final String cartProductId) {
        Intent intent = new Intent(OrderBaseActivity.this, AfterSaleDetailActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, cartProductId);
        startActivity(intent);

    }
}
