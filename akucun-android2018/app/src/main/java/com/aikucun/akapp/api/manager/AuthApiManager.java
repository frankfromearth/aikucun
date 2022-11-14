package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ak123 on 2017/12/21.
 * 实名认证API
 */

public class AuthApiManager {
    // 192.168.31.147:8080
    // 用户
    public static final String API_URI_USER = "user.do";
    //检查该用户是否已实名认证
    private static final String CHECK_USERCARDID_ISUSED = "ifidcardused";
    //发送银行卡短信
    private static final String SEND_SMSBANK_MOBLIE = "sendSMSBankMoblie";
    //新增实名认证
    private static final String ADD_REALNAME_AUTH = "validuser";

    /**
     * 检查该用户传入的身份证是否已被认证
     *
     * @param context
     * @param idcard
     * @param callback
     */
    public static void checkUserInfoIsUsed(Activity context, String idcard, AbsCallback callback) {
         String url = HttpConfig.getHost() + "/" + API_URI_USER;
//        String url = "http://192.168.31.147:8080/api/v1.0/" + API_URI_USER;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("idcard", idcard);
            jsonObject.put("userId", AppContext.getInstance().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String paramsUrl = HttpConfig.httpParamsUrl(url, CHECK_USERCARDID_ISUSED, null);
        OkGo.post(paramsUrl).tag(context).upJson(jsonObject).
                execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 发送实名认证银行卡预留手机号验证码
     *
     * @param context
     * @param mobile
     * @param callback
     */
    public static void sendSMSBankMoblie(Activity context, String mobile, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", mobile);
            jsonObject.put("type", 5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, SEND_SMSBANK_MOBLIE, null);
        OkGo.post(paramsUrl).tag(context).upJson(jsonObject).
                execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 添加实名认证
     *
     * @param context
     * @param realname
     * @param idcard
     * @param bankcard
     * @param mobile
     * @param code
     * @param base64Img
     * @param base64ImgBac
     * @param callback
     */
    public static void validUser(Activity context, String realname, String idcard, String bankcard, String mobile, String code, String base64Img, String base64ImgBac, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("realname", realname);
            jsonObject.put("idcard", idcard);
            jsonObject.put("bankcard", bankcard);
            jsonObject.put("mobile", mobile);
            jsonObject.put("code", code);
            jsonObject.put("base64Img", base64Img);
            jsonObject.put("base64ImgBac", base64ImgBac);
            jsonObject.put("userId", AppContext.getInstance().getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, ADD_REALNAME_AUTH, null);
        OkGo.post(paramsUrl).tag(context).upJson(jsonObject).
                execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
