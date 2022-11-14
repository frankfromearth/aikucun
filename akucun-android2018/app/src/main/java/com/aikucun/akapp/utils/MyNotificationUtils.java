package com.aikucun.akapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.MessageActivity;

/**
 * Created by ak123 on 2018/1/16.
 * 通知管理
 */

public class MyNotificationUtils {

    public static void showNotification(Context context, String content) {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager) context.getSystemService
                (android.content.Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MessageActivity.class);
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, intent, 0);
        // 通过Notification.Builder来创建通知
        Notification.Builder myBuilder = new Notification.Builder(context);
        myBuilder.setContentTitle("爱库存").setContentText(content).setTicker("您收到新的消息")
                //设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示
                .setSmallIcon(R.drawable.ic_notification)
                //设置默认声音和震动
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)//点击后取消
                .setWhen(System.currentTimeMillis())//设置通知时间
                .setContentIntent(contentItent);  //3.关联PendingIntent
        Notification notification = myBuilder.build();
        // 把Notification传递给NotificationManager
        notificationManager.notify(1, notification);
    }

}
