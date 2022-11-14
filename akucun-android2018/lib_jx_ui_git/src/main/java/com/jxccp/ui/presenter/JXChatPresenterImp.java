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

package com.jxccp.ui.presenter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.jxccp.im.JXErrorCode;
import com.jxccp.im.callback.JXConnectionListener;
import com.jxccp.im.callback.JXEventListner;
import com.jxccp.im.chat.common.message.FileMessage;
import com.jxccp.im.chat.common.message.ImageMessage;
import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.common.message.JXConversation.SessionStatus;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.ChatType;
import com.jxccp.im.chat.common.message.JXMessageUtil;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.common.message.VideoMessage;
import com.jxccp.im.chat.common.message.VoiceMessage;
import com.jxccp.im.chat.manager.JXEventNotifier;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.listeners.JXDemoMessageListener;
import com.jxccp.ui.model.JXRequestCusServiceTask;
import com.jxccp.ui.model.JXRequestCusServiceTask.RequestCusServiceCallback;
import com.jxccp.ui.view.JXChatView;

import java.io.File;

public class JXChatPresenterImp implements JXChatPresenter, JXDemoMessageListener, JXEventListner, JXConnectionListener {

    private JXChatView mChatView;

    private JXConversation mConversation;

    private String mUsername;
    
    private ChatType chatType;

    private  Handler mainHandler = new Handler(Looper.getMainLooper());

    public JXChatPresenterImp(@NonNull JXChatView chatView, @NonNull String username, 
            @NonNull ChatType chatType) {
        this.mChatView = chatView;
        this.mUsername = username;
        this.chatType = chatType;
        mConversation = JXImManager.Conversation.getInstance().getConversation(username,
                chatType);
        JXImManager.Message.getInstance().registerEventListener(this);
        JXUiHelper.getInstance().addMessagListener(this);
        JXImManager.Login.getInstance().addConnectionListener(this);
    }

