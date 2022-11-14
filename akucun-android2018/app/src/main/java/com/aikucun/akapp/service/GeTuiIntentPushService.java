package com.aikucun.akapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.MessageActivity;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by micker on 2017/7/15.
 */

public class GeTuiIntentPushService extends GTIntentService
{

    public GeTuiIntentPushService()
    {
        super();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        super.onHandleIntent(intent);
    }

    @Override
    public void onReceiveServicePid(Context context, int i)
    {

    }

    @Override
    public void onReceiveClientId(Context context, String s)
    {

    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage
            gtNotificationMessage)
    {
        SCLog.logi("-> 推送消息 ： " + gtNotificationMessage.getContent());
    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage
            gtNotificationMessage)
    {

    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage)
    {
        SCLog.debug(gtTransmitMessage.getPayloadId());

        String payload = new String(gtTransmitMessage.getPayload());
        SCLog.logi("-> 透传消息 ： " + payload);

        try
        {
            JSONObject jsonObject = JSON.parseObject(payload);
            if (jsonObject != null)
            {
                SCLog.logi("-> JSON 格式 消息");
                handlePushMessage(context, jsonObject);
            }

        }
        catch (JSONException e)
        {
            //
            SCLog.logi("-> 文本格式 消息");
            showNotification(context,null, payload);
        }

    }

    public void handlePushMessage(Context context, JSONObject jsonData)
    {
        int action = jsonData.getIntValue("action");
        JSONObject payload = jsonData.getJSONObject("payload");

        switch (action)
        {
            case 101:       //同步活动商品数据
            {
                if (payload == null) {
                    break;
                }
                String liveId = payload.getString("liveid");
                if (StringUtils.isEmpty(liveId)) {
                    break;
                }
                SCLog.logi("-> 同步活动：" + liveId);
                ProductManager.getInstance().deleteLiveData(liveId);
                //
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig
                        .MESSAGE_EVENT_SYNC_LIVEDATA, liveId));
            }
                break;

            case 102:       //活动商品下架
            {
                if (payload == null) {
                    break;
                }
                String liveId = payload.getString("liveid");
                String list = payload.getString("products");
                List<String> products = JSON.parseArray(list, String.class);
                if (products == null) {
                    break;
                }
                for (String pId : products)
                {
                    ProductManager.getInstance().deleteProduct(pId);
                }
                //
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig
                        .MESSAGE_EVENT_SYNC_LIVEDATA, liveId));
            }
                break;

            default:
            {
                String title = jsonData.getString("title");
                String message = jsonData.getString("message");
                if (StringUtils.isEmpty(message)) {
                    break;
                }

                showNotification(context, title, message);
            }
                break;
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b)
    {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage)
    {

        SCLog.debug(gtCmdMessage.getClientId());

    }

    /**
     * 在状态栏显示通知
     */
    private void showNotification(Context context, String title, String content)
    {
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager) this.getSystemService
                (android.content.Context.NOTIFICATION_SERVICE);

        String titleText = StringUtils.isEmpty(title) ? "爱库存" : title;

        Intent intent = new Intent(context, MessageActivity.class);
        PendingIntent contentItent = PendingIntent.getActivity(this, 0, intent, 0);
        // 通过Notification.Builder来创建通知
        Notification.Builder myBuilder = new Notification.Builder(context);
        myBuilder.setContentTitle(titleText).setContentText(content).setTicker("您收到新的消息")
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
/*
    //删除通知
    private void clearNotification()
    {
        // 启动后删除之前我们定义的通知
        NotificationManager notificationManager = (NotificationManager) this.getSystemService
                (NOTIFICATION_SERVICE);
        notificationManager.cancel(1);

    }*/
}
