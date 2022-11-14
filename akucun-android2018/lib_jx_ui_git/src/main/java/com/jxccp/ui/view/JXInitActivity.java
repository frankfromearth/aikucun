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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jxccp.im.JXErrorCode;
import com.jxccp.im.JXErrorCode.Mcs;
import com.jxccp.im.callback.JXEventListner;
import com.jxccp.im.chat.manager.JXEventNotifier;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.chat.mcs.entity.JXWorkgroup;
import com.jxccp.ui.AccountHelper;
import com.jxccp.ui.AccountHelper.OnInitMcsRequestCallback;
import com.jxccp.ui.JXConstants;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.service.CustomerService;
import com.jxccp.ui.service.MessageBoxService;
import com.jxccp.ui.utils.JXCommonUtils;
import com.jxccp.ui.view.adapter.JXWorkGroupAdapter;

import java.util.List;

/**
 * 客服用户初始化界面
 */
public class JXInitActivity extends FragmentActivity implements JXWorkGroupAdapter.OnEnterGroupListener,
        MessageBoxService.MessageBoxListener, JXEventListner, View.OnClickListener {
    public static final String TAG = JXInitActivity.class.getSimpleName();

    private ListView groupListView;

    private ProgressBar loadingProgressBar;

    protected ImageView actionbarLeftView;

    protected ImageView actionbarRightView;

    protected ImageView   actionbarReturn;

    protected TextView actionbarRightTextView;

    protected TextView actionbarTitleView;

    protected TextView badgeTextView;

    protected ImageView badgeView;

    protected RelativeLayout badgeLayout;

    private String extendData;

    private String chatkey = null;
    private String chatTitlekey = null;
    private boolean msgBoxEnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        // 获取扩展信息
        extendData = getIntent().getStringExtra(JXChatView.EXTEND_DATA);
        chatkey = getIntent().getStringExtra(JXConstants.EXTRA_CHAT_KEY);
        chatTitlekey = getIntent().getStringExtra(JXConstants.EXTRA_CHAT_TITLE_KEY);
        msgBoxEnable = getIntent().getBooleanExtra(JXConstants.EXTRA_MSG_BOX_KEY, false);
        Log.d(TAG, "extendData: " + extendData);

        Intent service = new Intent();
        service.setClass(getApplicationContext(), CustomerService.class);
        startService(service);

        setContentView(R.layout.jx_activity_login);
        actionbarRightView = (ImageView)findViewById(R.id.iv_right);
        actionbarRightTextView = (TextView)findViewById(R.id.tv_right);
        actionbarLeftView = (ImageView)findViewById(R.id.iv_left);
        actionbarTitleView = (TextView)findViewById(R.id.tv_actionTitle);
        actionbarReturn = (ImageView) findViewById(R.id.iv_return);
        badgeView = (ImageView) findViewById(R.id.iv_badge);
        badgeTextView = (TextView)findViewById(R.id.tv_badge);
        badgeLayout = (RelativeLayout)findViewById(R.id.rl_badge);
        actionbarTitleView.setText(R.string.jx_choose_group);
        actionbarRightView.setImageResource(R.drawable.ic_leave_message);
        if(msgBoxEnable){
            badgeLayout.setVisibility(View.VISIBLE);
        }
        LayoutParams layoutParams = (LayoutParams)badgeLayout.getLayoutParams();
        if(layoutParams != null){
            layoutParams.setMargins(0, 0, JXCommonUtils.dip2px(this, 60), 0);
            badgeLayout.setLayoutParams(layoutParams);
            badgeView.setImageResource(R.drawable.ic_message_box);
            badgeView.setVisibility(View.VISIBLE);
        }

        groupListView = (ListView)findViewById(R.id.lv_groups);
        loadingProgressBar = (ProgressBar)findViewById(R.id.pb_loading);
        JXImManager.Message.getInstance().registerEventListener(this);
        // 客服请求的初始化
        AccountHelper.getInstance().initMcsRequest(getApplicationContext(), new OnInitMcsRequestCallback() {

            @Override
            public void onInitMcsResult(final int code) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (code == Mcs.MCS_USER_INIT_SUCCESS) {
                            JXWorkgroup workgroup = JXImManager.McsUser.getInstance().isNeedRequest();
                            if(workgroup == null){
                                if(chatkey == null){
                                    fetchWorkgroupFromServer();
                                }else{
                                    requestCustomerService(chatkey, chatTitlekey);
                                }
                            }else{
                                requestCustomerService(workgroup.getMcsId(), workgroup.getDisplayName());
                            }
                        } else if (code == JXErrorCode.APPKEY_NOT_EXIST) {
                            JXCommonUtils.showToast(getApplicationContext(),
                                    getString(R.string.jx_appkey_not_exist));
                            finish();
                        } else {
                            JXCommonUtils.showToast(getApplicationContext(),
                                    getString(R.string.jx_request_customerFailed));
                            finish();
                        }
                    }
                });
            }
        });
    }

    private MessageBoxService currentMessageBoxService;
    @Override
    protected void onResume() {
        super.onResume();
        if(msgBoxEnable){
            setUpBadgeUnread(JXUiHelper.getInstance().getMessageBoxUnreadCount());
            synchronized (MessageBoxService.class) {
                if(currentMessageBoxService == null){
                    currentMessageBoxService = new MessageBoxService();
                    currentMessageBoxService.setMessageBoxListener(this);
                }else{
                    currentMessageBoxService.setMessageBoxListener(null);
                    currentMessageBoxService.cancel(true);
                    currentMessageBoxService = new MessageBoxService();
                }
                currentMessageBoxService.execute();
            }
        }
    }
    /**
     * @date 2015年9月24日
     * @Description: 请求客服
     * @throws
     */
    private void requestCustomerService(final String mcsId, final String displayName) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                if (!mcsId.equals("service")) {
//                    JXImManager.getInstance().setChannelNumber("10002");
//                } else {
//                    JXImManager.getInstance().setChannelNumber("10001");
//                }
                Intent intent = new Intent();
                intent.setClass(JXInitActivity.this, JXChatUIActivity.class);
                intent.putExtra(JXChatView.CHAT_KEY, mcsId);
                intent.putExtra(JXChatView.CHAT_SKILLS_DISPLAYNAME, displayName);
                intent.putExtra(JXChatView.EXTEND_DATA, extendData);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchWorkgroupFromServer() {
        new AsyncTask<Void, Void, List<JXWorkgroup>>() {
            boolean hasException = false;

            protected void onPreExecute() {
                loadingProgressBar.setVisibility(View.VISIBLE);
            };

            @Override
            protected List<JXWorkgroup> doInBackground(Void... params) {
                try {
                    List<JXWorkgroup> workgroups = JXImManager.McsUser.getInstance()
                            .getCustomerServices();
                    return workgroups;
                } catch (Exception e) {
                    e.printStackTrace();
                    hasException = true;
                }
                return null;
            }

            protected void onPostExecute(List<JXWorkgroup> result) {
                loadingProgressBar.setVisibility(View.GONE);
                if (result != null) {
                    if(result.size() == 1){
                        //如果只一个有技能组直接进入
                        requestCustomerService(result.get(0).getMcsId(), result.get(0).getDisplayName());
                    }else{
                        JXWorkGroupAdapter adapter = new JXWorkGroupAdapter(JXInitActivity.this, result);
                        adapter.setOnEnterGroupListener(JXInitActivity.this);
                        groupListView.setAdapter(adapter);
                    }
                } else {
                    if (hasException) {
                        JXCommonUtils.showToast(getApplicationContext(),
                                getString(R.string.jx_loadin_groups_failed));
                        finish();
                    }
                }
            };
        }.execute();
    }

    @Override
    public void onEnter(String mcsId, String displayName) {
        requestCustomerService(mcsId, displayName);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_right){
            startActivity(new Intent(JXInitActivity.this , JXLeaveMsgActivity.class).putExtra("skillId", ""));
        }else if(view.getId() == R.id.iv_right){
            Intent intent = new Intent();
            intent.setClass(JXInitActivity.this, JXChatUIActivity.class);
            intent.putExtra(JXChatView.CHAT_TYPE, JXChatView.CHATTYPE_MESSAGE_BOX);
            intent.putExtra(JXChatView.CHAT_SKILLS_DISPLAYNAME, getString(R.string.jx_message_center));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        if(currentMessageBoxService != null){
            currentMessageBoxService.setMessageBoxListener(null);
        }
        JXImManager.Message.getInstance().removeEventListener(this);
        super.onDestroy();
        AccountHelper.getInstance().cancelTask();
    }

    @Override
    public void onGetMessageCount(int messageCount) {
        JXUiHelper.getInstance().setMessageBoxUnreadCount(messageCount);
        setUpBadgeUnread(messageCount);
    }

    /**
     * 显示为读数
     * @param messageCount
     */
    private void setUpBadgeUnread(int messageCount) {
        if(msgBoxEnable){
            if(messageCount > 99){
                if(badgeTextView.getVisibility() != View.VISIBLE){
                    badgeTextView.setVisibility(View.VISIBLE);
                }
                badgeTextView.setText("...");
            }else if(messageCount > 0){
                if(badgeTextView.getVisibility() != View.VISIBLE){
                    badgeTextView.setVisibility(View.VISIBLE);
                }
                badgeTextView.setText(String.valueOf(messageCount));
            }else{
                if(badgeTextView.getVisibility() == View.VISIBLE){
                    badgeTextView.setVisibility(View.GONE);
                }
            }
        }
    }
    @Override
    public void onEvent(JXEventNotifier eventNotifier) {
        if(eventNotifier.getEvent() == JXEventNotifier.Event.MESSAGE_PUSH && msgBoxEnable){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int messageCount = JXUiHelper.getInstance().getMessageBoxUnreadCount();
                    setUpBadgeUnread(messageCount);
                }
            });
        }
    }
}
