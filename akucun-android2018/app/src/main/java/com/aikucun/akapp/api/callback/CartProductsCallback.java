package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.PinpaiCart;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 购物车列表返回数据解析
 * Created by jarry on 2017/6/9.
 */
public class CartProductsCallback extends ApiBaseCallback<List<PinpaiCart>>
{
    @Override
    public List<PinpaiCart> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("pgs");
        List<PinpaiCart> cartsList = JSON.parseArray(dataList, PinpaiCart.class);
        return cartsList;
    }
}
