package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.OrderModel;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 *
 * Created by jarry on 2017/6/10.
 */

public class OrderListCallback extends ApiBaseCallback<List<OrderModel>>
{
    @Override
    public List<OrderModel> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("orders");
        List<OrderModel> orders = JSON.parseArray(dataList, OrderModel.class);
        return orders;
    }
}
