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

package com.jxccp.ui.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.AnyRes;
import android.support.v4.util.ArrayMap;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jxccp.im.chat.common.entity.JXSatisfication;
import com.jxccp.im.chat.common.message.FileMessage;
import com.jxccp.im.chat.common.message.ImageMessage;
import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.Direction;
import com.jxccp.im.chat.common.message.JXMessage.Status;
import com.jxccp.im.chat.common.message.JXMessage.Type;
import com.jxccp.im.chat.common.message.JXMessageAttribute;
import com.jxccp.im.chat.common.message.RichTextMessage;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.common.message.VideoMessage;
import com.jxccp.im.chat.common.message.VoiceMessage;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.DateUtil;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXConstants;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.listeners.JXChatFragmentListener;
import com.jxccp.ui.listeners.JXVoicePlayListener;
import com.jxccp.ui.utils.JXCommonUtils;
import com.jxccp.ui.utils.JXSmileUtils;
import com.jxccp.ui.view.JXPhotoActivity;
import com.jxccp.ui.view.JXWebViewActivity;
import com.jxccp.ui.widget.JXCircleImageView;
import com.jxccp.ui.widget.JXLinkMovementMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JXChatAdapter extends JXBasicAdapter<JXMessage, ListView> {

    /**
     * 默认显示时间的间隔为3分钟
     */
    static final int DEFAULT_TIME_DIVIDE = 180000;

    static final int MESSAGE_TYPE_SEND_TXT = 0;

    static final int MESSAGE_TYPE_RECV_TXT = 1;

    static final int MESSAGE_TYPE_SEND_IMAGE = 2;

    static final int MESSAGE_TYPE_RECV_IMAGE = 3;

    static final int MESSAGE_TYPE_SEND_VOICE = 4;

    static final int MESSAGE_TYPE_RECV_VOICE = 5;

    static final int MESSAGE_TYPE_SEND_VIDEO = 6;

    static final int MESSAGE_TYPE_RECV_VIDEO = 7;

    static final int MESSAGE_TYPE_SEND_RICHTEXT = 8;

    static final int MESSAGE_TYPE_RECV_EVALUATION = 9;

    static final int MESSAGE_TYPE_SEND_NOTICE = 10;

    static final int MESSAGE_TYPE_SEND_NOTICE_CANCEL_WAIT = 11;
    
    static final int MESSAGE_TYPE_SEND_FILE = 12;
    
    static final int MESSAGE_TYPE_RECV_FILE = 13;

    private LayoutInflater mInflater;

    private String adminString;

    private String robotString;

    private int mScreenWidth;

    private JXSatisfication satisfication;

    private String defaultImage = "http://web.jiaxincloud.com/images/agent.png";

    public JXChatAdapter(Context context, List<JXMessage> list, ListView view,
            JXConversation conversation) {
        super(context, list, view);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adminString = context.getString(R.string.jx_admin);
        robotString = context.getString(R.string.jx_jxrobot);
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = 2 * dm.widthPixels / 3;
        if(null != conversation){
            satisfication = conversation.getSatisfication();
        }
        
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final JXMessage message = list.get(position);
        JXMessage.Type messageType = message.getType();
        if (convertView == null) {
            convertView = createViewByMessage(message, position);
            if (messageType == JXMessage.Type.TEXT) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.contentView = (TextView)convertView.findViewById(R.id.tv_message);
                if(message.getDirect() == Direction.SEND){
                    if(textSendItemBgResId != INVALID_RESID){
                        holder.contentView.setBackgroundResource(textSendItemBgResId);
                    }
                }else{
                    if(textRecvItemBgResId != INVALID_RESID){
                        holder.contentView.setBackgroundResource(textRecvItemBgResId);
                    }
                }
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                convertView.setTag(holder);
            } else if (messageType == Type.IMAGE) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.imgView = (ImageView)convertView.findViewById(R.id.iv_img);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                if (message.getDirect() == Direction.SEND) {
                    holder.progressTextView = (TextView)convertView.findViewById(R.id.tv_progress);
                }
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                convertView.setTag(holder);
            } else if (messageType == Type.VOICE) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.voiceLengthView = (TextView)convertView.findViewById(R.id.tv_voice_message);
                holder.voicePlayingView = (ImageView)convertView.findViewById(R.id.iv_voice_play);
                holder.voiceDurTextView = (TextView)convertView
                        .findViewById(R.id.tv_voice_message_duration);
                holder.unreadDot = (ImageView)convertView.findViewById(R.id.iv_unread_dot);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                if(message.getDirect() == Direction.SEND){
                    if(voiceSendItemBgResId != INVALID_RESID){
                        View voiceView = convertView.findViewById(R.id.rl_voice_message);
                        voiceView.setBackgroundResource(voiceSendItemBgResId);
                    }
                }else{
                    if(voiceRecvItemBgResId != INVALID_RESID){
                        View voiceView = convertView.findViewById(R.id.rl_voice_message);
                        voiceView.setBackgroundResource(voiceRecvItemBgResId);
                    }
                }
                convertView.setTag(holder);
            } else if (messageType == Type.VIDEO) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.imgView = (ImageView)convertView.findViewById(R.id.iv_img);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                if (message.getDirect() == Direction.SEND) {
                    holder.progressTextView = (TextView)convertView.findViewById(R.id.tv_progress);
                }else {
                    holder.downloadPb = (ProgressBar)convertView.findViewById(R.id.pb_download);
                }
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                holder.videoPlayView = (ImageView)convertView.findViewById(R.id.iv_video_play);
                convertView.setTag(holder);
            } else if (messageType == Type.RICHTEXT) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.imgView = (ImageView)convertView.findViewById(R.id.iv_img);
                holder.titleTextView = (TextView)convertView.findViewById(R.id.tv_title);
                holder.descriptionTextView = (TextView)convertView
                        .findViewById(R.id.tv_description);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.urlTextView = (TextView)convertView.findViewById(R.id.tv_url);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                if (message.getDirect() == Direction.SEND) {
                    holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                    holder.progressTextView = (TextView)convertView.findViewById(R.id.tv_progress);
                    if(richTextSendItemBgResId != INVALID_RESID){
                        View richTextView =  convertView.findViewById(R.id.ll_rich);
                        richTextView.setBackgroundResource(richTextSendItemBgResId);
                    }
                }
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                convertView.setTag(holder);
            } else if (messageType == Type.EVALUATION) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.containLayout = (LinearLayout)convertView.findViewById(R.id.ll_contain);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_from);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                holder.evaluateTitle = (TextView)convertView.findViewById(R.id.tv_evaluate_title);
                holder.evaluateButton = (TextView)convertView.findViewById(R.id.tv_submit_button);
                convertView.setTag(holder);
            } else if(messageType == Type.NOTIFICATION){
                holder = new ViewHolder();
                holder.contentView = (TextView)convertView.findViewById(R.id.tv_message);
                holder.cancelView = (TextView)convertView.findViewById(R.id.tv_cancel);
                convertView.setTag(holder);
            } else if (messageType == Type.FILE) {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.titleTextView = (TextView)convertView.findViewById(R.id.tv_file_title);
                holder.fileSizeTextView = (TextView)convertView.findViewById(R.id.tv_file_size);
                holder.fileMessageContain = (RelativeLayout)convertView.findViewById(R.id.rl_file_message);
                holder.downloadFileView = (ImageView)convertView.findViewById(R.id.iv_download);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_file_download);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.downloadProgressTextview = (TextView)convertView.findViewById(R.id.tv_download_progress);
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                if(message.getDirect() == Direction.SEND){
                    if(voiceSendItemBgResId != INVALID_RESID){
                        View voiceView = convertView.findViewById(R.id.rl_file_message);
                        voiceView.setBackgroundResource(fileSendItemBgResId);
                    }
                }else{
                    if(voiceRecvItemBgResId != INVALID_RESID){
                        View voiceView = convertView.findViewById(R.id.rl_file_message);
                        voiceView.setBackgroundResource(fileRecvItemBgResId);
                    }
                }
                convertView.setTag(holder);
            } else {
                holder = new ViewHolder();
                holder.photoView = (JXCircleImageView)convertView.findViewById(R.id.iv_photo);
                holder.contentView = (TextView)convertView.findViewById(R.id.tv_message);
                if (message.getDirect() == Direction.SEND) {
                    if (textSendItemBgResId != INVALID_RESID) {
                        holder.contentView.setBackgroundResource(textSendItemBgResId);
                    }
                } else {
                    if (textRecvItemBgResId != INVALID_RESID) {
                        holder.contentView.setBackgroundResource(textRecvItemBgResId);
                    }
                }
                holder.usernameTextView = (TextView)convertView.findViewById(R.id.tv_username);
                holder.sendFailedImageView = (ImageView)convertView
                        .findViewById(R.id.iv_send_failed);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.pb_sending);
                holder.timestampTextView = (TextView)convertView.findViewById(R.id.tv_timestamp);
                holder.delCheckBox = (CheckBox)convertView.findViewById(R.id.cb_del);
                convertView.setTag(holder);

            }
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        if(messageType != Type.NOTIFICATION){
            // 设置默认头像
            if (message.getDirect() == Direction.RECEIVE) {
                if (message.fromRobot()) {
                    if (TextUtils.isEmpty(JXImManager.McsUser.getInstance().getAgentHeadImg())) {
                        holder.photoView.setImageResource(R.drawable.jx_ic_chat_robot);
                    } else {
                        Glide.with(context)
                                .load(JXImManager.McsUser.getInstance().getAgentHeadImg())
                                .placeholder(R.drawable.jx_ic_chat_agent).into(holder.photoView);
                    }
                } else {
                    if (TextUtils.isEmpty(JXImManager.McsUser.getInstance().getAgentHeadImg())) {
                        holder.photoView.setImageResource(R.drawable.jx_ic_chat_agent);
                    } else {
                        Glide.with(context)
                                .load(JXImManager.McsUser.getInstance().getAgentHeadImg())
                                .placeholder(R.drawable.jx_ic_chat_agent).into(holder.photoView);
                    }
                }
            }else{
                Glide.with(context).load(JXConstants.SEND_MSG_USER_AVATAR_URL).fitCenter().crossFade().error(R.drawable.jx_ic_chat_default_contact)
                        .into(holder.photoView);
//                holder.photoView.setImageResource(R.drawable.jx_ic_chat_default_contact);
            }
            holder.photoView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(chatFragmentListener != null){
                        chatFragmentListener.onMessageAvatarClick(message);
                    }
                }
            });
            if (message.getDirect() == Direction.RECEIVE && holder.usernameTextView != null) {
                if (message.fromRobot()) {
                    if (TextUtils.isEmpty(JXUiHelper.getInstance().getRobotNickname())) {
                        holder.usernameTextView.setText(robotString);
                    }else {
                        holder.usernameTextView.setText(JXUiHelper.getInstance().getRobotNickname());
                    }
                } else {
                    // 设置username
                    Object typeObject = message.getAttributes().get(JXMessageAttribute.TYPE.value());
                    if (typeObject != null) {
                        if (message.getType() == Type.EVALUATION) {
                            holder.usernameTextView.setText("");
                        } else {
                            holder.usernameTextView.setText(adminString);
                        }
                    } else {
                        holder.usernameTextView.setText(message.getFrom());
                    }
                }
            }

            // 时间戳显示控制
            if (holder.timestampTextView != null) {
                if (position == 0) {
                    holder.timestampTextView.setVisibility(View.VISIBLE);
                    holder.timestampTextView
                    .setText(DateUtil.formatDatetime(message.getDate(), "yyyy-MM-dd HH:mm:ss"));
                } else if (position > 0) {
                    long currentTime = message.getDate();
                    long lastTime = list.get(position - 1).getDate();
                    if (currentTime - lastTime > DEFAULT_TIME_DIVIDE) {
                        holder.timestampTextView.setVisibility(View.VISIBLE);
                        holder.timestampTextView.setText(
                                DateUtil.formatDatetime(message.getDate(), "yyyy-MM-dd HH:mm:ss"));
                    } else {
                        holder.timestampTextView.setVisibility(View.GONE);
                    }
                    lastTime = currentTime;
                }
            }
        }

        switch (messageType) {
            case TEXT:
                TextMessage textMessage = (TextMessage)message;
                final List<String> imageUrlList = JXCommonUtils.getImgStr(textMessage.getContent());
                // 设置内容
                if (JXCommonUtils.isHtmlText(textMessage.getContent())) {
                    MyImageGetter getter = imageTagCacheArray.get(message.getMessageId());
                    if (getter == null) {
                        getter = new MyImageGetter();
                        imageTagCacheArray.put(message.getMessageId(), getter);
                    }
                    JXLinkMovementMethod movementMethod = JXLinkMovementMethod.getInstance();
                    if(isDelMode()){
                        movementMethod.setInterrupt(true);
                        holder.contentView.setFocusable(false);
                    }else{
                        movementMethod.setInterrupt(false);
                        holder.contentView.setFocusable(true);
                    }
                    String htmlContent = textMessage.getContent();
                    if (!TextUtils.isEmpty(htmlContent)){
                        htmlContent = htmlContent.replace("</p>", "");
                    }
                    holder.contentView.setMovementMethod(movementMethod);
                    Spanned spanned = Html.fromHtml(htmlContent, getter, null);
                    holder.contentView.setText(spanned);
                } else {
                    String htmlContent = textMessage.getContent();
                    if (!TextUtils.isEmpty(htmlContent)){
                        htmlContent = htmlContent.replace("</p>", "");
                    }
                    Spannable span = JXSmileUtils.getSmiledText(context, htmlContent);
                    try {
                        List<String> urls = getUrlsInContent(htmlContent);
                        for (String url : urls) {
                            int index = htmlContent.indexOf(url);
                            MyURLSpan myURLSpan = new MyURLSpan(url);
                            span.setSpan(myURLSpan, index,
                                    index + url.length(),
                                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        }
                    }catch (Exception e){
                        JXLog.e("JXChatAdatper , set span exception",e);
                    }
                    holder.contentView.setMovementMethod(JXLinkMovementMethod.getInstance());
                    holder.contentView.setText(span, BufferType.SPANNABLE);
                    Linkify.addLinks(holder.contentView, Linkify.PHONE_NUMBERS);
                    if (isDelMode()) {
                        holder.contentView.setFocusable(false);
                    } else {
                        holder.contentView.setFocusable(true);
                    }
                }
                if (mIsDelMode) {
                    holder.contentView.setDuplicateParentStateEnabled(true);
                }else {
                    holder.contentView.setDuplicateParentStateEnabled(false);
                }
                holder.contentView.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (mIsDelMode) {
                            view.setItemChecked(position, !view.isItemChecked(position));
                        }else {
                            if (imageUrlList.size() == 1) {
                                if (!imageUrlList.get(0).endsWith(".gif")) {
                                    MyImageGetter getter = imageTagCacheArray
                                            .get(message.getMessageId());
                                    getter.getDrawable(imageUrlList.get(0));
                                    Intent intent = new Intent(context, JXPhotoActivity.class);
                                    intent.putExtra("html", imageUrlList.get(0));
                                    context.startActivity(intent);
                                }else {
                                    Intent intent = new Intent(context, JXWebViewActivity.class);
                                    intent.putExtra("gifUrl", imageUrlList.get(0));
                                    context.startActivity(intent);
                                }
                            } else if (imageUrlList.size() > 1) {
                                showHtmlImgList(imageUrlList, message);
                            }
                        }
                    }
                });
                holder.contentView.setOnLongClickListener(new MessageItemLongClick(message));
                break;
            case IMAGE:
                ImageMessage imageMessage = (ImageMessage)message;
                handleImageView(position, holder, imageMessage);
                break;
            case VOICE:
                handleVoiceView(position, holder, message);
                break;
            case VIDEO:
                VideoMessage videoMessage = (VideoMessage)message;
                handleVideoView(holder, videoMessage,position);
                break;
            case RICHTEXT:
                RichTextMessage richTextMessage = (RichTextMessage)message;
                handleRichMessage(position, holder, richTextMessage);
                break;
            case EVALUATION:
                handleEvaluationMessage(position, holder, message);
                break;
            case NOTIFICATION:
                TextMessage notificationMsg = (TextMessage)message;
                holder.contentView.setText(notificationMsg.getContent());
                String uiType = message.getStringAttribute(JXUiHelper.JX_ATTR_TYPE, null);
                if(JXUiHelper.JX_ATTR_TYPE_CANCEL_WAIT.equals(uiType)){
                    if(holder.cancelView != null){
                        holder.cancelView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
                        holder.cancelView.getPaint().setAntiAlias(true);
                        holder.cancelView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(chatFragmentListener != null){
                                    if(chatFragmentListener.onMessageItemClick(message)){
                                        return;
                                    }
                                }
                            }
                        });
                    }
                }
                break;
            case LOCATION:
                break;
            case VCARD:
                break;
            case FILE:
                handleFileMessage(position, holder, message);
                break;
            case VOICE_CALL:
                break;
            case VIDEO_CALL:
                break;
            case CHATSTATE:
                break;
            default:
                handleUnknowMessage(position, holder, message);
                break;
        }
        if(messageType != Type.NOTIFICATION){
            if (message.getDirect() == JXMessage.Direction.SEND && holder.sendFailedImageView != null) {
                if (message.getStatus() == JXMessage.Status.FAILED) {
                    holder.sendFailedImageView.setVisibility(View.VISIBLE);
                    if (holder.progressBar != null) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (holder.progressBar != null) {
                        if (message.getStatus() == JXMessage.Status.SENDING) {
                            holder.progressBar.setVisibility(View.VISIBLE);
                        } else {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }
                    holder.sendFailedImageView.setVisibility(View.GONE);
                }
            }

            if (mIsDelMode) {
                holder.delCheckBox.setVisibility(View.VISIBLE);
            } else {
                holder.delCheckBox.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    /**
     * 释放资源
     */
    public void resetResource() {
        if (drawabelCache != null) {
            for (int i = 0; i < drawabelCache.size(); i++) {
                GlideBitmapDrawable bitmapDrawable = drawabelCache.valueAt(i);
                if (bitmapDrawable != null) {
                    bitmapDrawable.setCallback(null);
                }
            }
            drawabelCache.clear();
        }
        if (imageTagCacheArray != null) {
            imageTagCacheArray.clear();
        }
    }

    /**
     * 删除消息时同时删除对应的缓存
     * 
     * @param messageIdList
     */
    public void removeImageTagCache(List<String> messageIdList) {
        if (messageIdList != null && imageTagCacheArray != null) {
            for (int i = 0; i < messageIdList.size(); i++) {
                imageTagCacheArray.remove(messageIdList.get(i));
            }
        }
    }

    public static ArrayMap<String, GlideBitmapDrawable> drawabelCache = new ArrayMap<String, GlideBitmapDrawable>();

    ArrayMap<String, MyImageGetter> imageTagCacheArray = new ArrayMap<String, MyImageGetter>();

    private class MyImageGetter implements ImageGetter {

        public GlideBitmapDrawable mDrawable;

        @Override
        public Drawable getDrawable(final String source) {
            String imgurl = source;
            if(!imgurl.startsWith("http")){
                imgurl = "http://jiaxin.faqrobot.org" + source;
            }else if (imgurl.startsWith("https")) {
                    imgurl = imgurl.replace("https", "http");
            }
            final String url = imgurl;
            mDrawable = drawabelCache.get(url);
            if (mDrawable != null) {
                int drawableWidth = mDrawable.getIntrinsicWidth();
                int drawableHeight = mDrawable.getIntrinsicHeight();
                int inSampleSize = JXCommonUtils.calculateInSampleSize(
                        drawableWidth, drawableHeight, mScreenWidth,
                        mScreenWidth);
                if (inSampleSize == 1 && drawableWidth < 120) {
                    drawableWidth = drawableWidth * 2;
                    drawableHeight = drawableHeight * 2;
                }
                mDrawable.setBounds(0, 0, drawableWidth / inSampleSize,
                        drawableHeight / inSampleSize);
            } else {
                Glide.with(context).load(url).asBitmap().fitCenter()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                synchronized (drawabelCache) {
                                    GlideBitmapDrawable gDrawable = new GlideBitmapDrawable(
                                            context.getResources(), bitmap);
                                    if (drawabelCache.get(url) != null) {
                                        return;
                                    }
                                    drawabelCache.put(url, gDrawable);
                                    notifyDataSetChanged();
                                }
                            }
                        });
            }
            return mDrawable;
        }

    }

    @SuppressLint("InflateParams")
    private View createViewByMessage(JXMessage message, int position) {
        switch (message.getType()) {
            case TEXT:
                if (message.getDirect() == JXMessage.Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_chat_send_item, null);
                } else if (message.getDirect() == Direction.RECEIVE) {
                    return mInflater.inflate(R.layout.jx_chat_rev_item, null);
                }
            case IMAGE:
                if (message.getDirect() == JXMessage.Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_chat_sendimg_item, null);
                } else if (message.getDirect() == Direction.RECEIVE) {
                    return mInflater.inflate(R.layout.jx_chat_revimage_item, null);
                }
            case VOICE:
                if (message.getDirect() == Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_chat_sendvoice_item, null);
                } else {
                    return mInflater.inflate(R.layout.jx_chat_revvoice_item, null);
                }
            case VIDEO:
                if (message.getDirect() == Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_chat_sendimg_item, null);
                } else {
                    return mInflater.inflate(R.layout.jx_chat_revimage_item, null);
                }
            case RICHTEXT:
                if (message.getDirect() == Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_item_rich_message, null);
                }
            case EVALUATION:
                if (message.getDirect() == Direction.RECEIVE) {
                    return mInflater.inflate(R.layout.jx_chat_rev_satisfaction_item, null);
                }
            case NOTIFICATION:
                String uiType = message.getStringAttribute(JXUiHelper.JX_ATTR_TYPE, null);
                if(JXUiHelper.JX_ATTR_TYPE_CANCEL_WAIT.equals(uiType)){
                    return mInflater.inflate(R.layout.jx_chat_notification_cancel_item, null);
                }else{
                    return mInflater.inflate(R.layout.jx_chat_notification_item, null);
                }
            case FILE:
                if (message.getDirect() == Direction.SEND) {
                    return mInflater.inflate(R.layout.jx_chat_sendvoice_item, null);
                } else {
                    return mInflater.inflate(R.layout.jx_chat_revfile_item, null);
                }
            default:
                return mInflater.inflate(R.layout.jx_chat_rev_item, null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(list.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return 14;
    }

    private int getItemViewType(JXMessage message) {
        if (message.getDirect() == Direction.SEND) {
            if (message.getType() == Type.TEXT) {
                return MESSAGE_TYPE_SEND_TXT;
            } else if (message.getType() == Type.IMAGE) {
                return MESSAGE_TYPE_SEND_IMAGE;
            } else if (message.getType() == Type.VOICE) {
                return MESSAGE_TYPE_SEND_VOICE;
            } else if (message.getType() == Type.VIDEO) {
                return MESSAGE_TYPE_SEND_VIDEO;
            } else if (message.getType() == Type.RICHTEXT) {
                return MESSAGE_TYPE_SEND_RICHTEXT;
            } else if(message.getType() == Type.NOTIFICATION){
                String uiType = message.getStringAttribute(JXUiHelper.JX_ATTR_TYPE, null);
                if(JXUiHelper.JX_ATTR_TYPE_CANCEL_WAIT.equals(uiType)){
                    return MESSAGE_TYPE_SEND_NOTICE_CANCEL_WAIT;
                }else{
                    return MESSAGE_TYPE_SEND_NOTICE;
                }
            }else if (message.getType() == Type.FILE) {
                return MESSAGE_TYPE_SEND_FILE;
            }
        } else {
            if (message.getType() == Type.TEXT) {
                return MESSAGE_TYPE_RECV_TXT;
            } else if (message.getType() == Type.IMAGE) {
                return MESSAGE_TYPE_RECV_IMAGE;
            } else if (message.getType() == Type.VOICE) {
                return MESSAGE_TYPE_RECV_VOICE;
            } else if (message.getType() == Type.VIDEO) {
                return MESSAGE_TYPE_RECV_VIDEO;
            } else if (message.getType() == Type.EVALUATION) {
                return MESSAGE_TYPE_RECV_EVALUATION;
            }else if (message.getType() == Type.FILE) {
                return MESSAGE_TYPE_RECV_FILE;
            }
        }
        return MESSAGE_TYPE_RECV_TXT;
    }

    /**
     * 文本item的holder
     */
    static class ViewHolder {
        // public hold
        JXCircleImageView photoView;

        // text message hold
        TextView contentView;

        // image message hold
        ImageView imgView;
        
        ImageView videoPlayView;

        ProgressBar progressBar;
        
        ProgressBar downloadPb;

        // voice message hold
        ImageView voicImageView;

        TextView usernameTextView;

        // voice message hold
        TextView voiceLengthView;

        ImageView voicePlayingView;

        TextView voiceDurTextView;

        ImageView unreadDot;

        // satisfication message hold
        TextView evaluateTitle;

        TextView evaluateButton;

        // file message hold
        TextView fileSizeTextView;

        ImageView downloadFileView;
        
        TextView downloadProgressTextview;
        
        RelativeLayout fileMessageContain;
        //
        LinearLayout containLayout;

        ImageView sendFailedImageView;

        TextView titleTextView;

        TextView descriptionTextView;

        TextView urlTextView;

        TextView timestampTextView;

        TextView progressTextView;

        CheckBox delCheckBox;

        TextView cancelView;
    }

    /**
     * 处理图片消息
     * 
     * @param position
     * @param holder
     * @param imageMessage
     */
    private void handleImageView(final int position, ViewHolder holder,
            final ImageMessage imageMessage) {
        ImageView imgView = holder.imgView;
        String path = imageMessage.getLocalUrl();
        String thumbnailPath = imageMessage.getThumbnailUrl();
        Log.e("本地图片地址：",path);
//        Log.e("网络图片地址：",thumbnailPath);
        if (imageMessage.getStatus() != Status.DOWNLOADING){
            if (path!=null&&fileIsExists(path)) {
                if (path.endsWith(".gif")) {
                    scalleLayout(imgView, path);
                    Glide.with(context).load(new File(path)).asGif().centerCrop().crossFade().into(imgView);
                }else {
                    scalleLayout(imgView, path);
                    Glide.with(context).load(new File(path)).centerCrop().crossFade().into(imgView);
                }
            }else if(thumbnailPath!=null&&fileIsExists(thumbnailPath)){
                scalleLayout(imgView, thumbnailPath);
                Glide.with(context).load(new File(thumbnailPath)).centerCrop().crossFade().into(imgView);
            } else {
//           scalleLayout(imgView, null);
//           Glide.with(context).load(new File(path))
//                   .placeholder(R.drawable.jx_ic_photo_default).fitCenter().crossFade()
//                   .into(imgView);
            }
        }else{
            imgView.setImageResource(R.drawable.jx_ic_photo_default);
        }


        if (imageMessage.getDirect() == Direction.SEND) {
            if (imageMessage.getStatus() == JXMessage.Status.FAILED
                    || imageMessage.getStatus() != JXMessage.Status.SENDING) {
                holder.progressTextView.setText("");
            } else {
                // 发送进度
                int progress = imageMessage.getProgress();
                if (progress != 0 && progress != 100) {
                    holder.progressTextView.setText(progress + "%");
                } else {
                    holder.progressTextView.setText("");
                }
            }
        }else{
            switch (imageMessage.getStatus()) {
                case DOWNLOADING:
                    holder.sendFailedImageView.setVisibility(View.GONE);
                    break;
                case DELIVERED:
                    holder.sendFailedImageView.setVisibility(View.GONE);
                    break;
                case FAILED:
                    holder.sendFailedImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                    return;
                }
                if(chatFragmentListener != null){
                    if(chatFragmentListener.onMessageItemClick(imageMessage)){
                        return;
                    }
                   context.startActivity(new Intent(context,JXPhotoActivity.class)
                            .putExtra("MessageID", imageMessage.getMessageId())
                            .putExtra("LocalUrl", imageMessage.getLocalUrl())
                            .putExtra("Direction", imageMessage.getDirect().toString()));
                }
            }
        });
        imgView.setOnLongClickListener(new MessageItemLongClick(imageMessage));
    }

    /**
     * 处理语音消息
     * 
     * @param holder
     * @param message
     */
    private void handleVoiceView(final int position, ViewHolder holder, JXMessage message) {
        final VoiceMessage voiceMessage = (VoiceMessage)message;
        int voiceDuration = voiceMessage.getDuration();
        voiceDuration = voiceDuration / 1000;
        voiceDuration = voiceDuration< 1 ? 1 :  voiceDuration;
        holder.voiceDurTextView.setText(String.valueOf(voiceDuration) + "''");
        if (getVoicLength(voiceDuration) != null) {
            holder.voiceLengthView.setText(getVoicLength(voiceDuration));
        }
        holder.voiceLengthView.setOnClickListener(new VoiceHanlderListener(message,
                holder.unreadDot, holder.voicePlayingView, this, context, position));
        holder.voiceLengthView.setOnLongClickListener(new MessageItemLongClick(message));
        if (message.getDirect() == Direction.RECEIVE) {
            if (message.isRead()) {
                holder.unreadDot.setVisibility(View.INVISIBLE);
            } else {
                holder.unreadDot.setVisibility(View.VISIBLE);
            }
        }

        if (JXUiHelper.getInstance().getPlayingMsgID() != null
                && JXUiHelper.getInstance().getPlayingMsgID().equals(message.getMessageId())
                && JXVoicePlayListener.isPlaying) {
            AnimationDrawable voiceAnimation;
            if (voiceMessage.getDirect() == Direction.SEND) {
                holder.voicePlayingView.setImageResource(R.anim.jx_voice_send);
            } else {
                holder.voicePlayingView.setImageResource(R.anim.jx_voic_recive);
            }
            voiceAnimation = (AnimationDrawable)holder.voicePlayingView.getDrawable();
            voiceAnimation.start();
        } else {
            if (voiceMessage.getDirect() == Direction.SEND) {
                holder.voicePlayingView.setImageResource(R.drawable.voice_send_playing_a3);
            } else {
                holder.voicePlayingView.setImageResource(R.drawable.voice_rec_playing_a3);
            }
        }
    }

    /**
     * 处理视频消息
     * 
     * @param holder
     * @param videoMessage
     * @param position 
     */
    private void handleVideoView(final ViewHolder holder, final VideoMessage videoMessage, final int position) {
        holder.videoPlayView.setVisibility(View.VISIBLE);
        final String path = videoMessage.getLocalUrl();
        String thumbnailPath = videoMessage.getThumbnailUrl();
        if (thumbnailPath != null && fileIsExists(thumbnailPath)) {
            scalleLayout(holder.imgView, thumbnailPath);
            Glide.with(context).load(new File(thumbnailPath)).fitCenter().crossFade()
                    .into(holder.imgView);
        } else if(path != null && fileIsExists(path)){
            scalleLayout(holder.imgView, path);
            Glide.with(context).load(new File(path)).placeholder(R.drawable.jx_ic_photo_default)
                    .fitCenter().crossFade().into(holder.imgView);
        }else {
            scalleLayout(holder.imgView, null);
            Glide.with(context).load(new File(path)).placeholder(R.drawable.jx_ic_photo_default)
            .fitCenter().crossFade().into(holder.imgView);
        }
        if (videoMessage.getDirect() == Direction.SEND) {
            if (videoMessage.getStatus() == JXMessage.Status.FAILED
                    || videoMessage.getStatus() != JXMessage.Status.SENDING) {
                holder.progressTextView.setText("");
            } else {
                // 发送进度
                int progress = videoMessage.getProgress();
                if (progress != 0 && progress != 100) {
                    holder.progressTextView.setText(progress + "%");
                } else {
                    holder.progressTextView.setText("");
                }
            }
        } else {
            switch (videoMessage.getStatus()) {
                case DOWNLOADING:
                    holder.sendFailedImageView.setVisibility(View.GONE);
                    holder.downloadPb.setVisibility(View.VISIBLE);
                    holder.videoPlayView.setVisibility(View.GONE);
                    break;
                case DELIVERED:
                    holder.sendFailedImageView.setVisibility(View.GONE);
                    holder.downloadPb.setVisibility(View.GONE);
                    holder.videoPlayView.setVisibility(View.VISIBLE);
                    break;
                case FAILED:
                    holder.sendFailedImageView.setVisibility(View.VISIBLE);
                    holder.downloadPb.setVisibility(View.GONE);
                    holder.videoPlayView.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.sendFailedImageView.setVisibility(View.GONE);
                    holder.downloadPb.setVisibility(View.GONE);
                    holder.videoPlayView.setVisibility(View.VISIBLE);
                    break;
            }
        }
        holder.imgView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                    return;
                }
                if (path != null && fileIsExists(path)) {
                    openFile(path,"video");
                }else {
                    if (holder.downloadPb != null ) {
                        holder.downloadPb.setVisibility(View.VISIBLE);
                        holder.videoPlayView.setVisibility(View.GONE);
                    }
                    JXImManager.Message.getInstance().download(videoMessage.getMessageId());
                }
            }
        });
        holder.imgView.setOnLongClickListener(new MessageItemLongClick(videoMessage));
    }

    /**
     * 处理图文消息
     */
    private void handleRichMessage(final int position, ViewHolder holder,
            final RichTextMessage message) {
        holder.titleTextView.setText(message.getTitle());
        holder.descriptionTextView.setText(message.getContent());
        boolean localFile = message.isLocalFile();
        if (localFile) {
            String path = message.getLocalImageUrl();
            String thumbnailPath = message.getLocalThumbnailUrl();
            if (message.getDirect() == Direction.RECEIVE) {
                Glide.with(context).load(new File(thumbnailPath)).fitCenter().crossFade()
                        .into(holder.imgView);
            } else {
                if (path != null) {
                    Glide.with(context).load(new File(path)).fitCenter().crossFade()
                            .into(holder.imgView);
                } else if (thumbnailPath != null) {
                    Glide.with(context).load(new File(thumbnailPath)).fitCenter().crossFade()
                            .into(holder.imgView);
                }
            }
        } else {
            String remoteImageUrl = message.getRemoteImageUrl();
            if (remoteImageUrl != null) {
                Glide.with(context).load(remoteImageUrl).placeholder(R.drawable.jx_ic_photo_default).fitCenter().crossFade()
                        .into(holder.imgView);
            }
        }
        Linkify.addLinks(holder.titleTextView, Linkify.WEB_URLS | Linkify.PHONE_NUMBERS);
        final String url = message.getUrl();
        holder.urlTextView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG);
        holder.urlTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mIsDelMode) {
                    JXChatAdapter.this.view.setItemChecked(position,
                            !JXChatAdapter.this.view.isItemChecked(position));
                    return;
                }
                String linkUrl = url;
                if (!linkUrl.startsWith("http://")) {
                    linkUrl = "http://" + linkUrl;
                }
                if(chatFragmentListener != null){
                    chatFragmentListener.onMessageItemClick(message);
                }
            }
        });
        if (message.getDirect() == Direction.SEND) {
            if (message.getStatus() == JXMessage.Status.FAILED
                    || message.getStatus() != JXMessage.Status.SENDING) {
                holder.progressTextView.setText("");
            } else {
                // 发送进度
                int progress = message.getProgress();
                if (progress != 0 && progress != 100) {
                    holder.progressTextView.setText(progress + "%");
                } else {
                    holder.progressTextView.setText("");
                }
            }
        }
        holder.imgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                    return;
                }
                if (!TextUtils.isEmpty(url)){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    context.startActivity(intent);
                }

            }
        });
        holder.imgView.setOnLongClickListener(new MessageItemLongClick(message));
    }

    /**
     * 处理邀请评价消息
     * 
     * @param message
     * @param holder
     * @param position
     */
    public void handleEvaluationMessage(final int position, ViewHolder holder,
            final JXMessage message) {
        if (message.hasEvaluated()) {
            holder.evaluateButton.setText(context.getString(R.string.jx_evaluated));
            holder.evaluateButton.setBackgroundResource(R.drawable.jx_bg_evaluatebtn_off);
            holder.evaluateButton.setFocusable(false);
        } else {
            holder.evaluateButton.setText(context.getString(R.string.jx_evaluation));
            holder.evaluateButton.setBackgroundResource(R.drawable.jx_bg_button_shape_blue);
        }
        if (satisfication != null ) {
            if (satisfication.isShowHint()) {
                holder.evaluateTitle.setText(satisfication.getHint());
            }else{
                holder.evaluateTitle.setText(context.getString(R.string.jx_satisfaction_evaluate));
            }
        }else{
            holder.evaluateTitle.setText(context.getString(R.string.jx_evaluate_thanks_msg));
        }
        holder.evaluateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                    return;
                }
                if (message.hasEvaluated()) {
                    return;
                }
                if(chatFragmentListener != null){
                    chatFragmentListener.onMessageItemClick(message);
                }
            }
        });
        holder.containLayout.setOnLongClickListener(new MessageItemLongClick(message));
    }

    private void handleFileMessage(final int position, final ViewHolder holder, JXMessage message) {
        final FileMessage fileMessage = (FileMessage)message;
        holder.titleTextView.setText(fileMessage.getFilename());
        holder.fileSizeTextView.setText(JXCommonUtils.convertFileSize(fileMessage.getFilesize()));
        if (fileMessage.getDirect() == Direction.RECEIVE) {
            if (fileMessage.getStatus() == Status.DELIVERED) {
                if (fileIsExists(fileMessage.getLocalUrl())) {
                    holder.downloadProgressTextview.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.downloadFileView.setImageResource(R.drawable.jx_ic_file_downloading);
                }else {
                    holder.downloadProgressTextview.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                    holder.downloadFileView.setImageResource(R.drawable.jx_bg_download_file);
                }
            } else if (fileMessage.getStatus() == Status.FAILED) {
                holder.downloadProgressTextview.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.downloadFileView.setImageResource(R.drawable.jx_bg_download_file);
            } else if (fileMessage.getStatus() == Status.DOWNLOADING) {
                // 下载进度
                holder.downloadProgressTextview.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.downloadFileView.setImageResource(R.drawable.jx_ic_file_downloading);
                int progress = fileMessage.getProgress();
                if (progress != 0 && progress != 100) {
                    holder.progressBar.setProgress(progress);
                    holder.downloadProgressTextview.setText(progress + "%");
                } else {
                    holder.downloadProgressTextview.setText("");
                }
            }
        }
        holder.fileMessageContain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                    return;
                }
                if(chatFragmentListener != null){
                    if(chatFragmentListener.onMessageItemClick(fileMessage)){
                        return;
                    }
                    if (fileIsExists(fileMessage.getLocalUrl())) {
                        openFile(fileMessage.getLocalUrl() , fileMessage.getFilename());
                    } else {
                        Date expiredTime = new Date(fileMessage.getExpiredTime());
                        Date nowTime = new Date(System.currentTimeMillis());
                        if (nowTime.before(expiredTime)) {
                            holder.downloadProgressTextview.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.downloadFileView.setImageResource(R.drawable.jx_ic_file_downloading);
                            JXImManager.Message.getInstance().download(fileMessage.getMessageId());
                        }else {
                            JXCommonUtils.showToast(context, context.getString(R.string.jx_cannot_download_overdue));
                        }
                    }
                }
            }
        });
        holder.fileMessageContain.setOnLongClickListener(new MessageItemLongClick(fileMessage));
    }
    
    public void handleUnknowMessage(final int position, final ViewHolder holder, JXMessage message){
        holder.contentView.setText(context.getString(R.string.jx_unknow_type_message));
        if (mIsDelMode) {
            holder.contentView.setDuplicateParentStateEnabled(true);
        }else {
            holder.contentView.setDuplicateParentStateEnabled(false);
        }
        holder.contentView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mIsDelMode) {
                    view.setItemChecked(position, !view.isItemChecked(position));
                }
            }
        });
        holder.contentView.setOnLongClickListener(new MessageItemLongClick(message));
    }

    public void refreshMessageList(List<JXMessage> messages) {
        list.clear();
        list.addAll(messages);
        notifyDataSetChanged();
    }

    public void addMessageList(List<JXMessage> messages) {
        Collections.reverse(messages);
        list.addAll(0, messages);
        notifyDataSetChanged();
    }
    /**
     * @Description: 根据语音时长来，得到相应的textview长度
     */
    public String getVoicLength(int duration) {
        StringBuilder builder = new StringBuilder();
        builder.append("       ");
        for (int i = 0; i < duration; i++) {
            builder.append("   ");
        }
        return builder.toString();
    }

    private class VoiceHanlderListener extends JXVoicePlayListener {

        private int position;
        private JXMessage message;

        public VoiceHanlderListener(JXMessage message, ImageView unreadDot, ImageView voicePlaying,
                JXChatAdapter adapter, Context context, int position) {
            super(message, unreadDot, voicePlaying, adapter, context);
            this.position = position;
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            if (mIsDelMode) {
                view.setItemChecked(position, !view.isItemChecked(position));
                return;
            }
            if(chatFragmentListener != null){
                if(chatFragmentListener.onMessageItemClick(message)){
                    return;
                }
            }
            super.onClick(v);
        }
    }

    private boolean mIsDelMode = false;

    public boolean isDelMode() {
        return this.mIsDelMode;
    }

    /**
     * 设置删除模式
     * @param isDelMode 是否为删除模式
     */
    public void setDelChoiceMode(boolean isDelMode) {
        this.mIsDelMode = isDelMode;
        if (isDelMode) {
            view.clearChoices();
            view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else {
            view.setChoiceMode(ListView.CHOICE_MODE_NONE);
        }
        notifyDataSetChanged();
    }

    public JXChatFragmentListener chatFragmentListener = null;
    public void setJXChatFragmentListener(JXChatFragmentListener chatFragmentListener){
        this.chatFragmentListener = chatFragmentListener;
    }

    /**
     * 消息item长按事件
     */
    private class MessageItemLongClick implements View.OnLongClickListener {

        private JXMessage jxMessage;

        MessageItemLongClick(JXMessage jxMessage) {
            this.jxMessage = jxMessage;
        }

        @Override
        public boolean onLongClick(View v) {
            if (mIsDelMode)
                return false;
            if(chatFragmentListener != null){
                return chatFragmentListener.onMessageItemLongClick(jxMessage);
            }
            return false;
        }
    }

    static final int INVALID_RESID = -1;

    int textSendItemBgResId = INVALID_RESID;

    int imageSendItemBgResId = INVALID_RESID;

    int richTextSendItemBgResId = INVALID_RESID;

    int voiceSendItemBgResId = INVALID_RESID;

    int textRecvItemBgResId = INVALID_RESID;

    int imageRecvItemBgResId = INVALID_RESID;

    int richTextRecvItemBgResId = INVALID_RESID;

    int voiceRecvItemBgResId = INVALID_RESID;
    
    int fileRecvItemBgResId = INVALID_RESID;
    
    int fileSendItemBgResId = INVALID_RESID;

    /**
     * 设置消息Item的背景
     * @param type 消息类型
     * @param resId 消息Item的资源ID
     */
    public void setMessageItemBgDrawable(JXMessage.Type type, JXMessage.Direction direction, @AnyRes int resId){
        if(direction == JXMessage.Direction.SEND){
            if(type == Type.TEXT){
                textSendItemBgResId = resId;
            }else if(type == Type.IMAGE){
                imageSendItemBgResId = resId;
            }else if(type == Type.RICHTEXT){
                richTextSendItemBgResId = resId;
            }else if(type == Type.VOICE){
                voiceSendItemBgResId = resId;
            }else if (type == Type.FILE) {
                fileSendItemBgResId = resId;
            }
        }else{
            if(type == Type.TEXT){
                textRecvItemBgResId = resId;
            }else if(type == Type.IMAGE){
                imageRecvItemBgResId = resId;
            }else if(type == Type.RICHTEXT){
                richTextRecvItemBgResId = resId;
            }else if(type == Type.VOICE){
                voiceRecvItemBgResId = resId;
            }else if (type == Type.FILE) {
                fileRecvItemBgResId = resId;
            }
        }
        
    }
    
    public boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
    
    private void showHtmlImgList(final List<String> image, final JXMessage message) {
        final String[] imgList = new String[image.size()];
        for (int i = 0; i < image.size(); i++) {
            if (!TextUtils.isEmpty(image.get(i))) {
                if (!image.get(i).startsWith("http")) {
                    imgList[i] = "http://jiaxin.faqrobot.org" + image.get(i);
                } else {
                    if (image.get(i).startsWith("https")) {
                        imgList[i] = image.get(i).replace("https", "http");
                    }else{
                        imgList[i] = image.get(i);
                    }
                }
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.setItems(imgList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!imgList[which].endsWith(".gif")) {
                    MyImageGetter getter = imageTagCacheArray.get(message.getMessageId());
                    getter.getDrawable(image.get(which));
                    Intent intent = new Intent(context, JXPhotoActivity.class);
                    intent.putExtra("html", image.get(which));
                    context.startActivity(intent);
                    dialog.dismiss();
                }else{
                    Intent intent = new Intent(context, JXWebViewActivity.class);
                    intent.putExtra("gifUrl", imgList[which]);
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            }
        }).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
    
    /**
     * 是否GIF图片
     *
     * @param filepath 文件路径
     * @return
     */
    public static boolean isGif(String url) {
        if (url.endsWith(".gif")) {
            return true;
        }else{
            return false;
        }
    }
    
    private int screenWidth;
    private int screenHeight;
    
    public void scalleLayout(ImageView view , String filePath){
        int scalleWidth = 0;
        int scalleHeight = 0;
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        
        int maxWidth = 600;
        int maxHeight = 600;
        int minWidth = 220;
        int minHeight = 220;
        
        if (!TextUtils.isEmpty(filePath)) {
            int size[] = getImageSize(filePath);
            if (size.length<2) {
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                layoutParams.width = LayoutParams.WRAP_CONTENT;
                view.setLayoutParams(layoutParams);
                return;
            }

            int outWidth = size[0];
            int outHeight = size[1];
            if (readPictureDegree(filePath)==90 || readPictureDegree(filePath)==270){
                //旋转后的图片要将长款对换，以免图片和包裹框的长款不不一致
                outHeight = size[0];
                outWidth = size[1];
            }else {
                outWidth = size[0];
                outHeight = size[1];
            }
            
            
            if (outWidth / maxWidth > outHeight / maxHeight) {//
                if (outWidth >= maxWidth) {//
                    scalleWidth = maxWidth;
                    scalleHeight =(outHeight * maxWidth / outWidth);
                } else {
                    scalleWidth = outWidth;
                    scalleHeight = outHeight;
                }
                if (outHeight < minHeight) {
                    scalleHeight = minHeight;
                    int width = outWidth * minHeight / outHeight;
                    if (width > maxWidth) {
                        scalleWidth = maxWidth;
                    } else {
                        scalleWidth = width;
                    }
                }
            } else {
                if (outHeight >= maxHeight) {
                    scalleHeight = maxHeight;
                    scalleWidth = (outWidth * maxHeight / outHeight);
                } else {
                    scalleWidth = outWidth;
                    scalleHeight = outHeight;
                }
                if (outWidth < minWidth) {
                    scalleWidth=minWidth;
                    int height = outHeight * minWidth / outWidth;
                    if (height > maxHeight) {
                        scalleHeight = maxHeight;
                    } else {
                        scalleHeight = height;
                    }
                }
            }
            
            layoutParams.height = scalleHeight;
            layoutParams.width = scalleWidth;
        }else{
            layoutParams.height = 240;
            layoutParams.width = 320;
        }
        view.setLayoutParams(layoutParams);
    }
    
    public static int[] getImageSize(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize=1;
        BitmapFactory.decodeFile(path, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        int[] imgsize = new int[2];
        imgsize[0] = width;
        imgsize[1] = height;
        return imgsize;
    }

    /**
     * 打开文件
     * @param file
     */ 
    private void openFile(String filePath, String filename){ 
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(); 
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        //设置intent的Action属性 
        intent.setAction(Intent.ACTION_VIEW); 
        //获取文件file的MIME类型 
        String type = JXCommonUtils.getMIMEType(file); 
        //设置intent的data和Type属性。 
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type); 
        //跳转 
        if(intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(intent);   
        }else {
            JXCommonUtils.showToast(context, context.getString(R.string.jx_cannot_open_this_type_file)+filename);
        }
    }

    private class MyURLSpan extends ClickableSpan {

        private String url;

        public MyURLSpan(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View arg0) {
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                JXLog.e("JXChatAdatper , URLSpan Actvity was not found for intent, " + intent.toString(),e);
            }
        }

    }

    public List<String> getUrlsInContent(String content){
        List<String> termList = new ArrayList<String>();

        String patternString = "(http://|ftp://|https://|www.)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?";

        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(content);

        while(matcher.find()){
            termList.add(matcher.group());
        }

        return termList;
    }

    /**
     * 部分手机拍照后，图片会旋转，读取照片exif信息中的旋转角度
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
