package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品相关 API 接口
 * Created by jarry on 2017/6/9.
 */
public class ProductApiManager {
    public static final String API_URI_PRODUCT = "product.do";

    public static final String ACTION_PRODUCT_BUY = "buy";
    public static final String ACTION_CANCEL_BUY = "cancelBuy";
    public static final String ACTION_PRODUCT_FORWARD = "forward";
    public static final String ACTION_FORWARD_LIST = "forwardlist";
    public static final String ACTION_PRODUCT_REMARK = "remark";
    public static final String ACTION_PRODUCT_GET_SKU = "getsku";
    public static final String ACTION_PRODUCT_UPDATE_SKU = "updatesku";

    public static final String ACTION_PRODUCT_LACK = "reportlack";
    public static final String ACTION_PRODUCT_SCAN = "reportscan";
    public static final String ACTION_BARCODE_SEARCH = "barcodesearch";

    public static final String ACTION_PRODUCT_FOLLOW = "follow";
    public static final String ACTION_PRODUCT_FOLLOW_LIST = "followlist";


    /**
     * 添加商品到购物车
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param productId 商品 ID
     * @param skuId     商品 SKU ID
     * @param callback  请求成功回调处理
     */
    public static void buyProduct(Activity context, String productId, String skuId, String
            remark, String cartproductid, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);
        params.put("skuid", skuId);
        params.put("remark", StringUtils.isEmpty(remark) ? "" : remark);
        if (!StringUtils.isEmpty(cartproductid)) {
            params.put("cartproductid", cartproductid);
        }

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_BUY, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 取消购物车中的商品
     *
     * @param context       调用接口所在的Activity，用于标记Tag
     * @param cartProductId 购物车中的商品ID
     * @param callback      请求成功回调处理
     */
    public static void cancelBuyProduct(Activity context, String cartProductId, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("cartproductid", cartProductId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_CANCEL_BUY, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 购物车商品加备注
     *
     * @param context       调用接口所在的Activity，用于标记Tag
     * @param cartProductId 购物车中的商品ID
     * @param remark        备注信息
     * @param callback      请求成功回调处理
     */
    public static void remarkProduct(Activity context, String cartProductId, String remark,
                                     AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", cartProductId);
        params.put("remark", remark);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_REMARK, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 商品转发记录
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param productId 商品ID
     * @param callback  请求成功回调处理
     */
    public static void forwardProduct(Activity context, String productId, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_FORWARD, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 商品转发列表获取
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param pageno   页码
     * @param pagesize 页的大小
     * @param callback 请求成功回调处理
     */
    public static void forwardListProduct(Activity context, int pageno, int pagesize, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_FORWARD_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * @param context
     * @param index    位置
     * @param callback 请求成功回调处理
     */
    public static void forwardList2Product(Activity context, int index, AbsCallback callback) {
        int pagesize = 20;
        int pageno = index / pagesize + 1;

        forwardListProduct(context, pageno, pagesize, callback);
    }

    /**
     * 商品转发记录
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param productId 商品ID
     * @param callback  请求成功回调处理
     */
    public static void getSKUProduct(Activity context, String productId, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_GET_SKU, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 商品转发记录
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param products 商品IDs
     * @param callback 请求成功回调处理
     */
    public static void updateSKUProduct(Activity context, List<String> products, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        JSONObject jsonObj = new JSONObject();

        if (products != null && products.size() > 0) {
            jsonObj.put("products", (String[]) products.toArray(new String[products.size()]));
        } else {
            jsonObj.put("products", "[]");
        }

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_UPDATE_SKU, null);

        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    /**
     * 漏发申诉
     *
     * @param context       调用接口所在的Activity，用于标记Tag
     * @param cartproductid 商品IDs
     * @param callback      请求成功回调处理
     */
    public static void updateLackProduct(Activity context, String cartproductid, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cartproductid", cartproductid);
        jsonObj.put("userdesc", "");


        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_LACK, null);
        OkGo.post(paramsUrl).upJson(jsonObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
    }

    /**
     * 商品转发记录
     *
     * @param context       调用接口所在的Activity，用于标记Tag
     * @param cartproductid 商品IDs
     * @param adorderid     商品IDs
     * @param barcode       商品IDs
     * @param statu         商品IDs
     * @param callback      请求成功回调处理
     */
    public static void updateScanProduct(Activity context, String cartproductid, String
            adorderid, String barcode, int statu, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("cartproductid", cartproductid);
        jsonObj.put("adorderid", adorderid);
        jsonObj.put("barcode", barcode);
        jsonObj.put("statu", Integer.toString(statu));

        JSONObject postObj = new JSONObject();
        postObj.put("result", jsonObj);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_SCAN, null);

        OkGo.post(paramsUrl).upJson(postObj.toJSONString()).tag(context).execute(callback);

        SCLog.logv("HTTP - POST : " + paramsUrl);
        SCLog.logObj(postObj);
    }

    /**
     * 商品关注记录
     *
     * @param context   调用接口所在的Activity，用于标记Tag
     * @param productId 商品ID
     * @param statu     状态 ， 1 是关注，其它是取消关注
     * @param callback  请求成功回调处理
     */
    public static void followProduct(Activity context, String productId, int statu, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("productid", productId);
        params.put("statu", statu + "");

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_FOLLOW, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 商品关注列表获取
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param pageno   页码
     * @param pagesize 页的大小
     * @param callback 请求成功回调处理
     */
    public static void followListProduct(Activity context, int pageno, int pagesize, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_PRODUCT_FOLLOW_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    public static void barcodeSearch(Activity context, String barcode, String liveId, AbsCallback
            callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_PRODUCT;

        Map<String, String> params = new HashMap<>();
        params.put("barcode", barcode);
        if (liveId != null) {
            params.put("barcode", barcode);
        }

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_BARCODE_SEARCH, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

}
