package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.SynProductsResp;
import com.aikucun.akapp.storage.ProductManager;
import com.alibaba.fastjson.JSON;

import okhttp3.Call;

/**
 * 同步商品返回数据处理
 * Created by jarry on 2017/6/7.
 */
public class SyncProductsCallback extends ApiBaseCallback<SynProductsResp> {
    @Override
    public void onApiSuccess(SynProductsResp synProductsResp, Call call, ApiResponse jsonResponse) {
    }

    @Override
    public SynProductsResp parseResponse(ApiResponse responseData) throws Exception {
        String jsonString = responseData.getJsonObject().toJSONString();
        SynProductsResp response = JSON.parseObject(jsonString, SynProductsResp.class);

        // Save
//        if (response.lastupdate > ProductManager.getInstance().productUpdate) {
//            ProductManager.getInstance().productUpdate = response.lastupdate;
//        }
        ProductManager.getInstance().insertProducts(response.products);

        //没有更多的时候，获取所有的有效的品牌信息
//        if (!response.hasmore) {
//            LiveManager.getInstance().setLivePinpaiIds(ProductManager.getInstance().getPinpaiString());
//        }

        return response;

        //        String dataList = responseData.getJsonObject().getString("products");
        //        List<Product> products = JSON.parseArray(dataList, Product.class);
    }
}
