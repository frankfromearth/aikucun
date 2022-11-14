package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.CartProduct;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class AfterSaleCallback extends ApiBaseCallback<List<CartProduct>>
{

    @Override
    public List<CartProduct> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("products");
        List<CartProduct> products = JSON.parseArray(dataList, CartProduct.class);
        return products;
    }
}
