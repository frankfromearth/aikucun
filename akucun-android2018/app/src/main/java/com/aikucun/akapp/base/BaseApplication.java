package com.aikucun.akapp.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;

/**
 * Base Application
 * Created by jarry on 16/3/11.
 */
public class BaseApplication extends Application
{
    private static String PREF_NAME = "eachingmobile.pref";

    static Context _context;
    static Resources _resource;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _context = getApplicationContext();
        _resource = _context.getResources();
    }

    public static synchronized BaseApplication context()
    {
        return (BaseApplication) _context;
    }

    public static Resources resources()
    {
        return _resource;
    }

    public static void set(String key, int value)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(key, value);
        apply(editor);
    }

    public static void set(String key, long value)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putLong(key, value);
        apply(editor);
    }

    public static void set(String key, boolean value)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        apply(editor);
    }

    public static void set(String key, String value)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        apply(editor);
    }

    public static void set(String key, float value)
    {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putFloat(key, value);
        apply(editor);
    }

    public static boolean get(String key, boolean defValue)
    {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String get(String key, String defValue)
    {
        return getPreferences().getString(key, defValue);
    }

    public static int get(String key, int defValue)
    {
        return getPreferences().getInt(key, defValue);
    }

    public static long get(String key, long defValue)
    {
        return getPreferences().getLong(key, defValue);
    }

    public static float get(String key, float defValue)
    {
        return getPreferences().getFloat(key, defValue);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void apply(SharedPreferences.Editor editor)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
        {
            editor.apply();
        } else
        {
            editor.commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences()
    {
        return getPreferences(PREF_NAME);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences(String prefName)
    {
        return context().getSharedPreferences(prefName, 0);
    }
}
