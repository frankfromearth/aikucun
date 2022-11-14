package com.aikucun.akapp.api.manager;

import android.app.Activity;
import android.text.TextUtils;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户相关 HTTP 接口
 * Created by jarry on 16/5/11.
 */
public class UsersApiManager {
    // 用户
    public static final String API_URI_USER = "user.do";

    public static final String ACTION_WX_LOGIN = "weixinlogin";
    public static final String ACTION_USER_GETINFO = "getinfo";
    public static final String ACTION_USER_ADDADDR = "addaddr";
    public static final String ACTION_USER_UPDATE = "changeuser";
    public static final String ACTION_USER_ACCOUNT = "account";
    public static final String ACTION_ACCOUNT_DETAIL = "page";
    public static final String ACTION_ACCOUNT_PHONE_LOGIN = "phonelogin";
    public static final String ACTION_ACCOUNT_PHONE_REGISTER = "register";
    public static final String ACTION_ACCOUNT_PHONE_BIND = "bindphone";
    public static final String ACTION_ACCOUNT_AUTHCODE = "authcode";
    public static final String ACTION_ACCOUNT_LOGOUT = "logout";
    //子账户登录
    public static final String ACTION_ACCOUNT_SUBUSERLOGIN = "subuserlogin";
    //第三方登录
    public static final String ACTION_ACCOUNT_THIRDLOGIN = "thirdlogin";
    //绑定手机号
    public static final String ACTION_ACCOUNT_BINDPHONENUM = "bindphonenum";


    public static final String ACTION_ADDRESS_LIST = "addrlist";
    public static final String ACTION_ADDRESS_MODIFY = "modifyaddr";
    public static final String ACTION_ADDRESS_DEL = "deladdr";
    public static final String ACTION_ADDRESS_DEFAULT = "defaultaddr";

    public static final String ACTION_GET_PURCHASED = "getpurchases";
    public static final String ACTION_MEMBER_PURCHASED = "memberpurchase";


    public static final String ACTION_USER_GETDELTAS = "getdeltas";
    public static final String ACTION_USER_BUYDELTA = "buydelta";


    public static final String ACTION_USER_INVITECODE = "getreferralcode";
    public static final String ACTION_USER_ACTIVECODE = "usereferralcode";


    public static final String ACTION_USER_REFERRAL = "pagemyreferral";
    public static final String ACTION_USER_ACTIVE_REFERRAL = "activeuser";
    //客户对账单活动
    public static final String ACTION_PAGE_AFTER_ACTION = "pageAfterAction";
    public static final String ACTION_PAGE_AFTER_PRODUCT_ACTION = "pageLiveProduct";
    //搜索
    public static final String ACTION_SEARCH_PRODUCT_ACTION = "LiveProductByInfo";
    //上传用户活跃度
    public static final String ACTION_ACTIVE_UPLOAD = "activeupload";

