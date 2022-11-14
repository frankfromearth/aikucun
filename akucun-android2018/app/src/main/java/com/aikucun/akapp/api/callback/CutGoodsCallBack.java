package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.CutGoods;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by ak123 on 2018/1/5.
 * 今日切货
 */

public class CutGoodsCallBack extends ApiBaseCallback<List<CutGoods>> {

    @Override
    public List<CutGoods> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("list");
        List<CutGoods> CutGoodss = JSON.parseArray(dataList, CutGoods.class);
        return CutGoodss;
    }
}