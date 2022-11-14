package com.aikucun.akapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by micker on 2017/7/23.
 */

public class FileDownloadPackageBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        FileDownloadManager.checkDownloadStatus(context, "aaa ");//检查下载状态
    }
}
