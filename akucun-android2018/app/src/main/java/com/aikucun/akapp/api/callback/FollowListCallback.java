package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarry on 2017/6/11.
 */
public class FollowListCallback extends ApiBaseCallback<List<Product>>
{

    @Override
    public List<Product> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("products");
        List<String> productIds = JSON.parseArray(dataList, String.class);

        List<Product> products = new ArrayList<>();
        if (productIds.size() == 0)
        {
            return products;
        }

        for (String productId : productIds)
        {
            if (StringUtils.isEmpty(productId))
                continue;
            Product product = ProductManager.getInstance().findProductById(productId);
            if (product != null)
            {
                product.setFollow(1);
                products.add(product);
                ProductManager.getInstance().updateProduct(product);
            }
        }

        return products;
    }
}
