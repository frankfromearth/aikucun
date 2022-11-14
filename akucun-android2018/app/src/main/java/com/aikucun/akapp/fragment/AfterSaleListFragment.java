package com.aikucun.akapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.AfterSaleDetailActivity;
import com.aikucun.akapp.adapter.AfterSaleListAdapter;
import com.aikucun.akapp.api.callback.AfterSaleListCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.aikucun.akapp.api.manager.AfterSaleApiManager;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.utils.DisplayUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 售后列表页面
 * Created by jarry on 2017/6/1.
 */

public class AfterSaleListFragment extends BaseFragment implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener
{
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    public AfterSaleListAdapter adapter;

    private int page = 0;

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_after_sale_list;
    }


    @Override
    public void initView(View view)
    {
        super.initView(view);

        setEmptyText("暂无相关数据");
        recyclerView.setRefreshListener(this);

        adapter = new AfterSaleListAdapter(getActivity());
        adapter.setMore(R.layout.view_load_more, this);
        adapter.setNoMore(R.layout.view_nomore);

        adapter.setOnItemEventListener(new AfterSaleListAdapter.OnItemEventListener() {
            @Override
            public void onEvent(int event, AfterSaleItem afterSaleItem, int position) {
                Intent intent = new Intent(getActivity(), AfterSaleDetailActivity.class);
                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, afterSaleItem.getCartproductid());
                startActivityForResult(intent, 0);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, DisplayUtils.dip2px(getActivity(), 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void initData()
    {
        super.initData();
        doRefresh();
    }

    @Override
    public void onRefresh()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                doRefresh();
            }
        }, 600);
    }

    @Override
    public void onLoadMore() {
        requestListDatas(page + 1);
    }

    private void doRefresh() {
        page = 0;
        requestListDatas(page + 1);
    }

    public void setEmptyText(String text) {
        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);
        textView.setText(text);
        recyclerView.setEmptyView(emptyView);
    }

    private void requestListDatas(int index)
    {
        recyclerView.setRefreshing(true);

        AfterSaleApiManager.afterSaleList(getActivity(), index, 20, new AfterSaleListCallback()
        {
            @Override
            public void onApiSuccess(List<AfterSaleItem> products, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(products, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (0 == page) {
                    adapter.clear();
                }
                adapter.addAll(products);
                if (products.size() > 0)
                {
                    page++;
                }
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

}
