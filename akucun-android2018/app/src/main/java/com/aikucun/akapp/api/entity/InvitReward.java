package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by ak123 on 2018/1/17.
 * 邀请奖励明细成员
 */

public class InvitReward implements Serializable{

    private String id;
    private String nick;
    private String avator;
    private String daigou;
    private String daigoufzh;
    private String daigoufjl;
    private String jianjiedaigouf;
    private String jianjiedagoufjl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDaigou() {
        return daigou;
    }

    public void setDaigou(String daigou) {
        this.daigou = daigou;
    }

    public String getDaigoufzh() {
        return daigoufzh;
    }

    public void setDaigoufzh(String daigoufzh) {
        this.daigoufzh = daigoufzh;
    }

    public String getDaigoufjl() {
        return daigoufjl;
    }

    public void setDaigoufjl(String daigoufjl) {
        this.daigoufjl = daigoufjl;
    }

    public String getJianjiedaigouf() {
        return jianjiedaigouf;
    }

    public void setJianjiedaigouf(String jianjiedaigouf) {
        this.jianjiedaigouf = jianjiedaigouf;
    }

    public String getJianjiedagoufjl() {
        return jianjiedagoufjl;
    }

    public void setJianjiedagoufjl(String jianjiedagoufjl) {
        this.jianjiedagoufjl = jianjiedagoufjl;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }
}
