package com.aikucun.akapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ak123 on 2017/11/16.
 * activity管理主要处理页面跳转
 */

public class ActivityUtils {

    /**
     * Activity 跳转页面
     *
     * @param activity
     * @param clazz       跳转目标类
     * @param bundle      参数 无参数为null
     * @param requestCode 请求Code | 无需返回：-1
     */
    public static void startActivity(Activity activity, Class<?> clazz,
                                     Bundle bundle, int requestCode) {
        if (clazz == null || activity == null)
            return;
        Intent intent = new Intent(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (requestCode < 0) {
            activity.startActivity(intent);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * Activity页面跳转
     * @param activity
     * @param clazz 目标页面
     */
    public static void startActivity(Activity activity, Class<?> clazz){
        startActivity(activity,clazz,null,-1);
    }

    /**
     * Activity页面跳转
     * @param activity
     * @param clazz 目标页面
     * @param bundle 携带参数
     */
    public static void startActivity(Activity activity,Class<?> clazz,Bundle bundle){
        startActivity(activity,clazz,bundle,-1);
    }

    /**
     *  Activity页面跳转
     * @param activity
     * @param clazz 目标页面
     * @param requestCode 请求Code | 无需返回：-1
     */
    public static void startActivity(Activity activity,Class<?> clazz,int requestCode){
        startActivity(activity,clazz,null,requestCode);
    }
}
