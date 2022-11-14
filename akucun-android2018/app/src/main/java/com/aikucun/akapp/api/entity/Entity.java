package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Entity 数据对象
 * Created by jarry on 16/6/3.
 */
public abstract class Entity implements Serializable
{
    protected String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

}
