package com.aikucun.akapp.activity;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.ScanResultAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.BarcodeSearchCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * Created by ak123 on 2018/1/10.
 * 条形码扫码返回
 */

public class ScanResultActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private ScanResultAdapter scanResultAdapter;
    private String barcode = "";

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText.setText(R.string.scan_result);

        scanResultAdapter = new ScanResultAdapter(this);
        scanResultAdapter.setMore(R.layout.view_load_more, this);
        scanResultAdapter.setNoMore(R.layout.view_nomore);
    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.WHITE, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(scanResultAdapter);
        recyclerView.setRefreshing(true);
        barcode = getIntent().getStringExtra("barcode");
        recyclerView.setRefreshing(true);
        requestBarcodeSearch(barcode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_result_layout;
    }

    @Override
    public void onRefresh() {
        recyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {

    }


    private void requestBarcodeSearch(final String barcode) {

        ProductApiManager.barcodeSearch(this, barcode, "", new BarcodeSearchCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);
                recyclerView.setRefreshing(false);
                scanResultAdapter.addAll(cartProducts);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

}
