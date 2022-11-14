package com.akucun.checkbill.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaojin on 4/2/2018.
 */
public class DateUtil {
    public static String formateDateISO(Date date)
    {
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
