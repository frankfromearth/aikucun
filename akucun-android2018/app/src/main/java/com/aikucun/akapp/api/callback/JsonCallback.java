package com.aikucun.akapp.api.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import okhttp3.Call;
import okhttp3.Response;

/**
 * JSON 对象返回数据
 * Created by jarry on 16/11/8.
 */
public class JsonCallback extends ApiBaseCallback<JSONObject>
{
    @Override
    public JSONObject convertSuccess(Response response) throws Exception
    {
        String body = response.body().string();
        this.bodyString = body;

        JSONObject jsonObject = null;
        try
        {
            jsonObject = JSON.parseObject(body);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Logger.e("JSON String Error :\n" + body);
            return null;
        }

        return jsonObject;
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Call call, Response response)
    {
        onApiSuccess(jsonObject, call, null);
    }
}
