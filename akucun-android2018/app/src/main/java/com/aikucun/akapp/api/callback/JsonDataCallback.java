package com.aikucun.akapp.api.callback;

import com.alibaba.fastjson.JSONObject;

/**
 * API Data JSON 对象返回数据
 * Created by jarry on 16/11/8.
 */
public class JsonDataCallback extends ApiBaseCallback<JSONObject>
{
    @Override
    public JSONObject parseResponse(ApiResponse responseData) throws Exception
    {
        return responseData.getJsonObject();
    }

}
