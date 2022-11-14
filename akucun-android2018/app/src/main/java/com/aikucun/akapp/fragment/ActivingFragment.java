package com.aikucun.akapp.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ActivingCallBack;
import com.aikucun.akapp.api.callback.SyncProductsCallback;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.manager.LiveApiManager;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.StringUtils;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/3.
 * 活动中
 */

public class ActivingFragment extends BaseActivingFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener {
    String liveId;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @Override
    public void initView(View view) {
        super.initView(view);
        recyclerView.setRefreshListener(this);
        activingAdapter.singleZhuanChang = !StringUtils.isEmpty(this.liveId);

        activingAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                activingAdapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                activingAdapter.resumeMore();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        getActivityToday();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_activing_layout;
    }


    /**
     * 获取活动中的商品
     */
    private void getActivityToday() {
        LiveApiManager.getActivityToday(getActivity(), new ActivingCallBack() {
            @Override
            public void onSuccess(List<LiveInfo> liveInfos, Call call, Response response) {
                super.onSuccess(liveInfos, call, response);
                ArrayList<Product> products = new ArrayList<>();
                if (liveInfos != null) {
                    for (int i = 0; i < liveInfos.size(); i++) {
                        products.add(Product.fromLiveInfo(liveInfos.get(i)));
                    }
                }
                if (ProductManager.getInstance().getForwardIndex() == 1 && products.size()> 0 && products.get(0) != null){
                    syncProducts(products.get(0).getLiveid());
                    ProductManager.getInstance().setLiveId(products.get(0).getLiveid());
                    ProductManager.getInstance().setForwardIndex(products.get(0).getXuhao());
                }

                recyclerView.setRefreshing(false);
                activingAdapter.clear();
                activingAdapter.addAll(products);
            }
        });
    }

    private void syncProducts(String liveId) {
        Product product = ProductManager.getInstance().getLastXuHaoProduct(liveId);
        int lastId = 0;
        if (product != null) lastId = product.getLastxuhao();
        LiveApiManager.syncProductsByLiveId(getActivity(), liveId, lastId + "", new SyncProductsCallback() {
        });
    }

    @Override
    public void onRefresh() {
        getActivityToday();
    }

    @Override
    public void onLoadMore() {
        recyclerView.setRefreshing(false);
    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equals(AppConfig.MESSAGE_EVENT_REFRESH_ACTIVING_LIVES)) {
            recyclerView.setRefreshing(true);
            getActivityToday();
        }
    }
}
