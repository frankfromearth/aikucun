package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class AfterSaleListCallback extends ApiBaseCallback<List<AfterSaleItem>>
{

    @Override
    public List<AfterSaleItem> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("products");
        List<AfterSaleItem> products = JSON.parseArray(dataList, AfterSaleItem.class);
        return products;
    }
}
