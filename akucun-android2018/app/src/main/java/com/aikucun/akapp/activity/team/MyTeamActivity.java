package com.aikucun.akapp.activity.team;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.WebViewActivity;
import com.aikucun.akapp.adapter.TeamMemberAdapter;
import com.aikucun.akapp.api.callback.TeamInfoCallBack;
import com.aikucun.akapp.api.callback.TeamMembersCallBack;
import com.aikucun.akapp.api.entity.TeamInfo;
import com.aikucun.akapp.api.entity.TeamMembers;
import com.aikucun.akapp.api.manager.InviteManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import me.trojx.dancingnumber.DancingNumberView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/16.
 * 我的团队
 */

public class MyTeamActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {


    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;

    LinearLayout level_layout;
    //会员等级
    TextView level_text;
    //团队销售额
    DancingNumberView team_sales_volume_text;
    //待入账返利
    TextView paid_and_rebate_text;
    //累计入账
    TextView total_paid_and_rebate_text;
    LinearLayout myteam_detailed_rules_layout;
    ImageView play_video_iv;
    private TeamInfo mTeamInfo;
    private TeamMemberAdapter adapter;
    private int pageNo = 1;
    private boolean canLoadMore = true;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.my_team);
        adapter = new TeamMemberAdapter(this,true);
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

        View view = mInflater.inflate(R.layout.my_team_header_layout, null);
        MyTeamActivity.TeamMemberHeaderView headerView = new MyTeamActivity.TeamMemberHeaderView();
        headerView.onBindView(view);
        adapter.addHeader(headerView);

        getMyTeamInfo();
        getTeamMembers();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_layout;
    }

    @Override
    public void onLoadMore() {
        if (canLoadMore) {
            pageNo++;
            getTeamMembers();
        }else adapter.stopMore();
    }


    private class TeamMemberHeaderView implements RecyclerArrayAdapter.ItemView {

        public TeamMemberHeaderView() {
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.my_team_header_layout, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            level_layout = headerView.findViewById(R.id.level_layout);
            level_text = headerView.findViewById(R.id.level_text);
            team_sales_volume_text = headerView.findViewById(R.id.team_sales_volume_text);
            paid_and_rebate_text = headerView.findViewById(R.id.paid_and_rebate_text);
            total_paid_and_rebate_text = headerView.findViewById(R.id.total_paid_and_rebate_text);
            myteam_detailed_rules_layout = headerView.findViewById(R.id.myteam_detailed_rules_layout);
            play_video_iv = headerView.findViewById(R.id.play_video_iv);
            headerView.findViewById(R.id.team_rebate_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.startActivity(MyTeamActivity.this, TeamRebateActivity.class);
                }
            });
            if (myteam_detailed_rules_layout != null)
                myteam_detailed_rules_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(AppConfig.BUNDLE_KEY_WEB_URL, mTeamInfo.getRule().getRuleUrl());
                        bundle.putString(AppConfig.BUNDLE_KEY_WEB_TITLE, getString(R.string.detailed_rules));
                        ActivityUtils.startActivity(MyTeamActivity.this, WebViewActivity.class, bundle);
                    }
                });
            if (play_video_iv != null)
                play_video_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        String path = "http://cms.oss.aikucun.com/market/1516879850.mp4";
                        String path = mTeamInfo.getRule().getVideoUrl();
                        JCVideoPlayerStandard.startFullscreen(MyTeamActivity.this, JCVideoPlayerStandard.class, path, getString(R.string.team_reward_mechanism));
                    }
                });
        }
    }

    private void initLevel(String amountArea) {

        String videoUrl = mTeamInfo.getRule().getVideoUrl();
        String ruleUrl = mTeamInfo.getRule().getRuleUrl();
        play_video_iv.setVisibility(StringUtils.isEmpty(videoUrl) ? View.GONE : View.VISIBLE);
        myteam_detailed_rules_layout.setVisibility(StringUtils.isEmpty(ruleUrl) ? View.GONE : View.VISIBLE);

        ArrayList<LevelInfo> arrayList = new ArrayList<>();
        if (!TextUtils.isEmpty(amountArea)) {
            JSONObject jsonObject = JSONObject.parseObject(amountArea);
            if (jsonObject != null) {
                long level_1 = jsonObject.getLong("1");
                long level_2 = jsonObject.getLong("2");
                long level_3 = jsonObject.getLong("3");
                long level_4 = jsonObject.getLong("4");
                long level_5 = jsonObject.getLong("5");
                long level_6 = jsonObject.getLong("6");
                LevelInfo levelInfo1 = new LevelInfo(1, level_1);
                LevelInfo levelInfo2 = new LevelInfo(2, level_2);
                LevelInfo levelInfo3 = new LevelInfo(3, level_3);
                LevelInfo levelInfo4 = new LevelInfo(4, level_4);
                LevelInfo levelInfo5 = new LevelInfo(5, level_5);
                LevelInfo levelInfo6 = new LevelInfo(6, level_6);
                arrayList.add(levelInfo1);
                arrayList.add(levelInfo2);
                arrayList.add(levelInfo3);
                arrayList.add(levelInfo4);
                arrayList.add(levelInfo5);
                arrayList.add(levelInfo6);
            }
        }

        level_layout.removeAllViews();

        for (int i = 0; i < arrayList.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_level_line, null);
            LinearLayout level_item_layout = view.findViewById(R.id.level_item_layout);
            //等级
            TextView level_tv = view.findViewById(R.id.level_tv);
            //金额
            TextView amount_text = view.findViewById(R.id.amount_text);

            ImageView status_iv = view.findViewById(R.id.status_iv);
            level_tv.setText("T" + arrayList.get(i).level);
            if (arrayList.get(i).level == mTeamInfo.getLevel()) {
                //当前等级
                status_iv.setImageResource(R.drawable.icon_bg_normal);
                level_tv.setTextColor(getResources().getColor(R.color.color_accent));
                level_tv.setTextSize(18);

                //设置圆点大小
                ViewGroup.LayoutParams layoutParams = status_iv.getLayoutParams();
                layoutParams.width = layoutParams.height = (int) TDevice.dpToPixel(15);
                status_iv.setLayoutParams(layoutParams);
                status_iv.setImageResource(R.drawable.icon_bg_red);
            }
            if (arrayList.get(i).level < mTeamInfo.getLevel()) {
                status_iv.setImageResource(R.drawable.icon_bg_black);
            }

            if (arrayList.get(i).level - mTeamInfo.getLevel() == 1) {
                long temp = arrayList.get(i).amount - mTeamInfo.getMonthsTotal();
                int result = (int) temp / 1000000;
                String content = MessageFormat.format(getString(R.string.how_many_upgrades), result);
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(new ForegroundColorSpan(AppContext.getInstance().getResources().getColor(R.color.color_accent)), 1, String.valueOf(result).length() + 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                amount_text.setText(style);
            } else {
                int amount = (int) arrayList.get(i).amount / 1000000;
                String rewardTotal = StringUtils.getNum2Decimal((float) amount + "");
                amount_text.setText(rewardTotal + "w");
            }

            float w = TDevice.getScreenWidth();
            int tempw = (int) w / 6;
            ViewGroup.LayoutParams layoutParams = level_item_layout.getLayoutParams();
            layoutParams.width = tempw;
            level_item_layout.setLayoutParams(layoutParams);
            level_layout.addView(view);
        }
    }

    /**
     * 获取团队信息
     */
    private void getMyTeamInfo() {
        InviteManager.getMyTeamInfo(this, new TeamInfoCallBack() {
            @Override
            public void onSuccess(TeamInfo teamInfo, Call call, Response response) {
                super.onSuccess(teamInfo, call, response);
                mTeamInfo = teamInfo;

                if (teamInfo != null) {
                    initLevel(teamInfo.getAmountArea());
                    level_text.setText("T" + teamInfo.getLevel());
                    String monthsTotal = StringUtils.getNum2Decimal((float) teamInfo.getMonthsTotal() / 100 + "");
                    team_sales_volume_text.setText(monthsTotal);
                    team_sales_volume_text.dance();//启动效果,开始数字跳动
                    String todoReward = StringUtils.getNum2Decimal((float) teamInfo.getTodoReward() / 100 + "");
                    paid_and_rebate_text.setText(todoReward);
                    String rewardTotal = StringUtils.getNum2Decimal((float) teamInfo.getRewardTotal() / 100 + "");
                    total_paid_and_rebate_text.setText(rewardTotal);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    /**
     * 获取团队成员
     */
    private void getTeamMembers() {
        InviteManager.getTeamMembers(this, pageNo, new TeamMembersCallBack() {
            @Override
            public void onSuccess(List<TeamMembers> teamMembers, Call call, Response response) {
                super.onSuccess(teamMembers, call, response);
                recyclerView.setRefreshing(false);
                if (pageNo == 1) {
                    adapter.clear();
                }
                if (teamMembers != null && teamMembers.size() < 12) {
                    canLoadMore = false;
                } else canLoadMore = true;
                adapter.addAll(teamMembers);
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

    private class LevelInfo {
        int level;
        long amount;

        public LevelInfo(int _level, long _amount) {
            level = _level;
            amount = _amount;
        }
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
