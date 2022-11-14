package com.aikucun.akapp.storage;

import com.aikucun.akapp.api.entity.Product;
import com.alibaba.fastjson.JSON;


/**
 * Created by jinlibin on 2017/6/9.
 */

public class ProductDB
{

    public String productId;
    public String pinpai;
    public String desc;
    public String json;
    public int xuhao;
    public int lastxuhao;
    public long time;

    public String liveId;
    public String corpId;

    public int quehuo;

    public int forward;
    public int follow;

    public static ProductDB initWithModel(Product product)
    {
        ProductDB p = new ProductDB();
        p.productId = product.getId();
        p.pinpai = product.getPinpaiid();
        p.desc = product.getDesc();
        p.xuhao = product.getXuhao();
        p.lastxuhao = product.getLastxuhao();
        p.time = product.getShangjiashuzishijian();

        p.quehuo = product.isQuehuo()?1:0;
        p.liveId = product.getLiveid();
        p.corpId = product.getCorpid();
        p.json = JSON.toJSONString(product);


        p.forward = product.getForward();
        p.follow = product.getFollow();
        return p;
    }

    public Product productModel()
    {
        return Product.yy_modelWithJSON(this.json);
    }




}
