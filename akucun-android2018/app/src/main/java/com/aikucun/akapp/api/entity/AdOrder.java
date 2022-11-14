package com.aikucun.akapp.api.entity;

import android.graphics.Point;

import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jarry on 2017/7/1.
 */

public class AdOrder implements Serializable
{
    private String adorderid;
    private String odorderstr;

    private String pinpai;
    private String pinpaiURL;
    private String downloadurl;

    private String liveid;

    private String wuliugongsi;
    private String wuliuhao;
    private String shouhuoren;
    private String lianxidianhua;
    private String shouhuodizhi;

    private String optime;
    private String createtime;
    private String expectdelivertime;
    private long optimestamp;
    private long ctimestamp;

    private List<CartProduct> products;

    private int statu;      // 0: 初始态 1: 待发货 2: 处理中 3: 已发货 4: 形成了对账单

    private int outaftersale;

    private int num;        // 商品数量
    private int pnum;       // 实发数量
    private int cancelnum;  // 取消数量
    private int lacknum;    // 缺货数量

    private int dingdanjine;
    private int shangpinjine;
    private int zongjine;
    private int yunfeijine;
    private int dikoujine;
    private int tuikuanjine;

    private long aftersaletimenum;//售后截止时间

    private String barcodeconfig;
    //是否为虚拟商品1：是
    private int isvirtual;

    public int getIsvirtual() {
        return isvirtual;
    }

    public void setIsvirtual(int isvirtual) {
        this.isvirtual = isvirtual;
    }

    public int daifahuoNum()
    {
        return num - lacknum;   // num中已经不包含取消的了 20180307
    }

    public String getAdorderid()
    {
        return adorderid;
    }

    public void setAdorderid(String adorderid)
    {
        this.adorderid = adorderid;
    }

    public String getOdorderstr()
    {
        return odorderstr;
    }

