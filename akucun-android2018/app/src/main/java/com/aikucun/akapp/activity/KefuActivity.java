package com.aikucun.akapp.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.KefuMsgAdapter;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.KefuMsgListCallback;
import com.aikucun.akapp.api.entity.KefuMsgItem;
import com.aikucun.akapp.api.manager.KefuApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 消息列表界面
 * Created by jarry on 17/6/30.
 */
public class KefuActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener,TextView.OnEditorActionListener
{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.search_edit)
    EditText searchEdit;

    @BindView(R.id.send_action_btn)
    TextView sendTv;

    private KefuMsgAdapter messageAdapter;

    private int oldXuHao = 0;

    private boolean didLoad = false;

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText("联系客服");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new KefuMsgAdapter(this);
        recyclerView.setRefreshListener(KefuActivity.this);

        recyclerView.setAdapter(messageAdapter);

        messageAdapter.setOnItemEventListener(new KefuMsgAdapter.OnItemEventListener() {
            @Override
            public void onEvent(int event, KefuMsgItem kefuMsgItem, int position) {
                requestItemRead(kefuMsgItem);
            }
        });

    }

    @Override
    public void initData()
    {
        recyclerView.setRefreshing(false);
        onLoadMore();
        InputMethodUtils.showInputKeyboard(getApplicationContext(),searchEdit);
        startTimer(5*1000);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_kefu_message;
    }

    @Override
    public void onRefresh()
    {
        recyclerView.setRefreshing(false);
        if (oldXuHao == 0) {
            return;
        }
        requestMessageList(oldXuHao);
    }

    @Override
    public void onLoadMore()
    {
        doReloadLoad();
    }

    private void doReloadLoad() {

        messageAdapter.clear();
        messageAdapter.notifyDataSetChanged();
        oldXuHao = 0;
        refreshMessageList();
    }

    @Override
    @OnClick({R.id.send_action_btn})

    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.send_action_btn:
                doSendMsg();
                break;
        }
    }

    private void doSendMsg() {
        String content = searchEdit.getEditableText().toString();
        if (StringUtils.isEmpty(content)) {

            MToaster.showShort(this,"内容不能为空!",MToaster.IMG_INFO);
            return;
        }
        pushMessageList(content);
        searchEdit.setText("");
        InputMethodUtils.hideInputKeyboard(KefuActivity.this, searchEdit);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        String keyword = searchEdit.getText().toString();

        if (actionId == EditorInfo.IME_ACTION_SEARCH)
        {
            doSendMsg();
        }
        return true;
    }

    private void requestItemRead(final KefuMsgItem kefuMsgItem) {
        if (kefuMsgItem.getStatu() != 1) {
            KefuApiManager.reciptMsgs(this,kefuMsgItem.getMsgid(), new ApiBaseCallback() {
                @Override
                public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                    super.onApiSuccess(o, call, jsonResponse);
                    kefuMsgItem.setStatu(1);
                    messageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onApiFailed(String message, int code) {
                    super.onApiFailed(message, code);
                }
            });
        }
    }

    private void requestMessageList(int index)
    {
        KefuApiManager.pullMsgs(this, index, new KefuMsgListCallback()
        {
            @Override
            public void onApiSuccess(List<KefuMsgItem> messages, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(messages, call, jsonResponse);
                recyclerView.setRefreshing(false);

                if (messages.size() > 0)
                {
                    KefuMsgItem item = messages.get(messages.size()-1);
                    oldXuHao = item.getXuhao();
                }
                messageAdapter.insertAll(messages,0);
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });

    }

    private void refreshMessageList()
    {
        KefuApiManager.refreshMsgs(this, new KefuMsgListCallback()
        {
            @Override
            public void onApiSuccess(List<KefuMsgItem> messages, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(messages, call, jsonResponse);
                recyclerView.setRefreshing(false);
                if (messages.size() > 0)
                {
                    KefuMsgItem item = messages.get(messages.size()-1);
                    oldXuHao = item.getXuhao();
                } else {
                    messages.add(KefuMsgItem.welcome());
                }
                messageAdapter.addAll(messages);
                recyclerView.scrollToPosition(messageAdapter.getCount()-1);
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });

    }
    private void pushMessageList(String content)
    {
        KefuApiManager.pushMsgs(this, content, new JsonDataCallback()
        {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                doReloadLoad();
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
            }
        });

    }

    private void checkMessageList()
    {
        KefuApiManager.checkMsgs(this, new JsonDataCallback()
        {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse)
            {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                int cnt = jsonObject.getInteger("cnt");
                if (cnt > 0 ) {
                    doReloadLoad();
                }
            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
            }
        });

    }

    @Override
    protected void onDestroy()
    {
        stopTimer();
        super.onDestroy();
    }

    /**
     * 定时跟踪任务
     */
    private Timer timer;
    private Handler timerHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            //
            stopTimer();
            //
            checkMessageList();
        }
    };

    private void startTimer(long time)
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                Message message = new Message();
                message.what = 1;
                timerHandler.sendMessage(message);
            }
        };

        timer.schedule(task, time);
    }

    private void stopTimer()
    {
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }


}
