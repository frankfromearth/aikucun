package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by micker on 2017/8/13.
 */

public class CommentsApiManager {

    // 用户
    public static final String API_PROD_COMMENT = "product.do";
    public static final String API_LIVE_COMMENT = "live.do";

    public static final String ACTION_SEND_COMMENT = "comment";
    public static final String ACTION_CANCEL_COMMENT = "cancelComment";

    public static void sendComment(Activity context, String productId, String content, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_PROD_COMMENT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);
        params.put("comment", content);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SEND_COMMENT, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void cancelComment(Activity context, String productId, String commentId, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_PROD_COMMENT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);
        params.put("commentid", commentId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_CANCEL_COMMENT, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void syncComments(Activity context, long sync, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_LIVE_COMMENT;

        Map<String, String> params = new HashMap<>();
        params.put("sync", "" + sync);

        String paramsUrl = HttpConfig.httpParamsUrl(url, "syncComments", params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    public static void trackComments(Activity context, long sync, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_LIVE_COMMENT;

        Map<String, String> params = new HashMap<>();
        params.put("sync", "" + sync);

        String paramsUrl = HttpConfig.httpParamsUrl(url, "trackComments", params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

}
