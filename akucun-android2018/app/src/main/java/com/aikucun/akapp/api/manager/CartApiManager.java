package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 购物车相关 API 接口
 * Created by jarry on 2017/6/9.
 */
public class CartApiManager
{
    public static final String API_URI_CART = "cart.do";

    public static final String ACTION_CART_LIST = "getcart";
    public static final String ACTION_CART_RECYCLE_LIST = "recyclelist";


    /**
     * 获取购物车商品数据
     *
     * @param context   调用接口所在的Activity
     * @param callback  请求成功回调处理
     */
    public static void getCartProducts(Activity context, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_CART;

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_CART_LIST, null);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取回收购物清单商品数据
     *
     * @param context   调用接口所在的Activity
     * @param callback  请求成功回调处理
     */
    public static void getCartRecycleProducts(Activity context, int pageno, int pagesize, AbsCallback callback)
    {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_CART;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_CART_RECYCLE_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }


}
