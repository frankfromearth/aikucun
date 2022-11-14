package com.aikucun.akapp.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @created 16/3/12
 */
public class StringUtils
{
    private final static Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\" +
            ".\\w+([-.]\\w+)*");

    private final static Pattern IMG_URL = Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)");

    private final static Pattern URL = Pattern.compile("^(https|http)://.*?$(net|com|.com" + "" +
            ".cn|org|me|)");


    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input)
    {
        if (input == null || "".equals(input))
        {
            return true;
        }

        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、199（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String num = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email)
    {
        if (email == null || email.trim().length() == 0)
        {
            return false;
        }
        return emailer.matcher(email).matches();
    }

    /**
     * 判断一个url是否为图片url
     *
     * @param url
     * @return
     */
    public static boolean isImgUrl(String url)
    {
        if (url == null || url.trim().length() == 0)
        {
            return false;
        }
        return IMG_URL.matcher(url).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str)
    {
        if (str == null || str.trim().length() == 0)
        {
            return false;
        }
        return URL.matcher(str).matches();
    }

    public static String getPriceString(int price)
    {
        return String.format("¥%.2f", price / 100.0);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue)
    {
        try
        {
            return Integer.parseInt(str);
        }
        catch (Exception e)
        {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj)
    {
        if (obj == null)
        {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj)
    {
        try
        {
            return Long.parseLong(obj);
        }
        catch (Exception e)
        {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b)
    {
        try
        {
            return Boolean.parseBoolean(b);
        }
        catch (Exception e)
        {
        }
        return false;
    }

    public static String getString(String s)
    {
        return s == null ? "" : s;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is)
    {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try
        {
            String line;
            line = read.readLine();
            while (line != null)
            {
                res.append(line + "<br>");
                line = read.readLine();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (null != isr)
                {
                    isr.close();
                    isr.close();
                }
                if (null != read)
                {
                    read.close();
                    read = null;
                }
                if (null != is)
                {
                    is.close();
                    is = null;
                }
            }
            catch (IOException e)
            {
            }
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     */
    public static String getSubString(int start, int num, String str)
    {
        if (str == null)
        {
            return "";
        }
        int leng = str.length();
        if (start < 0)
        {
            start = 0;
        }
        if (start > leng)
        {
            start = leng;
        }
        if (num < 0)
        {
            num = 1;
        }
        int end = start + num;
        if (end > leng)
        {
            end = leng;
        }
        return str.substring(start, end);
    }

    /**
     * unicode转码
     *
     * @param ori 原始unicode字符串
     * @return
     */
    public static String convertUnicode(String ori)
    {
        char aChar;
        int len = ori.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; )
        {
            aChar = ori.charAt(x++);
            if (aChar == '\\')
            {
                aChar = ori.charAt(x++);
                if (aChar == 'u')
                {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++)
                    {
                        aChar = ori.charAt(x++);
                        switch (aChar)
                        {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed   \\uxxxx   " +
                                        "encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                }
                else
                {
                    if (aChar == 't')
                    {
                        aChar = '\t';
                    }
                    else if (aChar == 'r')
                    {
                        aChar = '\r';
                    }
                    else if (aChar == 'n')
                    {
                        aChar = '\n';
                    }
                    else if (aChar == 'f')
                    {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            }
            else
            {
                outBuffer.append(aChar);
            }

        }
        return outBuffer.toString();
    }

    public static boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static String getNum2Decimal(String temp) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String result = df.format(Double.valueOf(temp));
        return result;
    }
    public static String getNum2Decimal(double temp) {
        DecimalFormat df = new DecimalFormat("######0.00");
        String result = df.format(temp);
        return result;
    }
    public static String getDecimal(double temp) {
        DecimalFormat df = new DecimalFormat("######0");
        String result = df.format(temp);
        return result;
    }
}
