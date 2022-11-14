package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by micker on 2017/7/9.
 */

public class AfterSaleItem implements Serializable {

    private String cartproductid;
    private String desc;
    private String tupianURL;
    private String danwei;
    private int jiesuanjia;
    private ProductSKU sku;

    private String servicehao;
    private String  shenqingshijian;
    private String wentimiaoshu;
    private String servicedesc;
    private String pingzheng;

    private int servicetype;
    private int yuanyin;
    private int shouhouleixing;
    private int chulizhuangtai;

    private String refundcorp;
    private String refundhao;
    private String reissuecorp;
    private String reissuehao;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    private String orderid;

    public String getImageUrl()
    {
        String imageUrl = "";
        String[] arrayStr = this.getTupianURL().split(",");
        if (arrayStr.length > 0) {
            imageUrl = arrayStr[0];
        }
        return imageUrl;
    }

    public String jiesuanPrice()
    {
        return String.format("结算价：¥ %.2f", jiesuanjia/100.0f);
    }

    public String serviceTypeText()
    {
        switch (servicetype) {
            case 1:
                return "漏发补发";
            case 2:
                return "漏发退款";
            case 3:
                return "退货并补发";
            case 4:
                return "退货退款";

            default:
                break;
        }
        return "";
    }

    public static enum eASaleStatus {

        ASaleStatusSubmit("申请已提交", 1),
        ASaleStatusRejected("审核未通过", 2),
        ASaleStatusPending("售后处理中", 3),
        ASaleStatusLoufaBufa("漏发已补发", 4),
        ASaleStatusLoufaTuikuan("漏发已退款", 5),
        ASaleStatusTuihuoBufa("退货已补发", 6),
        ASaleStatusTuihuoTuikuan("退货已退款", 7),
        ASaleStatusTuihuoPending("退货处理中", 8);

        private String name;
        private int index;

        private eASaleStatus(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 覆盖方法
        @Override
        public String toString() {
            return this.index + "_" + this.name;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }
    }


    public String statusText()
    {
        for (eASaleStatus c : eASaleStatus.values()) {
            if (c.getIndex() == chulizhuangtai) {
                return c.getName();
            }
        }
        return "";
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTupianURL() {
        return tupianURL;
    }

    public void setTupianURL(String tupianURL) {
        this.tupianURL = tupianURL;
    }

    public String getDanwei() {
        return danwei;
    }

    public void setDanwei(String danwei) {
        this.danwei = danwei;
    }

    public int getJiesuanjia() {
        return jiesuanjia;
    }

    public void setJiesuanjia(int jiesuanjia) {
        this.jiesuanjia = jiesuanjia;
    }

    public ProductSKU getSku() {
        return sku;
    }

    public void setSku(ProductSKU sku) {
        this.sku = sku;
    }

    public String getServicehao() {
        return servicehao;
    }

    public void setServicehao(String servicehao) {
        this.servicehao = servicehao;
    }

    public String getShenqingshijian() {
        return shenqingshijian;
    }

    public void setShenqingshijian(String shenqingshijian) {
        this.shenqingshijian = shenqingshijian;
    }

    public String getWentimiaoshu() {
        return wentimiaoshu;
    }

    public void setWentimiaoshu(String wentimiaoshu) {
        this.wentimiaoshu = wentimiaoshu;
    }

    public String getServicedesc() {
        return servicedesc;
    }

    public void setServicedesc(String servicedesc) {
        this.servicedesc = servicedesc;
    }

    public String getPingzheng() {
        return pingzheng;
    }

    public void setPingzheng(String pingzheng) {
        this.pingzheng = pingzheng;
    }

    public int getServicetype() {
        return servicetype;
    }

    public void setServicetype(int servicetype) {
        this.servicetype = servicetype;
    }

    public int getYuanyin() {
        return yuanyin;
    }

    public void setYuanyin(int yuanyin) {
        this.yuanyin = yuanyin;
    }

    public int getShouhouleixing() {
        return shouhouleixing;
    }

    public void setShouhouleixing(int shouhouleixing) {
        this.shouhouleixing = shouhouleixing;
    }

    public int getChulizhuangtai() {
        return chulizhuangtai;
    }

    public void setChulizhuangtai(int chulizhuangtai) {
        this.chulizhuangtai = chulizhuangtai;
    }

    public String getReissuecorp() {
        return reissuecorp;
    }

    public void setReissuecorp(String reissuecorp) {
        this.reissuecorp = reissuecorp;
    }

    public String getReissuehao() {
        return reissuehao;
    }

    public void setReissuehao(String reissuehao) {
        this.reissuehao = reissuehao;
    }

    public String getCartproductid() {
        return cartproductid;
    }

    public void setCartproductid(String cartproductid) {
        this.cartproductid = cartproductid;
    }

    public String getRefundcorp() {
        return refundcorp;
    }

    public void setRefundcorp(String refundcorp) {
        this.refundcorp = refundcorp;
    }

    public String getRefundhao() {
        return refundhao;
    }

    public void setRefundhao(String refundhao) {
        this.refundhao = refundhao;
    }
}
