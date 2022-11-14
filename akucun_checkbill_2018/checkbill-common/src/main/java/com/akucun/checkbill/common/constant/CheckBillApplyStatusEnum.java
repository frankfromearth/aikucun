package com.akucun.checkbill.common.constant;

/**
 *  sc_checkbill_applys status字段
 * Created by zhaojin on 4/3/2018.
 */
public enum CheckBillApplyStatusEnum {

    FAIL("F","失败"),
    SUCESS("S","成功"),
    PROESS("P","片理中"),
    ;
    private String status;
    private String memo;

    private   CheckBillApplyStatusEnum(String status, String memo) {
        this.status = status;
        this.memo = memo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
