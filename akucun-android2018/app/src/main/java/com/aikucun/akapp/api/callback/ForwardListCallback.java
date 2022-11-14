package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarry on 2017/6/11.
 */
public class ForwardListCallback extends ApiBaseCallback<List<Product>>
{

    @Override
    public List<Product> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("products");
        List<String> productIds = JSON.parseArray(dataList, String.class);

        //
        JSONObject userObj = responseData.getJsonObject().getJSONObject("user");
        if (userObj != null)
        {
            int forwardcount = userObj.getIntValue("forwardcount");
            int keyongdikou = userObj.getIntValue("keyongdikou");
            int yiyongdikou = userObj.getIntValue("yiyongdikou");
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            // FIXME: 2018/1/4 
//            userInfo.setForwardcount(forwardcount);
//            userInfo.setKeyongdikou(keyongdikou);
//            userInfo.setYiyongdikou(yiyongdikou);
        }

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
                products.add(product);
            }
        }


        return products;
    }
}
