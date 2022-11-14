package com.aikucun.akapp.api.response;

/**
 * Created by jarry on 2017/6/10.
 */

public class OrderPayResp
{
    private String payment_id;
    private int total_amount;
    private int total_yunfeijine;
    private int total_shangpinjine;
    private int total_dikoujine;

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

    public int getTotal_yunfeijine()
    {
        return total_yunfeijine;
    }

    public void setTotal_yunfeijine(int total_yunfeijine)
    {
        this.total_yunfeijine = total_yunfeijine;
    }

    public int getTotal_shangpinjine()
    {
        return total_shangpinjine;
    }

    public void setTotal_shangpinjine(int total_shangpinjine)
    {
        this.total_shangpinjine = total_shangpinjine;
    }

    public int getTotal_dikoujine()
    {
        return total_dikoujine;
    }

    public void setTotal_dikoujine(int total_dikoujine)
    {
        this.total_dikoujine = total_dikoujine;
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

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }
}
