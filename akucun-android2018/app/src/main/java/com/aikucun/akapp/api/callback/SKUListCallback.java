package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.storage.ProductManager;
import com.alibaba.fastjson.JSON;

import java.util.List;

import okhttp3.Call;

/**
 * Created by jarry on 17/6/30.
 */

public class SKUListCallback extends ApiBaseCallback<List<ProductSKU>>
{

    protected Product product;

    public SKUListCallback(Product product) {
        this.product = product;
    }

    @Override
    public List<ProductSKU> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("skus");
        List<ProductSKU> msgs = JSON.parseArray(dataList, ProductSKU.class);
        return msgs;
    }

    @Override
    public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
        super.onApiSuccess(productSKUs, call, jsonResponse);

        if (product != null) {
            SKUListCallback.this.product.setSkus(productSKUs);
            ProductManager.getInstance().updateProduct(product);
        }
    }
}
