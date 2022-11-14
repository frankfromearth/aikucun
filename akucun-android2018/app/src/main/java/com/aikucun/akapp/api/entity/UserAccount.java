package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * 用户账户信息
 * Created by jarry on 2017/6/21.
 */
public class UserAccount implements Serializable
{
    private String userid;

    private int keyongyue;  // 可用余额，单位（分）
    private int suodingyue; // 锁定余额，单位（分）
    private int yue;        // 总余额
    private int statu;      // 帐户状态，0正常，1异常，2是帐户锁定，只有在正常的情况下才可以支付使用余额

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public int getKeyongyue()
    {
        return keyongyue;
    }

    public void setKeyongyue(int keyongyue)
    {
        this.keyongyue = keyongyue;
    }

    public int getSuodingyue()
    {
        return suodingyue;
    }

    public void setSuodingyue(int suodingyue)
    {
        this.suodingyue = suodingyue;
    }

    public int getYue()
    {
        return yue;
    }

    public void setYue(int yue)
    {
        this.yue = yue;
    }

    public int getStatu()
    {
        return statu;
    }

    public void setStatu(int statu)
    {
        this.statu = statu;
    }
}
