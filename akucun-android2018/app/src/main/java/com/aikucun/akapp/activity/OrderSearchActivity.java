package com.aikucun.akapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.OrderProductAdapter;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.response.OrderDetailResp;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.ScanResultPopWindow;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ADORDER;
import static com.aikucun.akapp.adapter.OrderAdapter.TYPE_ORDER;

/**
 * Created by jarry on 2017/6/11.
 */

public class OrderSearchActivity extends OrderBaseActivity implements TextView.OnEditorActionListener,View.OnClickListener,OrderProductAdapter
        .OnItemEventListener
{
    @BindView(R.id.search_edit)
    EditText searchEdit;

    @BindView(R.id.search_cancel_btn)
    TextView searchCancel;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.iv_scan)
    ImageView iv_scan;

    private String keyword;
    private int start = 0;

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_search_order;
    }

    @Override
    public void initView()
    {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, DisplayUtils
                .dip2px(this, 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        productAdapter = new OrderProductAdapter(this);
        productAdapter.setOnItemEventListener(this);

        recyclerView.setAdapter(productAdapter);

        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);
        textView.setText(R.string.search_no_date);
        recyclerView.setEmptyView(emptyView);

        searchEdit.setOnEditorActionListener(this);

        searchCancel.setOnClickListener(this);

        iv_scan.setOnClickListener(this);
    }

    @Override
    public void initData()
    {
        productAdapter.clear();
        orderId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_ORDER_ID);
        type = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_ORDER_TYPE, 0);

        if (type == TYPE_ORDER)
        {
            doUpdateOrder((OrderDetailResp) getIntent().getSerializableExtra(AppConfig.BUNDLE_KEY_ORDER_DETAIL_RESP));

        }
        else if (type == TYPE_ADORDER)
        {
            doUpdateAdOrder((AdOrder) getIntent().getSerializableExtra(AppConfig.BUNDLE_KEY_ADORDER_DETAIL_RESP));
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        String keyword = searchEdit.getText().toString();
        if (actionId == EditorInfo.IME_ACTION_SEARCH)
        {
            InputMethodUtils.hideInputKeyboard(OrderSearchActivity.this, searchEdit);
            this.keyword = keyword;
            doRearSearch();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_scan:{
                captureAction();
            }
            break;
            case R.id.search_cancel_btn:{
                InputMethodUtils.hideInputKeyboard(OrderSearchActivity.this, searchEdit);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
            break;
        }
    }

    @Override
    public void onEvent(ScanResultPopWindow popWindow , int event) {

    }

    @Override
    protected void handleScanResult(String barCode) {
//        barCode = "95352YLW/BLU";

        if (StringUtils.isEmpty(barCode))
            return;
        SCLog.debug("resutl = " + barCode);
        this.keyword = barCode;
        searchEdit.setText(barCode);
        searchEdit.setSelection(barCode.length());
        doRearSearch();
    }

    private void doRearSearch() {
        productAdapter.clear();

        List<CartProduct> products = null;
        List<CartProduct> resultProducts = new ArrayList<>();

        if (type == TYPE_ORDER) {
            products = orderDetailResp.getProducts();
        } else {
            products = mAdOrder.getProducts();
        }

        if (StringUtils.isEmpty(keyword)) {
            productAdapter.addAll(products);
            productAdapter.notifyDataSetChanged();
        }
        else {
            for (CartProduct product : products) {
                if (product.isConain(keyword)) {
                    resultProducts.add(product);
                }
            }

            productAdapter.addAll(resultProducts);
            productAdapter.notifyDataSetChanged();
        }

    }

}
