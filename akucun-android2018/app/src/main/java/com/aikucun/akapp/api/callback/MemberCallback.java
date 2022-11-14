package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.MemberInfo;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class MemberCallback extends ApiBaseCallback<List<MemberInfo>>
{

    @Override
    public List<MemberInfo> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("purchases");
        List<MemberInfo> data = JSON.parseArray(dataList, MemberInfo.class);
        return data;
    }
}
