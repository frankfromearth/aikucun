package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.SkuAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.view.MultiImageView;
import com.aikucun.akapp.view.SkuGridView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * Created by jarry on 2017/7/2.
 */

public class OrderRefundActivity extends BaseActivity
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.refund_tip)
    TextView tipMessageText;

    @BindView(R.id.refund_product_tip)
    TextView tipProductText;

    @BindView(R.id.refund_wuliu_edit)
    AppCompatEditText mEditWuliu;

    @BindView(R.id.refund_bianhao_edit)
    AppCompatEditText mEditBianhao;

    @BindView(R.id.refund_product_layout)
    View productView;

    @BindView(R.id.multiImagView)
    MultiImageView multiImageView;

    @BindView(R.id.contentTv)
    TextView contentText;

    @BindView(R.id.skuGridview)
    SkuGridView skuGridView;

    @BindView(R.id.ok_button)
    TextView okButton;

    @BindView(R.id.reason_button)
    TextView reasonButton;


    private SkuAdapter skuAdapter;

    private String orderId;
    private String cartProductId;
    private Product product;
    private ProductSKU selectSku;
    private int selectReason = 0;
    private String[] reasons = new String[]{"????????????","????????????","????????????","????????????"};

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.refund_for_refund);

        reasonButtonText();
    }

    @Override
    public void initData()
    {
        orderId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_ORDER_ID);
        cartProductId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);

        String productId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID);
        if (productId == null || productId.length() == 0)
        {
            return;
        }

        product = ProductManager.getInstance().findProductById(productId);
        if (product == null)
        {
            return;
        }

        productView.setVisibility(View.VISIBLE);

        final List<String> imageUrls = Product.getImageUrls(product);
        if (imageUrls != null && imageUrls.size() > 0)
        {
            multiImageView.imageWidth = DisplayUtils.dip2px(this, 50);
            multiImageView.setUrlList(imageUrls);
        }

        contentText.setText(product.getDesc());

        skuAdapter = new SkuAdapter(product.getSkus(), this);
        skuGridView.setAdapter(skuAdapter);

        int maxSkuLength = product.getMaxSkuLength();
        if (maxSkuLength < 8)
        {
            skuGridView.setNumColumns(4);
        }
        else
        {
            skuGridView.setNumColumns(3);
        }

        skuAdapter.setItemClickListener(new SkuAdapter.onItemClickListener()
        {
            @Override
            public void onItemClick(ProductSKU sku, int position)
            {
                okButton.setEnabled(sku.isSelected());

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

        if (product.isQuehuo())
        {
            tipProductText.setText("??????????????? ????????????????????????");
            return;
        }

        mTitleText.setText("????????????");
        tipMessageText.setText(R.string.order_huanhuo_tip);
        okButton.setEnabled(false);

    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_refund;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
    }

    @Override
    @OnClick({R.id.ok_button, R.id.addr_copy_btn,R.id.iv_scan,R.id.reason_button})
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.ok_button:
            {
                hideInputKeyboard();

                if (validate())
                {
                    String wuliu = mEditWuliu.getText().toString();
                    String bianhao = mEditBianhao.getText().toString();
                    requestCancelProduct(wuliu, bianhao);
                }
            }
            break;

            case R.id.addr_copy_btn:
            {
                hideInputKeyboard();

                String address = getResources().getString(R.string.order_refund_addr);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(address);
                cm.setPrimaryClip(ClipData.newPlainText("akucun", address));

                showMessage("???????????????");
            }
            break;

            case R.id.iv_scan: {
                captureAction();
            }
            break;
            case R.id.reason_button:{
                showReasons();
            }

            break;
            default:
                break;
        }
    }

    private void reasonButtonText() {

        reasonButton.setText("???????????????" + reasons[selectReason]);
    }


    private void showReasons() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????????????????");

        builder.setItems(reasons, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                selectReason = which;
                reasonButtonText();
            }
        });
        builder.show();
    }

    private boolean validate()
    {
        if (product != null)
        {
            if (selectSku == null)
            {
                showMessage("????????????????????????");
                return false;
            }
        }

        if (mEditWuliu.length() == 0)
        {
            mEditWuliu.setError("?????????????????????");
            mEditWuliu.requestFocus();
            return false;
        }

        if (mEditBianhao.length() == 0)
        {
            mEditBianhao.setError("?????????????????????");
            mEditBianhao.requestFocus();
            return false;
        }
        return true;
    }

    private void hideInputKeyboard()
    {
        if (mEditWuliu.isFocused())
        {
            InputMethodUtils.hideInputKeyboard(this, mEditWuliu);
        }
        else if (mEditBianhao.isFocused())
        {
            InputMethodUtils.hideInputKeyboard(this, mEditBianhao);
        }
    }

    private void requestCancelProduct(String wuliugongsi, String wuliuhao)
    {
        showProgress("");
        OrderApiManager.cancelProduct(this, orderId, cartProductId, new
                ResultCallback()
        {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(apiResponse, call, jsonResponse);

                if (product != null)
                {
                    requestBuyProduct(selectSku);
                }
                else
                {
                    cancelProgress();
//                    showMessage("?????????");
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /* ???????????????????????? */
    private void doRequestBuyProduct(ProductSKU sku,String remark)
    {

        ProductApiManager.buyProduct(this, sku.getProductid(), sku.getId(), remark, null, new ResultCallback(this)
        {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(apiResponse, call, jsonResponse);

                cancelProgress();
                showMessage("??????????????????,????????????????????????????????????");
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                onBackPressed();
            }
        });
    }

    private void requestBuyProduct(final ProductSKU sku)
    {
        if (!AppContext.get(AppConfig.UDK_REMARK_DATA,true)) {
            doRequestBuyProduct(sku,"");
            return;
        }
        final View view = View.inflate(this, R.layout.view_buy_check, null);
        final EditText editText = (EditText)view.findViewById(R.id.comment_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????????????????").setView(view)
                .setNegativeButton("?????????", null);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String remark = editText.getText().toString();
                doRequestBuyProduct(sku,remark);
            }
        });
        builder.show();
        InputMethodUtils.showInputKeyboard(this,editText);
    }

    //??????????????????

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (1010 == requestCode) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK)
            {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                handleScanResult(scanResult);
            }
        }
    }

    protected  void handleScanResult(String barCode) {

        if (!StringUtils.isEmpty(barCode)) {
            mEditBianhao.setText(barCode);
            mEditBianhao.setSelection(barCode.length());
            mEditBianhao.requestFocus();
        }
    }


    protected void captureAction() {

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>()
        {
            @Override
            public void call(Boolean granted)
            {
                if (granted)
                {
                    if (TDevice.isCameraAvailable())
                    {
                        // ????????????
                        startActivityForResult(new Intent(OrderRefundActivity.this, CaptureActivity.class), 1010);
                    }
                    else
                    {
                        showMessage(R.string.camera_is_not_available);
                    }
                }
                else
                {
                    showApplicationSettingDetails(getString(R.string.camera));
                }
            }
        });
    }
}
