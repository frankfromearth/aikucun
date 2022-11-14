package com.aikucun.akapp.api.response;

import java.util.List;

/**
 * Created by jarry on 2017/6/10.
 */

public class OrderCreateResp
{
    private int total_amount;
    private int total_yunfeijine;
    private int total_shangpinjine;
    private int total_dikoujine;

    private List<String> orderids;

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

    public List<String> getOrderids()
    {
        return orderids;
    }

    public void setOrderids(List<String> orderids)
    {
        this.orderids = orderids;
    }
}
