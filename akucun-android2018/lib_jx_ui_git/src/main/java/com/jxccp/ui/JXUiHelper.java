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

package com.jxccp.ui;

import android.os.Handler;
import android.os.Looper;

import com.jxccp.im.callback.JXMessageCallback;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.ChatType;
import com.jxccp.im.chat.common.message.JXMessageUtil;
import com.jxccp.ui.entities.JXCommodity;
import com.jxccp.ui.listeners.JXDemoMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @date 2015-9-16 上午11:05:49
 */
public class JXUiHelper extends JXMessageCallback {

    public static final String JX_ATTR_TYPE = "jxui_attr_type";

    public static final String JX_ATTR_TYPE_CANCEL_WAIT = "jxui_attr_cancel_wait";

    /**
     * 非转接人工或机器人
     */
    public static final int TAG_NOT_TRANSFER = 0;

    /**
     * 转接到机器人服务
     */
    public static final int TAG_TRANSFER_ROBOT = 1;

    /**
     * 转接到人工服务
     */
    public static final int TAG_TRANSFER_CUSTOMER_SERVICE = 2;

    
    /**
     * 取消客服等待
     */
    public static final int TAG_CANCEL_WAIT = 3;

    /**
     * 提交满意度
     */
    public static final int TAG_EVALUTE = 4;

    /**
     * 获取快捷提问问题列表
     */
    public static final int TAG_QUICK_QUESTION = 5;

    /**
     * 取消客服等待
     */
    public static final int TAG_CLOSE_SESSION = 6;
    
    private static JXUiHelper demoHelper;

    private String PlayingMsgID;

    private boolean isRecEvaluateMessage = false;

    private boolean hasRobot = false;

    // 超时评价消息缓存
    private Map<String, JXMessage> enableEvaluateCache;

    private String robotNickname= null;

    private Handler handler = new Handler(Looper.getMainLooper());

    private JXCommodity jxCommodity ;

    /**
     * 消息盒子未读消息数
     */
    private int messageBoxUnreadCount = 0;

    private JXUiHelper() {
        enableEvaluateCache = new HashMap<String, JXMessage>();
    }

    public static JXUiHelper getInstance() {
        if (null == demoHelper) {
            synchronized (JXUiHelper.class) {
                if (null == demoHelper) {
                    demoHelper = new JXUiHelper();
                }
            }
        }
        return demoHelper;
    }

    public String getPlayingMsgID() {
        return PlayingMsgID;
    }

    public void setPlayingMsgID(String playingMsgID) {
        PlayingMsgID = playingMsgID;
    }

    // public static String mcsID = null;

    private OnConversationListener conversationListener;

    public void setConversationListListener(OnConversationListener conversationListener) {
        this.conversationListener = conversationListener;
    }

