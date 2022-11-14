package com.aikucun.akapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.OrderProductAdapter;
import com.aikucun.akapp.api.callback.AfterSaleCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

import static com.aikucun.akapp.adapter.OrderProductAdapter.PRODUCT_EVENTS_REMARK;

/**
 * 退款／售后 商品列表界面
 * Created by jarry on 17/6/30.
 */
public class AfterSaleActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, OrderProductAdapter
        .OnItemEventListener {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.aftersale_type_all)
    TextView typeAllText;

    @BindView(R.id.aftersale_type_quehuo)
    TextView typeQuehuoText;

    @BindView(R.id.aftersale_type_cancel)
    TextView typeCancelText;

    @BindView(R.id.aftersale_type_tuihuo)
    TextView typeTuihuoText;

    @BindView(R.id.aftersale_type_yituihuo)
    TextView typeYituihuoText;

//    @BindView(R.id.aftersale_type_yiloufa)
//    TextView typeYiLouFaText;

    private OrderProductAdapter productAdapter;

    private int page = 0;

    private int orderType = -2;
    private TextView selectTypeText;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.refund_and_aftersale);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);


        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = emptyView.findViewById(R.id.empty_text);
        textView.setText(R.string.no_goods);
        recyclerView.setEmptyView(emptyView);


        productAdapter = new OrderProductAdapter(this);
        productAdapter.setMore(R.layout.view_load_more, this);
        productAdapter.setNoMore(R.layout.view_nomore);
        productAdapter.setOnItemEventListener(this);

        recyclerView.setAdapter(productAdapter);
        recyclerView.setRefreshListener(this);
        //

    }

    @Override
    public void initData() {
        int type = getIntent().getIntExtra(AppConfig.BUNDLE_KEY_AFTERSALE_TYPE, -1);
//        if (type == 1 ) {
//            type = 4;
//        }
        setSelectedType(type);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aftersale;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshAfterSaleProducts();
            }
        }, 600);

    }

    @Override
    public void onLoadMore() {
        requestAfterSales(page + 1);
    }

    @Override
    @OnClick({R.id.aftersale_type_all, R.id.aftersale_type_quehuo, R.id
            .aftersale_type_tuihuo, R.id.aftersale_type_yituihuo, R.id.aftersale_type_cancel, R.id.btn_right})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_right:
                Intent intent = new Intent(this, AfterSaleListActivity
                        .class);
                startActivity(intent);
                break;
            case R.id.aftersale_type_all: {
                setSelectedType(-1);
            }
            break;
            case R.id.aftersale_type_quehuo: {
                setSelectedType(0);
            }
            break;
            case R.id.aftersale_type_cancel: {
                setSelectedType(1);
            }
            break;
            case R.id.aftersale_type_tuihuo: {
                setSelectedType(2);
            }
            break;
            case R.id.aftersale_type_yituihuo: {
                setSelectedType(3);
            }
            break;
        }
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
            selectTypeText = typeQuehuoText;
        } else if (type == 1) {
            selectTypeText = typeCancelText;
        } else if (type == 2) {
            selectTypeText = typeTuihuoText;
        } else if (type == 3) {
            selectTypeText = typeYituihuoText;
        }

//        else if (type == 4)
//        {
//            selectTypeText = typeYiLouFaText;
//        }

        selectTypeText.setSelected(true);
        selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        orderType = type;

        refreshAfterSaleProducts();
    }

    private void refreshAfterSaleProducts() {
        recyclerView.setRefreshing(true);
        page = 0;
        productAdapter.clear();
        requestAfterSales(page + 1);
    }

    private void requestAfterSales(int index) {
        OrderApiManager.afterSaleProducts(this, index, 20, orderType, new AfterSaleCallback() {
            @Override
            public void onApiSuccess(List<CartProduct> cartProducts, Call call, ApiResponse
                    jsonResponse) {
                super.onApiSuccess(cartProducts, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (cartProducts.size() > 0) {
                    page++;
                }
                productAdapter.addAll(cartProducts);
                recyclerView.setRefreshing(false);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onEvent(int event, final CartProduct product, final int position) {
        switch (event) {
            case PRODUCT_EVENTS_REMARK: {
                MyDialogUtils.showSetRemarkDialog(this, new MyDialogUtils.ISetRemarkLisenter() {
                    @Override
                    public void onBack(String content) {
                        requestRemarkProduct(product, content, position);
                    }
                });
//                final EditText remarkEt = new EditText(this);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
//                        .setNegativeButton(R.string.cancel, null);
//                builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        String remark = remarkEt.getText().toString();
//                        if (!StringUtils.isEmpty(remark)) {
//                            requestRemarkProduct(product, remark, position);
//                        }
//                    }
//                });
//                builder.show();
            }
            break;

        }
    }

    private void requestRemarkProduct(final CartProduct product, final String remark, final int position) {
        ProductApiManager.remarkProduct(this, product.getCartproductid(), remark, new ResultCallback(this) {
            @Override
            public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(apiResponse, call, jsonResponse);
                product.setRemark(remark);
                productAdapter.update(product, position);
                productAdapter.notifyDataSetChanged();
                MToaster.showShort(AfterSaleActivity.this, R.string.add_remark_success, MToaster.IMG_INFO);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);

            }
        });
    }
}
