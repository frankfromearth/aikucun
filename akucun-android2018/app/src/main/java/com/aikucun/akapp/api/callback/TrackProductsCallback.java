package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.response.TrackProductsResp;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.SCLog;
import com.alibaba.fastjson.JSON;

import okhttp3.Call;


/**
 * Created by jarry on 2017/6/10.
 */

public class TrackProductsCallback extends ApiBaseCallback<TrackProductsResp>
{
    @Override
    public void onApiSuccess(TrackProductsResp trackProductsResp, Call call, ApiResponse
            jsonResponse)
    {
        SCLog.log("Update Time : " + trackProductsResp.getLastupdate() + "\nSKU update : " +
                trackProductsResp.getSkulastupdate() + "\nProduct Count : " + trackProductsResp
                .getProducts().size());
/*
        int skuCount = 0;
        if (trackProductsResp.getSkus() != null)
        {
            skuCount = trackProductsResp.getSkus().size();
        }
        SCLog.log("Update Time : " + trackProductsResp.getLastupdate() + "\nSKU update : " +
                trackProductsResp.getSkulastupdate() + "\nProduct Count : " + trackProductsResp
                .getProducts().size() + "\nSKU Count : " + skuCount);
*/
    }

    @Override
    public TrackProductsResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        TrackProductsResp response = JSON.parseObject(jsonString, TrackProductsResp.class);

        //
        ProductManager.getInstance().insertProducts(response.getProducts());

        if (response.getLastupdate() > 0)
        {
            ProductManager.getInstance().productUpdate = response.getLastupdate();
        }
        if (response.getSkulastupdate() > 0)
        {
            ProductManager.getInstance().skuUpdate = response.getSkulastupdate();
            AppContext.set(AppConfig.PREF_KEY_SKU_UPDATE, response.getSkulastupdate());
        }

        // skus
        if (response.getSkus() != null && response.getSkus().size() > 0)
        {
            for (ProductSKU sku : response.getSkus())
            {
                Product product = ProductManager.getInstance().findProductById(sku.getProductid());
                if (product != null)
                {
                    product.updateSKU(sku);
                    ProductManager.getInstance().updateProduct(product);
                }
            }
        }

        return response;
    }
}
