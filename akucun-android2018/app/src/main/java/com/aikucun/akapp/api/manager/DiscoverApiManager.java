package com.aikucun.akapp.api.manager;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.SCLog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 发现模块 HTTP 数据接口
 * Created by jarry on 2017/11/20.
 */
public class DiscoverApiManager {
    public static final String API_URI_DISCOVER = "discover.do";

    public static final String ACTION_DISCOVER_LIST = "page";
    public static final String ACTION_DISCOVER_CHECK = "checkupdate";
    public static final String ACTION_DISCOVER_UPLOAD = "upload";
    public static final String ACTION_DISCOVER_UPLOAD_VIDEO = "uploadnew";

    /**
     * 发现列表数据获取
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param pageno   页码
     * @param pagesize 每页请求数
     * @param callback 请求成功回调处理
     */
    public static void getListData(Activity context, int pageno, int pagesize, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;

        Map<String, String> params = new HashMap<>();
        params.put("pageno", String.valueOf(pageno));
        params.put("pagesize", String.valueOf(pagesize));

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_LIST, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 评论动态
     *
     * @param context
     * @param comment
     * @param discoverId
     * @param callback
     */
    public static void replayDiscover(Activity context, String comment, String discoverId, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("comment", comment);
            jsonObject.put("contentid", discoverId);
            jsonObject.put("userid", AppContext.getInstance().getUserId());

            Map<String, String> params = new HashMap<>();
            params.put("action", "comment");
            String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_LIST, params);


            OkGo.post(paramsUrl).upJson(jsonObject.toString()).tag(context).execute(callback);
            SCLog.logv("HTTP - Post : " + url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布动态
     *
     * @param context
     * @param title       标题
     * @param description 内容
     * @param latitude    经度
     * @param longitude   纬度
     * @param address     地址
     * @param videoPath   视频路径
     * @param imgPaths    图片数组（如果是视频就是视频封面路径）
     * @param callback
     */
    public static void pubishDiscvoer(Activity context, String title, String description, String latitude, String longitude, String address, String videoPath, ArrayList<String> imgPaths, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_UPLOAD, null);

        HttpParams httpParams = new HttpParams();
        httpParams.put("userid", AppContext.getInstance().getUserId());

        if (!TextUtils.isEmpty(videoPath)) {
            httpParams.put("video", new File(videoPath));
            httpParams.put("type", 1);
        } else httpParams.put("type", 0);

        if (imgPaths != null && imgPaths.size() > 0) {
            int j = 1;
            for (int i = 0, size = imgPaths.size(); i < size; i++) {
                if (!TextUtils.isEmpty(imgPaths.get(i)))
                    httpParams.put("images0" + j, new File(imgPaths.get(i)));
                Log.e("文件名称：", imgPaths.get(i));
                j++;
            }
        }
        httpParams.put("title", title);
        httpParams.put("description", description);
        if (!TextUtils.isEmpty(latitude))
            httpParams.put("latitude", latitude);
        if (!TextUtils.isEmpty(longitude))
            httpParams.put("longitude", longitude);
        if (!TextUtils.isEmpty(address))
            httpParams.put("address", address);
        OkGo.post(paramsUrl).tag(context).params(httpParams).
                execute(callback);
//        List<File> list = new ArrayList<>();
//        if (imgPaths != null && imgPaths.size() > 0) {
//            for (int i = 0, size = imgPaths.size(); i < size; i++) {
//                if (!TextUtils.isEmpty(imgPaths.get(i)))
//                    list.add(new File(imgPaths.get(i)));
//            }
//        }
//        OkGo.post(paramsUrl).tag(context).addFileParams("images01",list).params(httpParams).
//                execute(callback);

        SCLog.logv("HTTP - Post : " + paramsUrl);
    }

    /**
     * 发布视频动态
     * @param activity
     * @param title
     * @param content
     * @param latitude
     * @param longitude
     * @param address
     * @param videoId
     * @param imagesUrl
     * @param callback
     */
    public static void pubishDiscoverByVideo(Activity activity, String title, String content, String latitude, String longitude, String address, String videoId, String imagesUrl, AbsCallback callback) {
//        String url =  HttpConfig.getHost() + "/" +"http://192.168.31.35:8082/api/v1.0/discover.do" ;
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_UPLOAD_VIDEO, null);

        HttpParams httpParams = new HttpParams();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid",AppContext.getInstance().getUserId());
            jsonObject.put("type",1);
            jsonObject.put("title",title);
            jsonObject.put("content",content);
            jsonObject.put("latitude",latitude);
            jsonObject.put("longitude",longitude);
            jsonObject.put("address",address);
            jsonObject.put("videoId",videoId);
            jsonObject.put("imagesUrl",imagesUrl);
            OkGo.post(paramsUrl).tag(activity).upJson(jsonObject).execute(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        httpParams.put("userid", AppContext.getInstance().getUserId());
//        httpParams.put("type", 1);
//        httpParams.put("title", title);
//        httpParams.put("content", content);
//        httpParams.put("latitude", latitude);
//        httpParams.put("longitude", longitude);
//        httpParams.put("address", address);
//        httpParams.put("videoId", videoId);
//        httpParams.put("imagesUrl", imagesUrl);
//        OkGo.post(paramsUrl).tag(activity).params(httpParams).
//                execute(callback);
        Log.e("提交地址：",paramsUrl);
        SCLog.logv("HTTP - Post : " + paramsUrl);
    }

    /**
     * 检查发现是否有数据更新
     *
     * @param context  调用接口所在的Activity，用于标记Tag
     * @param lasttime 上一次检查更新时间（可以取列表中最新一条数据的时间戳，第一次为0，需要本地保存）
     * @param callback 请求成功回调处理
     */
    public static void checkUpdate(Activity context, long lasttime, AbsCallback callback) {
        // Url
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;

        Map<String, String> params = new HashMap<>();
        params.put("lasttime", "" + lasttime);

        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_CHECK, params);

        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }

    /**
     * 获取上传视频的token
     *
     * @param context
     * @param callback
     */
    public static void getUploadVideoToken(Activity context, AbsCallback callback) {
        String url = HttpConfig.getHost() + "/" + API_URI_DISCOVER;
        Map<String, String> params = new HashMap<>();
        params.put("action", "uploadauth");
        String paramsUrl = HttpConfig.httpParamsUrl(url, ACTION_DISCOVER_LIST, params);
        OkGo.get(paramsUrl).tag(context).execute(callback);

        SCLog.logv("HTTP - GET : " + paramsUrl);
    }
}
