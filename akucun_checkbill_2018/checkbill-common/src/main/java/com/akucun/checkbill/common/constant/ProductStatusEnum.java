package com.akucun.checkbill.common.constant;

/**
 * sc_order_detail shangpinzhuangtai 商品状态
 *  //  2已发货 ,0未捞 ,1已捞或处理中,3平台缺货未发货,4用户取消不发货,5用户取消需退货,6用户退货已收,9商品已补发 
 * Created by zhaojin on 4/3/2018.
 */
public enum ProductStatusEnum {
	//--send
	UNGET("0","未捞  "),
	GET_PROCESS("1","已捞或处理中  "),
    SEND("2","已发货 "),
    ADD_SEND("9","商品已补发  "),
    //---unsend
    LACK_UNSEND("3","平台缺货未发货"), 
    USER_CACEL_UNSEND("4","用户取消不发货"), 
    CACEL_REFUND("5","用户取消需退货"), 
    REFUNDED("6","用户退货已收"), 
    COMPLAIN_LACK("7","用户申诉漏发"), 
    VERIFY_REJUECT("8","平台审核打回"), 
    LACK_REFUND("10","漏发退款"), 
    SYS_CACEL_UNSEND("11","系统取消不发货"), 
    ;
    private String status;
    private String memo;

    private   ProductStatusEnum(String status, String memo) {
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
