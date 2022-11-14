package com.aikucun.akapp.fragment;

import android.os.Handler;
import android.support.v4.util.ArraySet;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.ProductAdapter;
import com.aikucun.akapp.adapter.RecycleFooterView;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.FollowListCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import okhttp3.Call;

/**
 * 已关注列表页面
 * Created by jarry on 2017/6/1.
 */

public class FollowFragment extends BaseProductFragment implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, ProductAdapter
        .OnItemEventListener {

    private RecycleFooterView footerView;
    private int page = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_follow;
    }


    @Override
    public void initView(View view) {
        super.initView(view);

        setEmptyText("暂无相关关注列表");
        productAdapter.hideForward = false;
        recyclerView.setRefreshListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        doRefresh();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doRefresh();
            }
        }, 600);
    }

    @Override
    public void onLoadMore() {
        requestProducts(page + 1);
    }

    private void doRefresh() {
        page = 0;
        requestProducts(page + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {

        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_FOLLOW_STATUS)) {
            doRefresh();
        }
    }

    ;

    private void onFollowListDidDownload(List<Product> products) {
        recyclerView.setRefreshing(false);

        if (0 == page) {
            productAdapter.clear();
        }
        if (null == products || products.size() == 0) {
            productAdapter.addAll(new ArraySet<Product>());
            return;
        }
        productAdapter.addAll(products);
        if (products.size() > 0) {
            page++;
        }
        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_FOLLOW_CLEAR_STATUS));
    }


    private void requestProducts(int index) {
        recyclerView.setRefreshing(true);

        ProductApiManager.followListProduct(getActivity(), index, 20, new FollowListCallback() {
            @Override
            public void onApiSuccess(List<Product> products, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(products, call, jsonResponse);

                updateSKUS(products, new UpdateSKUListener() {
                    @Override
                    public void onUpdateSuccess(List<Product> products) {
                        onFollowListDidDownload(products);
                    }

                    @Override
                    public void onUpdateFailed(List<Product> products) {
                        onFollowListDidDownload(products);
                    }
                });

            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }


    @Override
    public boolean isNeedToRemoveUnfollowdProduct() {
        return true;
    }
}
