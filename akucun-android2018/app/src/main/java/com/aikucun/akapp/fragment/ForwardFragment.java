package com.aikucun.akapp.fragment;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.ProductAdapter;
import com.aikucun.akapp.adapter.RecycleFooterView;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.ForwardListCallback;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import okhttp3.Call;

/**
 * 已转发列表页面
 * Created by jarry on 2017/6/1.
 */

public class ForwardFragment extends BaseProductFragment implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener, ProductAdapter
        .OnItemEventListener
{

    private RecycleFooterView footerView;
    private int page = 0;

    private ForwardHeaderView forwardHeaderView;

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_forward;
    }


    @Override
    public void initView(View view)
    {
        super.initView(view);

        forwardHeaderView = new ForwardHeaderView();
        setEmptyText("暂无相关转发");

        productAdapter.hideForward = true;
//        productAdapter.addHeader(new ForwardHeaderView());

        recyclerView.setRefreshListener(this);
        forwardHeaderView.onBindView(view);
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
    public void onLoadMore()
    {
        requestForwardProducts(page + 1);
    }

    private void doRefresh() {
        page = 0;
        requestForwardProducts(page + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {

        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_ADD_FORWARD)) {
            doRefresh();
        }
    };


    private void requestForwardProducts(int index)
    {
        recyclerView.setRefreshing(true);

        ProductApiManager.forwardListProduct(getActivity(), index, 20, new ForwardListCallback()
        {
            @Override
            public void onApiSuccess(List<Product> products, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(products, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (0 == page) {
                    productAdapter.clear();
                }
                productAdapter.addAll(products);
                if (products.size() > 0)
                {
                    page++;
                }
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_CLEAR_FORWARD));
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    /**
     * 已转发 Header View
     */
    public class ForwardHeaderView
    {

        public void onBindView(View headerView)
        {
            TextView yizhuanfaText =  headerView.findViewById(R.id.header_yizhuanfa);
            TextView jiangliText =  headerView.findViewById(R.id.header_yijiangli);
            TextView kedikouText =  headerView.findViewById(R.id.header_kedikou);
            TextView yidikouText =  headerView.findViewById(R.id.header_yidikou);
            TextView userNoTextView =  headerView.findViewById(R.id.header_user_no);

            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            if (userInfo != null)
            {
                // FIXME: 2018/1/4
//                userNoTextView.setText("代购编号： " + userInfo.getYonghubianhao());
//                yizhuanfaText.setText("已转发： " + userInfo.getForwardcount());
//                int jiangli = userInfo.getKeyongdikou() + userInfo.getYiyongdikou();
//                jiangliText.setText("已奖励： " + jiangli);
//                kedikouText.setText("可抵扣余额： " + userInfo.getKeyongdikou());
//                yidikouText.setText("已抵扣金额： " + userInfo.getYiyongdikou());
            }
        }
    }
}
