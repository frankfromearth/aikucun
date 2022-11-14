package com.aikucun.akapp.utils;

import android.util.Log;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Log工具类
 * Created by jarry on 16/3/11.
 */
public class SCLog
{

    public static final String LOG_TAG = "Akucun";
    public static boolean DEBUG = true;

    public static final void init()
    {
        Logger.init(LOG_TAG).logLevel(LogLevel.FULL).methodCount(1).hideThreadInfo();
    }

    public static final void log(String log)
    {
        if (DEBUG)
        {
            Logger.t(0).d(log);
        }
    }

    public static final void logv(String log)
    {
        if (DEBUG)
        {
            Logger.t(0).v(log);
        }
    }

    public static final void logi(String log)
    {
        if (DEBUG)
        {
            Logger.t(0).i(log);
        }
    }

    public static final void logObj(Object obj)
    {
        if (DEBUG)
        {
            Logger.t(0).d(obj);
        }
    }


    public static final void logJson(String json)
    {
        if (DEBUG)
        {
            Logger.t(0).json(json);
        }
    }

    public static final void error(String log)
    {
        Logger.e(log);
    }

    public static final void warn(String log)
    {
        if (DEBUG)
        {
            Log.w(LOG_TAG, log);
        }
    }

    public static final void debug(String log)
    {
        if (DEBUG)
        {
            Log.d(LOG_TAG, log);
        }
    }
}
