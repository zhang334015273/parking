package com.ctfo.parking.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.ctfo.parking.ParkingApplication;

/**
 * 存储 用户信息的工具类
 */
public class UserSPUtil {

    private static SharedPreferences sharedPreferences = ParkingApplication.getContext().getSharedPreferences("USER",Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor = sharedPreferences.edit();

    /**
     * 查看登录状态
     * @return
     */
    public static boolean isLogin(){
        String token = sharedPreferences.getString("token",null);
        return StringUtil.isNotEmpty(token);
    }

}
