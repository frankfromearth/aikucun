package com.akucun.checkbill.common.constant;

/**
 *  sc_checkbill_applys  typeCode字段,
 *  决定typeValue的哪种类型的值 
 * Created by zhaojin on 4/3/2018.
 */
public enum CheckBillApplyTypeCodeEnum {

    USER_MONTH("UM","用户按月"),
    USER_DELIVERY("UD","用户按发货单"),
    USER_ACTIVITY("UA","用户按活动"),
    
    ;
    private String code;
    private String memo;

    private   CheckBillApplyTypeCodeEnum(String code, String memo) {
        this.code = code;
        this.memo = memo;
    }
 

    public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
