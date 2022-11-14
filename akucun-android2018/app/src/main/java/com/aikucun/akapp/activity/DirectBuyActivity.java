package com.aikucun.akapp.activity;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.SkuAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.Address;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.AddressUtils;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.view.MultiImageView;
import com.aikucun.akapp.view.SkuGridView;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by jarry on 2017/11/8.
 */
public class DirectBuyActivity extends BaseActivity
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.address_text_name)
    TextView addrNameText;
    @BindView(R.id.address_text_mobile)
    TextView addrMobileText;
    @BindView(R.id.address_text_address)
    TextView addressText;

    @BindView(R.id.address_text_default)
    TextView address_text_default;
    @BindView(R.id.address_edit_btn_internal)
    TextView address_edit_btn_internal;

    @BindView(R.id.multiImagView)
    MultiImageView multiImageView;

    @BindView(R.id.contentTv)
    TextView contentText;

    @BindView(R.id.priceTv)
    TextView priceText;

    @BindView(R.id.skuGridview)
    SkuGridView skuGridView;

    @BindView(R.id.buy_option_remark_title)
    TextView remarkTitle;

    @BindView(R.id.buy_option_remark_text)
    TextView remarkText;

    @BindView(R.id.submit_button)
    Button submitButton;

    private Product mProduct;
    private SkuAdapter skuAdapter;
    private ProductSKU selectSku;

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_directbuy;
    }

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText("立即下单");


    }

    @Override
    public void initData()
    {
        updateAddress();

        String skuId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_PRODUCT_SKU);
        String productId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID);
        if (productId == null || productId.length() == 0)
        {
            return;
        }
        Product product = ProductManager.getInstance().findProductById(productId);
        if (product == null)
        {
            return;
        }

        mProduct = product;

        final List<String> imageUrls = Product.getImageUrls(product);
        if (imageUrls != null && imageUrls.size() > 0)
        {
            multiImageView.imageWidth = DisplayUtils.dip2px(this, 40);
            multiImageView.setUrlList(imageUrls);
        }

        contentText.setText(product.getDesc());

        int index = 0, selectIndex = -1;
        for (ProductSKU sku : product.getSkus())
        {
            if (sku.getId().equalsIgnoreCase(skuId))
            {
                sku.setSelected(true);
                selectSku = sku;
                selectIndex = index;
            }
            index++;
        }
        int maxSkuLength = product.getMaxSkuLength();
        if (maxSkuLength < 5)
        {
            skuGridView.setNumColumns(4);
        }
        else
        {
            skuGridView.setNumColumns(3);
        }
        skuAdapter = new SkuAdapter(product.getSkus(), this);
        skuGridView.setAdapter(skuAdapter);
        if (selectIndex >= 0)
        {
            skuAdapter.setSelectIndex(selectIndex);
        }

        skuAdapter.setItemClickListener(new SkuAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(ProductSKU sku, int position)
            {
                if (sku != selectSku)
                {
                    if (selectSku != null)
                    {
                        selectSku.setSelected(false);
                    }
                    if (sku.isSelected())
                    {
                        selectSku = sku;
                    }
                }
                skuAdapter.notifyDataSetChanged();
            }
        });
        priceText.setText("结算价："+StringUtils.getPriceString(mProduct.getJiesuanjia()));
    }

    private void updateAddress()
    {
        // FIXME: 2018/1/4
        Address address = AddressUtils.getSelectedAddress();
        // 2018/3/8 修复 登录后没有默认选中地址
        if (address == null) {
            address = AddressUtils.getDefaultAddress();
            AddressUtils.setSelectedAddress(address);
        }
        if (address != null)
        {
            addrNameText.setVisibility(View.VISIBLE);
            addrMobileText.setVisibility(View.VISIBLE);
            addrNameText.setText(address.getShoujianren());
            addrMobileText.setText(address.displayMobile());
            addressText.setText(address.displayAddress());
            addressText.setTextSize(13);
            address_edit_btn_internal.setText(R.string.editor);
            address_text_default.setVisibility(address.getDefaultflag() == 1 ?
                    View.VISIBLE : View.GONE);
        }
        else
        {
            addrNameText.setVisibility(View.GONE);
            addrMobileText.setVisibility(View.GONE);
            addressText.setText("请添加收货地址");
            addressText.setTextSize(14);
            address_edit_btn_internal.setText(R.string.add);
        }
    }

    @Override
    @OnClick({R.id.address_edit_btn, R.id.buy_option_remark, R.id.submit_button})
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.address_edit_btn:
            {
                Intent intent = new Intent(this, AddressListActivity.class);
                intent.putExtra("isChoose", true);
                startActivityForResult(intent, 100);
            }
            break;

            case R.id.submit_button:
            {
                UserInfo userInfo = AppContext.getInstance().getUserInfo();
                // FIXME: 2018/1/4
                Address address = AddressUtils.getSelectedAddress();
                if (address == null){
                    showMessage("请添加收货地址！");
                    return;
                }
                requestDirectBuy(AddressUtils.getSelectedAddress().getAddrid());
//                if (null != userInfo && userInfo.getDefaultAddr() == null)
//                {
//                    showMessage("请添加收货地址！");
//                    return;
//                }

//                requestDirectBuy(userInfo.getSelectAddr().getAddrid());
            }
            break;

            case R.id.buy_option_remark:
            {
//                final EditText remarkEt = new EditText(this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
//                        .setNegativeButton(R.string.cancel, null);
//                builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        String remark = remarkEt.getText().toString();
//                        if (!StringUtils.isEmpty(remark)) {
//                            remarkTitle.setText(R.string.remark);
//                            remarkText.setText(remark);
//                        }
//                    }
//                });
//                builder.show();
                MyDialogUtils.showSetRemarkDialog(this, new MyDialogUtils.ISetRemarkLisenter() {
                    @Override
                    public void onBack(String content) {
                        remarkTitle.setText(R.string.remark);
                        remarkText.setText(content);
                    }
                });
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
        {
            //
            updateAddress();
        }
    }


    private void requestDirectBuy(String addrId)
    {
        showProgress("");

        String remark = remarkText.getText().toString();
        OrderApiManager.directBuyProduct(this, mProduct.getLiveid(), addrId, mProduct.getId(),
                selectSku.getId(), remark, new ResultCallback(this)
        {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(apiResponse, call, jsonResponse);

                cancelProgress();
                finish();

                JSONObject jsonObject = jsonResponse.getJsonObject();
                int total = jsonObject.getIntValue("total_amount");
                int amount =  jsonObject.getIntValue("total_shangpinjine");
                int dikou = jsonObject.getIntValue("total_dikoujine");
                int yunfei = jsonObject.getIntValue("total_yunfeijine");
                String orderId = jsonObject.getString("orderid");

                Intent intent = new Intent(DirectBuyActivity.this, PayOrderActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_IDS, "[\""+orderId+"\"]");
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_AMOUNT, amount);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_DIKOU, dikou);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_YUNFEI, yunfei);
                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_TOTAL, total);
                //设置收货地址
                intent.putExtra(AddressUtils.orderReceiptName,AddressUtils.getSelectedAddress().getShoujianren());
                intent.putExtra(AddressUtils.orderAddressPhone,AddressUtils.getSelectedAddress().displayMobile());
                intent.putExtra(AddressUtils.orderAddress,AddressUtils.getSelectedAddress().displayAddress());

                startActivity(intent);
            }
        });
    }

}
