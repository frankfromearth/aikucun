package com.aikucun.akapp.activity.team;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.InviteManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;

import java.text.MessageFormat;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/16.
 * 团队返利明细
 */

public class TeamRebateActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.date_layout)
    LinearLayout date_layout;
    //带返利金额
    @BindView(R.id.paid_and_rebate_text)
    TextView paid_and_rebate_text;
    //累计返利金额
    @BindView(R.id.total_paid_and_rebate_text)
    TextView total_paid_and_rebate_text;
    //我的销售额
    @BindView(R.id.my_sales_volume_text)
    TextView my_sales_volume_text;
    //团队总销售额
    @BindView(R.id.team_sales_volume_text)
    TextView team_sales_volume_text;
    //直接返利金额
    @BindView(R.id.direct_team_rebate_amount_text)
    TextView direct_team_rebate_amount_text;
    //间接返利金额
    @BindView(R.id.indirect_team_rebate_text)
    TextView indirect_team_rebate_text;
    //团队销售额比例
    @BindView(R.id.team_sales_volume_percentage_text)
    TextView team_sales_volume_percentage_text;
    //个人销售额比例
    @BindView(R.id.personal_sales_volume_percentage_text)
    TextView personal_sales_volume_percentage_text;
    //直属团队销售额
    @BindView(R.id.directly_team_sales_text)
    TextView directly_team_sales_text;
    @BindView(R.id.month_total_text)
    TextView month_total_text;
    @BindView(R.id.month_amount_tv)
    TextView month_amount_tv;
    private int nowYear = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(getString(R.string.team_rebate_detail).replace(">", ""));
        Calendar c = Calendar.getInstance();
        nowYear = c.get(Calendar.YEAR);
    }

    @Override
    public void initData() {
        initDate(1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_team_rebate;
    }

    private void initDate(int index) {
        date_layout.removeAllViews();
        getTeamRebate(index);
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
            final int poistion = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDate(poistion);
                }
            });
            date_layout.addView(view);
        }
    }

    private void getTeamRebate(final int month) {
        String temp = "";
        if (month < 10) {
            temp = nowYear + "-0" + month;
        } else temp = nowYear + "-" + month;

        InviteManager.getTeamRebate(this, temp, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                if (jsonObject != null) {
                    long todoReward = jsonObject.getLong("todoReward");
                    long rewardTotal = jsonObject.getLong("rewardTotal");
                    //待入账返利金额
                    String todoRewardStr = StringUtils.getNum2Decimal((float) todoReward / 100 + "");
                    paid_and_rebate_text.setText(todoRewardStr);
                    String rewardTotalStr = StringUtils.getNum2Decimal((float) rewardTotal / 100 + "");
                    total_paid_and_rebate_text.setText(rewardTotalStr);
                    JSONObject object = jsonObject.getJSONObject("amounts");
                    if (object != null) {
                        long mySale = object.getLong("mySale");
                        my_sales_volume_text.setText(StringUtils.getNum2Decimal((float) mySale / 100 + ""));
                        //直接返利金额
                        long oneLevelAmount = object.getLong("oneLevelReward");
                        //间接返利金额
                        long twoLevelAmount = object.getLong("twoLevelReward");

                        direct_team_rebate_amount_text.setText(StringUtils.getNum2Decimal((float) oneLevelAmount / 100 + ""));
                        indirect_team_rebate_text.setText(StringUtils.getNum2Decimal((float) twoLevelAmount / 100 + ""));
                        team_sales_volume_percentage_text.setText(MessageFormat.format(getString(R.string.team_sales_volume_percentage), object.getString("oneLevelRewardRate")));
                        personal_sales_volume_percentage_text.setText(MessageFormat.format(getString(R.string.team_sales_volume_percentage), object.getString("twoLevelRewardRate")));
                        long totalReward = object.getLong("totalReward");
                        month_total_text.setText(StringUtils.getNum2Decimal((float) totalReward / 100 + ""));
                        month_amount_tv.setText(MessageFormat.format(getString(R.string.month_amount_rebate), month));
                        //直接团队销售额
                        long directlyTeamSales = object.getLong("oneLevelAmount");
                        directly_team_sales_text.setText(StringUtils.getNum2Decimal((float) directlyTeamSales / 100 + ""));
                        //间接团队销售额
                        long teamSales = object.getLong("twoLevelAmount");
                        team_sales_volume_text.setText(StringUtils.getNum2Decimal((float) teamSales / 100 + ""));
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(TeamRebateActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }
}
