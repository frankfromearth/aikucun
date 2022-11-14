package com.aikucun.akapp.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.SkuAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.view.MultiImageView;
import com.aikucun.akapp.view.SkuGridView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by jarry on 2017/7/2.
 */

public class ChangeProductActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.multiImagView)
    MultiImageView multiImageView;

    @BindView(R.id.contentTv)
    TextView contentText;

    @BindView(R.id.skuGridview)
    SkuGridView skuGridView;

    @BindView(R.id.ok_button)
    Button okButton;

    private SkuAdapter skuAdapter;

    private String orderId;
    private String cartProductId;
    private ProductSKU selectSku;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.update_size);

        okButton.setEnabled(false);
    }

    @Override
    public void initData() {
        orderId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_ORDER_ID);
        cartProductId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);
        Log.e("订单编号：",orderId);
        Log.e("购物车商品：",cartProductId);
        String productId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID);

        if (productId == null || productId.length() == 0) {
            return;
        }
        Log.e("商品：",productId);
        Product product = ProductManager.getInstance().findProductById(productId);
        if (product == null) {
            Log.e("查询失败：","");
            return;
        }

        final List<String> imageUrls = Product.getImageUrls(product);
        if (imageUrls != null && imageUrls.size() > 0) {
            multiImageView.imageWidth = DisplayUtils.dip2px(this, 50);
            multiImageView.setUrlList(imageUrls);
        }

        contentText.setText(product.getDesc());

        skuAdapter = new SkuAdapter(product.getSkus(), this);
        skuGridView.setAdapter(skuAdapter);

        int maxSkuLength = product.getMaxSkuLength();
        if (maxSkuLength < 8) {
            skuGridView.setNumColumns(4);
        } else {
            skuGridView.setNumColumns(3);
        }

        skuAdapter.setItemClickListener(new SkuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ProductSKU sku, int position) {
                okButton.setEnabled(sku.isSelected());

                if (sku != selectSku) {
                    if (selectSku != null) {
                        selectSku.setSelected(false);
                    }
                    if (sku.isSelected()) {
                        selectSku = sku;
                    }
                }
                skuAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_sku;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
    }

    @Override
    @OnClick({R.id.ok_button})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ok_button: {
                requestChangeSKU(cartProductId, selectSku);
            }
            break;

            default:
                break;
        }
    }


    private void requestChangeSKU(String productId, ProductSKU sku) {
        if (sku == null) {
            showMessage("请选择商品尺码");
            return;
        }

        showProgress("");
        OrderApiManager.changeProduct(this, orderId, productId, sku.getId(), new ResultCallback(this) {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);

                cancelProgress();
//                showMessage("已提交");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }
}
