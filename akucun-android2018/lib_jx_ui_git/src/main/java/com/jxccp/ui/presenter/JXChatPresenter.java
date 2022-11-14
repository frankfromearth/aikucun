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

import android.support.annotation.Nullable;

import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.ui.model.JXRequestCusServiceTask.RequestCusServiceCallback;

public interface JXChatPresenter {
    /**
     * 发送文本消息
     * @param text
     */
    void sendTextMessage(String text);

    /**
     * 发送图片消息
     * @param fileUri
     * @param username
     */
    void sendImageMessage(String filePath);
    
    /**
     * 发送语音消息
     */
    void sendVoiceMessage(String filePath, int voiceDur);
    
    /**
     * 发送视频消息
     */
    void sendVideoMessage(String filePath, int videoDur);

    /**
     * 加载更多聊天信息
     */
    public void loadMoreMessages(@Nullable String lastMesageId);

    /**
     * 消息重发
     * @param jxMessage
     */
    public void resendMessage(JXMessage jxMessage);

    void onDestroy();

    /**
     * 复制文本消息
     * @param message
     */
    void copyTextMessage(TextMessage message);

    /**
     * 删除消息
     * @param jxMessage
     */
    void deleteMessage(JXMessage jxMessage);

    /**
     * 客服请求
     * @param skillsId
     * @param Tag
     */
    void requestCustomer(String skillsId, int Tag, RequestCusServiceCallback callback);

    /**
     * 客服请求
     * @param skillsId
     * @param extendData
     * @param Tag
     */
    void requestCustomer(String skillsId, String extendData, int Tag, RequestCusServiceCallback callback);
}
