package com.aikucun.akapp.storage;

import com.aikucun.akapp.api.entity.AdOrder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

/**
 * Created by micker on 2017/7/23.
 */

public class YiFaAdOrderDB {

    private String id;// md5(adorderid + productid)
    //AdOrderId
    private String adorderid;
    private long ctimestamp;

    //String
    private String adOrderString;

    //Product
    private String productid;              // Product ID
    private String cartproductid;
    private String barcode;
    private String name;
    private String remark;

    //是否已经配货
    private  int isPeiHuo = 0;  //0表示没有配货，非0表示配货


    public String getId() {
        return !StringUtils.isEmpty(id)?id:"";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAdorderid() {
        return !StringUtils.isEmpty(adorderid)?adorderid:"";
    }

    public void setAdorderid(String adorderid) {
        this.adorderid = adorderid;
    }

    public long getCtimestamp() {
        return ctimestamp;
    }

    public void setCtimestamp(long ctimestamp) {
        this.ctimestamp = ctimestamp;
    }

    public String getAdOrderString() {
        return !StringUtils.isEmpty(adOrderString)?adOrderString:"";
    }

    public void setAdOrderString(String adOrderString) {
        this.adOrderString = adOrderString;
    }

    public String getProductid() {
        return !StringUtils.isEmpty(productid)?productid:"";
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getCartproductid() {
        return !StringUtils.isEmpty(cartproductid)?cartproductid:"";
    }

    public void setCartproductid(String cartproductid) {
        this.cartproductid = cartproductid;
    }

    public String getBarcode() {
        return !StringUtils.isEmpty(barcode)?barcode:"";
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return !StringUtils.isEmpty(name)?name:"";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return !StringUtils.isEmpty(remark)?remark:"";
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getIsPeiHuo() {
        return isPeiHuo;
    }

    public void setIsPeiHuo(int isPeiHuo) {
        this.isPeiHuo = isPeiHuo;
    }

    public static ArrayList<YiFaAdOrderDB> initWithModel(AdOrder adOrder)
    {
        ArrayList<YiFaAdOrderDB> result = new ArrayList<>();

        String orderString = JSON.toJSONString(adOrder);

        for (CartProduct product : adOrder.getProducts()) {
            YiFaAdOrderDB p = new YiFaAdOrderDB();
            p.adOrderString = orderString;
            p.adorderid = adOrder.getAdorderid();
            p.ctimestamp = adOrder.getCtimestamp();
            p.productid = product.getId();
            p.cartproductid = product.getCartproductid();
            p.barcode = product.getSku().getBarcode();
            p.name = product.getName();
            p.remark = product.getRemark();
            p.isPeiHuo = 0;

            p.id = RSAUtils.md5String(p.adorderid+p.cartproductid);
            result.add(p);
        }
        return result;
    }

    public AdOrder AdOrder()
    {
        return AdOrder.yy_modelWithJSON(this.adOrderString);
    }

}
