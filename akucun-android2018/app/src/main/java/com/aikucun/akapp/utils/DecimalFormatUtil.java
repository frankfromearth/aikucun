package com.aikucun.akapp.utils;

import java.text.DecimalFormat;

public class DecimalFormatUtil
{

    public static String formatValue(double value, int decimal)
    {
        StringBuffer formatStr = new StringBuffer("###,###");

        for (int i = 0; i < decimal; i++)
        {
            if (i == 0)
            {
                formatStr.append(".");
            }
            formatStr.append("0");
        }

        DecimalFormat format = new DecimalFormat(formatStr.toString());
        return format.format(value);
    }

    public static String formatPercent(double value, int decimal)
    {
        StringBuffer formatStr = new StringBuffer("#");

        for (int i = 0; i < decimal; i++)
        {
            if (i == 0)
            {
                formatStr.append(".");
            }
            formatStr.append("0");
        }

        formatStr.append("%");

        DecimalFormat format = new DecimalFormat(formatStr.toString());
        return format.format(value);
    }
}
