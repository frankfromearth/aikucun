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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnyRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxccp.im.chat.common.entity.JXSatisfication;
import com.jxccp.im.chat.common.entity.JXSatisfication.Option;
import com.jxccp.im.chat.common.message.ImageMessage;
import com.jxccp.im.chat.common.message.JXConversation;
import com.jxccp.im.chat.common.message.JXConversation.SessionStatus;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.ChatType;
import com.jxccp.im.chat.common.message.JXMessageUtil;
import com.jxccp.im.chat.common.message.RichTextMessage;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.entities.JXCommodity;
import com.jxccp.ui.listeners.JXChatFragmentListener;
import com.jxccp.ui.listeners.JXVoicePlayListener;
import com.jxccp.ui.model.JXEvaluateTask;
import com.jxccp.ui.model.JXEvaluateTask.JXEvaluteTaskCallback;
import com.jxccp.ui.model.JXRequestCusServiceTask;
import com.jxccp.ui.model.JXRequestCusServiceTask.RequestCusServiceCallback;
import com.jxccp.ui.presenter.JXChatMsgBoxPresenter;
import com.jxccp.ui.presenter.JXChatPresenter;
import com.jxccp.ui.presenter.JXChatPresenterImp;
import com.jxccp.ui.utils.JXCommonUtils;
import com.jxccp.ui.view.adapter.JXEvaluateAdapter;
import com.jxccp.ui.view.adapter.JXChatAdapter;
import com.jxccp.ui.view.adapter.JXQuickQuestionAdapter;
import com.jxccp.ui.widget.JXInputMenuLayout;
import com.jxccp.ui.widget.JXInputMenuLayout.JXInputLayoutListener;
import com.jxccp.ui.widget.JXVoiceHintView;
import com.jxccp.ui.widget.JXVoiceHintView.voiceRecorderCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class JXBaseChatFragment extends Fragment implements JXChatView, OnRefreshListener,
        OnTouchListener, View.OnClickListener, OnItemClickListener, JXChatFragmentListener, RequestCusServiceCallback{

    protected Context mContext;

    /**
     * 界面是否可见
     */
    public boolean isVisiable;

    protected Handler mHandler;

    protected JXInputMenuLayout menuLayout;

    protected JXVoiceHintView voiceHintView;

    protected TextView titleTextView;

    protected FrameLayout titleLayout;

    protected ImageView titleLeftView;
    protected TextView title_right_iv_bottom_tv;
    protected ImageView titleRightView;
    protected LinearLayout iv_right_layout;
    protected TextView titleRightText;

    protected TextView tipsCancelView;

    protected TextView tipsMessageView;
    
    protected TextView leaveMessageView;

    protected LinearLayout cancelLayout;

    public File cameraPhoto;

    public File cameraVideo;

    protected TextView badgeTextView;

    protected ImageView badgeView;

    protected RelativeLayout badgeLayout;
    
    private LinearLayout rootLayout;

    protected SwipeRefreshLayout refreshLayout;

    protected ListView chatListView;

    private TextView deleteTextView;

    protected JXChatPresenter chatPresenter;

    private InputMethodManager inputMethodManager;

    protected JXChatAdapter chatAdapter;

    protected JXConversation conversation;

    private String username;

    private ChatType chatType;

    protected int type;

    private JXChatFragmentListener chatFragmentListener = null;

    private List<Drawable> functionImages = new ArrayList<Drawable>();

    private List<Integer> functionDrawRes ;

    private List<String> functionNames = new ArrayList<String>();

    private JXMessage sessionTipsMessage;

    private JXMessage cancelWaitMessage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        Bundle bundle = getArguments();
        type = bundle.getInt(JXChatView.CHAT_TYPE, 0);
        if(type != 0){
            chatPresenter = new JXChatMsgBoxPresenter(this);
            return;
        }
        username = bundle.getString(CHAT_KEY);
        type = getChatType();
        if(type == CHATTYPE_CAHTROOM){
            chatType =  ChatType.CHATROOM;
        }else if(type == CHATTYPE_GROUPCHAT){
            chatType =  ChatType.GROUP_CHAT;
        }else if(type == CHATTYPE_SINGLECHAT){
            chatType =  ChatType.SINGLE_CHAT;
        }else if(type == CHATTYPE_CUSTOMER_SERVICE){
            chatType =  ChatType.CUSTOMER_SERVICE;
        }
        chatPresenter = new JXChatPresenterImp(this, username, chatType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
    }

    public void setJXChatFragmentListener(JXChatFragmentListener chatFragmentListener) {
        this.chatFragmentListener = chatFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View chatView = inflater.inflate(R.layout.jx_fragment_chat, container, false);
        rootLayout = (LinearLayout)chatView.findViewById(R.id.ll_root);
        refreshLayout = (SwipeRefreshLayout)chatView.findViewById(R.id.refresh);
        refreshLayout.setColorSchemeResources(R.color.orange500);
        refreshLayout.setOnRefreshListener(this);
        chatListView = (ListView)chatView.findViewById(R.id.messages_view);
        menuLayout = (JXInputMenuLayout)chatView.findViewById(R.id.inputlayout);
        voiceHintView = (JXVoiceHintView)chatView.findViewById(R.id.voice_hint_view);
        titleTextView = (TextView)chatView.findViewById(R.id.tv_title);
        deleteTextView = (TextView)chatView.findViewById(R.id.tv_del);
        titleLayout = (FrameLayout)chatView.findViewById(R.id.fl_title);
        titleLeftView = (ImageView)chatView.findViewById(R.id.iv_left);
        titleRightView = (ImageView)chatView.findViewById(R.id.iv_right);
        title_right_iv_bottom_tv = chatView.findViewById(R.id.title_right_iv_bottom_tv);
        titleRightText = (TextView)chatView.findViewById(R.id.tv_right);
        tipsCancelView = (TextView)chatView.findViewById(R.id.tv_cancel);
        leaveMessageView = (TextView)chatView.findViewById(R.id.tv_leave_message);
        badgeView = (ImageView) chatView.findViewById(R.id.iv_badge);
        badgeTextView = (TextView)chatView.findViewById(R.id.tv_badge);
        badgeLayout = (RelativeLayout)chatView.findViewById(R.id.rl_badge);
        tipsCancelView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        tipsCancelView.getPaint().setAntiAlias(true);
        leaveMessageView.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        leaveMessageView.getPaint().setAntiAlias(true);
        tipsMessageView = (TextView)chatView.findViewById(R.id.tv_cancel_tips);
        cancelLayout = (LinearLayout)chatView.findViewById(R.id.ll_cancel);
        iv_right_layout = chatView.findViewById(R.id.iv_right_layout);
        iv_right_layout.setOnClickListener(this);
        badgeView.setOnClickListener(this);
        titleRightText.setOnClickListener(this);
        titleLeftView.setOnClickListener(this);
        deleteTextView.setOnClickListener(this);
        menuLayout.sendMsgView.setOnClickListener(this);
        menuLayout.voicePressButton.setOnClickListener(this);
        chatListView.setOnTouchListener(this);
        menuLayout.contentText.setOnTouchListener(this);
        menuLayout.funcPanelView.setOnItemClickListener(this);
        inputMethodManager = (InputMethodManager)mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        
        if(type != CHATTYPE_MESSAGE_BOX){
            conversation = JXImManager.Conversation.getInstance().getConversation(username,chatType);

            if(functionImages != null && functionNames != null){
                menuLayout.configureFuncPanel(functionImages, functionNames);
            }
            menuLayout.setJXInputLayoutListener(inputLayoutListener);
            // 设置改会话的消息为已读
            conversation.setAsRead();
            chatAdapter = new JXChatAdapter(mContext, new ArrayList<JXMessage>(), chatListView,
                    conversation);
            chatAdapter.setJXChatFragmentListener(chatFragmentListener);
            chatListView.setAdapter(chatAdapter);
            chatAdapter.refreshMessageList(conversation.getMessageList());
            if (!JXImManager.McsUser.getInstance().isCustomerSendEmoticonEnable()) {
                menuLayout.expressionImageView.setVisibility(View.GONE);
            }
        }else{
            chatAdapter = new JXChatAdapter(mContext, new ArrayList<JXMessage>(), chatListView,
                    conversation);
            chatListView.setStackFromBottom(false);
            chatListView.setAdapter(chatAdapter);
        }
        configureView();
        return chatView;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        isVisiable = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isVisiable = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("拍照回调：","--------------->"+requestCode);
        if (requestCode == IMAGE_PREVW_CODE) {
            return;
        }
        if (requestCode == GET_IMAGE_VIA_CAMERA && resultCode == Activity.RESULT_OK ) {
            if (cameraPhoto!=null&&cameraPhoto.exists()) {
                String path = cameraPhoto.getAbsolutePath();
                Log.e("拍照返回地址：","--------------->"+path);
                if (!TextUtils.isEmpty(path)) {
                    File imageFile = new File(path);
                    ImageMessage imageMessage = JXMessageUtil.createSendImageMessage(ChatType.CUSTOMER_SERVICE,
                            username, imageFile, JXMessage.NO_BURN_AFTER_READ);
                    JXImManager.Message.getInstance().sendMessage(imageMessage);
                    refreshChatView(false, INVAILD_SELECTION);
                } else {
                }
                chatListView.setSelection(chatListView.getBottom());
            }
        }else if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    if (selectedImage == null) {
                        return;
                    }
                    String path = JXCommonUtils.getRealFilePath(mContext, selectedImage);
                    chatPresenter.sendImageMessage(path);
                    break;
                case VIDEO_REQUEST_CODE:
                    Uri selectedVideo = data.getData();
                    if (selectedVideo == null) {
                        return;
                    }
                    String videoPath = JXCommonUtils.getRealFilePath(mContext, selectedVideo);
                    if (!TextUtils.isEmpty(videoPath)) {
                        File videoFile = new File(videoPath);
                        int duration = JXCommonUtils.getVideoDuration(videoFile);
                        chatPresenter.sendVideoMessage(videoPath, duration);
                    } else {
                    }
                    break;
                case GET_VIDEO_VIA_CAMERA:
                    Uri camVideoUri=data.getParcelableExtra("uri");
                    if (camVideoUri == null) {
                        JXLog.e("cannot get video uri");
                        return;
                    }
                    String camVideoPath = JXCommonUtils.getRealFilePath(mContext, camVideoUri);
                    if (!TextUtils.isEmpty(camVideoPath)) {
                        File camVideoFile = new File(camVideoPath);
                        int duration = JXCommonUtils.getVideoDuration(camVideoFile);
                        chatPresenter.sendVideoMessage(camVideoPath, duration);
                    } else {
                    }
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatType == ChatType.CUSTOMER_SERVICE && sessionTipsMessage != null) {
            conversation.removeMessage(sessionTipsMessage.getMessageId());
        }
        if (chatType == ChatType.CUSTOMER_SERVICE && cancelWaitMessage != null) {
            conversation.removeMessage(cancelWaitMessage.getMessageId());
        }
        refreshChatView(false, INVAILD_SELECTION);
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (JXVoicePlayListener.currentPlayListener!=null&&JXVoicePlayListener.isPlaying) {
            JXVoicePlayListener.currentPlayListener.stopPlayVoice();
        }
        menuLayout.onDestory();
        chatPresenter.onDestroy();
        // 聊天界面关闭时，将当前的聊天界面的消息置为已读
        if (conversation != null) {
            conversation.setAsRead();
            JXUiHelper.getInstance().notifyConversationListChange();
        }
        if (chatAdapter != null) {
            chatAdapter.resetResource();
        }
        if(evaluateTask != null){
            evaluateTask.cancel(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
        isVisiable = false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textSendButton) {
            chatPresenter.sendTextMessage(menuLayout.contentText.getText().toString());
            menuLayout.contentText.setText("");
        } else if (v.getId() == R.id.iv_left) {
            if (mContext != null) {
                getActivity().onBackPressed();
            }
        } else if (v.getId() == R.id.tv_del) {
            onMessageDeleteClick();
        } else if (v.getId() == R.id.iv_right) {
            onTitleRightViewClick();
        }else if (v.getId() == R.id.iv_right_layout){
            onTitleRightViewClick();
        }else if(v.getId() == R.id.tv_right){
            onTitleRightTextClck();
        } else if (v.getId() == R.id.btn_close_evaluate) {
            if (evaluateWindow!=null&&evaluateWindow.isShowing()) {
                evaluateWindow.dismiss();
            }
            if (quickQuestionWindow!=null&&quickQuestionWindow.isShowing()) {
                quickQuestionWindow.dismiss();
            }
        } else {
            processOnclick(v);
        }
    }

    /**
     * 是否处于音频录制中
     */
    protected boolean isVoiceRecording = false;
    @Override
    public boolean onMessageItemClick(final JXMessage jxMessage) {
        if(jxMessage instanceof RichTextMessage){
            RichTextMessage richTextMessage = (RichTextMessage)jxMessage;
            chatPresenter.sendTextMessage(richTextMessage.getUrl());
            return true;
        }else if(jxMessage.getType() == JXMessage.Type.EVALUATION){
            showEvaluateWindow(jxMessage,false);
            return true;
        }else if(jxMessage.getType() == JXMessage.Type.VOICE && isVoiceRecording){
            return true;
        }
        return false;
    }

    protected abstract void onMessageDeleteClick();
    protected abstract void onTitleRightViewClick();
    protected abstract void onTitleRightTextClck();
    protected abstract void processOnclick(View v);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (v.getId() == R.id.messages_view) {
                menuLayout.expressionLayout.setVisibility(View.GONE);
                menuLayout.funcPanelView.setVisibility(View.GONE);
                hiddenInput();
            } else if (v.getId() == R.id.textinput) {
                menuLayout.funcPanelView.setVisibility(View.GONE);
                menuLayout.expressionLayout.setVisibility(View.GONE);
                chatListView.setSelection(chatListView.getBottom());
            }
        }
        return false;
    }

    @Override
    public void onRefresh() {
        if (chatAdapter == null) {
            refreshLayout.setRefreshing(false);
            return;
        }
        if (chatAdapter != null && chatAdapter.getCount() == 0) {
            refreshLayout.setRefreshing(false);
            return;
        }
        JXMessage jxMessage = (JXMessage)chatAdapter.getItem(0);
        chatPresenter.loadMoreMessages(jxMessage.getMessageId());
    }

    @Override
    public abstract void showProgress(int requestTag);

    @Override
    public abstract void hideProgress();

    @Override
    public void showFunctionPanel() {
        menuLayout.funcPanelView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFunctionPanel() {
        menuLayout.funcPanelView.setVisibility(View.GONE);
    }

    @Override
    public boolean isVisiable() {
        return isVisiable;
    }

    @Override
    public void refreshChatView(final boolean isSelectBottom, final int selection) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (chatListView != null && chatAdapter != null && conversation != null) {
                    chatAdapter.refreshMessageList(conversation.getMessageList());
                    if (isSelectBottom) {
                        chatListView.setSelection(chatListView.getBottom());
                    } else {
                        if (selection != JXChatView.DEFAULT_PAGE) {
                            chatListView.setSelection(selection);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(android.widget.AdapterView<?> parent, View view, int position,
            long id) {
        menuLayout.funcPanelView.setVisibility(View.GONE);
        onFunctionItemClick(position, functionDrawRes.get(position));
    };

    @Override
    public void interruptRefresh() {
        if(mHandler != null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(refreshLayout != null){
                        refreshLayout.setRefreshing(false);
                    }
                }
            });
        }
    }

    @Override
    public void showDeleteMessageSuccess(boolean isMultiDel) {
    }

    /**
     * 功能面板点击事件
     *
     * @param position
     * @param drawableResId
     */
    public abstract void onFunctionItemClick(int position, int drawableResId);

    /**
     * 隐藏输入法
     *
     */
    public void hiddenInput() {
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(menuLayout.contentText.getWindowToken(), 0);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (menuLayout.onBackEvent()) {
            return true;
        }
        if (chatAdapter != null && chatAdapter.isDelMode()) {
            chatAdapter.setDelChoiceMode(false);
            menuLayout.setVisibility(View.VISIBLE);
            deleteTextView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * 消息重发
     *
     * @param jxMessage
     */
    protected void resendMessage(JXMessage jxMessage) {
        chatPresenter.resendMessage(jxMessage);
    }

    /**
     * 复制文本消息
     *
     * @param message
     */
    protected void copyTextMessage(TextMessage message) {
        chatPresenter.copyTextMessage(message);
    }

    /**
     * 删除消息
     *
     * @param jxMessage
     */
    protected void deleteMessage(JXMessage jxMessage) {
        chatPresenter.deleteMessage(jxMessage);
    }

    /**
     * 设置是否为删除模式
     *
     * @param isDelChoiceMode
     */
    protected void setDelChoiceMode(boolean isDelChoiceMode) {
        if (isDelChoiceMode) {
            deleteTextView.setVisibility(View.VISIBLE);
            chatAdapter.setDelChoiceMode(true);
            menuLayout.setVisibility(View.GONE);
        } else {
            deleteTextView.setVisibility(View.GONE);
            chatAdapter.setDelChoiceMode(false);
            menuLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 客服请求
     *
     * @param skillsId
     * @param Tag
     *            <li>{@link JXRequestCusServiceTask#TAG_NOT_TRANSFER}</li>
     *            <li>
     *            {@link JXRequestCusServiceTask#TAG_TRANSFER_CUSTOMER_SERVICE}
     *            </li>
     *            <li>{@link JXRequestCusServiceTask#TAG_TRANSFER_ROBOT}</li>
     */
    protected void requestCustomer(String skillsId, int Tag, RequestCusServiceCallback callback) {
        chatPresenter.requestCustomer(skillsId, Tag, callback);
    }

    /**
     * 客服请求
     *
     * @param skillsId
     * @param Tag
     *            <li>{@link JXRequestCusServiceTask#TAG_NOT_TRANSFER}</li>
     *            <li>
     *            {@link JXRequestCusServiceTask#TAG_TRANSFER_CUSTOMER_SERVICE}
     *            </li>
     *            <li>{@link JXRequestCusServiceTask#TAG_TRANSFER_ROBOT}</li>
     */
    protected void requestCustomer(String skillsId, String extendData, int Tag, RequestCusServiceCallback callback) {
        chatPresenter.requestCustomer(skillsId, extendData, Tag, callback);
    }

    /**
     * 自定义配置控件
     * 若开发者需要重新定义控件的背景、大小等时，可override此方法
     */
    protected void configureView(){
        if (JXUiHelper.getInstance().getmTitleBgResId() != JXUiHelper.INVALID_INT) {
            titleLayout.setBackgroundResource(JXUiHelper.getInstance().getmTitleBgResId());
        }
        if (JXUiHelper.getInstance().getChatBgdrawableResId() != JXUiHelper.INVALID_INT) {
            setChatBackground(JXUiHelper.getInstance().getChatBgdrawableResId());
        }
        if (JXUiHelper.getInstance().getExpressionPanelHeight() != JXUiHelper.INVALID_INT) {
            menuLayout
                    .setExpressionPanelHeight(JXUiHelper.getInstance().getExpressionPanelHeight());
        }
        if (JXUiHelper.getInstance().getFunctionPanelHeight() != JXUiHelper.INVALID_INT) {
            menuLayout.setFunctionPanelHeight(JXUiHelper.getInstance().getFunctionPanelHeight());
        }
        if (JXUiHelper.getInstance().getmTitleHeight() != JXUiHelper.INVALID_INT) {
            setTitleHeight(JXUiHelper.getInstance().getmTitleHeight());
        }
        if (JXUiHelper.getInstance().getInputMenuHeight() != JXUiHelper.INVALID_INT) {
            menuLayout.setInputMenuHeight(JXUiHelper.getInstance().getInputMenuHeight());
        }
        if (JXUiHelper.getInstance().getmTitleBgColorId() != JXUiHelper.INVALID_INT) {
            titleLayout.setBackgroundColor(JXUiHelper.getInstance().getmTitleBgColorId());
        }
        if (JXUiHelper.getInstance().getmTitleLeftImgResId() != JXUiHelper.INVALID_INT) {
            titleLeftView.setImageResource(JXUiHelper.getInstance().getmTitleLeftImgResId());
        }
        if (JXUiHelper.getInstance().getmTitleRightImgResId() != JXUiHelper.INVALID_INT) {
            titleRightView.setImageResource(JXUiHelper.getInstance().getmTitleRightImgResId());
        }
        if (JXUiHelper.getInstance().getmTitleTextColorResId() != JXUiHelper.INVALID_INT) {
            titleTextView.setTextColor(
                    getResources().getColor(JXUiHelper.getInstance().getmTitleTextColorResId()));
            titleRightText.setTextColor(
                    getResources().getColor(JXUiHelper.getInstance().getmTitleTextColorResId()));
        }
    }

    /**
     * 设置标题高度
     * @param height
     */
    protected void setTitleHeight(int height){
        if(height < 0){
            return;
        }
        ViewGroup.LayoutParams titleParams = titleLayout.getLayoutParams();
        titleParams.height = height;
        titleLayout.setLayoutParams(titleParams);
    }

    /**
     * 设置消息Item的背景
     * @param type 消息类型
     * @param resId 消息Item的资源ID
     */
    public void setMessageItemBgDrawable(JXMessage.Type type, JXMessage.Direction direction, @AnyRes int resId){
        if(chatAdapter != null){
            chatAdapter.setMessageItemBgDrawable(type, direction, resId);
        }
    }

    /**
     * @return
     * @see
     * <ul>
     * <li>{@link JXChatView#CHATTYPE_CAHTROOM}</li>
     * <li>{@link JXChatView#CHATTYPE_CUSTOMER_SERVICE}</li>
     * <li>{@link JXChatView#CHATTYPE_GROUPCHAT}</li>
     * <li>{@link JXChatView#CHATTYPE_SINGLECHAT}</li>
     * </ul>
     */
    protected abstract int getChatType();

    protected void setChatBackground(@DrawableRes int drawableResId){
        if(chatListView != null){
            chatListView.setBackgroundResource(drawableResId);
        }
    }

    @SuppressWarnings("deprecation")
    protected void setChatBackground(Drawable backgroundDrawable){
        if(chatListView != null){
            chatListView.setBackgroundDrawable(backgroundDrawable);
        }
    }

    /**
     * 处理批量删除
     */
    protected void handleMultiMessageDel(){
        SparseBooleanArray booleanArray = chatListView.getCheckedItemPositions();
        List<String>messageIdList = new ArrayList<>();
        for (int i = 0; i < booleanArray.size(); i++) {
            if (booleanArray.valueAt(i)) {
                int  position = booleanArray.keyAt(i);
                messageIdList.add(chatAdapter.list.get(position).getMessageId());
            }
        }
        if(!messageIdList.isEmpty()){
            conversation.removeMessage(messageIdList);
            if(chatAdapter != null){
                chatAdapter.removeImageTagCache(messageIdList);
            }
            refreshChatView(false, INVAILD_SELECTION);
            setDelChoiceMode(false);
            showDeleteMessageSuccess(true);
        }else{
            showDeleteMessageSuccess(false);
        }
    }

    /**
     * 设置功能面板
     * @param functionDrawRes 功能面板资源数组
     * @param functionNames 功能面板对应的名称
     */
    public void configFunctionPanel(List<Integer> functionDrawRes, List<String> functionNames){
        if(mContext == null){
            return;
        }
        if(functionDrawRes != null){
            this.functionDrawRes = functionDrawRes;
            this.functionImages.clear();
            for (int i = 0; i < functionDrawRes.size(); i++) {
                this.functionImages.add(mContext.getResources().getDrawable(functionDrawRes.get(i)));
            }
        }
        if(this.functionImages != null && functionNames != null){
            this.functionNames.clear();
            this.functionNames.addAll(functionNames);
            menuLayout.configureFuncPanel(this.functionImages, this.functionNames);
        }
    }

    public PopupWindow evaluateWindow;
    ListView evaluateView;
    JXEvaluateAdapter mJXEvaluateAdapter;
    List<Option> evaluateList = new ArrayList<Option>();
    Button closeButton ;
    TextView titleView;
    View evaluateHeaderView;
    JXSatisfication satisfication;
    JXMessage evaluateMessage;
    boolean isCloseSession;
    /**
     * 显示满意度评价的窗口
     * @param message
     */
    @SuppressLint("InflateParams")
    public void showEvaluateWindow(JXMessage evaluateMessage, boolean isCloseSession) {
        if (conversation.getSessionStatus() == SessionStatus.Waiting) {
            JXCommonUtils.showToast(mContext, getString(R.string.jx_cannot_evaluate_in_waiting));
            return;
        }
        this.evaluateMessage = evaluateMessage;
        this.isCloseSession = isCloseSession;
        new AsyncTask<Void, Void, Boolean>() {
            protected void onPreExecute() {
                showProgress(JXUiHelper.TAG_EVALUTE);
            };

            @Override
            protected Boolean doInBackground(Void... arg0) {
                satisfication = conversation.getSatisfication();
                return null;
            }

            protected void onPostExecute(Boolean result) {
                if(mContext == null){
                    return;
                }
                hideProgress();
                if (satisfication != null && satisfication.getOptions() != null) {
                    if (evaluateView == null) {
                        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                        evaluateView = (ListView)inflater.inflate(R.layout.jx_evaluate_pop_window,
                                null);
                        evaluateHeaderView = inflater.inflate(R.layout.jx_evaluate_item_header, null);
                        evaluateView.addHeaderView(evaluateHeaderView);
                        titleView = (TextView)evaluateHeaderView.findViewById(R.id.tv_title);
                        titleView.setText(getString(R.string.jx_invite_evaluate));
                        closeButton = (Button)evaluateHeaderView
                                .findViewById(R.id.btn_close_evaluate);
                        closeButton.setOnClickListener(JXBaseChatFragment.this);
                    }
                    // 创建PopupWindow对象
                    if (evaluateWindow == null) {
                        evaluateWindow = new PopupWindow(evaluateView, LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT, false);
                        evaluateList = satisfication.getOptions();
                        mJXEvaluateAdapter = new JXEvaluateAdapter(mContext, evaluateList,
                                satisfication);
                        mJXEvaluateAdapter.setJXChatFragmentListener(JXBaseChatFragment.this);
                        evaluateView.setAdapter(mJXEvaluateAdapter);
                        evaluateView.setOnItemClickListener(evaluateItemClickListener);
                        evaluateWindow.setBackgroundDrawable(new ColorDrawable(0));
                        // 设置点击窗口外边窗口消失
                        evaluateWindow.setOutsideTouchable(true);
                        evaluateWindow.setFocusable(true);
                        evaluateWindow.setAnimationStyle(R.style.JXAnimationPreview);
                    }
                    evaluateWindow.showAtLocation(rootLayout,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(menuLayout.contentText.getWindowToken(), 0);
                    }
                    evaluateWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                        @Override
                        public void onDismiss() {
                            backgroundAlpha(1.0f);
                        }
                    });
                    backgroundAlpha(0.5f);
                } else {
                    JXCommonUtils.showToast(mContext, "get satisfication failed!");
                }
            };
        }.execute();
    }

    /**
     * 设置popupWindow显示和关闭时window背景的透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        if(mContext == null){
            return;
        }
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    private JXEvaluateTask evaluateTask = null;

    @Override
    public void onEvaluateItemClick(int value) {
        if(evaluateWindow != null){
            evaluateWindow.dismiss();
            evaluateTask = new JXEvaluateTask(conversation, evaluateMessage ,  value );
            evaluateTask.setJXEvaluteTaskCallback(callback);
            evaluateTask.execute();
        }
    }

    /**
     * 评价item监听器
     */
    OnItemClickListener evaluateItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (satisfication.getSatisficationType() != 3) {
                final Option option = (Option) mJXEvaluateAdapter.getItem(position - 1);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.jx_submit_evaluation);
                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        onEvaluateItemClick(option.getValue());
                    }
                });
                builder.setNegativeButton(android.R.string.no, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                builder.create().show();
            }
        }
    };

    private JXEvaluteTaskCallback callback = new JXEvaluteTaskCallback() {

        public void onEvalutePre() {
            showProgress(JXUiHelper.TAG_EVALUTE);
        };

        @Override
        public void onEvaluteSuccess() {
            hideProgress();
            if (evaluateMessage != null) {
                evaluateMessage.setEvaluated();
                conversation.setEvaluated(false);
            }
            if (satisfication.isShowThanksMeg()) {
                JXImManager.McsUser.getInstance().saveEvaluateFeedbackMessage("",
                        satisfication.getThanksMsg());
            }
            JXUiHelper.getInstance().setRecEvaluateMessage(false);
            refreshChatView(false, INVAILD_SELECTION);
            if (isCloseSession) {
                requestCustomer(conversation.getSkillsId(), JXUiHelper.TAG_CLOSE_SESSION, JXBaseChatFragment.this);
            }
        }

        @Override
        public void onEvaluetError(int errorCode) {
            hideProgress();
            if (errorCode == JXEvaluateTask.EVALUATE_TIMEOUT) {
                JXCommonUtils.showToast(mContext, getString(R.string.jx_submit_evaluation_timeout));
            }else {
                JXCommonUtils.showToast(mContext, getString(R.string.jx_submit_evaluation_failed));
            }
            if (isCloseSession) {
                requestCustomer(conversation.getSkillsId(), JXUiHelper.TAG_CLOSE_SESSION, JXBaseChatFragment.this);
            }
        }
    };

    public PopupWindow quickQuestionWindow;
    ListView quickQuestionListView;
    TextView questionTitle;
    Button questionCloseBtn;
    View quickQuestionHeaderView;
    JXQuickQuestionAdapter questionAdapter;
    List<String> questionList = new ArrayList<String>();
    /**显示快捷提问窗口**/
    public void showQuickQuestionWindows(){
        new AsyncTask<Void, Void, List<String>>() {

            protected void onPreExecute() {
                showProgress(JXUiHelper.TAG_QUICK_QUESTION);
            };

            @Override
            protected List<String> doInBackground(Void... params) {
                questionList = JXImManager.McsUser.getInstance().getQuickQuestions();
                JXLog.e("question list = "+questionList);
                return questionList;
            }

            @SuppressLint("InflateParams")
            @Override
            protected void onPostExecute(List<String> result) {
                if(mContext == null){
                    return;
                }
                hideProgress();
                if (result == null) {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_quick_question_exception));
                    return;
                }
                if (result.size()==0) {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_quick_question_is_null));
                }else{
                    if (quickQuestionListView == null) {
                        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                                Context.LAYOUT_INFLATER_SERVICE);
                        quickQuestionListView = (ListView)inflater.inflate(R.layout.jx_evaluate_pop_window,
                                null);
                        quickQuestionHeaderView = inflater.inflate(R.layout.jx_evaluate_item_header, null);
                        quickQuestionListView.addHeaderView(quickQuestionHeaderView);
                        questionTitle = (TextView)quickQuestionHeaderView.findViewById(R.id.tv_title);
                        questionTitle.setText(getString(R.string.jx_commonly_used_quick_questions));
                        questionCloseBtn = (Button)quickQuestionHeaderView
                                .findViewById(R.id.btn_close_evaluate);
                        questionCloseBtn.setOnClickListener(JXBaseChatFragment.this);
                    }
                    // 创建PopupWindow对象
                    if (quickQuestionWindow == null) {
                        questionAdapter = new JXQuickQuestionAdapter(mContext, result);
                        quickQuestionListView.setAdapter(questionAdapter);
                        quickQuestionListView.setOnItemClickListener(quickQuestionItemListener);
                        quickQuestionWindow = new PopupWindow(quickQuestionListView, LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT, false);
                        quickQuestionWindow.setBackgroundDrawable(new ColorDrawable(0));
                        // 设置点击窗口外边窗口消失
                        quickQuestionWindow.setOutsideTouchable(true);
                        quickQuestionWindow.setFocusable(true);
                        quickQuestionWindow.setAnimationStyle(R.style.JXAnimationPreview);
                    }
                    quickQuestionWindow.showAtLocation(rootLayout,
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(menuLayout.contentText.getWindowToken(), 0);
                    }
                    backgroundAlpha(0.5f);
                    quickQuestionWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                        @Override
                        public void onDismiss() {
                            backgroundAlpha(1.0f);
                        }
                    });
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    OnItemClickListener quickQuestionItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String question = (String)questionAdapter.getItem(position-1);
            menuLayout.contentText.setText(question);
            menuLayout.contentText.setSelection(question.length());
            if (quickQuestionWindow!=null&&quickQuestionWindow.isShowing()) {
                quickQuestionWindow.dismiss();
            }
        }
    };

    public void sendRichText(JXCommodity commodity){
        if(commodity != null && mContext != null){
            try {
                RichTextMessage message = JXMessageUtil.createSendRichMessage(ChatType.CUSTOMER_SERVICE, username,
                        commodity.title, commodity.content, commodity.url , commodity.imgUrl , JXMessage.NO_BURN_AFTER_READ);
                JXImManager.Message.getInstance().sendMessage(message);
                refreshChatView(true, INVAILD_SELECTION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected synchronized void saveTmpSessionMsg(String tipsMessage, String skillsId){
        if(chatType == ChatType.CUSTOMER_SERVICE && sessionTipsMessage == null){
            sessionTipsMessage = JXUiHelper.getInstance().saveSessionMsg("", username, null);
        }
        if(chatType == ChatType.CUSTOMER_SERVICE && sessionTipsMessage != null){
            conversation.removeMessage(sessionTipsMessage.getMessageId());
            TextMessage message = (TextMessage)sessionTipsMessage;
            message.setContent(tipsMessage);
            conversation.addMessage(sessionTipsMessage);
            refreshChatView(false, INVAILD_SELECTION);
        }
    }

    protected synchronized void saveTmpCancelWaitMsg(String tipsMessage, String skillsId){
        if(chatType == ChatType.CUSTOMER_SERVICE && cancelWaitMessage == null){
             Map<String, Object> hashMap = new HashMap<String, Object>();
             hashMap.put(JXUiHelper.JX_ATTR_TYPE, JXUiHelper.JX_ATTR_TYPE_CANCEL_WAIT);
            cancelWaitMessage = JXUiHelper.getInstance().saveSessionMsg(tipsMessage, skillsId, hashMap);
        }
        if(chatType == ChatType.CUSTOMER_SERVICE && cancelWaitMessage!= null){
            conversation.removeMessage(cancelWaitMessage.getMessageId());
            TextMessage message = (TextMessage)cancelWaitMessage;
            message.setContent(tipsMessage);
            conversation.addMessage(cancelWaitMessage);
            refreshChatView(false, INVAILD_SELECTION);
        }
    }

    JXInputLayoutListener inputLayoutListener = new JXInputLayoutListener() {

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
            isVoiceRecording = true;
            return voiceHintView.pressToSpeak(v, event, new voiceRecorderCallBack() {

                @Override
                public void recordSuccess(String recordingPath, int recordDur) {
                    isVoiceRecording = false;
                    chatPresenter.sendVoiceMessage(recordingPath, recordDur);
                }
                public void recordAbandon() {
                    isVoiceRecording = false;
                };
            });
        }
    };

    public void deleteSessionTipsMsg(boolean isDelNormalTips, boolean isDelCancelWait){
        if(chatType == ChatType.CUSTOMER_SERVICE && sessionTipsMessage!= null 
                && isDelNormalTips){
            conversation.removeMessage(sessionTipsMessage.getMessageId());
            refreshChatView(false, INVAILD_SELECTION);
        }
        if (chatType == ChatType.CUSTOMER_SERVICE && cancelWaitMessage != null
                && isDelCancelWait) {
            conversation.removeMessage(cancelWaitMessage.getMessageId());
        }
    }
}
