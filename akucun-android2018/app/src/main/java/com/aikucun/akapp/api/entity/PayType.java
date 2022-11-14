package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by jarry on 2017/6/22.
 */

public class PayType implements Serializable
{
    private int paytype;
    private int flag;
    private String name;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPaytype()
    {
        return paytype;
    }

    public void setPaytype(int paytype)
    {
        this.paytype = paytype;
    }

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
}
