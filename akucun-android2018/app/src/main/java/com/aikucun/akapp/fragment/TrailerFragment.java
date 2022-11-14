package com.aikucun.akapp.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.TrailerCallBack;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.Trailer;
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
 * 今日预告
 */

public class TrailerFragment extends BaseTrailerFragment implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @Override
    public void initView(View view) {
        super.initView(view);
        recyclerView.setRefreshListener(this);

        trailerAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                trailerAdapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                trailerAdapter.resumeMore();
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        getTrailers();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_trailer_layout;
    }

    private void getTrailers() {
        LiveApiManager.getActivityTrailder(getActivity(), new TrailerCallBack() {
            @Override
            public void onSuccess(List<Trailer> trailers, Call call, Response response) {
                super.onSuccess(trailers, call, response);
                ArrayList<Product> products = new ArrayList<>();
                for (int i = 0;  i < trailers.size(); i++) {
                    products.add(Product.fromTrainer(trailers.get(i)));
                }
                trailerAdapter.clear();
                trailerAdapter.addAll(products);
            }
        });
    }


    @Override
    public void onRefresh() {
        getTrailers();
    }

    @Override
    public void onLoadMore() {

    }


    @Override
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);
        if (event.messageId.equals(AppConfig.MESSAGE_EVENT_REFRESH_TRAILER)){
            recyclerView.setRefreshing(true);
            getTrailers();
        }
    }
}
