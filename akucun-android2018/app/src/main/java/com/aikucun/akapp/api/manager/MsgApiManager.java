package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jarry on 2017/6/29.
 */
public class MsgApiManager
{
    public static final String API_URI_MESSAGE = "msg.do";

    public static final String ACTION_MSG_LIST = "page";
    public static final String ACTION_MSG_READ = "readMsg";
    public static final String ACTION_MSG_RDALL = "readAllMsg";


    /**
     * 消息列表获取
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     */
    public static void getMsgList(Activity context, int pageno, int pagesize, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_MESSAGE;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_MSG_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 指定消息 ID 标记为已读
     *
     * @param context
     * @param msgid
     * @param callback
     */
    public static void readMsg(Activity context, String msgid, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_MESSAGE;

        Map<String, String> params = new HashMap<>();
        params.put("msgid", msgid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_MSG_READ, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 所有未读消息标记为已读
     *
     * @param context
     * @param callback
     */
    public static void readAllMsg(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_MESSAGE;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_MSG_RDALL, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
