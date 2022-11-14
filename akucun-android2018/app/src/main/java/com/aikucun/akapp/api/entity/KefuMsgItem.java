package com.aikucun.akapp.api.entity;

import com.aikucun.akapp.AppContext;

import java.io.Serializable;

/**
 * Created by jarry on 2017/6/29.
 */

public class KefuMsgItem implements Serializable
{
    private String msgid;
    private String userid;
    private String content;
    private String nicheng;

    private long createtime;
    private int statu;
    private int xuhao;       // 客服名称，在direction为1时有效
    private int direction;       // 0 是用户消息  1 是 客服回复的消息

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNicheng() {
        return nicheng;
    }

    public void setNicheng(String nicheng) {
        this.nicheng = nicheng;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public int getXuhao() {
        return xuhao;
    }

    public void setXuhao(int xuhao) {
        this.xuhao = xuhao;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isKefu() {
        return 1 == direction;
    }


    public static KefuMsgItem welcome() {
        KefuMsgItem item = new KefuMsgItem();

        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (userInfo!= null) {
            item.setContent(userInfo.getName() + " 您好！爱库存客服小爱为您服务!");
        } else {
            item.setContent("您好！爱库存客服小爱为您服务!");
        }
        item.setDirection(1);
        return item;
    }
}
