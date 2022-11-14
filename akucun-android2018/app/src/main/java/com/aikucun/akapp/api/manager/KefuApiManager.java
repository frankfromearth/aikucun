package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jarry on 2017/6/22.
 */

public class KefuApiManager
{
    public static final String API_URI_SYSTEM = "kefu.do";

    public static final String ACTION_KEFU_PUSH = "push";
    public static final String ACTION_KEFU_PULL = "pull";
    public static final String ACTION_KEFU_CHECK = "check";
    public static final String ACTION_KEFU_REFRESH = "refresh";
    public static final String ACTION_KEFU_RECEIPT = "receipt";



    /*
    表明消息已读
     */
    public static void pushMsgs(Activity context,String content, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("content", content);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_KEFU_PUSH, null);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
        SCLog.logObj(jsonObj);
    }

    /*
    表明消息已读
     */
    public static void pullMsgs(Activity context,int lastxuhao, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        Map<String, String> params = new HashMap<>();
        params.put("lastxuhao", "" + lastxuhao);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_KEFU_PULL, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    表明消息已读
     */
    public static void checkMsgs(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_KEFU_CHECK, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    表明消息已读
     */
    public static void refreshMsgs(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_KEFU_REFRESH, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    表明消息已读
     */
    public static void reciptMsgs(Activity context,String kefuIds, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        JSONObject jsonObj = new JSONObject();
        String[] msgids = {kefuIds};
        jsonObj.put("msgids", msgids);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_KEFU_RECEIPT, null);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
        SCLog.logObj(jsonObj);
    }
}
