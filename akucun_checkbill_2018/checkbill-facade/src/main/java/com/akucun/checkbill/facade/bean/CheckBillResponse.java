package com.akucun.checkbill.facade.bean;


import java.util.List;

/**
 * 通用返回消息
 * Created by zhaojin on 3/30/2018.
 */

public   class CheckBillResponse extends AbstractResponse {
 
	private static final long serialVersionUID = 1L;
	
	/** 响应 数据 */
    private List<CheckBillApply> data;
    public List<CheckBillApply> getData() {
        return data;
    }

    public void setData(List<CheckBillApply> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CheckBillResponse{");
        sb.append("data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
