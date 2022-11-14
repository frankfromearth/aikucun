package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * Created by ak123 on 2017/12/11.
 * 物流跟踪
 */

public class Trace implements Serializable {
    private String content;
    private String time;
    private boolean isEnd = false;
    private boolean isTop = false;

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
