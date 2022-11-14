package com.aikucun.akapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.aikucun.akapp.utils.SCLog;
import com.igexin.sdk.GTServiceManager;

/**
 * Created by micker on 2017/7/15.
 */

public class GeTuiPushService extends Service {
    public static final String TAG = GeTuiPushService.class.getName();

    @Override
    public void onCreate() {
        // 该行日志在 release 版本去掉
        SCLog.debug(TAG + " call -> onCreate -------");

        super.onCreate();
        GTServiceManager.getInstance().onCreate(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 该行日志在 release 版本去掉
        SCLog.debug(TAG + " call -> onStartCommand -------");

        super.onStartCommand(intent, flags, startId);
        return GTServiceManager.getInstance().onStartCommand(this, intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 该行日志在 release 版本去掉
        SCLog.debug("onBind -------");
        return GTServiceManager.getInstance().onBind(intent);
    }

    @Override
    public void onDestroy() {
        // 该行日志在 release 版本去掉
        SCLog.debug("onDestroy -------");

        super.onDestroy();
        GTServiceManager.getInstance().onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        GTServiceManager.getInstance().onLowMemory();
    }
}