    public void setOdorderstr(String odorderstr)
    {
        this.odorderstr = odorderstr;
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

    public String getWuliugongsi()
    {
        return wuliugongsi;
    }

    public void setWuliugongsi(String wuliugongsi)
    {
        this.wuliugongsi = wuliugongsi;
    }

    public String getWuliuhao()
    {
        return wuliuhao;
    }

    public void setWuliuhao(String wuliuhao)
    {
        this.wuliuhao = wuliuhao;
    }

    public String getShouhuoren()
    {
        return shouhuoren;
    }

    public void setShouhuoren(String shouhuoren)
    {
        this.shouhuoren = shouhuoren;
    }

    public String getLianxidianhua()
    {
        return lianxidianhua;
    }

    public void setLianxidianhua(String lianxidianhua)
    {
        this.lianxidianhua = lianxidianhua;
    }

    public String getShouhuodizhi()
    {
        return shouhuodizhi;
    }

    public void setShouhuodizhi(String shouhuodizhi)
    {
        this.shouhuodizhi = shouhuodizhi;
    }

    public String getOptime()
    {
        return optime;
    }

    public void setOptime(String optime)
    {
        this.optime = optime;
    }

    public String getCreatetime()
    {
        return createtime;
    }

    public void setCreatetime(String createtime)
    {
        this.createtime = createtime;
    }

    public long getOptimestamp()
    {
        return optimestamp;
    }

    public void setOptimestamp(long optimestamp)
    {
        this.optimestamp = optimestamp;
    }

    public long getCtimestamp()
    {
        return ctimestamp;
    }

    public void setCtimestamp(long ctimestamp)
    {
        this.ctimestamp = ctimestamp;
    }

    public List<CartProduct> getProducts()
    {
        return products;
    }

    public void setProducts(List<CartProduct> products)
    {
        this.products = products;
    }

    public int getDingdanjine()
    {
        return dingdanjine;
    }

    public void setDingdanjine(int dingdanjine)
    {
        this.dingdanjine = dingdanjine;
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

    public int getYunfeijine()
    {
        return yunfeijine;
    }

    public void setYunfeijine(int yunfeijine)
    {
        this.yunfeijine = yunfeijine;
    }

    public int getDikoujine()
    {
        return dikoujine;
    }

    public void setDikoujine(int dikoujine)
    {
        this.dikoujine = dikoujine;
    }

    public int getTuikuanjine()
    {
        return tuikuanjine;
    }

    public void setTuikuanjine(int tuikuanjine)
    {
        this.tuikuanjine = tuikuanjine;
    }

    public int getNum()
    {
        return num;
    }

    public void setNum(int num)
    {
        this.num = num;
    }

    public int getPnum()
    {
        return pnum;
    }

    public void setPnum(int pnum)
    {
        this.pnum = pnum;
    }

    public int getCancelnum()
    {
        return cancelnum;
    }

    public void setCancelnum(int cancelnum)
    {
        this.cancelnum = cancelnum;
    }

    public int getLacknum()
    {
        return lacknum;
    }

    public void setLacknum(int lacknum)
    {
        this.lacknum = lacknum;
    }

    public int getStatu()
    {
        return statu;
    }

    public void setStatu(int statu)
    {
        this.statu = statu;
    }

    public String getLiveid()
    {
        return liveid;
    }

    public void setLiveid(String liveid)
    {
        this.liveid = liveid;
    }

    public int getOutaftersale()
    {
        return outaftersale;
    }

    public void setOutaftersale(int outaftersale)
    {
        this.outaftersale = outaftersale;
    }

    public String getBarcodeconfig()
    {
        return barcodeconfig;
    }

    public void setBarcodeconfig(String barcodeconfig)
    {
        this.barcodeconfig = barcodeconfig;
    }

    public long getAftersaletimenum()
    {
        return aftersaletimenum;
    }

    public void setAftersaletimenum(long aftersaletimenum)
    {
        this.aftersaletimenum = aftersaletimenum;
    }

    public String getExpectdelivertime()
    {
        return expectdelivertime;
    }

    public void setExpectdelivertime(String expectdelivertime)
    {
        this.expectdelivertime = expectdelivertime;
    }

    public String displayWuliuInfo()
    {
        String[] wuliuhaoArray = wuliuhao.split(",");
        if (wuliuhaoArray.length <= 1)
        {
            return wuliugongsi + "\n单号：" + wuliuhao;
        }
        StringBuilder result = new StringBuilder("" + wuliugongsi);

        int index = 0;
        for (String wuliu : wuliuhaoArray)
        {
            result.append("\n单号 " + ++index + " : " + wuliu);
        }
        return result.toString();
    }

    public String shortWuliuInfo()
    {
        String[] wuliuhaoArray = wuliuhao.split(",");
        return wuliugongsi + "： " + wuliuhaoArray[0];
    }

    public static AdOrder yy_modelWithJSON(String json)
    {
        try
        {
            AdOrder p = JSONObject.parseObject(json, AdOrder.class);
            return p;
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
        return null;
    }

    public CartProduct getCartProductWithID(String cartProductId)
    {
        for (CartProduct pro : getProducts())
        {
            if (pro.getCartproductid().equalsIgnoreCase(cartProductId))
            {
                return pro;
            }
        }
        return null;
    }

    public boolean hasBarcodeConfig()
    {
        if (StringUtils.isEmpty(barcodeconfig)) {
            return false;
        }
        String range[] = barcodeconfig.split(",");
        if (range.length == 2) {
            return true;
        }
        else if (range.length == 1) {
            return StringUtils.isNumeric(barcodeconfig);
        }
        return false;
    }

    public Point barcodeRange(int length)
    {
        String range[] = barcodeconfig.split(",");
        if (range.length == 1)
        {
            int n = Integer.valueOf(range[0]);
            if (length <= Math.abs(n)) {
                return new Point(0, length);
            }
            if (n < 0) {
                return new Point(0, length+n);
            }
            return new Point(n, length-n);
        }

        int range0 = Integer.valueOf(range[0]);
        int range1 = Integer.valueOf(range[1]);
        if (range1 < 0) {
            if (length <= range0-range1) {
                return new Point(0, length);
            }
            return new Point(range0, length-range0+range1);
        }
        if (length < range0+range1) {
            return new Point(0, length);
        }
        return new Point(range0, range1);
    }
}
