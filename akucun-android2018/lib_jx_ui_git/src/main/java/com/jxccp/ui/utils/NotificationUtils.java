/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui.utils;

import android.app.LauncherActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.common.message.JXConversation.SessionStatus;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.Type;
import com.jxccp.im.chat.common.message.JXMessageAttribute;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXConstants;
import com.jxccp.ui.R;
import com.jxccp.ui.view.JXChatUIActivity;
import com.jxccp.ui.view.JXChatView;

public class NotificationUtils {
    public static NotificationManager notificationManager;
    /**
     * 震动提示
     *
     * @param context
     */
    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {
                0, 180, 80, 120
        }, -1);
    }


    public static void notifyRecallIncoming(Context context, String skillsId){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, JXChatUIActivity.class);
        intent.putExtra(JXConstants.EXTRA_INTENT_TYPE, false);
        intent.putExtra(JXChatView.CHAT_KEY, skillsId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        String tickerText = context.getString(R.string.jx_imcoming_message_ticker_text, context.getString(R.string.jx_recall_tips));

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle("[回呼]").setTicker(tickerText)
                .setContentText("有客服回呼您").setContentIntent(contentIntent)
                .setVibrate(new long[] {
                        0, 180, 80, 120
                }).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Notification notification = mBuilder.build();
        notificationManager.notify(R.string.jx_imcoming_message_ticker_text, notification);
    }

    /**
     * 收到消息时通知提示
     */
    public static void showIncomingMessageNotify(Context context, JXMessage jxmessage){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        String from = jxmessage.getFrom();
        Intent intent = new Intent(context, JXChatUIActivity.class);
        long conversationId = jxmessage.getConversationId();
        JXConversation conversation = JXImManager.Conversation.getInstance().getConversationById(conversationId);
        String skillId = null;
        if(jxmessage.fromRobot() && conversation != null){
            skillId = conversation.getSkillsId();
        }else{
            skillId = (String)jxmessage.getAttributes().get(JXMessageAttribute.SKILLS_ID.value());
        }
        String type = (String)jxmessage.getAttributes().get(JXMessageAttribute.TYPE.value());
        intent.putExtra(JXChatView.CHAT_KEY, skillId);
        JXLog.i("type:"+type);
        if(type !=null ){
            if (type.endsWith(JXMessageAttribute.TYPE_VALUE_EVALUATED)) {
                return;
            }
            intent.putExtra(JXConstants.EXTRA_INTENT_TYPE, true);
            from = context.getString(R.string.jx_admin);
        }else{
            if(conversation.getSessionStatus() != SessionStatus.Deactived){
                intent.putExtra(JXConstants.EXTRA_INTENT_TYPE, false);
            }else{
                intent.putExtra(JXConstants.EXTRA_INTENT_TYPE, true);
            }
        }
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Type messageType = jxmessage.getType();
        String message = "";
        switch (messageType) {
            case TEXT:
                message = ((TextMessage)jxmessage).getContent();
                message =  message.replaceAll("\\[.{2,3}\\]", context.getString(R.string.jx_expression_tips));
                if (JXCommonUtils.isHtmlText(message)) {
                    message = context.getString(R.string.jx_rich_text_message);
                }
                break;
            case IMAGE:
                message = context.getString(R.string.jx_image_message);
                break;
            case VOICE:
                message = context.getString(R.string.jx_voice_message);
                break;
            case RICHTEXT:
                message = context.getString(R.string.jx_rich_message);
                break;
            default:
                break;
        }
        String tickerText = context.getString(R.string.jx_imcoming_message_ticker_text, message);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle(from).setTicker(tickerText)
                .setContentText(message).setContentIntent(contentIntent)
                .setVibrate(new long[] {
                        0, 180, 80, 120
        }).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Notification notification = mBuilder.build();
        notificationManager.notify(R.string.jx_imcoming_message_ticker_text, notification);


    }

    public static void showMessagePushNotify(Context context, JXMessage jxmessage){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent();
        intent.setClass(context, JXChatUIActivity.class);
        intent.putExtra(JXChatView.CHAT_TYPE, JXChatView.CHATTYPE_MESSAGE_BOX);
        intent.putExtra(JXChatView.CHAT_SKILLS_DISPLAYNAME,
                context.getString(R.string.jx_message_center));
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Type messageType = jxmessage.getType();
        if(Type.TEXT == messageType){
            TextMessage textMessage = (TextMessage)jxmessage;
            String message = textMessage.getContent();
            String tickerText = context.getString(R.string.jx_push_message_ticker_text, message);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true).setContentTitle(context.getString(R.string.jx_push_message_title)).setTicker(tickerText)
                    .setContentText(message).setContentIntent(contentIntent)
                    .setVibrate(new long[] {
                            0, 180, 80, 120
            }).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            Notification notification = mBuilder.build();
            notificationManager.notify(R.string.jx_push_message_ticker_text, notification);
        }
    }

    public static void cancelAllNotifty(){
        if(notificationManager != null){
            notificationManager.cancelAll();
        }
    }

    public static void cancelNotify(int id){
        if(notificationManager != null){
            notificationManager.cancel(id);;
        }
    }

    /**
     * @param context
     * @param msg
     */
    public static void notify(Context context, int titleResId, int msgResId) {
        Notification notification = new Notification();

        // FIXME
        Intent notificationIntent = new Intent(context, LauncherActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle(context.getString(titleResId))
                .setContentText(context.getString(msgResId)).setContentIntent(contentIntent)
                .setVibrate(new long[] {
                        0, 180, 80, 120
        }).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notification = mBuilder.build();

        NotificationManager notificationManager = (NotificationManager)context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
        notificationManager.notify(1, notification);
    }
}
