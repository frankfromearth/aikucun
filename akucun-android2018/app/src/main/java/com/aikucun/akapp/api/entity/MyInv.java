package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by jarry on 2017/6/29.
 */

public class MyInv implements Serializable
{
    private String id;
    private String referralcode;
    private String userid;
    private String ruserid;
    private String codeid;
    private String rowOrder000;
    private String yonghubianhao;
    private String nicheng;
    private String createtime;

    private int statu;       // 是否已读标志， 0未读 1已读

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferralcode() {
        return referralcode;
    }

    public void setReferralcode(String referralcode) {
        this.referralcode = referralcode;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRuserid() {
        return ruserid;
    }

    public void setRuserid(String ruserid) {
        this.ruserid = ruserid;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getRowOrder000() {
        return rowOrder000;
    }

    public void setRowOrder000(String rowOrder000) {
        this.rowOrder000 = rowOrder000;
    }

    public String getYonghubianhao() {
        return yonghubianhao;
    }

    public void setYonghubianhao(String yonghubianhao) {
        this.yonghubianhao = yonghubianhao;
    }

    public String getNicheng() {
        return nicheng;
    }

    public void setNicheng(String nicheng) {
        this.nicheng = nicheng;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }
}
