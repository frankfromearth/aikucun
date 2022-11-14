package com.aikucun.akapp.api.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 用户地址信息
 * Created by jarry on 2017/6/7.
 */

public class Address implements Serializable
{
    private String addrid;
    private String shoujianren;
    private String dianhua;
    private String sheng;
    private String shi;
    private String qu;
    private String detailaddr;


    // 1： 默认地址  其它： 非默认地址
    private   int  defaultflag;

    public String displayMobile()
    {
        if (dianhua == null)
        {
            return "";
        }
        if (dianhua.length() < 7)
        {
            return dianhua;
        }
        int len = dianhua.length() - 7;
        StringBuffer text = new StringBuffer();
        for (int i = 0; i < len; i++)
        {
            text.append("*");
        }
        return dianhua.substring(0, 3) + text + dianhua.substring(dianhua.length() - 4);
    }

    public String displayAddress()
    {
        if (TextUtils.equals(sheng, shi))
        {
            return sheng + qu + " " + detailaddr;
        }
        return sheng + shi + qu + " " + detailaddr;
    }

    public String getShoujianren()
    {
        return shoujianren;
    }

    public void setShoujianren(String shoujianren)
    {
        this.shoujianren = shoujianren;
    }

    public String getDianhua()
    {
        return dianhua;
    }

    public void setDianhua(String dianhua)
    {
        this.dianhua = dianhua;
    }

    public String getSheng()
    {
        return sheng;
    }

    public void setSheng(String sheng)
    {
        this.sheng = sheng;
    }

    public String getShi()
    {
        return shi;
    }

    public void setShi(String shi)
    {
        this.shi = shi;
    }

    public String getQu()
    {
        return qu;
    }

    public void setQu(String qu)
    {
        this.qu = qu;
    }

    public String getDetailaddr()
    {
        return detailaddr;
    }

    public String getFullAddr() {
        return sheng + " " + shi + " " + qu + " " + detailaddr;
    }

    public void setDetailaddr(String detailaddr)
    {
        this.detailaddr = detailaddr;
    }

    public int getDefaultflag() {
        return defaultflag;
    }

    public void setDefaultflag(int defaultflag) {
        this.defaultflag = defaultflag;
    }

    public String getAddrid() {
        return addrid;
    }

    public void setAddrid(String addrid) {
        this.addrid = addrid;
    }
}
