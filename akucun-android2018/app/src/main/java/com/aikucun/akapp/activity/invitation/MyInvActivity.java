package com.aikucun.akapp.activity.invitation;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.MyInvAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.MyInvListCallback;
import com.aikucun.akapp.api.entity.MyInv;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;


/**
 * 消息列表界面
 * Created by jarry on 17/6/30.
 */
public class MyInvActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;


    private MyInvAdapter messageAdapter;

    private int page = 0;

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText("我的邀请记录");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        View emptyView = mInflater.inflate(R.layout.view_empty, null);
        TextView textView = (TextView) emptyView.findViewById(R.id.empty_text);
        textView.setText("暂无相关数据");
        recyclerView.setEmptyView(emptyView);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 20, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        messageAdapter = new MyInvAdapter(this);
        messageAdapter.setMore(R.layout.view_load_more, this);
        messageAdapter.setNoMore(R.layout.view_nomore);

        recyclerView.setAdapter(messageAdapter);
        recyclerView.setRefreshListener(this);

        messageAdapter.setOnItemEventListener(new MyInvAdapter.OnItemEventListener() {
            @Override
            public void onEvent(int event, MyInv address, int position) {
                requestItemRead(position);
            }
        });

    }

    @Override
    public void initData()
    {
        recyclerView.setRefreshing(true);
        page = 0;
        requestMessageList(page + 1);

    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_my_inv;
    }

    @Override
    public void onRefresh()
    {
        messageAdapter.clear();
        recyclerView.setRefreshing(true);
        page = 0;
        requestMessageList(page + 1);
    }

    @Override
    public void onLoadMore()
    {
        requestMessageList(page + 1);
    }


    private void requestItemRead(final int position) {
        final MyInv message = messageAdapter.getAllData().get(position);
        if (message.getStatu() != 1) {
            UsersApiManager.activiuser(this,message.getReferralcode(),message.getRuserid(), new JsonDataCallback(){

                @Override
                public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                    super.onApiSuccess(jsonObject, call, jsonResponse);
                    message.setStatu(1);
                    messageAdapter.notifyItemChanged(position);
                }

                @Override
                public void onApiFailed(String message, int code) {
                    super.onApiFailed(message, code);
                }
            });
        }
    }

    private void requestMessageList(int pageIndex)
    {
        UsersApiManager.pagemyreferral(this, pageIndex, 20, new MyInvListCallback()
        {
            @Override
            public void onApiSuccess(List<MyInv> messages, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(messages, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (messages.size() > 0)
                {
                    page++;
                }
                messageAdapter.addAll(messages);
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
