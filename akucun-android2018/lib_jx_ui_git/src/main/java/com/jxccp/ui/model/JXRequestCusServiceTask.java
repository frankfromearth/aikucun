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

import com.jxccp.im.JXErrorCode;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.exception.JXException;
import com.jxccp.ui.JXUiHelper;

/**
 * 异步请求客服的任务线程
 */
public class JXRequestCusServiceTask extends AsyncTask<Void, Void, Integer> {

    private String mSkillsId;
    private String extendData;

    private RequestCusServiceCallback mCallback;

    private boolean misNeedFinishAty;

    private int mTag = JXUiHelper.TAG_NOT_TRANSFER;
    
    private String message;

    public JXRequestCusServiceTask(String skillsId,
            RequestCusServiceCallback callback) {
        this.mSkillsId = skillsId;
        this.mCallback = callback;
    }

    public JXRequestCusServiceTask(String skillsId, String extendData,
            RequestCusServiceCallback callback) {
        this.mSkillsId = skillsId;
        this.extendData = extendData;
        this.mCallback = callback;
    }

    public void setTransferTag(int tag, boolean isNeedFinishAty) {
        this.mTag = tag;
        this.misNeedFinishAty = isNeedFinishAty;
    }

    @Override
    protected Integer doInBackground(Void... arg0) {
        try {
            if (mTag == JXUiHelper.TAG_NOT_TRANSFER) {
                JXImManager.McsUser.getInstance().requestCustomerService(mSkillsId, extendData);
            } else if (mTag == JXUiHelper.TAG_TRANSFER_ROBOT) {
                JXImManager.McsUser.getInstance().transferRobot(mSkillsId);
            } else if (mTag == JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                JXImManager.McsUser.getInstance().transferCustomerService(mSkillsId, extendData);
            } else if(mTag == JXUiHelper.TAG_CANCEL_WAIT){
                JXImManager.McsUser.getInstance().cancelWait(mSkillsId);
            } else if (mTag == JXUiHelper.TAG_CLOSE_SESSION) {
                JXImManager.McsUser.getInstance().closeSession(mSkillsId);
            }
            if (mTag != JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE && mTag != JXUiHelper.TAG_CANCEL_WAIT) {
                //还原是否有机器人的标志位
                JXUiHelper.getInstance().setHasRobot(false);
            }
        } catch (JXException e) {
            e.printStackTrace();
            if (e.getErrorCode() == JXErrorCode.Mcs.AFTER_WORK_NOT_ACCEPT) {
                message = e.getMessage();
            }
            return e.getErrorCode();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (mCallback != null) {
            mCallback.onRequestCallback(result, mTag, misNeedFinishAty, message);
        }
    }

    public void clearCallback() {
        mCallback = null;
    }

    public String getmSkillsId() {
        return mSkillsId;
    }

    public interface RequestCusServiceCallback {
        /**
         * @param code 返回码
         * @param tag 调用的类型
         * @param isNeedFinishAty 是否需要关闭activity
         */
        public void onRequestCallback(int code, int tag, boolean isNeedFinishAty , String message);
    }
}
