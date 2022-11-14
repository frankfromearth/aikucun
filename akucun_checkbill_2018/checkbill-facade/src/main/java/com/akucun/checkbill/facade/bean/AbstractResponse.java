package com.akucun.checkbill.facade.bean;

import java.io.Serializable;

/**
 * 通用返回消息
 * Created by zhaojin on 3/30/2018.
 */

public abstract class AbstractResponse implements Serializable
{
    /** 响应码 200 表示成功 */
    private String respCode;

    /** 响应码对应文本消息 */
    private String respMsg;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "respCode='" + respCode + '\'' +
                ", respMsg='" + respMsg + '\'' +
                '}';
    }
}
