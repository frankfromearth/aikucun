package com.aikucun.akapp.api.entity;

import com.aikucun.akapp.utils.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 购物车商品
 * Created by jarry on 2017/6/7.
 */
public class CartProduct implements Serializable {
    public final static int ProductStatusInit = 0;         // 初始态、未支付
    public final static int ProductStatusWeifahuo = 1;     // 已支付、未发货
    public final static int ProductStatusYifahuo = 2;      // 已发货
    public final static int ProductStatusFahuo = 3;        // 发货 处理中

    public final static int ProductStatusCancel = 4;       // 取消（未支付取消）
    public final static int ProductStatusQuehuo = 5;       // 平台缺货 退款中
    public final static int ProductStatusTuihuo = 6;       // 退货、已退款
    public final static int ProductStatusPending = 7;      // 退货退款 处理中
    public final static int ProductStatusTuikuan = 8;      // 用户取消 退款中

    public final static int ProductStatusTuikuanDone = 9;  // 用户取消 已退款
    public final static int ProductStatusQuehuoDone = 10;  // 平台缺货 已退款
//    ProductStatusResend = 11 ,      // 漏发已补发
//    ProudctStatusRefund = 12 ,      // 漏发已退款

    public final static int ProductASaleSubmit = 13;       // 售后 已提交 (审核中)
    public final static int ProductASaleRejected = 14;     // 售后 审核不通过
    public final static int ProductASalePending = 15;     // 售后 审核通过（售后处理中)
    public final static int ProductASaleLoufaBufa = 16;    // 售后 漏发已补发
    public final static int ProductASaleLoufaTuikuan = 17; // 售后 漏发已退款
    public final static int ProductASaleTuihuoBufa = 18;   // 售后 退货已补发
    public final static int ProductASaleTuihuoTuikuan = 19; // 售后 退货已退款
    public final static int ProductASaleTuihuoPending = 20;  // 售后 退货处理中

    // 订单商品状态
//    public final static int ProductStatusInit = 0;      // 初始态、未支付
//    public final static int ProductStatusWeifahuo = 1;  // 已支付、未发货
//    public final static int ProductStatusYifahuo = 2;   // 已发货
//    public final static int ProductStatusFahuo = 3;     // 发货 处理中
//    public final static int ProductStatusCancel = 4;    // 取消
//    public final static int ProductStatusQuehuo = 5;    // 缺货、已退款
//    public final static int ProductStatusTuihuo = 6;    // 退货、已退款
//    public final static int ProductStatusPending = 7;   // 退货退款 处理中
//    public final static int ProductStatusTuikuan = 8;   // 取消、已退款（未发货 退款)
//    public final static int ProductStatusAppeal = 9;     // 漏发已申诉
//    public final static int ProductStatusAppealBack = 10;   // 审核未漏发
//    public final static int ProductStatusResend = 11;   // 漏发已补发
//    public final static int ProudctStatusRefund = 12;   // 漏发已退款
//
//
//    public final static int ProductASaleSubmit = 13 ;      // 售后 已提交 (审核中)
//    public final static int ProductASaleRejected = 14 ;     // 售后 审核不通过
//    public final static int ProductASalePending = 15 ;      // 售后 审核通过（售后处理中)
//    public final static int ProductASaleLoufaBufa = 16 ;    // 售后 漏发已补发
//    public final static int ProductASaleLoufaTuikuan = 17 ; // 售后 漏发已退款
//    public final static int ProductASaleTuihuoBufa = 18 ;   // 售后 退货已补发
//    public final static int ProductASaleTuihuoTuikuan = 19 ; // 售后 退货已退款
//    public final static int ProductASaleTuihuoPending = 20 ; // 售后 退货处理中


    private String id;              // Product ID
    private String productid;
    private String cartproductid;
    private String barcode;
    private String name;
    private String pinpai;
    private String pinpaiurl;
    private String remark;
    private String tupianURL;
    private String desc;
    private String danwei;
    private String cunfangdi;
    private String chima;
    private String yanse;
    private String kuanhao;

    private long buytimestamp;
    private int shangpinzhuangtai;
    private int shuliang;
    private int diaopaijia;
    private int xiaoshoujia;
    private int jiesuanjia;

    private long pinpaiHash;
    private boolean isSelected = true;
    private boolean isAllSelected = true;

    private ProductSKU sku;

    public boolean outaftersale;    // 超出售后时间

    private String skuid;
    private int buystatus;  //用于购物车回收列表， 2: 回收  3:已复购

