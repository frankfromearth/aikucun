package com.aikucun.akapp.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by micker on 2017/7/23.
 */

public class FileDownloadManager {

    public static long taskId;
    private static String TAGStr = "FileDownloadManager";
//    public static String downloadUrl = "http://116.211.184.174/imtt.dd.qq.com/16891/A57EB927AA21AB73D521BE20DD0BF61B.apk?mkey=59100251fe3137ee&f=9512&c=0&fsname=com.tencent.mm_6.5.7_1041.apk&csr=1bbd&p=.apk";
    public static String sdcardPath = "/download/";

    //使用系统下载器下载
    public static long downloadAPK(Context context, String downloadUrl, String dirType, String subPath, BroadcastReceiver receiver) {
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);


        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED))
        {
            return 0;
        }

        String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+ "/akucun/").getAbsolutePath() + File.separator + subPath;
        File file = new File(downloadPath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir(dirType, subPath);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        long mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        context.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        return mTaskId;//返回任务ID
    }

    //检查下载状态
    public static void checkDownloadStatus(Context context, String fileName) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(taskId);//筛选下载任务，传入任务ID，可变参数
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //除了下载完成状态能监听到，其它在小米5P上试都监听不到不知道为什么
                case DownloadManager.STATUS_PAUSED:
                    Log.i(TAGStr, ">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i(TAGStr, ">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i(TAGStr, ">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i(TAGStr, ">>>下载完成");
                    //下载完成安装APK
                    String downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + fileName;
//                    installAPK(context, new File(downloadPath));
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.i(TAGStr, ">>>下载失败");
                    break;
            }
        }
    }
}
