package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.LiveInfo;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by ak123 on 2018/1/5.
 * 活动中
 */

public class ActivingCallBack extends ApiBaseCallback<List<LiveInfo>> {

    @Override
    public List<LiveInfo> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("list");
        List<LiveInfo> LiveInfos = JSON.parseArray(dataList, LiveInfo.class);
        return LiveInfos;
    }
}