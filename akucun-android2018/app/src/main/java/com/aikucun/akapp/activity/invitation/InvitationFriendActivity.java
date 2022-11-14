package com.aikucun.akapp.activity.invitation;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.WebViewActivity;
import com.aikucun.akapp.activity.team.MyTeamActivity;
import com.aikucun.akapp.api.callback.InviteInfoCallBack;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.InviteInfo;
import com.aikucun.akapp.api.entity.InviteMember;
import com.aikucun.akapp.api.manager.InviteManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.GuideUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.RoundImageView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2018/1/17.
 * 邀请好友
 */

public class InvitationFriendActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    //待批准列表
    @BindView(R.id.invit_approved_layout)
    LinearLayout invit_approved_layout;
    //待入账奖励
    @BindView(R.id.paid_reward_text)
    TextView paid_reward_text;
    //已入账奖励
    @BindView(R.id.haved_reward_text)
    TextView haved_reward_text;
    //已邀请数量
    @BindView(R.id.member_count_text)
    TextView member_count_text;
    //待批准数量
    @BindView(R.id.invitations_approved_text)
    TextView invitations_approved_text;
    @BindView(R.id.no_members_text)
    TextView no_members_text;
    @BindView(R.id.go_to_my_team_layout)
    LinearLayout go_to_my_team_layout;

    @BindView(R.id.detailed_rules_layout)
    View detailedRuleBtn;
    @BindView(R.id.play_video_iv)
    View playVideoBtn;

    private String videoUrl;
    private String ruleUrl;

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
        getInviteInfo();
        if (AppContext.getInstance().getUserInfo().getMembers() > 0) {
            go_to_my_team_layout.setVisibility(View.VISIBLE);
        } else go_to_my_team_layout.setVisibility(View.GONE);
    }

    @Override
    @OnClick({R.id.bottomLayout, R.id.go_to_my_team_layout, R.id.detailed_rules_layout, R.id.team_rebate_layout,R.id.play_video_iv})
    public void onClick(View v) {
        super.onClick(v);
        final int id = v.getId();
        switch (id) {
            case R.id.bottomLayout:
                ActivityUtils.startActivity(this, InvActivity.class);
                break;
            case R.id.go_to_my_team_layout:
                ActivityUtils.startActivity(this, MyTeamActivity.class);
                break;
            case R.id.detailed_rules_layout:
                Bundle bundle = new Bundle();
//                bundle.putString(AppConfig.BUNDLE_KEY_WEB_URL, "http://api.aikucun.com/teaminviterule.do?action=inviterule");
                bundle.putString(AppConfig.BUNDLE_KEY_WEB_URL,ruleUrl);
                bundle.putString(AppConfig.BUNDLE_KEY_WEB_TITLE, getString(R.string.detailed_rules));
                ActivityUtils.startActivity(this, WebViewActivity.class, bundle);
                break;
            case R.id.play_video_iv:
//                String path = "http://cms.oss.aikucun.com/market/1516879844.mp4";
                JCVideoPlayerStandard.startFullscreen(this, JCVideoPlayerStandard.class, videoUrl, getString(R.string.inviting_incentive_mechanism));
                break;
            case R.id.team_rebate_layout:
                ActivityUtils.startActivity(this, InvitationRewardActivity.class);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invitation_friends_layout;
    }


    private void initUnApprovalUsers(final List<InviteMember> list) {
        invit_approved_layout.removeAllViews();
        for (int i = 0, size = list.size(); i < size; i++) {
            View view = mInflater.inflate(R.layout.item_invi_approved_layout, null);
            RoundImageView head_image = view.findViewById(R.id.head_image);
            TextView user_name_text = view.findViewById(R.id.user_name_text);
            TextView approval_btn = view.findViewById(R.id.approval_btn);
            Glide.with(InvitationFriendActivity.this).load(list.get(i).getAvatar()).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(head_image);
            user_name_text.setText(list.get(i).getNick());
            final InviteMember mInviteMember = list.get(i);
            if (mInviteMember.getStatus() == 1) {
                approval_btn.setBackgroundResource(R.drawable.btn_bg_search_selector);
                approval_btn.setEnabled(false);
                approval_btn.setText(R.string.approvaled);
            } else {
                approval_btn.setBackgroundResource(R.drawable.btn_bg_red_selector);
                approval_btn.setText(R.string.approval);
                approval_btn.setEnabled(true);
            }
            approval_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String referralcode = AppContext.getInstance().getUserInfo().getPreferralcode();
                    approvedUser(mInviteMember.getId(), referralcode, new ICallBack() {
                        @Override
                        public void onResult(String userId, String referralcode) {
                            getInviteInfo();
                        }
                    });
                }
            });
            invit_approved_layout.addView(view);
            if (i == 0) {
                boolean showMyTeamGuideView = AppContext.get(GuideUtils.showGuideApprovalFriend, true);
                if (showMyTeamGuideView) {
                    GuideUtils.showGuideView(this, approval_btn, GuideUtils.showApprovalFriend);
                }
            }
        }
    }

    /**
     * 获取邀请信息
     */
    private void getInviteInfo() {
        InviteManager.getInviteInfo(this, new InviteInfoCallBack() {
            @Override
            public void onSuccess(InviteInfo inviteInfo, Call call, Response response) {
                super.onSuccess(inviteInfo, call, response);
                if (inviteInfo != null) {
                    String todoReward = StringUtils.getNum2Decimal((float) inviteInfo.getTodoReward() / 100 + "");
                    String rewardTotal = StringUtils.getNum2Decimal((float) inviteInfo.getRewardTotal() / 100 + "");
                    paid_reward_text.setText(todoReward);
                    haved_reward_text.setText(rewardTotal);
                    member_count_text.setText(MessageFormat.format(getString(R.string.invited_members), inviteInfo.getMemberCount()));
                    if (inviteInfo.getTodo() != null && inviteInfo.getTodo().size() != 0) {
                        initUnApprovalUsers(inviteInfo.getTodo());
                        no_members_text.setVisibility(View.GONE);
                        invit_approved_layout.setVisibility(View.VISIBLE);
                        invitations_approved_text.setText(MessageFormat.format(getString(R.string.invitations_approved), inviteInfo.getTodo().size()));
                    } else {
                        invit_approved_layout.setVisibility(View.GONE);
                        no_members_text.setVisibility(View.VISIBLE);
                        invitations_approved_text.setText(MessageFormat.format(getString(R.string.invitations_approved), 0));
                    }

                    videoUrl = inviteInfo.getRule().getVideoUrl();
                    ruleUrl = inviteInfo.getRule().getRuleUrl();
                    playVideoBtn.setVisibility(StringUtils.isEmpty(videoUrl) ? View.GONE : View.VISIBLE);
                    detailedRuleBtn.setVisibility(StringUtils.isEmpty(ruleUrl) ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(InvitationFriendActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }


    interface ICallBack {
        void onResult(String userId, String referralcode);
    }

    private void approvedUser(final String userId, final String referralcode, final ICallBack mICallBack) {
        showProgress("");
        UsersApiManager.activiuser(this, referralcode, userId, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                cancelProgress();
                if (jsonObject != null) {
                    int code = jsonObject.getIntValue("code");
                    if (code == 0)
                        mICallBack.onResult(userId, referralcode);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                cancelProgress();
                if (!TextUtils.isEmpty(message))
                    MToaster.showShort(InvitationFriendActivity.this, message, MToaster.IMG_INFO);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }else{
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
