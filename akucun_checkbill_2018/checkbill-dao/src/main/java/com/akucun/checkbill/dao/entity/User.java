package com.akucun.checkbill.dao.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class User {

    private String id;
    private String userNo;
    private String name;
    private String password;

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("userNo", userNo)
                .append("name", name)
                .append("password", password)
                .toString();
    }
}