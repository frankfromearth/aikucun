package com.aikucun.akapp.activity.reconciliation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.StateMentActivity;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.manager.DeliverApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/22.
 * 客户对账
 */

public class CustomerReconActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, CustomerLiveProductAdapter.OnItemEventListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.apply_bill_text)
    TextView apply_bill_text;
    @BindView(R.id.download_bill_text)
    TextView download_bill_text;
    @BindView(R.id.live_name_text)
    TextView live_name_text;
    @BindView(R.id.type_layout)
    LinearLayout type_layout;
    @BindView(R.id.top_layout)
    LinearLayout top_layout;
    //活动列表
    @BindView(R.id.live_recyclerView)
    EasyRecyclerView live_recyclerView;
    //活动View
    @BindView(R.id.live_layout)
    View live_layout;
    @BindView(R.id.live_iv)
    ImageView live_iv;

    private LiveInfo selectedLiveInfo;
    private CustomerLiveProductAdapter productAdapter;
    //活动适配器
    private ChooseLiveInfoAdapter liveInfoAdapter;
    private int livePageNo = 1;
    private int proudctPageNo = 1;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.customer_reconciliation);

        productAdapter = new CustomerLiveProductAdapter(this);
        productAdapter.setMore(R.layout.view_load_more, this);
        productAdapter.setNoMore(R.layout.view_nomore);
        productAdapter.setOnItemEventListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(productAdapter);
        recyclerView.showEmpty();
        initLiveView();
        //申请对账、下载对账单、更新对账单
        getLivesData();
    }

    @Override
    public void initData() {
        apply_bill_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBill();
            }
        });
        download_bill_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/1/23 下载对账单
                if (selectedLiveInfo != null && !TextUtils.isEmpty(selectedLiveInfo.getChecksheeturl())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", selectedLiveInfo.getChecksheeturl());
                    ActivityUtils.startActivity(CustomerReconActivity.this, StateMentActivity.class, bundle);
                }
            }
        });
        findViewById(R.id.btn_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedLiveInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("liveInfoId", selectedLiveInfo.getLiveid());
                    bundle.putString("liveInfoName", selectedLiveInfo.getPinpaiming());
                    ActivityUtils.startActivity(CustomerReconActivity.this, SearchProductActivity.class, bundle);
                }
            }
        });
        top_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                live_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_customer_recon_layout;
    }


    private void initLiveView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        live_recyclerView.setLayoutManager(layoutManager);
//        DividerDecoration itemDecoration = new DividerDecoration(Color.TRANSPARENT, 1, 0, 0);
//        itemDecoration.setDrawLastItem(true);
//        live_recyclerView.addItemDecoration(itemDecoration);
        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = emptyView.findViewById(R.id.empty_text);
        textView.setText(R.string.no_goods);
        live_recyclerView.setEmptyView(emptyView);

        liveInfoAdapter = new ChooseLiveInfoAdapter(this);
        liveInfoAdapter.setMore(R.layout.view_load_more, new RecyclerArrayAdapter.OnMoreListener() {
            @Override
            public void onMoreShow() {
                livePageNo++;
                getLivesData();
            }

            @Override
            public void onMoreClick() {
                livePageNo++;
                getLivesData();
            }
        });
        liveInfoAdapter.setNoMore(R.layout.view_nomore);

        live_recyclerView.setAdapter(liveInfoAdapter);
        live_recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                livePageNo = 1;
                getLivesData();
            }
        });
        liveInfoAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                live_layout.setVisibility(View.GONE);
                findViewById(R.id.btn_right).setVisibility(View.VISIBLE);
                selectedLiveInfo = liveInfoAdapter.getItem(position);
                if (selectedLiveInfo != null) {
                    proudctPageNo = 1;
                    Glide.with(CustomerReconActivity.this).load(selectedLiveInfo.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                            .ALL).into(live_iv);
                    live_name_text.setText(selectedLiveInfo.getPinpaiming());
                    getData(selectedLiveInfo.getLiveid());
                }
            }
        });
    }

    /**
     * 商品数据
     *
     * @param liveId
     */
    private void getData(String liveId) {
        UsersApiManager.getPageLiveProduct(this, proudctPageNo, liveId, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                recyclerView.setRefreshing(false);
                if (proudctPageNo == 1) {
                    productAdapter.clear();
                }
                if (jsonObject != null) {
                    String list = jsonObject.getString("list");
                    List<CartProduct> arrList = JSONObject.parseArray(list, CartProduct.class);
                    if ((arrList == null || arrList.size() == 0) && proudctPageNo == 1)
                        top_layout.setVisibility(View.GONE);
                    else top_layout.setVisibility(View.VISIBLE);
                    productAdapter.addAll(arrList);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
            }
        });
    }

    @Override
    public void onRefresh() {
        if (selectedLiveInfo != null) {
            proudctPageNo = 1;
            getData(selectedLiveInfo.getLiveid());
        } else {
            recyclerView.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMore() {
        if (selectedLiveInfo != null) {
            proudctPageNo++;
            getData(selectedLiveInfo.getLiveid());
        } else {
            productAdapter.stopMore();
        }
    }

    @Override
    public void onEvent(int event, final CartProduct product, final int position) {
        if (event == CustomerLiveProductAdapter.PRODUCT_EVENTS_REMARK) {

//            final EditText remarkEt = new EditText(this);
//            remarkEt.setHint(product.getRemark());
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView
//                    (remarkEt).setNegativeButton(R.string.cancel, null);
//            builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    String remark = remarkEt.getText().toString();
//                    if (!StringUtils.isEmpty(remark)) {
//                        requestRemarkProduct(product, remark, position);
//                    }
//                }
//            });
//            builder.show();
            MyDialogUtils.showSetRemarkDialog(this, new MyDialogUtils.ISetRemarkLisenter() {
                @Override
                public void onBack(String content) {
                    requestRemarkProduct(product, content, position);
                }
            });
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
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);

                    }
                });
    }

    private void updateBill() {
        showProgress("");
        DeliverApiManager.updateBill(this, selectedLiveInfo.getLiveid(), new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                if (jsonObject != null) {
                    int code = jsonObject.getIntValue("code");
                    if (code == 0) {
                        MToaster.showShort(CustomerReconActivity.this, R.string.success, MToaster.IMG_INFO);
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(CustomerReconActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }

    /**
     * 获取活动列表
     */
    private void getLivesData() {
        UsersApiManager.getPageAfter(this, livePageNo, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                live_recyclerView.setRefreshing(false);
                if (jsonObject != null) {
                    String list = jsonObject.getString("list");
                    List<LiveInfo> arrList = JSONObject.parseArray(list, LiveInfo.class);
                    if ((arrList == null || arrList.size() == 0) && livePageNo == 1) {
                        top_layout.setVisibility(View.GONE);
                        live_layout.setVisibility(View.GONE);
                    } else {
                        live_layout.setVisibility(View.VISIBLE);
                        if (livePageNo == 1) {
                            liveInfoAdapter.clear();
                        }
                        liveInfoAdapter.addAll(arrList);
                    }
                }
            }
        });
    }

}