    @Override
    public void sendImageMessage(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File imageFile = new File(filePath);
            ImageMessage imageMessage = JXMessageUtil.createSendImageMessage(chatType, mUsername,
                    imageFile, JXMessage.NO_BURN_AFTER_READ);
            JXImManager.Message.getInstance().sendMessage(imageMessage);
            mChatView.refreshChatView(true, JXChatView.INVAILD_SELECTION);
        } else {
        }
    }

    @Override
    public void onDestroy() {
        if (cusServiceTask != null) {
            cusServiceTask.clearCallback();
            cusServiceTask.cancel(true);
        }
        JXImManager.Message.getInstance().removeEventListener(this);
        JXUiHelper.getInstance().removeMessageListener(this);
        JXImManager.Login.getInstance().removeConnectionListener(this);
    }

    private boolean moreData = true;

    private boolean loading = false;
    @Override
    public void loadMoreMessages(@Nullable String lastMesageId) {
        if(mChatView != null){
            if (!loading && moreData) {
                int count = 0;
                if(TextUtils.isEmpty(lastMesageId)){
                    count = mConversation.loadMoreMessage(null, JXChatView.DEFAULT_PAGE).size();
                }else{
                    count =  mConversation.loadMoreMessage(lastMesageId, JXChatView.DEFAULT_PAGE).size();
                }
                if (count > 0) {
                    mChatView.refreshChatView(false, 19);
                    loading = false;
                }else{
                    moreData = false;
                }
            }
            mChatView.interruptRefresh();
        }
    }

    @Override
    public void onEvent(JXEventNotifier eventNotifier) {
        if(mChatView != null){
            if(eventNotifier.getEvent() == JXEventNotifier.Event.MESSAGE_PUSH){
                  mChatView.onMessageBoxUpdate();
            }else{
                mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
            }
        }
    }

    @Override
    public void onSuccess(JXMessage message) {
        if(mChatView != null){
            mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onError(final JXMessage message, final int code, String reason) {
        if(mChatView != null){
            mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(JXErrorCode.Message.FILE_SIZE_EXCEEDED == code 
                            || JXErrorCode.Message.FILE_TYPE_MISMATCH== code){
                        if(message instanceof FileMessage){
                            Context context = null;
                            if(mChatView instanceof Activity){
                                context = (Activity)mChatView;
                            }else if(mChatView instanceof Fragment){
                                context = ((Fragment)mChatView).getActivity();
                            }else if(mChatView instanceof android.app.Fragment){
                                context = ((android.app.Fragment)mChatView).getActivity();
                            }
                            if(context != null){
                                String tips = JXErrorCode.Message.FILE_TYPE_MISMATCH== code ? context.getString(R.string.jx_file_format_no_match)
                                        : String.format(context.getString(R.string.jx_file_over_size), 
                                                (JXImManager.Config.getInstance().getFileMsgMaxSize(message.getType())/1024));
                                mChatView.showErrorTips(code, tips);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onTransfered(JXMessage message, long totalSize, long currentSize) {
        if(mChatView != null){
            mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
        }
    }

    @Override
    public void sendTextMessage(String text) {
        if(mChatView != null){
            TextMessage msg = JXMessageUtil.createSendTextMessage(chatType , mUsername,
                    text, JXMessage.NO_BURN_AFTER_READ);
            JXImManager.Message.getInstance().sendMessage(msg);
            mChatView.refreshChatView(true, JXChatView.INVAILD_SELECTION);
        }
    }

    @Override
    public void resendMessage(JXMessage jxMessage) {
        if(mChatView != null){
            JXImManager.Message.getInstance().resendMessage(jxMessage);
            mChatView.refreshChatView(true, JXChatView.INVAILD_SELECTION);
        }
    }

    /*@Override
    public void onRequestCallback(int code, int tag, boolean isNeedFinishAty) {
        if(mChatView != null){
            if(code == 0){
                if(tag != JXRequestCusServiceTask.TAG_NOT_TRANSFER){
                    mChatView.hideProgress();
                }
            }else{
                mChatView.hideProgress();
            }
        }
    }*/

    JXRequestCusServiceTask cusServiceTask;
    @Override
    public void requestCustomer(String skillsId, int Tag, RequestCusServiceCallback callback){
        if(mChatView != null){
            mChatView.showProgress(Tag);
            cusServiceTask = new JXRequestCusServiceTask(skillsId, callback);
            if(Tag != JXUiHelper.TAG_CANCEL_WAIT){
                cusServiceTask.setTransferTag(Tag, true);
            }else{
                cusServiceTask.setTransferTag(Tag, false);
            }
            cusServiceTask.execute();
        }
    }

    @Override
    public void requestCustomer(String skillsId, String extendData, int Tag, RequestCusServiceCallback callback){
        if(mChatView != null){
            mChatView.showProgress(Tag);
            cusServiceTask = new JXRequestCusServiceTask(skillsId, extendData, callback);
            if(Tag != JXUiHelper.TAG_CANCEL_WAIT){
                cusServiceTask.setTransferTag(Tag, true);
            }else{
                cusServiceTask.setTransferTag(Tag, false);
            }
            cusServiceTask.execute();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void copyTextMessage(TextMessage message) {
        Context context = null;
        if(mChatView instanceof Activity){
            context = (Activity)mChatView;
        }else if(mChatView instanceof Fragment){
            context = ((Fragment)mChatView).getActivity();
        }else if(mChatView instanceof android.app.Fragment){
            context = ((android.app.Fragment)mChatView).getActivity();
        }
        if(context != null){
            // 获取剪贴板管理服务
            ClipboardManager cm = (ClipboardManager)context.getSystemService(
                    Context.CLIPBOARD_SERVICE);
            // 将文本数据复制到剪贴板
            ClipData clip = ClipData.newPlainText("simple text",
                    message.getContent());
            cm.setPrimaryClip(clip);
        }
    }

    @Override
    public void deleteMessage(JXMessage jxMessage) {
        if(mChatView != null && mConversation != null){
            mConversation.removeMessage(jxMessage.getMessageId());
            mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
        }
    }

    @Override
    public void sendVoiceMessage(String filePath, int voiceDur) {
        if (!TextUtils.isEmpty(filePath)) {
            File voiceFile = new File(filePath);
            VoiceMessage voiceMessage = JXMessageUtil.createSendVoiceMessage(chatType, mUsername, voiceFile, voiceDur, JXMessage.NO_BURN_AFTER_READ);
            JXImManager.Message.getInstance().sendMessage(voiceMessage);
            mChatView.refreshChatView(true, JXChatView.INVAILD_SELECTION);
        } else {
        }
    }

    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDisconnectioned(int errorCode) {
        if (mConversation.getSessionStatus() == SessionStatus.Waiting) {
            mChatView.closeOnDisconnect();
        }
    }

    @Override
    public void onReconnecting() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendVideoMessage(String filePath, int videoDur) {
        if (!TextUtils.isEmpty(filePath)) {
            File videoFile = new File(filePath);
            VideoMessage videoMessage = JXMessageUtil.createSendVideoMessage(chatType, mUsername, videoFile, videoDur, JXMessage.NO_BURN_AFTER_READ);
            JXImManager.Message.getInstance().sendMessage(videoMessage);
            mChatView.refreshChatView(true, JXChatView.INVAILD_SELECTION);
        } else {
        }
    }

}
