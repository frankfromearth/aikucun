package com.aikucun.akapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.OrderProductAdapter;
import com.aikucun.akapp.api.callback.AfterSaleInfoCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.aikucun.akapp.api.manager.AfterSaleApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 售后详情页面
 * Created by jarry on 17/6/30.
 */
public class AfterSaleDetailActivity extends BaseActivity {

    public static final int AFTER_SALE_DETAIL_REQUEST = 1013;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.toolbar_layout)
    RelativeLayout toolbarLayout;


    private ASHeaderView headerView;
    private AfterSaleItem afterSaleItem;

    private String cartProductId;

    private OrderProductAdapter productAdapter;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.after_sale_details);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        productAdapter = new OrderProductAdapter(this);
        recyclerView.setAdapter(productAdapter);
        headerView = new ASHeaderView();
        productAdapter.addHeader(headerView);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requesDatas();
                    }
                }, 100);
            }
        });
    }

    @Override
    public void initData() {
        afterSaleItem = (AfterSaleItem) getIntent().getSerializableExtra("afterSaleItem");
        cartProductId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);
        requesDatas();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aftersale_detail;
    }


    @Override
    @OnClick({R.id.tv_contact})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_contact: {
                startActivity(new Intent(this, KefuActivity.class));
            }
            break;
        }
    }

    private void requesDatas() {

        if (StringUtils.isEmpty(cartProductId)) {
            return;
        }
        showProgress("");
        AfterSaleApiManager.afterSaleQueryInfo(this, cartProductId, new AfterSaleInfoCallback() {
            @Override
            public void onApiSuccess(AfterSaleItem afterSaleItem, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(afterSaleItem, call, jsonResponse);
                AfterSaleDetailActivity.this.afterSaleItem = afterSaleItem;
                headerView.updateData();
                recyclerView.setRefreshing(false);
                cancelProgress();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                recyclerView.setRefreshing(false);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            requesDatas();
        }
    }

    public class ASHeaderView implements RecyclerArrayAdapter.ItemView, View.OnClickListener {


        TextView serverNo;
        TextView orderNumber;
        TextView statusText;
        TextView serverTime;
        LinearLayout llServerProgress;
        TextView tvWuliu;
        TextView tvDetail;
        TextView tvKuaidBtn;
        RelativeLayout rlKuaidiInfo;
        TextView tvLiuyan;
        TextView tvQuestionDesc;
        ImageView image0;

        LinearLayout llShenhe;
        ImageView iv0;
        TextView tv0;
        LinearLayout ll0;

        View line1;
        ImageView iv1;
        TextView tv1;
        LinearLayout ll1;

        View line2;
        ImageView iv2;
        TextView tv2;
        LinearLayout ll2;

        View line3;
        ImageView iv3;
        TextView tv3;
        LinearLayout ll3;

        TextView refreshButton;

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.activity_aftersale_detail_head, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {


            serverNo =   headerView.findViewById(R.id.server_no);
            orderNumber =   headerView.findViewById(R.id.order_number);
            statusText =  headerView.findViewById(R.id.status_text);
            serverTime =  headerView.findViewById(R.id.server_time);
            llServerProgress =  headerView.findViewById(R.id.ll_server_progress);
            tvWuliu = headerView.findViewById(R.id.tv_server_pro_status_more);
            tvDetail = headerView.findViewById(R.id.tv_server_pro_status);
            tvKuaidBtn =headerView.findViewById(R.id.tv_kuaid_btn);
            rlKuaidiInfo = headerView.findViewById(R.id.rl_kuaidi_info);
            tvLiuyan = headerView.findViewById(R.id.tv_liuyan);
            tvQuestionDesc = headerView.findViewById(R.id.tv_question_desc);
            image0 = headerView.findViewById(R.id.image0);
            llShenhe = headerView.findViewById(R.id.ll_shenhe);
            iv0 = headerView.findViewById(R.id.iv0);
            tv0 = headerView.findViewById(R.id.tv0);
            ll0 = headerView.findViewById(R.id.ll0);
            line1 = headerView.findViewById(R.id.line1);
            iv1 = headerView.findViewById(R.id.iv1);
            tv1 = headerView.findViewById(R.id.tv1);
            ll1 = headerView.findViewById(R.id.ll1);
            line2 = headerView.findViewById(R.id.line2);
            iv2 = headerView.findViewById(R.id.iv2);
            tv2 = headerView.findViewById(R.id.tv2);
            ll2 = headerView.findViewById(R.id.ll2);
            line3 = headerView.findViewById(R.id.line3);
            iv3 = headerView.findViewById(R.id.iv3);
            ll3 = headerView.findViewById(R.id.ll3);
            refreshButton = headerView.findViewById(R.id.refresh_status);

            tvKuaidBtn.setOnClickListener(this);
            refreshButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.tv_kuaid_btn: {
                    buttonAction();
                }
                break;
                case R.id.refresh_status: {
                    requesDatas();
                }
                break;
            }
        }


        private void updateData() {

            if (afterSaleItem == null)
                return;

            serverNo.setText(getResources().getString(R.string.service_number) + afterSaleItem.getServicehao());
            orderNumber.setText(getResources().getString(R.string.order_number_text) + afterSaleItem.getOrderid());
            statusText.setText(afterSaleItem.serviceTypeText());
            serverTime.setText(getResources().getString(R.string.apply_time1)+ afterSaleItem.getShenqingshijian());
//        tv_server_pro_status.setText(afterSaleItem.statusText());

            if (StringUtils.isEmpty(afterSaleItem.getServicedesc())) {
                llShenhe.setVisibility(View.GONE);
            } else {
                llShenhe.setVisibility(View.VISIBLE);
                tvLiuyan.setText(afterSaleItem.getServicedesc());
            }
            tvQuestionDesc.setText(afterSaleItem.getWentimiaoshu());

            if (!StringUtils.isEmpty(afterSaleItem.getPingzheng())) {

                image0.setVisibility(View.VISIBLE);
                Glide.with(AfterSaleDetailActivity.this).load(afterSaleItem.getPingzheng()).diskCacheStrategy(DiskCacheStrategy
                        .ALL).placeholder(R.color.color_bg_image).into(image0);
            } else {
                image0.setVisibility(View.GONE);
            }

            configStep();
            configProgressState();
            configActionButton();
        }

        private void updateDetail(String text) {

            if (StringUtils.isEmpty(text)) {
                tvDetail.setVisibility(View.GONE);
            } else {
                tvDetail.setText(text);
                tvDetail.setVisibility(View.VISIBLE);
            }
        }

        private void updateProgressMore(String text) {

            if (StringUtils.isEmpty(text)) {
                tvWuliu.setVisibility(View.GONE);
            } else {
                tvWuliu.setText(text);
                tvWuliu.setVisibility(View.VISIBLE);
            }
        }

        private void updateButtonDetail(String text) {

            if (StringUtils.isEmpty(text)) {
                tvKuaidBtn.setVisibility(View.GONE);
            } else {
                tvKuaidBtn.setText(text);
                tvKuaidBtn.setVisibility(View.VISIBLE);
            }
        }


        private void configStep() {

            int iRedDrawable = R.drawable.quan;
            int iGreenDrawable = R.drawable.dui;
            Drawable redColor = getResources().getDrawable(R.color.color_accent);
            Drawable greenColor = getResources().getDrawable(R.color.green1);

            iv1.setImageResource(iGreenDrawable);
            tv1.setText(getResources().getString(R.string.after_sales_audit));
            line1.setBackgroundColor(iGreenDrawable);

            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            updateDetail("");
            int zhuantai = afterSaleItem.getChulizhuangtai();
            if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusRejected.getIndex()) {
                //审核未通过
                line1.setBackground(redColor);
                tv1.setText(getResources().getString(R.string.aftersale_refused));
                iv1.setImageResource(iRedDrawable);
                updateDetail(getResources().getString(R.string.aftersale_audit_failed));
            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusSubmit.getIndex()) {
                //申请已提交
                tv1.setText(getResources().getString(R.string.after_sales_audit));
            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusPending.getIndex()) {
                //售后处理中
                line2.setBackground(redColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);
                tv2.setText(getResources().getString(R.string.ftersale_processing));
                iv2.setImageResource(iRedDrawable);
            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoPending.getIndex()) {
                //退货处理中
                line2.setBackground(redColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);
                tv2.setText(getResources().getString(R.string.return_processing));
                iv2.setImageResource(iRedDrawable);
                updateDetail(getResources().getString(R.string.return_processing));
            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusLoufaBufa.getIndex()) {
                //漏发已补发
                line2.setBackground(greenColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                tv2.setText(getResources().getString(R.string.reissued));
                iv2.setImageResource(iGreenDrawable);

                updateDetail(getResources().getString(R.string.goods_reissued));


            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusLoufaTuikuan.getIndex()) {
                //漏发已退款
                line2.setBackground(greenColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                tv2.setText(getResources().getString(R.string.refunded));
                iv2.setImageResource(iGreenDrawable);

            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoBufa.getIndex()) {
                //退货已补发
                line2.setBackground(greenColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                tv2.setText(getResources().getString(R.string.returned_goods));
                iv2.setImageResource(iGreenDrawable);

                line3.setBackground(greenColor);
                line3.setVisibility(View.VISIBLE);
                ll3.setVisibility(View.VISIBLE);

                tv3.setText(getResources().getString(R.string.reissued));
                iv3.setImageResource(iGreenDrawable);


                updateDetail(getResources().getString(R.string.goods_reissued));


            } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoTuikuan.getIndex()) {
                //退货已退款
                line2.setBackground(greenColor);
                line2.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.VISIBLE);

                tv2.setText(getResources().getString(R.string.returned_goods));
                iv2.setImageResource(iGreenDrawable);

                line3.setBackground(greenColor);
                line3.setVisibility(View.VISIBLE);
                ll3.setVisibility(View.VISIBLE);

                tv3.setText(getResources().getString(R.string.refunded));
                iv3.setImageResource(iGreenDrawable);
            }
        }

        private void configProgressState() {

            int zhuantai = afterSaleItem.getChulizhuangtai();

            if (afterSaleItem.getServicetype() <= 2) {
                if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusSubmit.getIndex()) {
                    updateDetail("漏发申请已提交, 客服审核中");
                } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusPending.getIndex()) {
                    updateDetail("漏发申请审核已通过, 售后处理中");
                } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusLoufaTuikuan.getIndex()) {
                    updateDetail("漏发申请审核已通过, 已退款到您的账户余额");
                }
            } else {

                if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusSubmit.getIndex() ||
                        zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusPending.getIndex() ) {

                    updateButtonDetail("填写退货单号");


                    if (!StringUtils.isEmpty(afterSaleItem.getRefundhao())) {
                        updateDetail("售后处理中");

                    } else {
                        updateDetail("请填写退货的物流单号");
                    }

                } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusLoufaTuikuan.getIndex()) {
                    updateDetail("已退款到您的账户余额");
                } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoTuikuan.getIndex()) {
                    updateDetail("客服已收到您退回的货品，已退款到您的账户余额");
                } else if (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoPending.getIndex()) {
                    updateDetail("退货处理中");
                }
            }
        }

        private void configActionButton() {

            int zhuantai = afterSaleItem.getChulizhuangtai();

            if (!StringUtils.isEmpty(afterSaleItem.getReissuehao())) {
                updateButtonDetail("查看物流");
                updateProgressMore("补发单号： " + afterSaleItem.getReissuecorp() + " " + afterSaleItem.getReissuehao());

            } else if (!StringUtils.isEmpty(afterSaleItem.getRefundhao()) &&
                    (zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusPending.getIndex() ||
                            zhuantai == AfterSaleItem.eASaleStatus.ASaleStatusTuihuoPending.getIndex())) {
                updateButtonDetail("查看退货物流");
                updateProgressMore("退货单号： " + afterSaleItem.getRefundcorp() + " " + afterSaleItem.getRefundhao());
            }
        }

        private void buttonAction() {

            if (!StringUtils.isEmpty(afterSaleItem.getReissuehao())) {

                Intent intent = new Intent(AfterSaleDetailActivity.this, WuLiuDetailActivity.class);
                intent.putExtra("title","快递查询");
                intent.putExtra("wuliu",afterSaleItem.getReissuehao());
                startActivity(intent);
            } else if (!StringUtils.isEmpty(afterSaleItem.getRefundhao())) {

                Intent intent = new Intent(AfterSaleDetailActivity.this, WuLiuDetailActivity.class);
                intent.putExtra("title","快递查询");
                intent.putExtra("wuliu",afterSaleItem.getRefundhao());
                startActivity(intent);
            } else {

                Intent intent = new Intent(AfterSaleDetailActivity.this, AfterSaleAddressActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, afterSaleItem.getCartproductid());
                startActivityForResult(intent, AFTER_SALE_DETAIL_REQUEST);
            }
        }
    }

}
