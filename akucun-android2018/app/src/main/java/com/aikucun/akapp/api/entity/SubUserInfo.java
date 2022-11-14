package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by ak123 on 2018/1/4.
 * 子账户
 */

public class SubUserInfo implements Serializable {


    private String subUserId;

    private String userid;

    private String tmptoken;

    private String subusername;
    // 手机号 是否已绑定
    private int validflag;
    // 是否已登录
    private int islogin;
    // 设备平台
    private String platform;
    // 设备名称
    private String devicename;
    // 登录时间
    private String logintime;
    // APP版本
    private String appversion;
    // 登录类型
    private int thirdtype;

    public String getSubUserId() {
        return subUserId;
    }

    public void setSubUserId(String subUserId) {
        this.subUserId = subUserId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTmptoken() {
        return tmptoken;
    }

    public void setTmptoken(String tmptoken) {
        this.tmptoken = tmptoken;
    }

    public String getSubusername() {
        return subusername;
    }

    public void setSubusername(String subusername) {
        this.subusername = subusername;
    }

    public int getValidflag() {
        return validflag;
    }

    public void setValidflag(int validflag) {
        this.validflag = validflag;
    }

    public int getIslogin() {
        return islogin;
    }

    public void setIslogin(int islogin) {
        this.islogin = islogin;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDevicename() {
        return devicename;
    }

    public void setDevicename(String devicename) {
        this.devicename = devicename;
    }

    public String getLogintime() {
        return logintime;
    }

    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public int getThirdtype() {
        return thirdtype;
    }

    public void setThirdtype(int thirdtype) {
        this.thirdtype = thirdtype;
    }

}
