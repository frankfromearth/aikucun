package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Discover;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/11/20.
 */
public class DiscoverListCallback extends ApiBaseCallback<List<Discover>>
{

    @Override
    public List<Discover> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("discover");
        List<Discover> datas = JSON.parseArray(dataList, Discover.class);
        return datas;
    }
}
