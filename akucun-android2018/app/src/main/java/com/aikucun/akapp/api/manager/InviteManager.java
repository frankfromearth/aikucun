package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ak123 on 2018/1/19.
 * 邀请相关
 */

public class InviteManager {

    private static final String API_URL_USERINVITE = "userinvite.do";
    private static final String API_URL_TEAM = "team.do";
    private static final String INVITE_BASE_URL = "http://member.test.akucun.com/backend/api/" + HttpConfig.API_VERSION;

    private static final String ACTION_USERINVITE = "invite";
    private static final String ACTION_TEAM_INFO = "info";
    private static final String ACTION_TEAM_MEMBER = "member";
    private static final String ACTION_TEAM_DETAIL = "teamDetail";
    private static final String ACTION_USERINVITE_DETAIL = "userinviteDetail";


    /**
     * 获取用户邀请信息
     *
     * @param context
     * @param callback
     */
    public static void getInviteInfo(Activity context, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URL_USERINVITE;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USERINVITE, null);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取我的团队信息
     *
     * @param context
     * @param callback
     */
    public static void getMyTeamInfo(Activity context, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URL_TEAM;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_TEAM_INFO, null);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取团队成员
     *
     * @param context
     * @param callback
     */
    public static void getTeamMembers(Activity context, int pageno, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URL_TEAM;
        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", "12");

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_TEAM_MEMBER, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取团队返利明细
     *
     * @param context
     * @param callback
     */
    public static void getTeamRebate(Activity context, String month, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URL_TEAM;
        Map<String, String> params = new HashMap<>();
        params.put("month", month);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_TEAM_DETAIL, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 邀请明细
     *
     * @param context
     * @param month
     * @param callback
     */
    public static void getInviteReward(Activity context, int pageno, String month, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URL_USERINVITE;
        Map<String, String> params = new HashMap<>();
        params.put("month", month);
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", "12");

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USERINVITE_DETAIL, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
