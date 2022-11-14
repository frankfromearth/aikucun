package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.AdOrder;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 2017/7/1.
 */

public class DeliverDetailCallback extends ApiBaseCallback<AdOrder>
{
    @Override
    public AdOrder parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().getString("adorder");
        AdOrder order = JSON.parseObject(jsonString, AdOrder.class);
        return order;
    }
}
