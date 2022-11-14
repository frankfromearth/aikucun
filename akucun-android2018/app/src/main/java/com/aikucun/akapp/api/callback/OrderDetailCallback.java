package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.OrderDetailResp;
import com.alibaba.fastjson.JSON;

/**
 *
 * Created by jarry on 2017/6/12.
 */
public class OrderDetailCallback extends ApiBaseCallback<OrderDetailResp>
{
    @Override
    public OrderDetailResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        OrderDetailResp response = JSON.parseObject(jsonString, OrderDetailResp.class);
        return response;
    }
}
