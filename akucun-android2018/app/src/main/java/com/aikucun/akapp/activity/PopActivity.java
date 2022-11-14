package com.aikucun.akapp.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SystemShareUtils;
import com.aikucun.akapp.widget.FastForwardPopWindow;
import com.aikucun.akapp.widget.FloatWindowSmallView;
import com.aikucun.akapp.widget.PopWindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * Created by micker on 2017/7/9.
 */

public abstract class PopActivity extends BaseActivity implements FastForwardPopWindow.OnWindowItemListener {

    private FastForwardPopWindow popWindow;

    private FloatWindowSmallView smallWindow;

    private Product shareProduct;

    //悬浮窗处理逻辑
    public void configWindow(final View view) {
        smallWindow = PopWindowManager.createSmallWindow(smallWindow, this);
        smallWindow.setOnWindowItemListener(new FloatWindowSmallView.OnSmallWindowItemListener() {
            @Override
            public void onEvent(int event) {
                showFastforward(view);
            }
        });
    }

    public void showFastforward(View view) {
        Product product = ProductManager.getInstance().getForwardProduct(0);
        View parent = (null == view) ? getRootView() : view;
        if (null == product) {
            product = ProductManager.getInstance().getForwardProduct(0);
        }
        if (null != product) {
            showOrHidePopWindow(false);
            popWindow = new FastForwardPopWindow(this);
            popWindow.setOnWindowItemListener(this);
            popWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            popWindow.setProduct(product);
            popWindow.setTitle(ProductManager.getInstance().getForwardIndex() > 1 ? "继续转发商品" : "开始转发商品");
        }
        onInitialZhuanFa();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {


        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_FORWARD_PINPAIID_CHANGED) ||
                event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_ADD_FORWARD)) {
            Product product = (Product) event.content;
            if (null == product) {
                product = ProductManager.getInstance().getForwardProduct(0);
            }
            if (null != product && smallWindow != null) {
                onInitialZhuanFa();
            }
        } else if (event.messageId.equalsIgnoreCase(AppConfig.FASTWINDOW_HIDE)) {
            showOrHidePopWindow(false);
        } else if (event.messageId.equalsIgnoreCase(AppConfig.FASTWINDOW_SHOW)) {
            showOrHidePopWindow(true);
        }

    }

    ;


    public void onEvent(int event) {

        if (0 == event) {
            //显示
            showOrHidePopWindow(true);
            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_FORWARD_PINPAIID_CHANGED));

        } else if (2 == event) {
            if (shareProduct != null && (null == shareProduct.getId() || shareProduct.hasNoSku())) {
                return;
            }

            Product product = ProductManager.getInstance().getForwardProduct(0);
            if (null != product) {
                requestForwardProduct(product);

            }
        }
    }

    public void onInitialZhuanFa() {
        Product product = ProductManager.getInstance().getForwardProduct(0);
        int xuhao = ProductManager.getInstance().getForwardIndex();
        String text = "开始转发";
        if (xuhao > 1 && null != product) {
            text = String.format("继续转发\n( %d )", product.getXuhao());
        }
        PopWindowManager.setButtonText(smallWindow, text);
    }

    public void showOrHidePopWindow(boolean flag) {
        if (flag && !PopWindowManager.isWindowShowing(smallWindow)) {
            smallWindow = PopWindowManager.createSmallWindow(smallWindow, this);
        }

        if (!flag && PopWindowManager.isWindowShowing(smallWindow)) {
            PopWindowManager.removeSmallWindow(smallWindow, this);
        }
    }


    private void requestForwardProduct(final Product product) {
        if (product.getId() == null || product.hasNoSku())
            return;
        ProductApiManager.forwardProduct(this, product.getId(), new ResultCallback() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);

                Product nextProduct = ProductManager.getInstance().getValidForwardProduct(product.getXuhao() + 1);
                if (null == nextProduct) {
                    MToaster.showShort(PopActivity.this, "已经转发到该品牌下的最后一条!", MToaster.IMG_ALERT);
                } else {
                    ProductManager.getInstance().setForwardIndex(product.getXuhao() + 1);
                }

                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_ADD_FORWARD, product));
            }
        });
    }


    public void setShareProduct(Product shareProduct) {
        this.shareProduct = shareProduct;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SystemShareUtils.SHARE_RESULT_CODE) {
            onEvent(2);
        }
    }
}

