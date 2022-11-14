package com.aikucun.akapp.activity;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.MessageAdapter;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.MsgListCallback;
import com.aikucun.akapp.api.entity.Message;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.MsgApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 消息列表界面
 * Created by jarry on 17/6/30.
 */
public class MessageActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;


    private MessageAdapter messageAdapter;

    private int page = 0;

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText("消息列表");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        messageAdapter = new MessageAdapter(this);
        messageAdapter.setMore(R.layout.view_load_more, this);
        messageAdapter.setNoMore(R.layout.view_nomore);

        recyclerView.setAdapter(messageAdapter);
        recyclerView.setRefreshListener(this);

        messageAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
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
        return R.layout.activity_message;
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

    @Override
    @OnClick({R.id.btn_right})

    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                requestAllMessageRead();
                break;
        }
    }

    private void requestAllMessageRead() {
        MsgApiManager.readAllMsg(this,new JsonDataCallback(){
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                page = 0;
                messageAdapter.clear();
                requestMessageList(page + 1);
                UserInfo userInfo = AppContext.getInstance().getUserInfo();
                if (userInfo != null) {
                    userInfo.setUnreadnum(0);
                }
            }
        });
    }

    private void requestItemRead(final int position) {
        final Message message = messageAdapter.getAllData().get(position);
        if (message.getReadflag() != 1) {
            MsgApiManager.readMsg(this,message.getMsgid(), new JsonDataCallback(){

                @Override
                public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                    super.onApiSuccess(jsonObject, call, jsonResponse);
                    message.setReadflag(1);
                    messageAdapter.notifyItemChanged(position);
                    UserInfo userInfo = AppContext.getInstance().getUserInfo();
                    if (userInfo != null) {
                        userInfo.setUnreadnum(userInfo.getUnreadnum()-1);
                    }
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
        MsgApiManager.getMsgList(this, pageIndex, 20, new MsgListCallback()
        {
            @Override
            public void onApiSuccess(List<Message> messages, Call call, ApiResponse jsonResponse)
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
