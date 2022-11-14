package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.AdOrder;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/7/1.
 */

public class DeliverListCallback extends ApiBaseCallback<List<AdOrder>>
{
    @Override
    public List<AdOrder> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("adorders");
        List<AdOrder> orders = JSON.parseArray(dataList, AdOrder.class);
        return orders;
    }
}
