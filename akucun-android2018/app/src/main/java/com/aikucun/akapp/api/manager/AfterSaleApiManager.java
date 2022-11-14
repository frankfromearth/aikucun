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

public class AfterSaleApiManager
{
    public static final String API_URI_SYSTEM = "aftersale.do";

    public static final String ACTION_AFTER_SALE= "apply";
    public static final String ACTION_AFTER_SALE_MORE = "applymore";
    public static final String ACTION_AFTER_SALE_LIST= "page";
    public static final String ACTION_AFTER_SALE_QUERY_INFO= "queryinfo";

    /*
    10.1	申请售后
     */
    public static void afterSaleApply(Activity context,
                                      String cartproductid,
                                      int problemtype,
                                      int expecttype,
                                      String problemdesc,
                                      String pingzheng,
             AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        JSONObject jsonObj = new JSONObject();
        if (!StringUtils.isEmpty(pingzheng)) {
            jsonObj.put("pingzheng", new String[]{pingzheng});
        } else {
            jsonObj.put("pingzheng", new String[]{});
        }
        jsonObj.put("cartproductid", cartproductid);
        jsonObj.put("problemdesc", !StringUtils.isEmpty(problemdesc)?problemdesc:"");
        jsonObj.put("problemtype", problemtype+1);
        jsonObj.put("expecttype", expecttype);


        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_AFTER_SALE, null);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
        SCLog.logObj(jsonObj);
    }

    /*
    10.2	申请售后退货填写单号
     */
    public static void afterSaleApplyMore(Activity context,
                                          String cartProductId,
                                          String wuliugongsi,
                                          String wuliuhao,
                                          AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cartproductid", cartProductId);
        jsonObj.put("refundcorp", wuliugongsi);
        jsonObj.put("refundhao", wuliuhao);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_AFTER_SALE_MORE, null);
        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
        SCLog.logObj(jsonObj);
    }

    /*
    10.3	申请售后记录查询
     */
    public static void afterSaleList(Activity context, int pageno, int pagesize, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_AFTER_SALE_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /*
    10.3	申请售后记录查询
     */

    public static void afterSaleQueryInfo(Activity context, String cartproductId, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_SYSTEM;

        Map<String, String> params = new HashMap<>();
        params.put("cartproductid", cartproductId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_AFTER_SALE_QUERY_INFO, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

}
