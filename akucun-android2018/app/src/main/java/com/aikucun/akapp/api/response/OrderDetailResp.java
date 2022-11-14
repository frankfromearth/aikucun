package com.aikucun.akapp.api.response;

import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.Logistics;
import com.aikucun.akapp.api.entity.OrderModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jarry on 2017/6/12.
 */
public class OrderDetailResp implements Serializable
{
    private List<CartProduct> products;

    private OrderModel order;

    private Logistics logistics;


    public List<CartProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<CartProduct> products)
    {
        this.products = products;
    }

    public OrderModel getOrder()
    {
        return order;
    }

    public void setOrder(OrderModel order)
    {
        this.order = order;
    }

    public Logistics getLogistics()
    {
        return logistics;
    }

    public void setLogistics(Logistics logistics)
    {
        this.logistics = logistics;
    }
}
