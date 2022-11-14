package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Trace;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/11/20.
 */
public class TraceListCallback extends ApiBaseCallback<List<Trace>> {

    @Override
    public List<Trace> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("list");
        List<Trace> datas = JSON.parseArray(dataList, Trace.class);
        return datas;
    }
}
