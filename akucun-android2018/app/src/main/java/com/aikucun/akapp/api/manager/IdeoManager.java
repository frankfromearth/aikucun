package com.aikucun.akapp.api.manager;

import android.app.Activity;
import android.util.Log;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ak on 2018/2/28.
 * 活动引导图片和弹出图片
 */

public class IdeoManager {

    public static final String API_URI_IDEOS = "ideos.do";

    //获取图片
    public static final String ACTION__GET_LATEST_IDEOS_LIST = "getLatestIdeosList";


    /**
     * 获取引导图片
     *
     * @param context
     * @param type     图片类型0 开机启动图片1 首页弹窗图片
     * @param callback
     */
    public static void getLatestIdeosList(Activity context, int type, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_IDEOS;

        Map<String, String> params = new HashMap<>();
        params.put("type", "" + type);
        params.put("osType", "Android");

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION__GET_LATEST_IDEOS_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
