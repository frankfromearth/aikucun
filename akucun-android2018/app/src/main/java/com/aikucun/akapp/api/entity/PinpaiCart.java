package com.aikucun.akapp.api.entity;

import java.util.List;

/**
 * 购物车品牌对象
 * Created by jarry on 2017/6/7.
 */

public class PinpaiCart
{
    private String pinpai;
    private int yunfeijine;
    private int dikoujine;
    private List<CartProduct> cartproducts;

    public String getPinpai()
    {
        return pinpai;
    }

    public void setPinpai(String pinpai)
    {
        this.pinpai = pinpai;
    }

    public int getYunfeijine()
    {
        return yunfeijine;
    }

    public void setYunfeijine(int yunfeijine)
    {
        this.yunfeijine = yunfeijine;
    }

    public int getDikoujine()
    {
        return dikoujine;
    }

    public void setDikoujine(int dikoujine)
    {
        this.dikoujine = dikoujine;
    }

    public List<CartProduct> getCartproducts()
    {
        return cartproducts;
    }

    public void setCartproducts(List<CartProduct> cartproducts)
    {
        this.cartproducts = cartproducts;
    }
}
