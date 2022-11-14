package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by micker on 2017/9/17.
 */

public class RechargeItem implements Serializable {

    private int jine;
    private int payjine;

    private String title;
    private String content;
    private String paytype;
    private String deltaid;

    public int getJine() {
        return jine;
    }

    public void setJine(int jine) {
        this.jine = jine;
    }

    public int getPayjine() {
        return payjine;
    }

    public void setPayjine(int payjine) {
        this.payjine = payjine;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getDeltaid() {
        return deltaid;
    }

    public void setDeltaid(String deltaid) {
        this.deltaid = deltaid;
    }
}
