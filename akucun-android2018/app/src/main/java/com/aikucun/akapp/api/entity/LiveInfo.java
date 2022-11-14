package com.aikucun.akapp.api.entity;

import java.util.Date;
import java.util.List;

/**
 * 直播任务信息
 * Created by jarry on 2017/6/7.
 */
public class LiveInfo
{
    private boolean isSelected=false;
    private String liveid;
    private String content;
    private String pinpai;
    private String pinpaiming;
    private String pinpaiurl;
    private String yugaoneirong;
    private String yugaotupian;
    private String checksheeturl;

    private long begintimestamp;
    private long endtimestamp;

    private int buymodel;

    private List<Comment> comments;

    public boolean hasBegun() {
        return begintimestamp < new Date().getTime();
    }

    public boolean hasEnded() {
        return endtimestamp > new Date().getTime();
    }

    public boolean isValid() {
        return hasBegun() && !hasEnded();
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPinpai()
    {
        return pinpai;
    }

    public void setPinpai(String pinpai)
    {
        this.pinpai = pinpai;
    }

    public String getPinpaiming()
    {
        return pinpaiming;
    }

    public void setPinpaiming(String pinpaiming)
    {
        this.pinpaiming = pinpaiming;
    }

    public String getPinpaiurl()
    {
        return pinpaiurl;
    }

    public void setPinpaiurl(String pinpaiurl)
    {
        this.pinpaiurl = pinpaiurl;
    }

    public String getYugaoneirong()
    {
        return yugaoneirong;
    }

    public void setYugaoneirong(String yugaoneirong)
    {
        this.yugaoneirong = yugaoneirong;
    }

    public String getYugaotupian()
    {
        return yugaotupian;
    }

    public void setYugaotupian(String yugaotupian)
    {
        this.yugaotupian = yugaotupian;
    }

    public long getBegintimestamp()
    {
        return begintimestamp;
    }

    public void setBegintimestamp(long begintimestamp)
    {
        this.begintimestamp = begintimestamp;
    }

    public long getEndtimestamp()
    {
        return endtimestamp;
    }

    public void setEndtimestamp(long endtimestamp)
    {
        this.endtimestamp = endtimestamp;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public int getBuymodel()
    {
        return buymodel;
    }

    public void setBuymodel(int buymodel)
    {
        this.buymodel = buymodel;
    }

    public List<Comment> getComments()
    {
        return comments;
    }

    public void setComments(List<Comment> comments)
    {
        this.comments = comments;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getChecksheeturl() {
        return checksheeturl;
    }

    public void setChecksheeturl(String checksheeturl) {
        this.checksheeturl = checksheeturl;
    }
}
