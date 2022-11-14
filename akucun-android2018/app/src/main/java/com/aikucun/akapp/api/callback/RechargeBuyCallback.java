package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.RechargeItem;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 17/6/30.
 */

public class RechargeBuyCallback extends ApiBaseCallback<RechargeItem>
{

    @Override
    public RechargeItem parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        RechargeItem response = JSON.parseObject(jsonString, RechargeItem.class);
        return response;
    }
}
