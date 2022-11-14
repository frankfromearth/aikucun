package com.aikucun.akapp.api.entity;

import java.io.Serializable;

/**
 * 直播定时跟踪控制
 * Created by jarry on 2017/6/7.
 */

public class LiveControl implements Serializable
{
    private int flag;   // 0 为 随机，1 为 周期
    private int max;    // 随机数的大值
    private int min;    // 随机数的小值
    private int period; // 周期定时，单位为秒

    public int getFlag()
    {
        return flag;
    }

    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public int getMax()
    {
        return max;
    }

    public void setMax(int max)
    {
        this.max = max;
    }

    public int getMin()
    {
        return min;
    }

    public void setMin(int min)
    {
        this.min = min;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period)
    {
        this.period = period;
    }
}
