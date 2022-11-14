package com.aikucun.akapp.utils;

import android.app.Activity;
import android.view.View;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.view.fancyshowcase.AnimationListener;
import com.aikucun.akapp.view.fancyshowcase.FancyShowCaseView;
import com.aikucun.akapp.view.fancyshowcase.FocusShape;

/**
 * Created by ak123 on 2018/1/22.
 * 新手引导效果工具
 */

public class GuideUtils {

    //显示未支付订单
    public static final int showPaidOrder = 1;
    //显示邀请码功能激活时
    public static final int showPrcActivation = 2;
    //有新的好友邀请需要批准
    public static final int showNewFriendCanApproval = 3;
    //显示我的团队
    public static final int showMyTeam = 4;
    //批准好友
    public static final int showApprovalFriend= 5;
    private static boolean isShow = false;

    public static void showGuideView(Activity context, View inView, final int type) {
        if (isShow) return;
        String title = "";
        if (type == showPaidOrder) {
            //未支付订单
            title = context.getString(R.string.unpay_order_guide_text);
        } else if (type == showPrcActivation) {
            title = context.getString(R.string.code_acti_guide_text);
        } else if (type == showNewFriendCanApproval) {
            title = context.getString(R.string.new_friend_approval);
        } else if (type == showMyTeam) {
            title = context.getString(R.string.my_team_guide_text);
        }else if (type == showApprovalFriend ){
            title = context.getString(R.string.approval_friend);
        }
        new FancyShowCaseView.Builder(context)
                .focusOn(inView)
                .title(title)
                .focusShape(FocusShape.CIRCLE)
                .titleSize(14, 1).animationListener(new AnimationListener() {
            @Override
            public void onEnterAnimationEnd() {

            }

            @Override
            public void onExitAnimationEnd() {
                if (type == showPaidOrder) {
                    AppContext.set(showGuidePaidOrder, false);
                } else if (type == showPrcActivation) {
                    AppContext.set(showGuidePicActivation, false);
                } else if (type == showNewFriendCanApproval) {
                    AppContext.set(showGuideNewFriendApproval, false);
                } else if (type == showMyTeam) {
                    AppContext.set(showGuideMyTeam, false);
                }else if (type == showApprovalFriend){
                    AppContext.set(showGuideApprovalFriend, false);
                }
                isShow = false;
            }
        })
                .build()
                .show();
    }

    public static final String showGuidePaidOrder = "showGuidePaidOrder";
    public static final String showGuidePicActivation = "showGuidePicActivation";
    public static final String showGuideNewFriendApproval = "showGuideNewFriendApproval";
    public static final String showGuideMyTeam = "showGuideMyTeam";
    public static final String showGuideApprovalFriend = "showGuideApprovalFriend";

}
