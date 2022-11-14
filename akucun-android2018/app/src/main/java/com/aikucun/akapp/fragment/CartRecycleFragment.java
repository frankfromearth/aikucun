package com.aikucun.akapp.fragment;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.CartProductAdapter;
import com.aikucun.akapp.adapter.ProductAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.CartRecycleProductsCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.CartApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import okhttp3.Call;

import static com.aikucun.akapp.adapter.CartProductAdapter.PRODUCT_EVENT_BUY_AGAIN;

/**
 * Created by micker on 2017/9/2.
 */

public class CartRecycleFragment extends BaseProductFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener, ProductAdapter.OnItemEventListener,CartProductAdapter.OnItemEventListener {

    private int page = 0;

    private CartProductAdapter cartAdapter;

//    private StickyRecyclerHeadersDecoration headersDecoration;

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_cart_recycle;
    }


    @Override
    public void initView(View view)
    {
        super.initView(view);
        recyclerView.setRefreshListener(this);
        cartAdapter = new CartProductAdapter(getContext());
        cartAdapter.setOnItemEventListener(this);
        cartAdapter.setCartRecycle(true);
        recyclerView.setAdapter(cartAdapter);
    }

    @Override
    public void initData()
    {
        super.initData();

        productAdapter.clear();
        page = 0;
        requestCartRecycleProducts(page + 1);

        setEmptyText("暂无回收记录");
    }

    @Override
    public void onRefresh()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                page = 0;
                requestCartRecycleProducts(page + 1);
            }
        }, 300);
    }

    @Override
    public void onLoadMore()
    {
        requestCartRecycleProducts(page + 1);
    }

    @Override
    public void onEvent(int event, final CartProduct product, final int position) {
        switch (event) {
            case PRODUCT_EVENT_BUY_AGAIN: {

                ProductApiManager.buyProduct(getActivity(), product.getProductid(), product.getSkuid(), product.getRemark(), product.getCartproductid(), new ResultCallback(getActivity())
                {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse)
                    {
                        super.onApiSuccess(apiResponse, call, jsonResponse);
                        ((BaseActivity)getActivity()).cancelProgress();

                        product.setBuystatus(3);
                        cartAdapter.notifyDataSetChanged();
                        MToaster.showShort(getActivity(),"已添加到购物车",MToaster.IMG_INFO);
                        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_ADD_CART));
                    }
                });
            }
            break;
        }
    }

    private void requestCartRecycleProducts(int index)
    {
        recyclerView.setRefreshing(true);

        CartApiManager.getCartRecycleProducts(getActivity(), index, 50, new CartRecycleProductsCallback()
        {
            @Override
            public void onApiSuccess(List<CartProduct> pinpaiCarts, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(pinpaiCarts, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (pinpaiCarts.size() > 0) {
                    cartAdapter.clear();
                    cartAdapter.addAll(pinpaiCarts);
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
