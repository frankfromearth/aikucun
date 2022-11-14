package com.akucun.checkbill.facade.bean;

/**
 * Created by zhaojin on 3/31/2018.
 */
public enum ErrorCode {

    SUCCESS("200","成功"),
    
    DATE_FORMAT_ERROR("101","日期格式错误"),
    USER_NOT_EXIST("102","用户不存在"),
    DENYED ("103","任务正在处理,拒绝"),
    PARAM_EMPTY("104","参数不能为空"),
    DELIVERY_NOT_EXIST("105","发货单号不存在"),
    ACTIVITY_NOT_EXIST("106","活动号不存在"),

    
    ERROR ("199","系统错误"),

    ;
    private String code;
    private String msg;
    private ErrorCode(String code,String msg)
    {
        this.code=code;
        this.msg=msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
