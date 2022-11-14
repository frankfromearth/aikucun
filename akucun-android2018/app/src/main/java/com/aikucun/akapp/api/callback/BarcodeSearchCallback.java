package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.CartProduct;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/12/12.
 */

public class BarcodeSearchCallback extends ApiBaseCallback<List<CartProduct>>
{

    @Override
    public List<CartProduct> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("products");
        List<CartProduct> list = JSON.parseArray(dataList, CartProduct.class);
        return list;
    }

}
