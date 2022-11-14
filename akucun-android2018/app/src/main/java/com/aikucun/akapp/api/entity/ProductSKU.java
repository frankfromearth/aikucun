package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Product SKU
 * Created by jarry on 2017/6/7.
 */

public class ProductSKU implements Serializable
{
    private String id;
    private String productid;
    private String barcode;
    private String chima;
    private String yanse;
    private String cunfangdi;

    private int shuliang;

    private boolean selected;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getProductid()
    {
        return productid;
    }

    public void setProductid(String productid)
    {
        this.productid = productid;
    }

    public String getBarcode()
    {
        return barcode;
    }

    public void setBarcode(String barcode)
    {
        this.barcode = barcode;
    }

    public String getChima()
    {
        return chima;
    }

    public void setChima(String chima)
    {
        this.chima = chima;
    }

    public String getYanse()
    {
        return yanse;
    }

    public void setYanse(String yanse)
    {
        this.yanse = yanse;
    }

    public String getCunfangdi()
    {
        return cunfangdi;
    }

    public void setCunfangdi(String cunfangdi)
    {
        this.cunfangdi = cunfangdi;
    }

    public int getShuliang()
    {
        return shuliang;
    }

    public void setShuliang(int shuliang)
    {
        this.shuliang = shuliang;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
