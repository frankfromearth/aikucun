package com.aikucun.akapp.activity.reconciliation;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.BarcodeSearchCallback;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.BindView;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by ak123 on 2018/2/1.
 */

public class SearchProductActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.sure_btn)
    TextView sure_btn;
    private CustomerLiveProductAdapter adapter;
    private String liveInfoId;
    @BindView(R.id.search_edit)
    EditText search_edit;
    @BindView(R.id.scan_iv)
    ImageView scan_iv;
    private String barcode;

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        barcode = getIntent().getStringExtra("barcode");
        liveInfoId = getIntent().getStringExtra("liveInfoId");

        adapter = new CustomerLiveProductAdapter(this);
        adapter.setMore(R.layout.view_load_more, this);
        adapter.setNoMore(R.layout.view_nomore);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(adapter);

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (!TextUtils.isEmpty(keyword)) {
                    sure_btn.setEnabled(true);
                    sure_btn.setBackgroundResource(R.drawable.btn_bg_main);
                } else {
                    sure_btn.setBackgroundResource(R.drawable.btn_bg_disabled);
                    sure_btn.setEnabled(false);
                }
            }
        });

        sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = search_edit.getText().toString();
                if (!TextUtils.isEmpty(keyword)) {
                    recyclerView.setRefreshing(true);
                    searchData(keyword);
                }
            }
        });
        scan_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureAction(1010);
            }
        });
        if (!TextUtils.isEmpty(barcode)) {
            getQrSearch(barcode);
            search_edit.setText(barcode);
        }
    }

    protected void captureAction(final int requestCode) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (TDevice.isCameraAvailable()) {
                        // 开启扫描
                        startActivityForResult(new Intent(SearchProductActivity.this, CaptureActivity
                                .class), requestCode);
                    } else {
                        MToaster.showShort(SearchProductActivity.this, R.string.camera_is_not_available, MToaster.IMG_INFO);
                    }
                } else {
                    MyDialogUtils.showPermissionsDialog(SearchProductActivity.this, getString(R.string.camera));
                }
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_search_layout;
    }

    @Override
    public void onRefresh() {
        recyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        recyclerView.setRefreshing(false);
        adapter.stopMore();
    }


    private void searchData(String keyword) {
        UsersApiManager.searchProductByInfo(this, keyword, liveInfoId, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                recyclerView.setRefreshing(false);
                adapter.clear();
                if (jsonObject != null) {
                    String list = jsonObject.getString("list");
                    List<CartProduct> arrList = JSONObject.parseArray(list, CartProduct.class);
                    adapter.addAll(arrList);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                if (!TextUtils.isEmpty(scanResult)) {
                    search_edit.setText(scanResult);
                    getQrSearch(scanResult);
                }
            }
        }
    }

    private void getQrSearch(String barcode) {
        adapter.clear();
        ProductApiManager.barcodeSearch(this, barcode, "", new BarcodeSearchCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);
                adapter.addAll(cartProducts);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }
}
