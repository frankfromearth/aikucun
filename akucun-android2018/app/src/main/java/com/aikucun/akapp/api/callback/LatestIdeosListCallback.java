package com.aikucun.akapp.api.callback;

import android.util.Log;

import com.aikucun.akapp.api.entity.LatestIdeosList;
import com.alibaba.fastjson.JSON;

/**
 * Created by ak on 2018/2/28.
 * 引导活动图片
 */

public class LatestIdeosListCallback extends ApiBaseCallback<LatestIdeosList> {

    @Override
    public LatestIdeosList parseResponse(ApiResponse responseData) throws Exception {
        String jsonString = responseData.getJsonObject().toJSONString();
        LatestIdeosList latestIdeosList=JSON.parseObject(jsonString,LatestIdeosList.class);
        return latestIdeosList;
    }
}
