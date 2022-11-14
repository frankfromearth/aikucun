package com.aikucun.akapp.api.callback;

import android.support.annotation.Nullable;
import android.util.Log;

import com.aikucun.akapp.AppManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.SCLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.orhanobut.logger.Logger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * API 返回数据处理 基类
 * Created by jarry on 16/5/12.
 */
public abstract class ApiBaseCallback<T> extends AbsCallback<T> {
    public String bodyString;

    public ApiResponse responseData;

    /**
     * 返回数据解析，各实体类重写该方法实现具体数据解析
     *
     * @param responseData 返回数据对象
     * @return <T> 具体数据对象
     * @throws Exception
     */
    public T parseResponse(ApiResponse responseData) throws Exception {
        return null;
    }

    /**
     * API 成功调用回调
     *
     * @param t            <T> 具体数据对象
     * @param call         回调接口
     * @param jsonResponse 返回数据对象
     */
    public void onApiSuccess(T t, Call call, ApiResponse jsonResponse) {
        SCLog.logi("HTTP - Response :");
        SCLog.logJson(this.bodyString);
    }

    /**
     * API 失败调用回调
     *
     * @param message 失败错误信息
     */
    public void onApiFailed(String message, int code) {
        SCLog.logi("HTTP - Failed : " + message);
        BaseActivity activity = AppManager.getAppManager().currentActivity();
        if (activity != null) {
            activity.handleApiFailed(message, code);
        }
    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        this.bodyString = response.body().string();

        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(this.bodyString);
        } catch (JSONException e) {
            e.printStackTrace();
            Logger.e("JSON String Error :\n" + this.bodyString);
            return null;
        }

        String status = jsonObject.getString("status");

        if (status.equals("error") || status.equals("fail")) {
            Integer code = jsonObject.getInteger("code");
            String message = jsonObject.getString("message");
            responseData = new ApiResponse(false, code, message, null);
            return null;
        }
        Object object = jsonObject.get("data");

        if (object instanceof JSONArray) {
            Log.e("返回数据类型属于arr：", "----->"+object);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("list", object);
            responseData = new ApiResponse(true, 0, null, jsonObject1);
        } else if (object instanceof String) {
            Log.e("返回数据类型属于字符串：", "----->"+object);
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("data", object);
            responseData = new ApiResponse(true, 0, null, jsonObject1);
        } else {
            Log.e("返回数据类型属于obj：", "----->"+object);
            responseData = new ApiResponse(true, 0, null, jsonObject.getJSONObject("data"));
        }


        T t = parseResponse(responseData);
        response.close();

        return t;
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {
        if (responseData != null && (responseData.getCode() > 0 || responseData.getCode() == -1)) {
            Logger.e("Failed : " + responseData.getCode() + " - " + responseData.getMessage());
            onApiFailed(responseData.getMessage(), responseData.getCode().intValue());
            return;
        }

        if (t != null) {
            onApiSuccess(t, call, responseData);
            return;
        }
        Log.e("ApiBaseCallBack","onSuccess"+call.request().url()+"");
        onApiFailed("数据解析错误 !", 0);
    }

    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);

        Logger.e("HTTP Error : " + e.toString());
        Log.e("ApiBaseCallBack","onError"+call.request().url()+"");
        if (response != null) {
            onApiFailed("数据解析错误 !", 0);
            return;
        }
        onApiFailed("网络连接异常 !", 0);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
    }

    @Override
    public void onCacheSuccess(T t, Call call) {
        super.onCacheSuccess(t, call);
    }


    @Override
    public void onCacheError(Call call, Exception e) {
        super.onCacheError(call, e);
    }

    @Override
    public void parseError(Call call, Exception e) {
        super.parseError(call, e);
    }

    @Override
    public void onAfter(@Nullable T t, @Nullable Exception e) {
        super.onAfter(t, e);
    }

    @Override
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
        super.upProgress(currentSize, totalSize, progress, networkSpeed);
    }

    @Override
    public void downloadProgress(long currentSize, long totalSize, float progress, long
            networkSpeed) {
        super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
    }
}
