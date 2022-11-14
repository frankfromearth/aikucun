package com.akucun.checkbill.common;



public final class StrUtils {

	public static boolean isEmpty(Object o){
		return (o==null || o.toString().trim().length()==0);
	}
	/**
	 * 表示 &lt;null&gt;，长度为0的字符串 ，null,"",'',undefined都被视为空内容
	 * @param o
	 * @param isBlankOrNull
	 * @return
	 */
	public static boolean isEmpty(Object o,boolean isBlankOrNull){
		if(isBlankOrNull){
			if(o==null) return true;
			String tmp=o.toString().trim();
			return (tmp.length()==0 || tmp.equals("\"\"") || tmp.equals("''") || tmp.equals("null") || tmp.equals("undefined"));
		}else return  (o==null || o.toString().trim().length()==0);
	}

	public static String null2string(Object s){
		return (s==null)?"":s.toString();
	}
	/**
	 * 当s为空时返回defaultval
	 * @param s
	 * @param defaultVal
	 * @return
	 */
	public static String null2string(Object s,String defaultVal){
		return isEmpty(s)?defaultVal:s.toString();
	}
}
