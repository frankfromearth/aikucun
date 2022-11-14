package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jarry on 2017/6/22.
 */

public class SystemApiManager
{
    public static final String API_URI_SYSTEM = "sys.do";

    public static final String ACTION_SYS_CHECK = "check";
    public static final String ACTION_SYS_PAYTYPE = "getpaytype";
    public static final String ACTION_USER_REPORT = "report";
    public static final String ACTION_USER_REPORTPUSH = "reporttuisong";


    public static void checkUpdate(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.SERVER_HOST_DEFAULT + "/" + "system.do";

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYS_CHECK, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void getPayTypes(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYS_PAYTYPE, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    /*
    *
    * 上传pushid，目前pushId取的是个推的clientId
    *
    * */
    public static void requestReportPush(Activity context, String pushId, AbsCallback callback) {
        if (StringUtils.isEmpty(pushId)) {
            SCLog.logv("pushId IS EMPTY");
            return;
        }
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        Map<String, String> params = new HashMap<>();
        params.put("tuisongid", "" + pushId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_REPORTPUSH, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    /*
    *
    * did,  imei
    * sourceNo , 渠道号
    *
    * */
    public static void requestReportInfo(Activity context, String did, String sourceNo, AbsCallback callback) {
        if (StringUtils.isEmpty(did) || StringUtils.isEmpty(sourceNo)) {
            SCLog.logv("DID OR SOURCE_NO IS EMPTY");
            return;
        }
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        Map<String, String> params = new HashMap<>();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("did", did);
        jsonObj.put("sourceno", sourceNo);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_USER_REPORT, params);
        OkGo.post(paramsUrl).tag(context).upJson(jsonObj.toJSONString()).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

}
