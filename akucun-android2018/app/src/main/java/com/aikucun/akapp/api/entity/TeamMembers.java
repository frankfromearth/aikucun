package com.aikucun.akapp.api.entity;

/**
 * Created by ak123 on 2018/1/19.
 * 团队成员信息
 */

public class TeamMembers {
    private long forwardCount;
    private long monthsTotal;
    private String nick;
    private String avatar;

    public long getForwardCount() {
        return forwardCount;
    }

    public void setForwardCount(long forwardCount) {
        this.forwardCount = forwardCount;
    }

    public long getMonthsTotal() {
        return monthsTotal;
    }

    public void setMonthsTotal(long monthsTotal) {
        this.monthsTotal = monthsTotal;
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
