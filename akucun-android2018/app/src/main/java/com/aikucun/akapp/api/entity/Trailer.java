package com.aikucun.akapp.api.entity;

/**
 * Created by micker on 2017/7/4.
 */

public class Trailer {
    //TODO 这里还需要获取活动ID
    private String liveid;

    private String begin;
    private String end;
    private String pinpaiid;
    private String pinpaiming;
    private String pinpaiurl;
    private String content;
    private String yugaoneirong;
    private String yugaotupian;

    private long begintimestamp;
    private long endtimestamp;

    private int productcount;
    private int skucount;
    private int num;


    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPinpaiid() {
        return pinpaiid;
    }

    public void setPinpaiid(String pinpaiid) {
        this.pinpaiid = pinpaiid;
    }

    public String getPinpaiming() {
        return pinpaiming;
    }

    public void setPinpaiming(String pinpaiming) {
        this.pinpaiming = pinpaiming;
    }

    public String getPinpaiurl() {
        return pinpaiurl;
    }

    public void setPinpaiurl(String pinpaiurl) {
        this.pinpaiurl = pinpaiurl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getYugaoneirong() {
        return yugaoneirong;
    }

    public void setYugaoneirong(String yugaoneirong) {
        this.yugaoneirong = yugaoneirong;
    }

    public String getYugaotupian() {
        return yugaotupian;
    }

    public void setYugaotupian(String yugaotupian) {
        this.yugaotupian = yugaotupian;
    }

    public long getBegintimestamp() {
        return begintimestamp;
    }

    public void setBegintimestamp(long begintimestamp) {
        this.begintimestamp = begintimestamp;
    }

    public long getEndtimestamp() {
        return endtimestamp;
    }

    public void setEndtimestamp(long endtimestamp) {
        this.endtimestamp = endtimestamp;
    }

    public int getProductcount() {
        return productcount;
    }

    public void setProductcount(int productcount) {
        this.productcount = productcount;
    }

    public int getSkucount() {
        return skucount;
    }

    public void setSkucount(int skucount) {
        this.skucount = skucount;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }
}
