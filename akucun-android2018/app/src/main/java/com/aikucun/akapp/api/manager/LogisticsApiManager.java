package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ak123 on 2017/12/11.
 * 物流管理
 */

public class LogisticsApiManager {
    public static final String API_URI_DISCOVER = "deliver.do";
    public static final String ACTION_DISCOVER_LOGISTICS = "trace";

    /**
     * 物流信息
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param deliverId 快递单号
     * @param logistics 快递公司类型：18-德邦，6-京东
     * @param callback  请求成功回调处理
     */
    public static void getListData(Activity context, String deliverId, int logistics, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;
        Map<String, String> params = new HashMap<>();
        params.put("deliverId", deliverId);
        params.put("logistics", String.valueOf(logistics));
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_LOGISTICS, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
