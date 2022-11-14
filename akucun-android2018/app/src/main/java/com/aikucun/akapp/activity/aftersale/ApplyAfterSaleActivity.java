package com.aikucun.akapp.activity.aftersale;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.AfterSaleAddressActivity;
import com.aikucun.akapp.activity.AfterSaleDetailActivity;
import com.aikucun.akapp.activity.AfterSaleServiceActivity;
import com.aikucun.akapp.activity.reconciliation.ChooseLiveInfoAdapter;
import com.aikucun.akapp.activity.reconciliation.SearchProductActivity;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sucang.sczbar.CaptureActivity;
import okhttp3.Call;
import okhttp3.Response;
import rx.functions.Action1;

/**
 * Created by ak123 on 2018/2/1.
 */

public class ApplyAfterSaleActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    //活动布局
    @BindView(R.id.live_layout)
    View live_layout;
    @BindView(R.id.live_recyclerView)
    EasyRecyclerView live_recyclerView;
    @BindView(R.id.top_layout)
    LinearLayout top_layout;
    @BindView(R.id.live_name_text)
    TextView live_name_text;
    @BindView(R.id.live_iv)
    ImageView live_iv;

    //商品适配器
    private ApplyAfterSaleProductAdapter productAdapter;
    //活动适配器
    private ChooseLiveInfoAdapter liveInfoAdapter;
    private int livePageNo = 1;
    private int proudctPageNo = 1;
    //选中的活动
    private LiveInfo selectedLiveinfo;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.apply_after_sale);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = emptyView.findViewById(R.id.empty_text);
        textView.setText(R.string.no_goods);
        recyclerView.setEmptyView(emptyView);

        productAdapter = new ApplyAfterSaleProductAdapter(this);
        productAdapter.setMore(R.layout.view_load_more, this);
        productAdapter.setNoMore(R.layout.view_nomore);

        recyclerView.setAdapter(productAdapter);
        recyclerView.setRefreshListener(this);
        recyclerView.showEmpty();
        initLiveView();

        productAdapter.setOnItemEventListener(new ApplyAfterSaleProductAdapter.OnItemEventListener() {
            @Override
            public void onEvent(int event, final CartProduct product, final int position) {
                if (event == 1 && product != null) {
                    Intent intent = new Intent(ApplyAfterSaleActivity.this, AfterSaleServiceActivity.class);
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT, product);
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
                    intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product.getCartproductid());
                    startActivityForResult(intent, 100);
                    overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
                } else if (event == ApplyAfterSaleProductAdapter.PRODUCT_EVENTS_REMARK && product != null) {
                    MyDialogUtils.showSetRemarkDialog(ApplyAfterSaleActivity.this, new MyDialogUtils.ISetRemarkLisenter() {
                        @Override
                        public void onBack(String content) {
                            requestRemarkProduct(product, content, position);
                        }
                    });

//                    final EditText remarkEt = new EditText(ApplyAfterSaleActivity.this);
//                    remarkEt.setMaxLines(3);
//                    remarkEt.setHint(product.getRemark());
//                    AlertDialog.Builder builder = new AlertDialog.Builder(ApplyAfterSaleActivity.this);
//                    builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView
//                            (remarkEt).setNegativeButton(R.string.cancel, null);
//                    builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
//
//                        public void onClick(DialogInterface dialog, int which) {
//                            String remark = remarkEt.getText().toString();
//                            if (!StringUtils.isEmpty(remark)) {
//                                requestRemarkProduct(product, remark, position);
//                            }
//                        }
//                    });
//                    builder.show();
                }
            }
        });
    }

    /**
     * 设置备注信息
     * @param product
     * @param remark
     * @param position
     */
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

    @Override
    public void initData() {
        getLivesData();
    }

    private void initLiveView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        live_recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.TRANSPARENT, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        live_recyclerView.addItemDecoration(itemDecoration);
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
                selectedLiveinfo = liveInfoAdapter.getItem(position);
                if (selectedLiveinfo != null) {
                    top_layout.setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_right_search).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn_right_scan).setVisibility(View.VISIBLE);
                    Glide.with(ApplyAfterSaleActivity.this).load(selectedLiveinfo.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                            .ALL).into(live_iv);
                    live_name_text.setText(selectedLiveinfo.getPinpaiming());
                    proudctPageNo = 1;
                    getData(selectedLiveinfo.getLiveid());
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_after_sale_layout;
    }

    @Override
    @OnClick({R.id.btn_right_search, R.id.top_layout, R.id.btn_right_scan})
    public void onClick(View v) {
        super.onClick(v);
        final int id = v.getId();
        switch (id) {
            case R.id.btn_right_scan:
                captureAction(1010);
                break;
            case R.id.btn_right_search:
                if (selectedLiveinfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("liveInfoId", selectedLiveinfo.getLiveid());
                    bundle.putString("liveInfoName", selectedLiveinfo.getPinpaiming());
                    ActivityUtils.startActivity(ApplyAfterSaleActivity.this, SearchProductActivity.class, bundle);
                }
                break;
            case R.id.top_layout:
                live_layout.setVisibility(View.VISIBLE);
                break;
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
                        startActivityForResult(new Intent(ApplyAfterSaleActivity.this, CaptureActivity
                                .class), requestCode);
                    } else {
                        MToaster.showShort(ApplyAfterSaleActivity.this, R.string.camera_is_not_available, MToaster.IMG_INFO);
                    }
                } else {
                    MyDialogUtils.showPermissionsDialog(ApplyAfterSaleActivity.this, getString(R.string.camera));
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1010) {
            if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK) {
                Bundle bundle = data.getExtras();
                final String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
                if (!TextUtils.isEmpty(scanResult)) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("barcode", scanResult);
                    ActivityUtils.startActivity(ApplyAfterSaleActivity.this, SearchProductActivity.class, bundle1);
                }
            }

        } else if (requestCode == 100 && resultCode == RESULT_OK) {
            final int problem = data.getIntExtra("PROBLEM_TYPE", -1);
            final String cartProductId = data.getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);
            if (!StringUtils.isEmpty(cartProductId)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (problem > 1) {
                            showTuiHuoConfirm(cartProductId);
                        } else {
                            showAfterSaleDetail(cartProductId);
                        }
                    }
                }, 600);
            }
        }
    }

    @Override
    public void onRefresh() {
        if (selectedLiveinfo != null) {
            proudctPageNo = 1;
            getData(selectedLiveinfo.getLiveid());
        } else {
            recyclerView.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMore() {
        if (selectedLiveinfo != null) {
            proudctPageNo++;
            getData(selectedLiveinfo.getLiveid());
        } else {
            productAdapter.stopMore();
        }
    }


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

    /**
     * 获取活动列表
     */
    private void getLivesData() {
        UsersApiManager.getPageAfter(this, livePageNo, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                if (jsonObject != null) {
                    String list = jsonObject.getString("list");
                    List<LiveInfo> arrList = JSONObject.parseArray(list, LiveInfo.class);
                    if ((arrList == null || arrList.size() == 0) && livePageNo == 1) {
                        live_layout.setVisibility(View.GONE);
                        findViewById(R.id.btn_right_search).setVisibility(View.GONE);
                        findViewById(R.id.btn_right_scan).setVisibility(View.GONE);
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


    protected void showTuiHuoConfirm(final String cartProductId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("售后申请已提交").
                setMessage("您需要将问题货品寄回爱库存，现在填写退货快递单号吗 ？").
                setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ApplyAfterSaleActivity.this,
                                AfterSaleAddressActivity.class);
                        intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, cartProductId);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    protected void showAfterSaleDetail(final String cartProductId) {
        Intent intent = new Intent(ApplyAfterSaleActivity.this, AfterSaleDetailActivity.class);
        intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, cartProductId);
        startActivity(intent);

    }
}
