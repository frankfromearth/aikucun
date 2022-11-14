package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.MemberOrderPayResp;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 2017/6/10.
 */

public class MemberOrderPayCallback extends ApiBaseCallback<MemberOrderPayResp>
{
    @Override
    public MemberOrderPayResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        MemberOrderPayResp response = JSON.parseObject(jsonString, MemberOrderPayResp.class);
        return response;
    }
}
