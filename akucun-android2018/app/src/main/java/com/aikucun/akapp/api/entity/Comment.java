package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Product Comment
 * Created by jarry on 2017/6/7.
 */

public class Comment implements Serializable
{

    private String id;

    private String content;
    private String name;
    private String productid;
    private String pinglunzheID;
    private long modifytime;
    private int status;
    private int xuhao;


    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getProductid()
    {
        return productid;
    }

    public void setProductid(String productid)
    {
        this.productid = productid;
    }

    public String getPinglunzheID()
    {
        return pinglunzheID;
    }

    public void setPinglunzheID(String pinglunzheID)
    {
        this.pinglunzheID = pinglunzheID;
    }

    public long getModifytime()
    {
        return modifytime;
    }

    public void setModifytime(long modifytime)
    {
        this.modifytime = modifytime;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public int getXuhao()
    {
        return xuhao;
    }

    public void setXuhao(int xuhao)
    {
        this.xuhao = xuhao;
    }
}
