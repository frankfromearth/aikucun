package com.aikucun.akapp.activity.logistics;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.LogisticsTraceAdapter;
import com.aikucun.akapp.api.callback.TraceListCallback;
import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.Logistics;
import com.aikucun.akapp.api.entity.OrderModel;
import com.aikucun.akapp.api.entity.Trace;
import com.aikucun.akapp.api.manager.LogisticsApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.widget.ColorFilterImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2017/12/7.
 * 物流查询
 */

public class QueryLogisticsActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener {


    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    LogisticsTraceAdapter adapter;
    private TranceHeaderView headerView;
    private String deliverId;
    private int logiscticsType;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.query_logistics);
        recyclerView.setRefreshListener(this);
        adapter = new LogisticsTraceAdapter(this);
        adapter.setNoMore(R.layout.view_nomore);
    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);
        View view = LayoutInflater.from(this).inflate(R.layout.header_logistics_trace_layout, null);
        deliverId = getIntent().getStringExtra("deliver_no");
        if (getIntent() != null && getIntent().hasExtra("type")) {
            String type = getIntent().getStringExtra("type");
            if (type.equals("adorder")) {
                AdOrder order_info = (AdOrder) getIntent().getSerializableExtra("order_info");
                if (order_info != null) {
                    headerView = new TranceHeaderView(order_info.getPinpai(), order_info.getWuliugongsi(), deliverId, order_info.getOdorderstr(), order_info.getCreatetime(), order_info.getPnum() + "", order_info.getPinpaiURL());
                    logiscticsType = order_info.getWuliuhao().startsWith("VB") ? 6 : 18;
                }
            } else {
                OrderModel orderModel = (OrderModel) getIntent().getSerializableExtra("order_info");
                Logistics logistics = (Logistics) getIntent().getSerializableExtra("logistics");
                headerView = new TranceHeaderView(orderModel.getPinpai(), logistics.getWuliugongsi(), deliverId, orderModel.getOrderid(), orderModel.getDingdanshijian(), orderModel.getShangpinjianshu() + "", orderModel.getPinpaiURL());
                logiscticsType = logistics.getWuliuhao().startsWith("VB") ? 6 : 18;
            }
            headerView.onBindView(view);
            adapter.addHeader(headerView);
            getLogisticsTraceData(deliverId, logiscticsType);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_query_logistics_layout;
    }

    /**
     * 获取物流信息
     *
     * @param deliverId 快递单号
     * @param logistics 快递公司类型：18-德邦，6-京东
     */
    private void getLogisticsTraceData(String deliverId, int logistics) {
        LogisticsApiManager.getListData(this, deliverId, logistics, new TraceListCallback() {
            @Override
            public void onSuccess(List<Trace> traces, Call call, Response response) {
                if (traces != null && traces.size() != 0) {
                    traces.get(0).setTop(true);
                    traces.get(traces.size() - 1).setEnd(true);
                }
                super.onSuccess(traces, call, response);
                recyclerView.setRefreshing(false);
                adapter.clear();
                adapter.addAll(traces);
            }

            @Override
            public void onApiFailed(String message, int code) {
                recyclerView.setRefreshing(false);
                super.onApiFailed(message, code);
            }
        });
    }

    private class TranceHeaderView implements RecyclerArrayAdapter.ItemView {

        private TextView copay_order_num_tv;
        private TextView brand_name_tv;
        private ColorFilterImageView headImage;
        private TextView delivery_no_tv;
        private TextView delivery_time_tv;
        private TextView logistics_no_tv;
        private TextView logistics_company_tv;
        private TextView total_count_tv;
        String pinpai, wuliugongsi, wuliuhao, delivery_no, delivery_time, total_count, pinpai_url;

        public TranceHeaderView(String _pinpai, String _wuliugongsi, String _wuliuhao, String _delivery_no, String _delivery_time, String _total_count, String _pinpai_url) {
            pinpai = _pinpai;
            wuliugongsi = _wuliugongsi;
            wuliuhao = _wuliuhao;
            delivery_no = _delivery_no;
            delivery_time = _delivery_time;
            total_count = _total_count;
            pinpai_url = _pinpai_url;
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.header_logistics_trace_layout, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            copay_order_num_tv = headerView.findViewById(R.id.copay_order_num_tv);
            brand_name_tv = headerView.findViewById(R.id.brand_name_tv);
            delivery_no_tv = headerView.findViewById(R.id.delivery_no_tv);
            headImage = headerView.findViewById(R.id.headImage);
            delivery_time_tv = headerView.findViewById(R.id.delivery_time_tv);
            logistics_no_tv = headerView.findViewById(R.id.logistics_no_tv);
            logistics_company_tv = headerView.findViewById(R.id.logistics_company_tv);
            total_count_tv = headerView.findViewById(R.id.total_count_tv);

            brand_name_tv.setText(pinpai);
            logistics_company_tv.setText(wuliugongsi);
            logistics_no_tv.setText(wuliuhao);
            delivery_no_tv.setText(delivery_no);
            delivery_time_tv.setText(delivery_time);
            total_count_tv.setText(MessageFormat.format(getResources().getString(R.string.total_count), total_count));
            Glide.with(QueryLogisticsActivity.this).load(pinpai_url + "?x-oss-process=image/resize,w_300,limit_0").diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(headImage);

            copay_order_num_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(logistics_no_tv.getText());
                    cm.setPrimaryClip(ClipData.newPlainText("akucun", logistics_no_tv.getText()));
                    MToaster.showShort(QueryLogisticsActivity.this, getResources().getString(R.string.logistics_no_copyed) + "\n" +
                            logistics_no_tv.getText(), MToaster.IMG_INFO);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        recyclerView.setRefreshing(true);
        getLogisticsTraceData(deliverId, logiscticsType);
    }
}
