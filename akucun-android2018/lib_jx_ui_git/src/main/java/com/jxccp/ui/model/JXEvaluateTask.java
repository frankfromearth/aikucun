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

package com.jxccp.ui.model;

import android.os.AsyncTask;

import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.ui.JXUiHelper;

/**
 * 满意度评价的异步线程
 */
public class JXEvaluateTask extends AsyncTask<Void, Void, Boolean> {

    private JXEvaluteTaskCallback evaluteCallback = null;

    private JXConversation conversation;

    private JXMessage message;

    private int value;

    private int errorCode = 0;

    public static final int EVALUATE_TIMEOUT = 1;

    public JXEvaluateTask(JXConversation conversation, JXMessage message, int value) {
        this.conversation = conversation;
        this.value = value;
        this.message = message;
    }

    public void setJXEvaluteTaskCallback(JXEvaluteTaskCallback evaluteCallback) {
        this.evaluteCallback = evaluteCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (evaluteCallback != null) {
            evaluteCallback.onEvalutePre();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
//        if (message !=null && !JXUiHelper.getInstance().isEnableEvaluateMessage(message)) {
//            errorCode = EVALUATE_TIMEOUT;
//            return false;
//        }
        return conversation.submitSatisfaction(value);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            if (message != null) {
                message.setEvaluated();
            }
            conversation.setEvaluated(true);
            if (evaluteCallback != null) {
                evaluteCallback.onEvaluteSuccess();
            }
        } else {
            if (evaluteCallback != null) {
                evaluteCallback.onEvaluetError(errorCode);
            }
        }
    }

    public static abstract class JXEvaluteTaskCallback {
        public abstract void onEvaluteSuccess();

        public abstract void onEvaluetError(int errorCode);

        public void onEvalutePre() {
        }
    }
}
