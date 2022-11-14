package com.aikucun.akapp.activity.bill;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.BillListAdapter;
import com.aikucun.akapp.api.callback.BillListCallback;
import com.aikucun.akapp.api.entity.Bill;
import com.aikucun.akapp.api.manager.OrderApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/9.
 * 账单详情
 */

public class BillListActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener  {
    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private BillListAdapter billListAdapter;
    private int page = 1;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText.setText(R.string.bill_info);

        billListAdapter = new BillListAdapter(this);
        billListAdapter.setMore(R.layout.view_load_more, this);
        billListAdapter.setNoMore(R.layout.view_nomore);

    }

    @Override
    public void initData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerDecoration itemDecoration = new DividerDecoration(Color.WHITE, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(billListAdapter);
        recyclerView.setRefreshing(true);
        page = 1;
        getData();

        billListAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bill bill = billListAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("bill", bill);
                ActivityUtils.startActivity(BillListActivity.this, BillDetailActivity.class, bundle);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_list;
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    @Override
    public void onLoadMore() {
        recyclerView.setRefreshing(false);
        page++;
        getData();
    }

    private void getData() {
        OrderApiManager.getBillList(this, page, new BillListCallback() {
            @Override
            public void onSuccess(List<Bill> bills, Call call, Response response) {
                super.onSuccess(bills, call, response);
                recyclerView.setRefreshing(false);
                if (page == 1) {
                    billListAdapter.clear();
                }
                billListAdapter.addAll(bills);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }
}
