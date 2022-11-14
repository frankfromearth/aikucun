package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.PayType;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/6/22.
 */

public class PayTypesCallback extends ApiBaseCallback<List<PayType>>
{
    @Override
    public List<PayType> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("paytype");
        List<PayType> payTypes = JSON.parseArray(dataList, PayType.class);
        return payTypes;
    }
}
