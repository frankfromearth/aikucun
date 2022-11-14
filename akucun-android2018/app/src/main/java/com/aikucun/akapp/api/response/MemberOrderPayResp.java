package com.aikucun.akapp.api.response;

/**
 * Created by jarry on 2017/6/10.
 */

public class MemberOrderPayResp
{
    private int total_amount;
    private String orderId;

    private int paytype;

    private Object payinfo;

    public int getTotal_amount()
    {
        return total_amount;
    }

    public void setTotal_amount(int total_amount)
    {
        this.total_amount = total_amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaytype()
    {
        return paytype;
    }

    public void setPaytype(int paytype)
    {
        this.paytype = paytype;
    }

    public Object getPayinfo()
    {
        return payinfo;
    }

    public void setPayinfo(Object payinfo)
    {
        this.payinfo = payinfo;
    }
}
