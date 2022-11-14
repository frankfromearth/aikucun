package com.aikucun.akapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.AccountAdapter;
import com.aikucun.akapp.api.callback.AccountRecordsCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.entity.AccountRecord;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by jarry on 2017/6/21.
 */

public class AccountActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener

{
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recharge_tv)
    TextView recharge_tv;

    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    @BindView(R.id.user_account_amount)
    TextView accountAmountText;

    private AccountAdapter accountAdapter;

    private int page = 0;

    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.account_balance_details);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, 1, 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);

        accountAdapter = new AccountAdapter(this);
        accountAdapter.setMore(R.layout.view_load_more, this);
        accountAdapter.setNoMore(R.layout.view_nomore);

        recyclerView.setAdapter(accountAdapter);
        recyclerView.setRefreshListener(this);

    }

    @Override
    public void initData()
    {
        // FIXME: 2018/1/4 
       // UserAccount account = AppContext.getInstance().getUserInfo().getAccount();
        //accountAmountText.setText(StringUtils.getPriceString(account.getKeyongyue()));

        recyclerView.setRefreshing(true);
        page = 0;
        requestAccountRecords(page + 1);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_account;
    }

    @Override
    public void onRefresh()
    {
        accountAdapter.clear();
        recyclerView.setRefreshing(true);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                page = 0;
                requestAccountRecords(page + 1);
            }
        }, 500);
    }

    @Override
    @OnClick({R.id.recharge_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge_tv: {
                Intent intent = new Intent(this, RechargeActivity.class);
                startActivityForResult(intent,0);
            }
            break;
        }
    }

    @Override
    public void onLoadMore()
    {
        requestAccountRecords(page + 1);
    }

    private void requestAccountRecords(int pageIndex)
    {
        UsersApiManager.accountRecords(this, pageIndex, 20, new AccountRecordsCallback()
        {
            @Override
            public void onApiSuccess(List<AccountRecord> accountRecords, Call call, ApiResponse
                    jsonResponse)
            {
                super.onApiSuccess(accountRecords, call, jsonResponse);

                recyclerView.setRefreshing(false);

                if (accountRecords.size() > 0)
                {
                    page++;
                }
                accountAdapter.addAll(accountRecords);

            }

            @Override
            public void onApiFailed(String message, int code)
            {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onRefresh();
    }
}
