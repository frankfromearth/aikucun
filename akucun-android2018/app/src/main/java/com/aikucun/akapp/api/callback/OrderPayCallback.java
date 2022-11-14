package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.OrderPayResp;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 2017/6/10.
 */

public class OrderPayCallback extends ApiBaseCallback<OrderPayResp>
{
    @Override
    public OrderPayResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        OrderPayResp response = JSON.parseObject(jsonString, OrderPayResp.class);
        return response;
    }
}
