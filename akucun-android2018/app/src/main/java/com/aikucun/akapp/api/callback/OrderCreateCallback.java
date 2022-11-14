package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.OrderCreateResp;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 2017/6/10.
 */

public class OrderCreateCallback extends ApiBaseCallback<OrderCreateResp>
{
    @Override
    public OrderCreateResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        OrderCreateResp response = JSON.parseObject(jsonString, OrderCreateResp.class);
        return response;
    }
}
