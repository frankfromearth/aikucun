package com.akucun.checkbill.dao.detail;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 会员对账单 按月 导出excel格式
 * Created by zhaojin on 3/30/2018.
 */
public class CheckBillMonthDetail implements Serializable
{
    //---order 级
    /** 订单ID	 	*/
    private String orderId;

    /** 订单生成时间*/
    private Date createTime;

    /**  支付时间*/
    private Date payTime;
    /** 商品金额 */
    private BigDecimal productPrice;

    /**  运费金额*/
    private BigDecimal carriageFee;

    /**  支付金额 */
    private BigDecimal payPrice;


    /**  商品件数 */
    private int pieceNum;

    /**  支付状态  */
    private String payStatus;

    /**  支付方式  */
    private String payType;

    /**  品牌 */
    private String brand;
    /**  订单状态  */
    private String  orderStatus;
      /**  配送订单号  */
    private  String deliveryOrderNo;

    //---order detail级

    /**  商品描述  */
    private  String productDescription;
    /**  尺码  */
    private  String size;
    /**  结算价	  */
    private BigDecimal settlePrice;
    /**  商品状态	  */
    private  String productStatus;
    /**  取消购买	  */
    private  String cancleBuy;
    /**  备注	  */
    private  String memo;

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public BigDecimal getCarriageFee() {
        return carriageFee;
    }

    public void setCarriageFee(BigDecimal carriageFee) {
        this.carriageFee = carriageFee;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPieceNum(int pieceNum) {
        this.pieceNum = pieceNum;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDeliveryOrderNo() {
        return deliveryOrderNo;
    }

    public void setDeliveryOrderNo(String deliveryOrderNo) {
        this.deliveryOrderNo = deliveryOrderNo;
    }


    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(BigDecimal settlePrice) {
        this.settlePrice = settlePrice;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getCancleBuy() {
        return cancleBuy;
    }

    public void setCancleBuy(String cancleBuy) {
        this.cancleBuy = cancleBuy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("orderId", orderId)
                .append("createTime", createTime)
                .append("payTime", payTime)
                .append("productPrice", productPrice)
                .append("carriageFee", carriageFee)
                .append("payPrice", payPrice)
                .append("pieceNum", pieceNum)
                .append("payStatus", payStatus)
                .append("payType", payType)
                .append("brand", brand)
                .append("orderStatus", orderStatus)
                .append("deliveryOrderNo", deliveryOrderNo)
                .append("memo", memo)
                .append("productDescription", productDescription)
                .append("size", size)
                .append("settlePrice", settlePrice)
                .append("productStatus", productStatus)
                .append("cancleBuy", cancleBuy)
                .toString();
    }
}
