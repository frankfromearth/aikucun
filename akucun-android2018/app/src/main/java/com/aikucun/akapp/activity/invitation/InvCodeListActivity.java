package com.aikucun.akapp.activity.invitation;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.InvCodeListAdapter;
import com.aikucun.akapp.base.BaseActivity;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import butterknife.BindView;

/**
 * Created by ak123 on 2018/3/5.
 */

public class InvCodeListActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private InvCodeListAdapter adapter;
    private TextView residual_invitation_code_tv;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.inviting_friends);
    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);

        View view = mInflater.inflate(R.layout.my_team_header_layout1, null);
        InvCodeListHeaderView headerView = new InvCodeListHeaderView();
        headerView.onBindView(view);
        adapter.addHeader(headerView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_layout;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }


    private class InvCodeListHeaderView implements RecyclerArrayAdapter.ItemView {

        public InvCodeListHeaderView() {
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.invcode_list_header_layout, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            residual_invitation_code_tv = headerView.findViewById(R.id.residual_invitation_code_tv);
        }
    }
}
