package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单相关 API 接口
 * Created by jarry on 2017/6/9.
 */
public class OrderApiManager {
    public static final String API_URI_ORDER = "order.do";

    public static final String ACTION_ORDER_CREATE = "create";
    public static final String ACTION_ORDER_PAY = "pay";

    public static final String ACTION_ORDER_DIRECTBUY = "directcreate";

    public static final String ACTION_ORDER_PAGE = "page";
    public static final String ACTION_ORDER_DETAIL = "detail";
    public static final String ACTION_ORDER_CANCEL_PRODUCT = "cancelProduct";

    public static final String ACTION_ORDER_CANCEL = "cancelOrder";
    public static final String ACTION_ORDER_COUNT = "statOrder";

    public static final String ACTION_ORDER_CHANGE = "changeProduct";

    public static final String ACTION_ORDER_AFTERSALE = "pageAfterSale";
    //支付结果查询
    public static final String ACTION_PAY_QUERY = "payquery";
    //查询明细
    public static final String ACTION_GET_BILL_LIST = "getstand";

    /**
     * 购物车中的商品进行订单提交 ，可以指定商品I，也可以不指定，不指定说明是全部要购买的商品
     *
     * @param context
     * @param products
     * @param callback
     */
    public static void createOrder(Activity context, List<String> products, String addrid, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        JSONObject jsonObj = new JSONObject();

        if (products != null && products.size() > 0) {
            jsonObj.put("cartproductids", (String[]) products.toArray(new String[products.size()]));
        } else {
            jsonObj.put("cartproductids", "[]");
        }

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("addrid", addrid);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_CREATE, urlParams);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    public static void directBuyProduct(Activity context, String liveId, String addrId, String
            productId, String skuId, String remark, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("liveid", liveId);
        jsonObj.put("addrid", addrId);
        jsonObj.put("productid", productId);
        jsonObj.put("skuid", skuId);
        jsonObj.put("remark", StringUtils.isEmpty(remark) ? "" : remark);

//        Map<String, String> urlParams = new HashMap<>();
//        urlParams.put("addrid",  addrId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_DIRECTBUY, null);
        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    /**
     * 对订单进行支付
     *
     * @param context
     * @param orderIds 订单列表，可以是一个订单，也可以是多个
     * @param payType  支付方式 -- 1：支付宝  2：微信支付
     * @param callback
     */
    public static void payOrder(Activity context, String orderIds, int payType, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("paytype", "" + payType);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("orderids", JSONArray.parseArray(orderIds));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_PAY, urlParams);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl + "\n");
        SCLog.logObj(jsonObj);
    }

    /**
     * 订单全页获取，默认全部订单
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param callback
     */
    public static void pageOrder(Activity context, int pageno, int pagesize, AbsCallback callback) {
        // 全部订单
        int dingdanstatu = -1;
        pageOrder2(context, pageno, pagesize, dingdanstatu, callback);
    }

    /**
     * 订单分页获取
     *
     * @param context
     * @param pageno
     * @param pagesize
     * @param dingdanstatu -1: 是全部订单 0： 未支付 1：待发货 2：已发货  4：已取消订单
     * @param callback
     */
    public static void pageOrder2(Activity context, int pageno, int pagesize, int dingdanstatu,
                                  AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));
        params.put("dingdanstatu", String.valueOf(dingdanstatu));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_PAGE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


    /**
     * 订单详情获取
     *
     * @param context
     * @param orderid
     * @param callback
     */
    public static void detailOrder(Activity context, String orderid, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();

        params.put("orderid", orderid);


        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_DETAIL, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 订单中的商品取消购买
     *
     * @param context
     * @param orderid
     * @param cartproductid
     * @param callback
     */
    public static void cancelProduct(Activity context, String orderid, String cartproductid,
                                     AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();
        params.put("cartproductid", cartproductid);
        params.put("orderid", orderid);
        params.put("isall", "false");

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_CANCEL_PRODUCT, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 未支付订单进行取消
     *
     * @param context
     * @param orderid
     * @param callback
     */
    public static void cancelOrder(Activity context, String orderid, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();
        params.put("orderid", orderid);


        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_CANCEL, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void changeProduct(Activity context, String orderid, String productId, String
            skuId, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();
        params.put("orderid", orderid);
        params.put("cartproductid", productId);
        params.put("newskuid", skuId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_CHANGE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 不同状态订单数量统计
     *
     * @param context
     * @param callback
     */
    public static void orderCount(Activity context, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_COUNT, null);

        String cacheKey = RSAUtils.md5String(url + ACTION_ORDER_COUNT);
        OkGo.get(paramsUrl).cacheKey(cacheKey).cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void afterSaleProducts(Activity context, int pageno, int pagesize, int status,
                                         AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));
        params.put("status", String.valueOf(status));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ORDER_AFTERSALE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取联动支付返回结果
     *
     * @param context
     * @param payment_id
     * @param callback
     */
    public static void getPayResult(Activity context, String payment_id, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;
        Map<String, String> params = new HashMap<>();
        params.put("payment_id", payment_id);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PAY_QUERY, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取收支明细列表
     *
     * @param context
     * @param page
     * @param callback
     */
    public static void getBillList(Activity context, int page, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_ORDER;
        Map<String, String> params = new HashMap<>();
        params.put("pagenum", page + "");
        params.put("pagesize", "12");
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_GET_BILL_LIST, params);
        OkGo.post(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
