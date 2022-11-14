package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * 商品订单
 * Created by jarry on 2017/6/7.
 */

public class OrderModel implements Serializable
{
    private String orderid;
    private String dingdanshijian;
    private String pinpai;
    private String pinpaiURL;
    private String downloadurl;
    private String jiesuanfangshi;


    private int jiesuanfangshishuzi;
    private int shangpinjianshu;
    private int shangpinjine;
    private int zongjine;
    private int yunfei;
    private int dikoujine;
    private int status;

    private int outaftersale;       //

    private long dingdanshijianshuzi;
    private long overtimeshuzi;
    //是否为虚拟商品1：是
    private int isvirtual;

    public int getIsvirtual() {
        return isvirtual;
    }

    public void setIsvirtual(int isvirtual) {
        this.isvirtual = isvirtual;
    }

    public String displayOrderId()
    {
        if (orderid.length() <= 12)
        {
            return orderid;
        }
        return orderid.substring(12);
    }

    public String orderStatusText()
    {
        if (this.status == 0) {
            return "待支付";
        }
        else if (this.status == 1) {
            return "待发货";
        }
        else if (this.status == 2) {
            return "已发货";
        }
        else if (this.status == 3) {
            return "拣货中";
        }
        else if (this.status == 4) {
            return "未支付 用户取消";
        }
        else if (this.status == 5) {
            return "未支付 超时取消";
        }
        else if (this.status == 6) {
            return "已支付 用户取消";
        }
        return "未知状态";
    }

    public String getOrderid()
    {
        return orderid;
    }

    public void setOrderid(String orderid)
    {
        this.orderid = orderid;
    }

    public String getDingdanshijian()
    {
        return dingdanshijian;
    }

    public void setDingdanshijian(String dingdanshijian)
    {
        this.dingdanshijian = dingdanshijian;
    }

    public String getPinpai()
    {
        return pinpai;
    }

    public void setPinpai(String pinpai)
    {
        this.pinpai = pinpai;
    }

    public String getPinpaiURL()
    {
        return pinpaiURL;
    }

    public void setPinpaiURL(String pinpaiURL)
    {
        this.pinpaiURL = pinpaiURL;
    }

    public String getDownloadurl()
    {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl)
    {
        this.downloadurl = downloadurl;
    }

    public String getJiesuanfangshi()
    {
        return jiesuanfangshi;
    }

    public void setJiesuanfangshi(String jiesuanfangshi)
    {
        this.jiesuanfangshi = jiesuanfangshi;
    }

    public int getJiesuanfangshishuzi()
    {
        return jiesuanfangshishuzi;
    }

    public void setJiesuanfangshishuzi(int jiesuanfangshishuzi)
    {
        this.jiesuanfangshishuzi = jiesuanfangshishuzi;
    }

    public int getShangpinjianshu()
    {
        return shangpinjianshu;
    }

    public void setShangpinjianshu(int shangpinjianshu)
    {
        this.shangpinjianshu = shangpinjianshu;
    }

    public int getShangpinjine()
    {
        return shangpinjine;
    }

    public void setShangpinjine(int shangpinjine)
    {
        this.shangpinjine = shangpinjine;
    }

    public int getZongjine()
    {
        return zongjine;
    }

    public void setZongjine(int zongjine)
    {
        this.zongjine = zongjine;
    }

    public int getYunfei()
    {
        return yunfei;
    }

    public void setYunfei(int yunfei)
    {
        this.yunfei = yunfei;
    }

    public int getDikoujine()
    {
        return dikoujine;
    }

    public void setDikoujine(int dikoujine)
    {
        this.dikoujine = dikoujine;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public long getDingdanshijianshuzi()
    {
        return dingdanshijianshuzi;
    }

    public void setDingdanshijianshuzi(long dingdanshijianshuzi)
    {
        this.dingdanshijianshuzi = dingdanshijianshuzi;
    }

    public int getOutaftersale()
    {
        return outaftersale;
    }

    public void setOutaftersale(int outaftersale)
    {
        this.outaftersale = outaftersale;
    }

    public long getOvertimeshuzi() {
        return overtimeshuzi;
    }

    public void setOvertimeshuzi(long overtimeshuzi) {
        this.overtimeshuzi = overtimeshuzi;
    }
}
