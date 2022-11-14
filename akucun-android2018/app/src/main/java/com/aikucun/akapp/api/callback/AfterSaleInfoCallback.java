package com.aikucun.akapp.api.callback;

import android.util.Log;

import com.aikucun.akapp.api.entity.AfterSaleItem;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 17/6/30.
 */

public class AfterSaleInfoCallback extends ApiBaseCallback<AfterSaleItem>
{

    @Override
    public AfterSaleItem parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("product");
        AfterSaleItem afterSaleItem = JSON.parseObject(dataList, AfterSaleItem.class);
        return afterSaleItem;
    }
}
