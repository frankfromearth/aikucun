package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by ak123 on 2018/1/19.
 * 待批准成员信息
 */

public class InviteMember implements Serializable {
//    "id": "ddddddddddd",
//            "nick": "昵称",
//            "avtor": "https://shared-https.ydstatic.com/dict/v2016/logo.png",
//            "daigou": "102824",
//            "applydate": "2018-01-10"
    private String id;
    private String nick;
    private String avatar;
    private String daigou;
    private String applydate;
    private String referralcode;
    private int status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avtor) {
        this.avatar = avtor;
    }

    public String getDaigou() {
        return daigou;
    }

    public void setDaigou(String daigou) {
        this.daigou = daigou;
    }

    public String getApplydate() {
        return applydate;
    }

    public void setApplydate(String applydate) {
        this.applydate = applydate;
    }

    public String getReferralcode() {
        return referralcode;
    }

    public void setReferralcode(String referralcode) {
        this.referralcode = referralcode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