    /**
     * 用户登录接口请求
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param openId   微信授权 OpenID
     * @param unionId  微信授权 UnionID
     * @param wxName   微信昵称
     * @param avatar   微信头像
     * @param callback 请求成功回调处理
     */
    public static void weixinLogin(Activity context, String openId, String unionId, String
            wxName, String avatar, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("openid", openId);
        params.put("unionid", unionId);
        params.put("weixinName", wxName);
        params.put("avatar", avatar);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_WX_LOGIN, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取用户基本信息接口请求
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param callback 请求成功回调处理
     */
    public static void userGetInfo(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_GETINFO, null);

        String cacheKey = RSAUtils.md5String(url + ACTION_USER_GETINFO);
        OkGo.get(paramsUrl).cacheKey(cacheKey).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 更新修改用户信息
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param name      用户名
     * @param imageBase 用户头像
     * @param callback  请求成功回调处理
     */
    public static void userUpdateInfo(Activity context, String name, String imageBase, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        JSONObject jsonObj = new JSONObject();
        if (!TextUtils.isEmpty(name))
            jsonObj.put("nicheng", name);
        if (!TextUtils.isEmpty(imageBase))
            jsonObj.put("base64Img", imageBase);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_UPDATE, null);
        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    /**
     * 用户添加地址信息接口
     *
     * @param context     调用接口所在的Activity，用于标记Tag
     * @param name        用户名
     * @param mobile      手机号
     * @param province    省份
     * @param city        城市
     * @param area        地区
     * @param address     详细地址
     * @param defaultflag 是否为默认地址
     * @param callback    请求成功回调处理
     */
    public static void userAddAddress(Activity context, String name, String mobile, String
            province, String city, String area, String address, int defaultflag, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        JSONObject jsonObj = new JSONObject();
        JSONObject resultObj = new JSONObject();

        jsonObj.put("shoujianren", name);
        jsonObj.put("dianhua", mobile);
        jsonObj.put("sheng", province);
        jsonObj.put("shi", city);
        jsonObj.put("qu", area);
        jsonObj.put("detailaddr", address);
        jsonObj.put("defaultflag", String.valueOf(defaultflag));
        resultObj.put("addr", jsonObj);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_ADDADDR, null);
        OkGo.post(paramsUrl).tag(context).upJson(resultObj.toJSONString()).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    /**
     * 用户账户余额信息
     *
     * @param context
     * @param callback
     */
    public static void userAccount(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_ACCOUNT, null);

        String cacheKey = RSAUtils.md5String(url + ACTION_USER_ACCOUNT);

        OkGo.get(paramsUrl).cacheKey(cacheKey).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 用户账户余额明细
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     */
    public static void accountRecords(Activity context, int pageno, int pagesize, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_DETAIL, params);

        String cacheKey = RSAUtils.md5String(url + ACTION_ACCOUNT_DETAIL);

        OkGo.get(paramsUrl).cacheKey(cacheKey).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    /*
    *根据手机号来接收验证码
    *
    *
    * userid : 在type为3时必填
    * type  1 : 手机号新用户注册
            2: 找回密码
            3: 微信用户绑定手机号验证
            4: 重置密码
            5: 手机号+验证码登录
    * */
    public static void authCode(Activity context, String phonenum, String userid, int type, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("phonenum", phonenum);
        params.put("userid", userid);
        params.put("type", String.valueOf(type));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_AUTHCODE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 手机号登录
     *
     * @param context
     * @param phonenum
     * @param authcode
     * @param callback
     */
    public static void phoneLogin(Activity context, String phonenum, String authcode, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("phonenum", phonenum);
            jsonObject.put("authcode", authcode);
            jsonObject.put("deviceid", TDevice.getIMEI());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_PHONE_LOGIN, null);
        OkGo.post(paramsUrl).upJson(jsonObject).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    * 手机号用户注册
    *
    * 用户手机号登录，支持验证码或者是密码登录
    * 原密码   （与验证码必填一项）
    * 验证码    （与密码必填一项，有验证时验证码优先使用）
    * */
    public static void phoneRegister(Activity context, String phonenum, String password, String authcode, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("phonenum", phonenum);
//        params.put("password", password);
        params.put("authcode", authcode);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_PHONE_REGISTER, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    * 手机号绑定
    *
    * 用户手机号登录，支持验证码或者是密码登录
    *
    *
    * */
    public static void phoneBind(Activity context, String userid, String token, String phonenum, String password, String authcode, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);
        params.put("phonenum", phonenum);
//        params.put("password", password);     //暂不使用
        params.put("authcode", authcode);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_PHONE_BIND, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 子账户登录
     *
     * @param activity
     * @param userid
     * @param subuserid
     * @param token
     * @param callback
     */
    public static void subuserlogin(Activity activity, String userid, String subuserid, String token, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("deviceid", TDevice.getIMEI());
            jsonObject.put("userid", userid);
            jsonObject.put("subuserid", subuserid);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_SUBUSERLOGIN, null);
        OkGo.post(paramsUrl).upJson(jsonObject).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);

    }

    /**
     * 第三方登录
     *
     * @param activity
     * @param logintype 第三方账号类型：2 - 微信
     * @param openid    第三方账号ID标识，对应微信的OpenId
     * @param unionid   第三方账号ID标识，对应微信的UnionId
     * @param name      第三方账号昵称，对应微信的昵称
     * @param avatar    第三方账号的用户头像URL
     * @param callback
     */
    public static void thirdlogin(Activity activity, int logintype, String openid, String unionid, String name, String avatar, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("logintype", logintype);
            jsonObject.put("openid", openid);
            jsonObject.put("unionid", unionid);
            jsonObject.put("name", name);
            jsonObject.put("avatar", avatar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_THIRDLOGIN, null);
        OkGo.post(paramsUrl).upJson(jsonObject).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);

    }

