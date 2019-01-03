package com.ctfo.parking.util;

/**
 * String 工具类
 */
public class StringUtil {


    /**
     * 字符串为空时，格式化返回
     * @param str
     * @param replace  格式化返回字符串
     * @return
     */
    public static String formatString(String str, String replace){
        if(str!= null && !"".equals(str)){
            return str;
        }
        return replace;
    }

    /**
     * 字符串非空判断
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str){
        return str != null && !"".equals(str);
    }

}