    /**
     * @Description: 通知会话窗口更新
     * @date 2015-9-16 上午11:24:39
     */
    public void notifyConversationListChange() {
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (conversationListener != null) {
                    conversationListener.onConversationChanged();
                }
            }
        });
    }

    public interface OnConversationListener {
        public void onConversationChanged();
    }

    public void notifySendingMessage(JXMessage message, long totalSize, long currentSize) {
        for (JXDemoMessageListener listener : sendMessageListeners) {
            listener.onTransfered(message, totalSize, currentSize);
        }
    }

    public void notifySendMessageSuccess(JXMessage message) {
        for (JXDemoMessageListener listener : sendMessageListeners) {
            listener.onSuccess(message);
        }
    }

    public void notifySendMessageError(JXMessage message, int code, String reason) {
        for (JXDemoMessageListener listener : sendMessageListeners) {
            listener.onError(message, code, reason);
        }
    }

    List<JXDemoMessageListener> sendMessageListeners = new ArrayList<JXDemoMessageListener>();

    public void addMessagListener(JXDemoMessageListener listener) {
        if (listener != null) {
            if (!sendMessageListeners.contains(listener)) {
                sendMessageListeners.add(listener);
            }
        }
    }

    public void removeMessageListener(JXDemoMessageListener listener) {
        if (listener != null) {
            sendMessageListeners.remove(listener);
        }
    }

    @Override
    public void onSuccess(JXMessage message) {
        notifySendMessageSuccess(message);
    }

    @Override
    public void onError(JXMessage message, int code, String reason) {
        notifySendMessageError(message, code, reason);
    }

    @Override
    public void onTransfered(JXMessage message, long totalSize, long currentSize) {
        notifySendingMessage(message, totalSize, currentSize);
    }

    public JXMessage saveSessionMsg(String tipsMessage, String skillsId, Map<String, Object> map) {
        return JXMessageUtil.saveNotification(skillsId, ChatType.CUSTOMER_SERVICE, tipsMessage,
                false, map);
    }

    public static final int INVALID_INT = -1;

    private int mTitleHeight = INVALID_INT;

    private int chatBgdrawableResId = INVALID_INT;

    private int inputMenuHeight = INVALID_INT;

    private int functionPanelHeight = INVALID_INT;

    private int expressionPanelHeight = INVALID_INT;

    private int mTitleBgResId = INVALID_INT;

    private int mTitleBgColorId = INVALID_INT;

    private int mTitleLeftBgResId = INVALID_INT;

    private int mTitleRightBgResId = INVALID_INT;

    private String mConversationExitTips = null;

    private int mTitleTextColorResId = INVALID_INT;

    public int getmTitleHeight() {
        return mTitleHeight;
    }

    /**
     * 设置标题栏高度
     * 
     * @param mTitleHeight
     */
    public void setmTitleHeight(int mTitleHeight) {
        this.mTitleHeight = mTitleHeight;
    }

    public int getChatBgdrawableResId() {
        return chatBgdrawableResId;
    }

    /**
     * 设置聊天背景
     * 
     * @param chatBgdrawableResId
     */
    public void setChatBgdrawableResId(int chatBgdrawableResId) {
        this.chatBgdrawableResId = chatBgdrawableResId;
    }

    public int getInputMenuHeight() {
        return inputMenuHeight;
    }

    /**
     * 设置输入布局的高度
     * 
     * @param inputMenuHeight
     */
    public void setInputMenuHeight(int inputMenuHeight) {
        this.inputMenuHeight = inputMenuHeight;
    }

    public int getFunctionPanelHeight() {
        return functionPanelHeight;
    }

    /**
     * 设置功能面板高度
     * 
     * @param functionPanelHeight
     */
    public void setFunctionPanelHeight(int functionPanelHeight) {
        this.functionPanelHeight = functionPanelHeight;
    }

    public int getExpressionPanelHeight() {
        return expressionPanelHeight;
    }

    /**
     * 设置表情面板高度
     * 
     * @param expressionPanelHeight
     */
    public void setExpressionPanelHeight(int expressionPanelHeight) {
        this.expressionPanelHeight = expressionPanelHeight;
    }

    public int getmTitleBgResId() {
        return mTitleBgResId;
    }

    /**
     * 设置标题栏背景
     *
     * @param mTitleBgResId
     */
    public void setmTitleBgResId(int mTitleBgResId) {
        this.mTitleBgResId = mTitleBgResId;
    }

    /**
     * 设置标题栏颜色
     *
     * @return
     */
    public void setmTitleBgColorId(int mTitleBgColorId) {
        this.mTitleBgColorId = mTitleBgColorId;
    }

    public int getmTitleBgColorId() {
        return mTitleBgColorId;
    }

    /** 设置标题栏左侧图标 **/
    public void setmTitleLeftImgResId(int mTitleLeftResId) {
        this.mTitleLeftBgResId = mTitleLeftResId;
    }

    public int getmTitleLeftImgResId() {
        return mTitleLeftBgResId;
    }

    public int getmTitleRightImgResId() {
        return mTitleRightBgResId;
    }

    /** 设置标题栏右侧图标 **/
    public void setmTitleRightImgResId(int mTitleRightBgResId) {
        this.mTitleRightBgResId = mTitleRightBgResId;
    }

    public String getmConversationExitTips() {
        return mConversationExitTips;
    }

    /**
     * 设置会话被结束时显示提示
     * 
     * @param mConversationExitTips
     */
    public void setmConversationExitTips(String mConversationExitTips) {
        this.mConversationExitTips = mConversationExitTips;
    }

    private int currentPosition = -1;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getmTitleTextColorResId() {
        return mTitleTextColorResId;
    }

    /**
     * 设置标题栏的文字颜色
     *
     * @param mTitleTextColorResId
     */
    public void setmTitleTextColorResId(int mTitleTextColorResId) {
        this.mTitleTextColorResId = mTitleTextColorResId;
    }

    public boolean isRecEvaluateMessage() {
        return isRecEvaluateMessage;
    }

    public void setRecEvaluateMessage(boolean isRecEvaluateMessage) {
        this.isRecEvaluateMessage = isRecEvaluateMessage;
    }

    public void saveEnableEvaluateMessage(final JXMessage message) {
        enableEvaluateCache.put(message.getMessageId(), message);
        // 暂时不做有评价消息的时效控制
        // evaTimeOutHandler.postDelayed(new Runnable() {
        // public void run() {
        // enableEvaluateCache.remove(message.getMessageId());
        // JXImManager.McsUser.getInstance().saveMcsEndMessage();
        // notifySendMessageSuccess(message);
        // }
        // }, 600*1000);
    }

    public boolean isEnableEvaluateMessage(JXMessage message) {
        if (enableEvaluateCache.containsKey(message.getMessageId())) {
            return true;
        }
        return false;
    }

    public boolean isHasRobot() {
        return hasRobot;
    }

    public void setHasRobot(boolean hasRobot) {
        this.hasRobot = hasRobot;
    }

    public void destroy() {
        enableEvaluateCache = null;
    }

    public int getMessageBoxUnreadCount() {
        return messageBoxUnreadCount;
    }

    public void setMessageBoxUnreadCount(int messageBoxUnreadCount) {
        synchronized (JXUiHelper.class) {
            this.messageBoxUnreadCount = messageBoxUnreadCount;
        }
    }

    public String getRobotNickname() {
        return robotNickname;
    }

    public void setRobotNickname(String robotNickname) {
        this.robotNickname = robotNickname;
    }

    public JXCommodity getJxCommodity() {
        return jxCommodity;
    }

    public void setJxCommodity(JXCommodity jxCommodity) {
        this.jxCommodity = jxCommodity;
    }

    /**
     * 消息盒子更新回调
     */
    public interface MessageBoxListener {
        /**
         * 消息盒子更新
         */
        public void onUpdate();
    }

    private List<MessageBoxListener> messageBoxListeners = new ArrayList<>();

    public void addMessageBoxListener(MessageBoxListener listener){
        synchronized (messageBoxListeners) {
            if(listener != null){
                if(!messageBoxListeners.contains(listener)){
                    messageBoxListeners.add(listener);
                }
            }
        }
    }

    public void removeMessageBoxListener(MessageBoxListener listener){
        synchronized (messageBoxListeners) {
            if(listener != null){
                messageBoxListeners.remove(listener);
            }
        }
    }

    /**
     * 通知ui进行更新
     */
    public void notifyMessageBoxUpdate(){
        for(MessageBoxListener listener : messageBoxListeners){
            listener.onUpdate();
        }
    }

    private String skillsId;
    public void setSkillsId(String _skillsid){
        this.skillsId = _skillsid;
    }
    public String getSkillsId(){
        return skillsId;
    }
}
