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

package com.jxccp.ui.listeners;

public interface JXChatGeneralCallback {

    /**
     * 功能面板的点击回调
     * @param position item的位置
     * @param drawableResId item的图片资源id
     */
    public void onFunctionItemClick(int position, int drawableResId);

    /**
     * 处于会话
     * @param isWithRobot是否与机器人聊天
     */
    public void onChatSession(boolean isWithRobot);

    public void onMessageBoxClick();
}
