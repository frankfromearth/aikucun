package com.aikucun.akapp.api.entity;

import java.io.Serializable;
import java.util.List;

/**
 * UserInfo 用户信息
 * Created by jarry on 16/5/13.
 */
public class UserInfo implements Serializable {

    private List<SubUserInfo> subUserinfos;
    private String userid;

    public List<SubUserInfo> getSubUserinfos() {
        return subUserinfos;
    }

    public void setSubUserinfos(List<SubUserInfo> subUserinfos) {
        this.subUserinfos = subUserinfos;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String avator;

    private String name;

    private int viplevel;

    private String shoujihao;

    private String yonghubianhao;

    private int unreadnum;
    //邀请码是否有效1：有效
    private int prcstatu;

    private String vipendtime;

    private int allowUpload;
    private int identityflag;
    //月度统计
    private int monthstat;
    //上月统计
    private int lastmonthstat;
    //昨日统计
    private int todaystat;
    //今日代购费
    private int  todayfee;
    //本月代购费
    private int  monthfee;
    //上月代购费
    private int lastmonthfee;
    //邀请码
    private String  preferralcode;
    //团队成员数量
    private int members;
    //待批准成员数量
    private int waitApproveMembers;

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViplevel() {
        return viplevel;
    }

    public void setViplevel(int viplevel) {
        this.viplevel = viplevel;
    }

    public String getShoujihao() {
        return shoujihao;
    }

    public void setShoujihao(String shoujihao) {
        this.shoujihao = shoujihao;
    }

    public String getYonghubianhao() {
        return yonghubianhao;
    }

    public void setYonghubianhao(String yonghubianhao) {
        this.yonghubianhao = yonghubianhao;
    }

    public int getUnreadnum() {
        return unreadnum;
    }

    public void setUnreadnum(int unreadnum) {
        this.unreadnum = unreadnum;
    }

    public int getPrcstatu() {
        return prcstatu;
    }

    public void setPrcstatu(int prcstatu) {
        this.prcstatu = prcstatu;
    }

    public String getVipendtime() {
        return vipendtime;
    }

    public void setVipendtime(String vipendtime) {
        this.vipendtime = vipendtime;
    }

    public int getAllowUpload() {
        return allowUpload;
    }

    public void setAllowUpload(int allowUpload) {
        this.allowUpload = allowUpload;
    }

    public int getIdentityflag() {
        return identityflag;
    }

    public void setIdentityflag(int identityflag) {
        this.identityflag = identityflag;
    }

    public int getMonthstat() {
        return monthstat;
    }

    public void setMonthstat(int monthstat) {
        this.monthstat = monthstat;
    }

    public int getTodaystat() {
        return todaystat;
    }

    public void setTodaystat(int todaystat) {
        this.todaystat = todaystat;
    }

    public String getPreferralcode() {
        return preferralcode;
    }

    public void setPreferralcode(String preferralcode) {
        this.preferralcode = preferralcode;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getWaitApproveMembers() {
        return waitApproveMembers;
    }

    public void setWaitApproveMembers(int waitApproveMembers) {
        this.waitApproveMembers = waitApproveMembers;
    }

    public int getLastmonthstat() {
        return lastmonthstat;
    }

    public void setLastmonthstat(int lastmonthstat) {
        this.lastmonthstat = lastmonthstat;
    }

    public int getTodayfee() {
        return todayfee;
    }

    public void setTodayfee(int todayfee) {
        this.todayfee = todayfee;
    }

    public int getMonthfee() {
        return monthfee;
    }

    public void setMonthfee(int monthfee) {
        this.monthfee = monthfee;
    }

    public int getLastmonthfee() {
        return lastmonthfee;
    }

    public void setLastmonthfee(int lastmonthfee) {
        this.lastmonthfee = lastmonthfee;
    }
}
