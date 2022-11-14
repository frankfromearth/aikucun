package com.aikucun.akapp.activity.team;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.TeamMemberAdapter;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.TeamMembers;
import com.aikucun.akapp.api.manager.InviteManager;
import com.aikucun.akapp.base.BaseActivity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/30.
 */

public class MyTeamActivity1 extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {


    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    private TeamMemberAdapter adapter;
    private int pageNo = 1;
    private boolean canLoadMore = true;
    private int totalMembers = 0;
    private TextView team_member_tv;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.my_team);
        adapter = new TeamMemberAdapter(this,false);
        adapter.setMore(R.layout.view_load_more, this);
        adapter.setNoMore(R.layout.view_nomore);
    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);

        View view = mInflater.inflate(R.layout.my_team_header_layout1, null);
        MyTeamActivity1.TeamMemberHeaderView headerView = new MyTeamActivity1.TeamMemberHeaderView();
        headerView.onBindView(view);
        adapter.addHeader(headerView);

        getTeamMembers();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_layout;
    }

    private class TeamMemberHeaderView implements RecyclerArrayAdapter.ItemView {

        public TeamMemberHeaderView() {
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.my_team_header_layout1, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            team_member_tv = headerView.findViewById(R.id.team_member_tv);
        }
    }
    /**
     * 获取团队成员
     */
    private void getTeamMembers() {
        InviteManager.getTeamMembers(this, pageNo, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                recyclerView.setRefreshing(false);
                if (jsonObject != null) {
                    String pages = jsonObject.getString("pages");
                    JSONObject jsonObject1 = JSONObject.parseObject(pages);
                    totalMembers = jsonObject1.getIntValue("recordCount");
                    team_member_tv.setText(MessageFormat.format(getString(R.string.team_members), totalMembers));

                    String dataList = jsonObject.getString("items");
                    List<TeamMembers> teamMembers = JSON.parseArray(dataList, TeamMembers.class);
                    if (pageNo == 1) {
                        adapter.clear();
                    }
                    if (teamMembers != null && teamMembers.size() < 12) {
                        canLoadMore = false;
                    } else canLoadMore = true;
                    adapter.addAll(teamMembers);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                recyclerView.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getTeamMembers();
    }

    @Override
    public void onLoadMore() {
        if (canLoadMore) {
            pageNo++;
            getTeamMembers();
        } else adapter.stopMore();
    }


    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        } else {
            super.onBackPressed();
            AppManager.getAppManager().finishActivity(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

}