    public int scanstatu;   // 扫码分拣 标记
    //虚拟商品信息
    public String extrainfo;
    //是否为虚拟商品1：是
    private int isvirtual;
    private String orderid;

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public int getIsvirtual() {
        return isvirtual;
    }

    public void setIsvirtual(int isvirtual) {
        this.isvirtual = isvirtual;
    }

    public String getExtrainfo() {
        return extrainfo;
    }

    public void setExtrainfo(String extrainfo) {
        this.extrainfo = extrainfo;
    }

    public String getImageUrl() {
        String imageUrl = "";
        String[] arrayStr = this.getTupianURL().split(",");
        if (arrayStr.length > 0) {
            imageUrl = arrayStr[0];
        }
        return imageUrl;
    }

    public List<String> akucunImageUrls() {
        String[] arrayStr = tupianURL.replaceAll(" ", "").split(",");
        List<String> imagesList = java.util.Arrays.asList(arrayStr);
        return imagesList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCartproductid() {
        return cartproductid;
    }

    public void setCartproductid(String cartproductid) {
        this.cartproductid = cartproductid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinpai() {
        return pinpai;
    }

    public void setPinpai(String pinpai) {
        this.pinpai = pinpai;
        setPinpaiHash((long) pinpai.hashCode());
    }

    public String getPinpaiurl() {
        return pinpaiurl;
    }

    public void setPinpaiurl(String pinpaiurl) {
        this.pinpaiurl = pinpaiurl;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {

        this.remark = remark;
    }

    public String getTupianURL() {
        return tupianURL;
    }

    public void setTupianURL(String tupianURL) {
        this.tupianURL = tupianURL;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDanwei() {
        return danwei;
    }

    public void setDanwei(String danwei) {
        this.danwei = danwei;
    }

    public String getCunfangdi() {
        return cunfangdi;
    }

    public void setCunfangdi(String cunfangdi) {
        this.cunfangdi = cunfangdi;
    }

    public String getChima() {
        return chima;
    }

    public void setChima(String chima) {
        this.chima = chima;
    }

    public String getYanse() {
        return yanse;
    }

    public void setYanse(String yanse) {
        this.yanse = yanse;
    }

    public String getKuanhao() {
        return kuanhao;
    }

    public void setKuanhao(String kuanhao) {
        this.kuanhao = kuanhao;
    }

    public long getBuytimestamp() {
        return buytimestamp;
    }

    public void setBuytimestamp(long buytimestamp) {
        this.buytimestamp = buytimestamp;
    }

    public int getShangpinzhuangtai() {
        return shangpinzhuangtai;
    }

    public void setShangpinzhuangtai(int shangpinzhuangtai) {
        this.shangpinzhuangtai = shangpinzhuangtai;
    }

    public int getShuliang() {
        return shuliang;
    }

    public void setShuliang(int shuliang) {
        this.shuliang = shuliang;
    }

    public int getDiaopaijia() {
        return diaopaijia;
    }

    public void setDiaopaijia(int diaopaijia) {
        this.diaopaijia = diaopaijia;
    }

    public int getXiaoshoujia() {
        return xiaoshoujia;
    }

    public void setXiaoshoujia(int xiaoshoujia) {
        this.xiaoshoujia = xiaoshoujia;
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

    public long getPinpaiHash() {
        return pinpaiHash;
    }

    public void setPinpaiHash(long pinpaiHash) {
        this.pinpaiHash = Math.abs(pinpaiHash);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAllSelected() {
        return isAllSelected;
    }

    public void setAllSelected(boolean allSelected) {
        isAllSelected = allSelected;
    }

    public boolean isQueHuo() {
        return shuliang <= 0;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public int getBuystatus() {
        return buystatus;
    }

    public void setBuystatus(int buystatus) {
        this.buystatus = buystatus;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public boolean isConain(String keyword) {

        keyword = keyword.toLowerCase();
        if (!StringUtils.isEmpty(name)) {
            if (name.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(desc)) {
            if (desc.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(barcode)) {
            if (barcode.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        if (null != sku && !StringUtils.isEmpty(sku.getBarcode())) {
            if (sku.getBarcode().toLowerCase().contains(keyword))
                return true;
        }

        if (!StringUtils.isEmpty(remark)) {
            if (remark.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(kuanhao)) {
            if (kuanhao.toLowerCase().contains(keyword)) {
                return true;
            }
        }

        return false;

    }
}
