package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.RechargeItem;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class RechargeCallback extends ApiBaseCallback<List<RechargeItem>>
{
    public String desc;

    @Override
    public List<RechargeItem> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("deltas");
        List<RechargeItem> data = JSON.parseArray(dataList, RechargeItem.class);
        this.desc = responseData.getJsonObject().getString("desc");
        return data;
    }
}
