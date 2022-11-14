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
 * Created by jarry on 2017/7/1.
 */

public class DeliverApiManager
{
    public static final String API_URI_DELIVER = "deliver.do";

    public static final String ACTION_DELIVER_LIST = "page";
    public static final String ACTION_DELIVER_DETAIL = "detail";
    public static final String ACTION_DELIVER_APPLY = "apply";
    public static final String ACTION_DELIVER_ADDR = "modifyaddr";

    /**
     * 发货单列表
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param statu
     * @param callback
     */
    public static void deliverList(Activity context, int pageno, int pagesize, int statu,
                                   AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DELIVER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));
        params.put("statu", String.valueOf(statu));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DELIVER_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 发货单详情
     *
     * @param context
     * @param adorderid
     * @param callback
     */
    public static void deliverDetail(Activity context, String adorderid, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DELIVER;

        Map<String, String> params = new HashMap<>();
        params.put("adorderid", String.valueOf(adorderid));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DELIVER_DETAIL, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 7.3	申请临时对帐单
     *
     * @param context
     * @param adorderid
     * @param callback
     * 申请的过程是直接返回的，但生成对帐单是异步的，需要时间，可能是二分钟，也可能更久，根据商品数据大小有关以及服务器处理时机有关，
     * 申请后，过段时间获取发货单详情时，可得到downloadurl , 当发货单已正式发货后，临时申请的地址不再返回。
     * 可允许用户多次申请，因为待发货时对帐单是不稳定的
     */
    public static void applyDeliverDetail(Activity context, String adorderid, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DELIVER;

        Map<String, String> params = new HashMap<>();
        params.put("adorderid", String.valueOf(adorderid));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DELIVER_APPLY, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 更新对账单
     * @param context
     * @param liveId
     * @param callback
     */
    public static void updateBill(Activity context, String liveId, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DELIVER;

        Map<String, String> params = new HashMap<>();
        params.put("liveid", liveId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DELIVER_APPLY, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
    /**
     * 用户订单地址修改接口
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param name     用户名
     * @param mobile   手机号
     * @param province 省份
     * @param city     城市
     * @param area     地区
     * @param address  详细地址
     * @param adorderid  订单ID
     * @param callback 请求成功回调处理
     */
    public static void modifyOrderAddress(Activity context, String name, String mobile, String
            province, String city, String area, String address, String adorderid,  AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DELIVER;

        JSONObject jsonObj = new JSONObject();
        JSONObject resultObj = new JSONObject();

        jsonObj.put("shoujianren", name);
        jsonObj.put("dianhua", mobile);
        jsonObj.put("sheng", province);
        jsonObj.put("shi", city);
        jsonObj.put("qu", area);
        jsonObj.put("detailaddr", address);
        resultObj.put("addr",jsonObj);

        Map<String, String> params = new HashMap<>();
        params.put("adorderid", adorderid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DELIVER_ADDR, params);
        OkGo.post(paramsUrl).tag(context).upJson(resultObj.toJSONString()).execute(callback);
        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

}
