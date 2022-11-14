package com.aikucun.akapp.activity.invitation;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.InvitationRewardAdapter;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.InvitReward;
import com.aikucun.akapp.api.manager.InviteManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/17.
 * 邀请奖励明细
 */

public class InvitationRewardActivity extends BaseActivity implements SwipeRefreshLayout
        .OnRefreshListener, RecyclerArrayAdapter.OnLoadMoreListener {

    LinearLayout date_layout;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    //月邀请奖励金额
    TextView month_amount_reward_text;
    //待入账奖励
    TextView paid_reward_text;
    //累计奖励金额
    TextView total_reward_text;
    private InvitationRewardAdapter adapter;
    private int selectedMonth = 1;
    private int nowYear = 0;
    private int pageNo = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.invitation_reward);
        adapter = new InvitationRewardAdapter(this);
        adapter.setNoMore(R.layout.view_nomore);
        Calendar c = Calendar.getInstance();
        nowYear = c.get(Calendar.YEAR);

    }

    @Override
    public void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setRefreshListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshing(true);
        adapter.setMore(R.layout.view_load_more, this);

        View view = mInflater.inflate(R.layout.invitation_reward_header_layout, null);
        InvitaRewardHeaderView headerView = new InvitaRewardHeaderView();
        headerView.onBindView(view);
        adapter.addHeader(headerView);
        initDate(selectedMonth);
        getInvitaReward(selectedMonth);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_list_layout;
    }


    private void initDate(int index) {
        date_layout.removeAllViews();

        for (int i = 1; i < 13; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_rebate_date, null);
            View lineView = view.findViewById(R.id.bottom_line_view);
            TextView date_tv = view.findViewById(R.id.date_tv);
            date_tv.setText(i + "月");
            if (i == index) {
                date_tv.setTextColor(getResources().getColor(R.color.color_accent));
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.INVISIBLE);
                date_tv.setTextColor(getResources().getColor(R.color.text_dark));
            }
            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedMonth = position;
                    initDate(selectedMonth);
                    getInvitaReward(selectedMonth);
                }
            });
            date_layout.addView(view);
        }
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        recyclerView.setRefreshing(true);
        getInvitaReward(selectedMonth);
    }

    @Override
    public void onLoadMore() {
        pageNo++;
        getInvitaReward(selectedMonth);
    }

    private class InvitaRewardHeaderView implements RecyclerArrayAdapter.ItemView {

        public InvitaRewardHeaderView() {
        }

        @Override
        public View onCreateView(ViewGroup parent) {
            View header = mInflater.inflate(R.layout.invitation_reward_header_layout, null);
            return header;
        }

        @Override
        public void onBindView(View headerView) {
            month_amount_reward_text = headerView.findViewById(R.id.month_amount_reward_text);
            paid_reward_text = headerView.findViewById(R.id.paid_reward_text);
            total_reward_text = headerView.findViewById(R.id.total_reward_text);
            date_layout = headerView.findViewById(R.id.date_layout);
        }
    }

    /**
     * 获取邀请明细
     *
     * @param month
     */
    private void getInvitaReward(int month) {
        String temp = "";
        if (month < 10) {
            temp = nowYear + "-0" + month;
        } else temp = nowYear + "-" + month;
        InviteManager.getInviteReward(this, pageNo, temp, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                if (jsonObject != null) {
                    long todoReward = jsonObject.getLong("todoReward");
                    paid_reward_text.setText(StringUtils.getNum2Decimal((float) todoReward / 100 + ""));
                    long rewardTotal = jsonObject.getLong("rewardTotal");
                    total_reward_text.setText(StringUtils.getNum2Decimal((float) rewardTotal / 100 + ""));
                    long rewardMonth = jsonObject.getLong("rewardMonth");
                    month_amount_reward_text.setText(StringUtils.getNum2Decimal((float) rewardMonth / 100 + ""));
                    String users = jsonObject.getString("users");
                    initDate(selectedMonth);
                    recyclerView.setRefreshing(false);
                    if (!TextUtils.isEmpty(users)) {
                        List<InvitReward> list = JSON.parseArray(users, InvitReward.class);
                        if (pageNo == 1)
                            adapter.clear();
                        adapter.addAll(list);
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                recyclerView.setRefreshing(false);
                super.onApiFailed(message, code);
            }
        });
    }
}
