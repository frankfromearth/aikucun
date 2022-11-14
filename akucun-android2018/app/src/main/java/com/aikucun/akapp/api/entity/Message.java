package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by jarry on 2017/6/29.
 */

public class Message implements Serializable
{
    private String msgid;
    private String title;
    private String content;
    private String msgtypename;
    private String messagetime;

    private int readflag;       // 是否已读标志， 0未读 1已读


    public String getMsgid()
    {
        return msgid;
    }

    public void setMsgid(String msgid)
    {
        this.msgid = msgid;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getMsgtypename()
    {
        return msgtypename;
    }

    public void setMsgtypename(String msgtypename)
    {
        this.msgtypename = msgtypename;
    }

    public String getMessagetime()
    {
        return messagetime;
    }

    public void setMessagetime(String messagetime)
    {
        this.messagetime = messagetime;
    }

    public int getReadflag()
    {
        return readflag;
    }

    public void setReadflag(int readflag)
    {
        this.readflag = readflag;
    }
}
