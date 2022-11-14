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

package com.jxccp.ui.service;

import android.os.AsyncTask;

import com.jxccp.im.chat.manager.JXImManager;

public class MessageBoxService extends AsyncTask<Void, Void, Integer> {

    public interface MessageBoxListener {
        /**
         * 消息未读数
         */
        public void onGetMessageCount(int messageCount);
    }

    public void getMessageBoxUnReadCount() {

    }
    private MessageBoxListener messageBoxListener;

    public void setMessageBoxListener(MessageBoxListener messageBoxListener) {
        this.messageBoxListener = messageBoxListener;
    }
    @Override
    protected Integer doInBackground(Void... params) {
        try {
            return JXImManager.McsUser.getInstance().getMessageBoxUnReadCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    protected void onPostExecute(Integer result) {
        int count = result != null ? result: 0;
        if(messageBoxListener != null){
            messageBoxListener.onGetMessageCount(count);
        }
    }

}
