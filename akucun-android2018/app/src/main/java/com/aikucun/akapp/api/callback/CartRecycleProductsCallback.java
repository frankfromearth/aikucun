package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.CartProduct;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 购物车列表返回数据解析
 * Created by jarry on 2017/6/9.
 */
public class CartRecycleProductsCallback extends ApiBaseCallback<List<CartProduct>>
{
    @Override
    public List<CartProduct> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("cartproducts");
        List<CartProduct> cartsList = JSON.parseArray(dataList, CartProduct.class);
        return cartsList;
    }
}
