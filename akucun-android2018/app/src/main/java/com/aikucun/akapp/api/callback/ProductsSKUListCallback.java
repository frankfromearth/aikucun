package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.storage.ProductManager;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by jarry on 17/6/30.
 */

public class ProductsSKUListCallback extends ApiBaseCallback<List<ProductSKU>>
{
    private List<Product> originProducts;

    private List<Product> resultProducts;

    public ProductsSKUListCallback(List<Product> originProducts) {
        this.originProducts = originProducts;
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

        for (ProductSKU sku:productSKUs) {
            Product product = ProductManager.getInstance().findProductById(sku.getProductid());
            if (null != product) {
                product.updateSKU(sku);
                ProductManager.getInstance().updateProduct(product);
            }
        }

        if (originProducts != null && originProducts.size() > 0) {
            ArrayList<Product> results = new ArrayList<>();
            for (Product product:originProducts) {
                Product result = ProductManager.getInstance().findProductById(product.getId());
                if (null != product) {
                    results.add(product);
                }
            }
            setResultProducts(results);
        }
    }

    public List<Product> getResultProducts() {
        if (resultProducts == null)
            return originProducts;
        return resultProducts;
    }

    public void setResultProducts(List<Product> resultProducts) {
        this.resultProducts = resultProducts;
    }
}