    /**
     * 绑定手机号
     *
     * @param activity
     * @param phonenum
     * @param authcode  短信验证码
     * @param subuserid 子账号 ID，第三方登录接口返回
     * @param token     临时 Token，第三方登录接口返回
     * @param callback
     */
    public static void bindphonenum(Activity activity, String phonenum, String authcode, String subuserid, String token, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("phonenum", phonenum);
            jsonObject.put("authcode", authcode);
            jsonObject.put("subuserid", subuserid);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_BINDPHONENUM, null);
        OkGo.post(paramsUrl).upJson(jsonObject).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 用户账户余额信息
     *
     * @param context
     * @param callback
     */
    public static void userLogout(Activity context, String userid, String token, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("userid", userid);
        params.put("token", token);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACCOUNT_LOGOUT, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void userAddressList(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("userid", AppContext.getInstance().getUserId());
        params.put("token", AppContext.getInstance().getToken());

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ADDRESS_LIST, params);

        String cacheKey = RSAUtils.md5String(url + ACTION_ADDRESS_LIST);

        OkGo.get(paramsUrl).cacheKey(cacheKey).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void userEditAddress(Activity context, String name, String mobile, String
            province, String city, String area, String address, String addrid, int defaultflag, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        JSONObject jsonObj = new JSONObject();
        JSONObject resultObj = new JSONObject();
        jsonObj.put("shoujianren", name);
        jsonObj.put("dianhua", mobile);
        jsonObj.put("sheng", province);
        jsonObj.put("shi", city);
        jsonObj.put("qu", area);
        jsonObj.put("detailaddr", address);
        jsonObj.put("addrid", addrid);
        jsonObj.put("defaultflag", String.valueOf(defaultflag));

        resultObj.put("addr", jsonObj);
        params.put("addrid", addrid);
        params.put("defaultflag", String.valueOf(defaultflag));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ADDRESS_MODIFY, params);
        OkGo.post(paramsUrl).tag(context).upJson(resultObj.toJSONString()).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    public static void userDefaultAddress(Activity context, String addrid, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("addrid", addrid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ADDRESS_DEFAULT, params);
        OkGo.get(paramsUrl).cacheKey(url).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void userDeleteAddress(Activity context, String addrid, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("addrid", addrid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ADDRESS_DEL, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void getPurchased(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_GET_PURCHASED, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void memberPurchased(Activity context, String productId, String payType, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);
        params.put("paytype", "" + payType);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_MEMBER_PURCHASED, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void getDeltas(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_GETDELTAS, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void buyDeltas(Activity context, String deltaid, String payType, int payjine, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        //String url = "http://192.168.37.1:8080/api/v1.0/" + API_URI_USER;
        Map<String, String> params = new HashMap<>();
        params.put("deltaid", StringUtils.isEmpty(deltaid) ? "" : deltaid);
        params.put("paytype", payType);
        params.put("payjine", "" + payjine);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_BUYDELTA, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void sendInviteCode(Activity context, String referralcode, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("referralcode", "" + referralcode);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_ACTIVECODE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void pagemyreferral(Activity context, int pageno, int pagesize, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_REFERRAL, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void activiuser(Activity context, String referralcode, String ruserid, AbsCallback
            callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;

        Map<String, String> params = new HashMap<>();
        params.put("referralcode", referralcode);
        params.put("ruserid", ruserid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_ACTIVE_REFERRAL, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 客户对账活动
     *
     * @param activity
     * @param pageNo
     * @param callback
     */
    public static void getPageAfter(Activity activity, int pageNo, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        Map<String, String> params = new HashMap<>();
        params.put("pageno", pageNo + "");
        params.put("pagesize", "20");
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PAGE_AFTER_ACTION, params);
        OkGo.get(paramsUrl).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 活动对账活动对应的商品
     *
     * @param activity
     * @param pageNo
     * @param liveid
     * @param callback
     */
    public static void getPageLiveProduct(Activity activity, int pageNo, String liveid, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        Map<String, String> params = new HashMap<>();
        params.put("pageno", pageNo + "");
        params.put("pagesize", "20");
        params.put("liveid", liveid);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PAGE_AFTER_PRODUCT_ACTION, params);
        OkGo.get(paramsUrl).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 搜索商品
     *
     * @param activity
     * @param keyWord
     * @param liveid
     * @param callback
     */
    public static void searchProductByInfo(Activity activity, String keyWord, String liveid, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        Map<String, String> params = new HashMap<>();
        params.put("liveid", liveid);
        params.put("info", keyWord);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SEARCH_PRODUCT_ACTION, params);
        OkGo.get(paramsUrl).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 上传用户活跃度
     *
     * @param activity
     * @param callback
     */
    public static void activeUpload(Activity activity, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_USER;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACTIVE_UPLOAD, null);
        OkGo.get(paramsUrl).tag(activity).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

}