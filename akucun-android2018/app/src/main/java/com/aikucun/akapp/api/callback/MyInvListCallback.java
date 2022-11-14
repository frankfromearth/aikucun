package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.MyInv;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class MyInvListCallback extends ApiBaseCallback<List<MyInv>>
{

    @Override
    public List<MyInv> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("lst");
        List<MyInv> msgs = JSON.parseArray(dataList, MyInv.class);
        return msgs;
    }
}
