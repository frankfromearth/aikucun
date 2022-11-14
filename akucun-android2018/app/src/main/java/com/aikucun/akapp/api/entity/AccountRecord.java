package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by jarry on 2017/6/21.
 */

public class AccountRecord implements Serializable
{
    public static final int AccountRecordIN = 0;        // 0: 入帐
    public static final int AccountRecordOUT = 1;       // 1: 出帐

    public static final int ARecordINLACK = 2;          // 缺货退款
    public static final int ARecordINRETURN = 3;        // 商品返仓退款
    public static final int ARecordINDEFECTIVE = 4;     // 次品协商退款
    public static final int ARecordINFREIGHT = 5;       // 运费返还
    public static final int ARecordINCANCEL = 6;        // 商品未发货退款

    public static final int ARecordOUTORDER = 100;      // 订单支付
    public static final int ARecordOUTUSER = 101;       // 用户提现
    public static final int ARecordOUTSYSTEM = 102;     // 系统默认提现


    private String userid;
    private String miaoshu;
    private String time;

    private String title;

    private int type;       // 0: 入帐， 1: 出帐
    private int jine;       // 此记录的金额变化大小
    private int zongyue;    // 此变更后，帐户中的可用余额
    private int biangengyuanyin;

    public String yuanyinDescription()
    {
        String text = "";
        switch (biangengyuanyin)
        {
            case ARecordINLACK:
                text = "缺货退款";
                break;

            case ARecordINRETURN:
                text = "商品返仓退款";
                break;

            case ARecordINDEFECTIVE:
                text = "次品协商退款";
                break;

            case ARecordINFREIGHT:
                text = "运费返还";
                break;

            case ARecordINCANCEL:
                text = "商品未发货退款";
                break;

            case ARecordOUTORDER:
                text = "订单支付";
                break;

            case ARecordOUTUSER:
                text = "用户提现";
                break;

            case ARecordOUTSYSTEM:
                text = "系统默认提现";
                break;
        }
        return text;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getMiaoshu()
    {
        return miaoshu;
    }

    public void setMiaoshu(String miaoshu)
    {
        this.miaoshu = miaoshu;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public int getJine()
    {
        return jine;
    }

    public void setJine(int jine)
    {
        this.jine = jine;
    }

    public int getZongyue()
    {
        return zongyue;
    }

    public void setZongyue(int zongyue)
    {
        this.zongyue = zongyue;
    }

    public int getBiangengyuanyin()
    {
        return biangengyuanyin;
    }

    public void setBiangengyuanyin(int biangengyuanyin)
    {
        this.biangengyuanyin = biangengyuanyin;
    }
}
