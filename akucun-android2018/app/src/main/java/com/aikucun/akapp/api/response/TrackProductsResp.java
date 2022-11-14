package com.aikucun.akapp.api.response;

import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarry on 2017/6/10.
 */

public class TrackProductsResp
{
    private long lastupdate;
    private long skulastupdate;

    private List<Product> products;
    private List<ProductSKU> skus;

    public long getLastupdate()
    {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate)
    {
        this.lastupdate = lastupdate;
    }

    public long getSkulastupdate()
    {
        return skulastupdate;
    }

    public void setSkulastupdate(long skulastupdate)
    {
        this.skulastupdate = skulastupdate;
    }

    public List<Product> getProducts()
    {
        return products;
    }

    public List<Product> getLiveProducts(String liveId)
    {
        if (StringUtils.isEmpty(liveId))
            return products;

        List<Product> liveProducts = new ArrayList<>();
        for (Product pro:products) {
            if (pro.getLiveid().equalsIgnoreCase(liveId)) {
                liveProducts.add(pro);
            }
        }
        return liveProducts;
    }



    public void setProducts(List<Product> products)
    {
        this.products = products;
    }

    public List<ProductSKU> getSkus()
    {
        return skus;
    }

    public void setSkus(List<ProductSKU> skus)
    {
        this.skus = skus;
    }
}
