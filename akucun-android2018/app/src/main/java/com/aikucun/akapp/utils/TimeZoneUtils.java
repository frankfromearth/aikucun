package com.aikucun.akapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时区转换
 *
 * @date 2014年10月9日
 */
public class TimeZoneUtils
{

    /**
     * 判断用户的设备时区是否为东八区（中国） 2014年7月31日
     *
     * @return
     */
    public static boolean isInEasternEightZones()
    {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
            defaultVaule = true;
        else
            defaultVaule = false;
        return defaultVaule;
    }

    /**
     * 根据不同时区，转换时间 2014年7月31日
     *
     * @param date
     * @return
     */
    public static Date transformTime(Date date, TimeZone oldZone, TimeZone newZone)
    {
        Date finalDate = null;
        if (date != null)
        {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }

    public static String longToString(long currentTime) {
        Date date = new Date(currentTime);
        return  new SimpleDateFormat("MM/dd HH:mm").format(date);
    }
}
