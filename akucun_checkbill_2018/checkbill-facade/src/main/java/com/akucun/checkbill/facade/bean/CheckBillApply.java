package com.akucun.checkbill.facade.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * 会员对账单申请
 * Created by zhaojin on 3/30/2018.
 */
public class CheckBillApply implements Serializable
{
  	private static final long serialVersionUID = 1L;

	/**  id 主键*/
    private long id;

    /**  用户ID*/
    private String userId;

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

    /** 
      *  UM=UserMonth用户按月,UD=UserDelivery用户按发货单,UA=UserAcitivty用户按活动
      */
    private String typeCode;

    /**  对应于typeCode的值 */
    private String typeValue;

    
    /**  总支付成功金额  */
    private BigDecimal payTotalAmount;

    /**  总支付取消金额  */
    private BigDecimal cancelTotalAmount;
    /**  总支付成功笔数  */
    private int  payTotalCount;
    /**  总支付取消笔数  */
    private int  payCancelTotalCount;
    /** 申请时间 */
    private Date createTime;
    /** 文件生成完成时间 */
    private Date finishTime;

    public int getPayTotalCount() {
        return payTotalCount;
    }

    public void setPayTotalCount(int payTotalCount) {
        this.payTotalCount = payTotalCount;
    }

    public int getPayCancelTotalCount() {
        return payCancelTotalCount;
    }

    public void setPayCancelTotalCount(int payCancelTotalCount) {
        this.payCancelTotalCount = payCancelTotalCount;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
		StringBuilder builder = new StringBuilder();
		builder.append("CheckBillApply [id=");
		builder.append(id);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", fileName=");
		builder.append(fileName);
		builder.append(", fileUrl=");
		builder.append(fileUrl);
		builder.append(", status=");
		builder.append(status);
		builder.append(", typeCode=");
		builder.append(typeCode);
		builder.append(", typeValue=");
		builder.append(typeValue);
		builder.append(", payTotalAmount=");
		builder.append(payTotalAmount);
		builder.append(", cancelTotalAmount=");
		builder.append(cancelTotalAmount);
		builder.append(", payTotalCount=");
		builder.append(payTotalCount);
		builder.append(", payCancelTotalCount=");
		builder.append(payCancelTotalCount);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", finishTime=");
		builder.append(finishTime);
		builder.append("]");
		return builder.toString();
	}

	 
}
