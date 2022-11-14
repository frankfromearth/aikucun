package com.aikucun.akapp.api.manager;

import android.app.Activity;

import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品直播相关 HTTP 接口
 * Created by jarry on 2017/6/7.
 */
public class LiveApiManager {
    public static final String API_URI_LIVE = "live.do";

    public static final String ACTION_LIVE_GETSTATE = "getState2";
    public static final String ACTION_LIVE_TRAILER = "getTrailer";
    public static final String ACTION_TRACK_PRODUCTS = "trackProducts";
    public static final String ACTION_TRACK_COMMENTS = "trackComments";
    public static final String ACTION_SYNC_PRODUCTS = "syncProducts";
    public static final String ACTION_SYNC_COMMENTS = "syncComments";
    //活动预告
    public static final String ACTION_ACTIVITY_PREPARATION = "preparation";
    //活动进行中
    public static final String ACTION_ACTIVITY_TO_DAY = "todayAction";
    //今日切货
    public static final String ACTION_CUT_GOODS_LIVING = "cutLiving";
    //通过活动id同步商品
    public static final String ACTION_SYNC_PRODUCTS_BY_LIVEID = "getProductAct";
    //跟踪商品
    public static final String ACTION_SYNC_PRODUCTS_TRACK_SKUS = "trackSkus";


    /**
     * 当前直播状态获取
     *
     * @param context
     * @param time     跟踪时间，用于获取下架商品和失效图片
     * @param callback
     */
    public static void getLiveState(Activity context, long time, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;

        Map<String, String> params = new HashMap<>();
        params.put("sync", "" + time);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_LIVE_GETSTATE, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 同步商品数据
     *
     * @param context
     * @param sync
     * @param callback
     */
    public static void syncProducts(Activity context, long sync, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;

        Map<String, String> params = new HashMap<>();
        params.put("sync", "" + sync);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYNC_PRODUCTS, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 通过活动id查询商品
     * @param context
     * @param liveId
     * @param lastxuhao 最后商品的序号的最大的值
     * @param callback
     */
    public static void syncProductsByLiveId(Activity context, String liveId,String lastxuhao,AbsCallback callback){
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;
        Map<String, String> params = new HashMap<>();
        params.put("liveid", liveId);
        params.put("lastxuhao", lastxuhao);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYNC_PRODUCTS_BY_LIVEID, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
    }

    /**
     * 同步评论数据
     *
     * @param context
     * @param sync
     * @param callback
     */
    public static void syncComments(Activity context, long sync, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;

        Map<String, String> params = new HashMap<>();
        params.put("sync", "" + sync);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYNC_COMMENTS, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 跟踪商品信息
     *
     * @param context
     * @param synclive
     * @param syncsku
     * @param callback
     */
    public static void trackProducts(Activity context, long synclive, long syncsku, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;

        Map<String, String> params = new HashMap<>();
        params.put("synclive", "" + synclive);
        params.put("syncsku", "" + syncsku);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_TRACK_PRODUCTS, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取活动预告
     *
     * @param context
     * @param callback
     */
    public static void getActivityTrailder(Activity context, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACTIVITY_PREPARATION, null);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 活动进行中
     *
     * @param context
     * @param callback
     */
    public static void getActivityToday(Activity context, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_ACTIVITY_TO_DAY, null);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 今日切货
     * @param context
     * @param callback
     */
    public static void getCutGoodsLiving(Activity context, AbsCallback callback){
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_CUT_GOODS_LIVING, null);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 跟踪商品
     * @param context
     * @param liveid
     * @param syncsku
     * @param callback
     */
    public static void trackSkus(Activity context,String liveid,long syncsku, AbsCallback callback){
        String url = HttpConfig.getHost() + "/" + API_URI_LIVE;
        Map<String, String> params = new HashMap<>();
        params.put("liveid",liveid);
        params.put("syncsku", "" + syncsku);
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_SYNC_PRODUCTS_TRACK_SKUS, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);
        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

}
