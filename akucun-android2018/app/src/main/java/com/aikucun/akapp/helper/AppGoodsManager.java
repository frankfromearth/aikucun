package com.aikucun.akapp.helper;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.adapter.ProductAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.callback.SKUListCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.MyDialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;

/**
 * Created by micker on 2017/7/13.
 */

public class AppGoodsManager {

    protected ProductAdapter productAdapter;

    protected ProductSKU selectSKU;

    protected BaseActivity activity;

    protected Product product;

    protected String remark;

    public AppGoodsManager(ProductAdapter productAdapter,Product product, ProductSKU selectSKU, String remark, BaseActivity activity) {
        this.productAdapter = productAdapter;
        this.selectSKU = selectSKU;
        this.activity = activity;
        this.remark = remark;
    }

    /* 添加商品到购物车 */
    public void requestBuyProduct(final ProductSKU sku, String cartproductid)
    {
        (activity).showProgress("");

        ProductApiManager.buyProduct(activity, sku.getProductid(), sku.getId(), remark, cartproductid,new
                ResultCallback(activity)
                {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
                    {
                        super.onApiSuccess(apiResponse, call, jsonResponse);

                        (activity).cancelProgress();

                        int count = selectSKU.getShuliang();
                        count--;
                        selectSKU.setShuliang(count);
                        productAdapter.notifyDataSetChanged();

                        MToaster.showShort(activity,"已添加到购物车",MToaster.IMG_INFO);
                        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_ADD_CART));
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (60018 == code) {
                            //纠正库存

                            ProductApiManager.getSKUProduct(activity,sku.getProductid(),new SKUListCallback(AppGoodsManager.this.product) {

                                @Override
                                public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(productSKUs, call, jsonResponse);
                                    AppGoodsManager.this.productAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
    }


    public void onShowRemarkDialog(final String cartProductId) {

        if (StringUtils.isEmpty(cartProductId) || !AppContext.get(AppConfig.UDK_REMARK_DATA,true)) {
            MToaster.showShort(activity,"已添加到购物车",MToaster.IMG_INFO);
            return;
        }

        MyDialogUtils.showSetRemarkDialog(activity, new MyDialogUtils.ISetRemarkLisenter() {
            @Override
            public void onBack(String content) {
                requestRemarkProduct(cartProductId, content);
            }
        });
    }

    public void requestRemarkProduct(String cartProductId, String remark) {
        ProductApiManager.remarkProduct(activity, cartProductId, remark, new ResultCallback(activity) {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);
                MToaster.showShort(activity, "成功添加备注", MToaster.IMG_INFO);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

}
