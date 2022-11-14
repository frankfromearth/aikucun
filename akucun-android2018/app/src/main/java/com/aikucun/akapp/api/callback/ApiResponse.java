package com.aikucun.akapp.api.callback;

import com.alibaba.fastjson.JSONObject;

/**
 * API 返回数据对象
 * Created by jarry on 16/5/12.
 */
public class ApiResponse
{
    private boolean status;

    private Integer code;

    private String message;

    private JSONObject jsonObject;


    public ApiResponse(boolean status, Integer code, String message, JSONObject jsonObject)
    {
        this.status = status;
        this.code = code;
        this.message = message;
        this.jsonObject = jsonObject;
    }

    public boolean isStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }

    public JSONObject getJsonObject()
    {
        return jsonObject;
    }
}
