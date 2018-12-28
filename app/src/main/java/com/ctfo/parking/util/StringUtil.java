package com.ctfo.parking.util;

public class StringUtil {

    public static String formatString(String str, String replace){
        if(str!= null && !"".equals(str)){
            return str;
        }
        return replace;
    }
}
