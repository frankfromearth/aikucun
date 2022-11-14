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

package com.jxccp.ui.view;

import com.jxccp.im.chat.common.message.JXMessage;

import java.util.List;

public interface JXChatView {

    public static final String CHAT_KEY = "jx_username";

    public static final String CHAT_TYPE = "jx_chatType";

    /**
     * 扩展信息字段常量定义
     */
    public static final String EXTEND_DATA = "jx_extend_data";

    public static final String CHAT_SKILLS_DISPLAYNAME = "jx_skills_displayName";

    public static final String FUNCTION_PANEL_DRAWABLES = "jx_functionDrawable";

    public static final String FUNCTION_PANEL_NAMES = "jx_functionNames";

    public static final int INVAILD_SELECTION = -1;

    /**
     * 默认一页显示的大小
     */
    public static final int DEFAULT_PAGE = 20;

    /**
     * 获取图库图片的请求码
     */
    public static final int IMAGE_REQUEST_CODE = 0x10;

    /**
     * 获取图库视频文件的请求码
     */
    public static final int VIDEO_REQUEST_CODE = 0x11;

    /**
     * 图片预览时的请求码
     */
    public static final int IMAGE_PREVW_CODE = 0x12;

    /**
     * 消息列表刷新的结果码
     */
    public static final int IMAGE_PREVW_RESULT_CODE = 0x13;

    /**
     * 拍照
     */
    public static final int GET_IMAGE_VIA_CAMERA = 0x14;

    /**
     * 聊天室对话类型
     */
    public static final int CHATTYPE_CAHTROOM = 0x15;

    /**
     * 群聊对话类型
     */
    public static final int CHATTYPE_GROUPCHAT = 0x16;

    /**
     * 单聊对话类型
     */
    public static final int CHATTYPE_SINGLECHAT = 0x17;

    /**
     * 客服对话类型
     */
    public static final int CHATTYPE_CUSTOMER_SERVICE = 0x18;

    
    /**
     * 录像
     */
    public static final int GET_VIDEO_VIA_CAMERA = 0x19;

    /**
     * 消息盒子类型
     */
    public static final int CHATTYPE_MESSAGE_BOX = 0x20;
    /**
     * 全局进度显示
     */
    public void showProgress(int requestTag);

    /**
     * 隐藏进度显示
     */
    public void hideProgress();

    /**
     * 显示功能面板
     */
    public void showFunctionPanel();

    /**
     * 隐藏功能面板
     */
    public void hideFunctionPanel();

    /**
     * 显示消息删除失败
     * @param isMultiDel
     */
    public void showDeleteMessageError(boolean isMultiDel);

    /**
     * 显示消息删除成功
     * @param isMultiDel
     */
    public void showDeleteMessageSuccess(boolean isMultiDel);

    /**
     * 聊天界面是否可见
     * @return
     */
    public boolean isVisiable();

    /**
     * 刷新聊天界面
     * @param isSelectBottom
     */
    public void refreshChatView(boolean isSelectBottom, int selection);

    public void interruptRefresh();

    /**
     * 排队等待状态下断开连接
     */
    public void closeOnDisconnect();

    /**
     * 消息列表加载完成
     * @param newMessages
     */
    public void onMessageBoxLoaded(int offset, List<JXMessage> newMessages);

    /**
     * 消息列表加载失败
     */
    public void onMessageBoxLoadFailed();

    /**
     * 消息盒子更新
     */
    public void onMessageBoxUpdate();
    
    /**
     * 异常提示
     * @param errorCode
     */
    public void showErrorTips(int errorCode, String tips);
}
