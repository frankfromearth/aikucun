package com.aikucun.akapp.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.CutGoodsCallBack;
import com.aikucun.akapp.api.entity.CutGoods;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.manager.LiveApiManager;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/3.
 * 今日切货
 */

public class CutGoodsFragment extends BaseCutGoodsFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @Override
    public void initView(View view) {
        super.initView(view);
        recyclerView.setRefreshListener(this);
        //productAdapter.singleZhuanChang = !StringUtils.isEmpty(this.liveId);

        cutGoodsAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                cutGoodsAdapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                cutGoodsAdapter.resumeMore();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        getCutGoodsLiving();

//        this.titleName = "爱库存";
//        List<Product> products = ProductManager.loadProducts(0, this.liveId);
//        productAdapter.clear();
//        productAdapter.addAll(products);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trailer_layout;
    }


    /**
     * 获取今日切货
     */
    private void getCutGoodsLiving() {
        LiveApiManager.getCutGoodsLiving(getActivity(), new CutGoodsCallBack() {
            @Override
            public void onSuccess(List<CutGoods> cutGoods, Call call, Response response) {
                super.onSuccess(cutGoods, call, response);
                ArrayList<Product> products = new ArrayList<>();
                for (int i = 0; i < cutGoods.size(); i++) {
                    products.add(CutGoods.fromCutGoods(cutGoods.get(i)));
                }
                cutGoodsAdapter.clear();
                cutGoodsAdapter.addAll(products);
            }
        });
    }


    @Override
    public void onRefresh() {
        getCutGoodsLiving();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equals(AppConfig.MESSAGE_EVENT_REFRESH_WHOLESALE)) {
            recyclerView.setRefreshing(true);
            getCutGoodsLiving();
        }
    }
}
