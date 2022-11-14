package com.akucun.checkbill.dao.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 会员对账单申请
 * Created by zhaojin on 3/30/2018.
 */
public class CheckBillApplyEntity implements Serializable
{
    /**  id 主键*/
    private String id;

    /**  用户ID*/
    private String userId;

    /**  用户编号*/
    private String userNo;

    /**  用户名称*/
    private String userName;

    /**  文件名*/
    private String fileName;

    /**  文件URL*/
    private String fileUrl;
    /**  状态　
     *　S=Success,P=process,F=fail
     * */
    private String status;

    /**  总支付成功金额  */
    private BigDecimal payTotalAmount;

    /**  总支付取消金额  */
    private BigDecimal cancelTotalAmount;

    /**  总支付成功笔数  */
    private int  payTotalCount;
    /**  总支付取消笔数  */
    private int  cancelTotalCount;
    /** 申请时间 */
    private Date createTime;
    /** 文件生成完成时间 */
    private Date finishTime;
    
    /** UM=UserMonth,UD=UserDelivery,UA=UserDcitivty */
    private String  typeCode;
    
    private String  typeValue;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public int getCancelTotalCount() {
        return cancelTotalCount;
    }

    public void setCancelTotalCount(int cancelTotalCount) {
        this.cancelTotalCount = cancelTotalCount;
    }

    public int getPayTotalCount() {
        return payTotalCount;
    }

    public void setPayTotalCount(int payTotalCount) {
        this.payTotalCount = payTotalCount;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPayTotalAmount() {
        return payTotalAmount;
    }

    public void setPayTotalAmount(BigDecimal payTotalAmount) {
        this.payTotalAmount = payTotalAmount;
    }

    public BigDecimal getCancelTotalAmount() {
        return cancelTotalAmount;
    }

    public void setCancelTotalAmount(BigDecimal cancelTotalAmount) {
        this.cancelTotalAmount = cancelTotalAmount;
    }
 

    public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	@Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userId", userId)
                .append("userNo", userNo)
                .append("userName", userName)
                .append("fileName", fileName)
                .append("fileUrl", fileUrl)
                .append("status", status)
                .append("payTotalAmount", payTotalAmount)
                .append("cancelTotalAmount", cancelTotalAmount)
                .append("payTotalCount", payTotalCount)
                .append("cancelTotalCount", cancelTotalCount)
                .append("createTime", createTime)
                .append("finishTime", finishTime)
                .append("typeCode", typeCode)
                .append("typeValue", typeValue)
                .toString();
    }
}
