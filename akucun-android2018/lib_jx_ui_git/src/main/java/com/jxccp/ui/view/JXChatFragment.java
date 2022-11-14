/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jxccp.im.JXErrorCode;
import com.jxccp.im.JXErrorCode.Mcs;
import com.jxccp.im.callback.JXMcsStatusListener;
import com.jxccp.im.callback.JXUserSelfQueueListener;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.Status;
import com.jxccp.im.chat.common.message.JXMessage.Type;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.JXUiHelper.MessageBoxListener;
import com.jxccp.ui.R;
import com.jxccp.ui.listeners.JXChatGeneralCallback;
import com.jxccp.ui.model.JXRequestCusServiceTask.RequestCusServiceCallback;
import com.jxccp.ui.utils.JXCommonUtils;

import java.util.List;

/**
 * 聊天界面的fragment实现类
 */
public class JXChatFragment extends JXBaseChatFragment
        implements JXMcsStatusListener, JXUserSelfQueueListener, RequestCusServiceCallback, MessageBoxListener {

    private String TAG = this.getClass().getSimpleName();

    public boolean isChatWithRobot = false;

    private String skillsId;

    private String skillsDisplayName;

    // 请求技能组时扩展信息 
    private String extendData;

    private Drawable onlineDrawable = null;

    private String agentNickName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setJXChatFragmentListener(this);
        if (type != CHATTYPE_MESSAGE_BOX) {
            JXImManager.McsUser.getInstance().addMcsStatusListener(this);
            JXImManager.McsUser.getInstance().addUserSelfQueueListeners(this);
            JXUiHelper.getInstance().addMessageBoxListener(this);
        }
        Bundle bundle = getArguments();
        skillsId = bundle.getString(CHAT_KEY);
        JXUiHelper.getInstance().setSkillsId(skillsId);
        skillsDisplayName = bundle.getString(CHAT_SKILLS_DISPLAYNAME);
        extendData = bundle.getString(EXTEND_DATA);
        boolean isNeedRequest = JXImManager.McsUser.getInstance().isNeedRequest() == null;
        agentNickName = isNeedRequest ? null : skillsDisplayName;
        Log.d(TAG, "chat fragment extendData: " + extendData);

        if (!TextUtils.isEmpty(skillsId)) {
            if (TextUtils.isEmpty(extendData)) {
                requestCustomer(skillsId, JXUiHelper.TAG_NOT_TRANSFER, this);
            } else {
                // 扩展数据不为空时，带上扩展信息
                requestCustomer(skillsId, extendData, JXUiHelper.TAG_NOT_TRANSFER, this);
            }
        }
    }

    @Override
    public void onDetach() {
        if (type != CHATTYPE_MESSAGE_BOX) {
            JXImManager.McsUser.getInstance().removeMcsStatusListener(this);
            JXImManager.McsUser.getInstance().removeUserSelfQueueListeners(this);
            JXUiHelper.getInstance().removeMessageBoxListener(this);
        } else {
            JXUiHelper.getInstance().notifyMessageBoxUpdate();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void configureView() {
        super.configureView();
        tipsCancelView.setOnClickListener(this);
        leaveMessageView.setOnClickListener(this);
        if (!TextUtils.isEmpty(skillsDisplayName)) {
            titleTextView.setText(skillsDisplayName);
        }
        onlineDrawable = getResources().getDrawable(R.drawable.jx_ic_online);
        onlineDrawable.setBounds(0, 0, onlineDrawable.getMinimumWidth(), onlineDrawable.getMinimumHeight());
        if (type == CHATTYPE_MESSAGE_BOX) {
            refreshLayout.setRefreshing(true);
            chatPresenter.loadMoreMessages(null);
            badgeView.setVisibility(View.GONE);
            menuLayout.setVisibility(View.GONE);
        } else {
            if (JXUiHelper.getInstance().getCurrentPosition() != -1) {
                tipsMessageView.setVisibility(View.VISIBLE);
                cancelLayout.setVisibility(View.VISIBLE);
                tipsMessageView.setText(getString(R.string.jx_waiting) + JXUiHelper.getInstance().getCurrentPosition());
            }
            badgeView.setImageResource(R.drawable.jx_message_box);
            badgeView.setVisibility(View.VISIBLE);
            menuLayout.setVisibility(View.VISIBLE);
            setUpBadgeUnread();
        }
    }

    @Override
    public boolean onMessageItemClick(final JXMessage jxMessage) {
        if (jxMessage.getType() == JXMessage.Type.NOTIFICATION) {
            requestCustomer(skillsId, JXUiHelper.TAG_CANCEL_WAIT, this);
            return true;
        }
        return super.onMessageItemClick(jxMessage);
    }

    int resArr = 0;

    @Override
    public boolean onMessageItemLongClick(final JXMessage message) {
        if (type == CHATTYPE_MESSAGE_BOX) {
            return true;
        } else if (message.getType() == JXMessage.Type.VOICE && isVoiceRecording) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alertDialog = null;
        if (message.getType() == Type.TEXT && !message.fromRobot()) {
            if (message.getStatus() == Status.FAILED) {
                resArr = R.array.jx_text_message_operation_item_extra;
            } else {
                resArr = R.array.jx_text_message_operation_item;
            }
        } else {
            if (message.getStatus() == Status.FAILED) {
                resArr = R.array.jx_other_message_operation_item_extra;
            } else {
                resArr = R.array.jx_other_message_operation_item;
            }
        }
        alertDialog = builder.setItems(resArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deleteMessage(message);
                        break;
                    case 1:
                        dialog.dismiss();
                        setDelChoiceMode(true);
                        break;
                    case 2:
                        if (resArr == R.array.jx_other_message_operation_item_extra) {
                            resendMessage(message);
                        } else {
                            copyTextMessage((TextMessage) message);
                        }
                        break;
                    case 3:
                        resendMessage(message);
                        break;
                    default:
                        break;
                }
            }
        }).create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
        return true;
    }

    @Override
    public void onMessageAvatarClick(final JXMessage jxMessage) {
    }

    @Override
    public void onUserSelfQueueUpdate(final String skillsId, final int currentPosition) {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tipsMessageView.setVisibility(View.VISIBLE);
                cancelLayout.setVisibility(View.VISIBLE);
                tipsMessageView.setText(getString(R.string.jx_waiting) + JXUiHelper.getInstance().getCurrentPosition());
            }
        });
    }

    @Override
    public void onUserSelfStatus(final String skillsId, final int status, final String nickName) {
        if (mHandler == null) {
            return;
        }
        JXLog.d("jxchatfragment status = " + status);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (JXUserSelfQueueListener.USER_STATUS_PENDING != status) {
                    hideProgress();
                }
                if (JXUserSelfQueueListener.USER_STATUS_ROBOT == status) {
                    isChatWithRobot = true;
                    JXUiHelper.getInstance().setHasRobot(true);
                    JXLog.d("jxchatfragment ,onUserSelfStatus , set robot true");
                    if (iv_right_layout.getVisibility() != View.VISIBLE) {
                        iv_right_layout.setVisibility(View.VISIBLE);
                    }
                    if (titleRightText.getVisibility() == View.VISIBLE) {
                        titleRightText.setVisibility(View.GONE);
                    }
                    titleRightView.setImageResource(R.drawable.jx_ic_transfer_agent);
                    title_right_iv_bottom_tv.setText(R.string.jx_turn_to_man);
                    iv_right_layout.setOnClickListener(JXChatFragment.this);
                    if (chatGeneralCallback != null) {
                        chatGeneralCallback.onChatSession(true);
                    }
                    menuLayout.voiceImageView.setVisibility(View.GONE);
                    menuLayout.expressionImageView.setVisibility(View.GONE);
                } else {
                    if (menuLayout.expressionImageView.getVisibility() != View.VISIBLE) {
                        menuLayout.expressionImageView.setVisibility(View.VISIBLE);
                    }
                    if (JXUserSelfQueueListener.USER_STATUS_PENDING != status) {
                        isChatWithRobot = false;
                        if (iv_right_layout.getVisibility() != View.INVISIBLE) {
                            iv_right_layout.setVisibility(View.INVISIBLE);
                        }
                        if (titleRightText.getVisibility() == View.VISIBLE) {
                            titleRightText.setVisibility(View.GONE);
                        }
                        if (chatGeneralCallback != null) {
                            chatGeneralCallback.onChatSession(false);
                        }
                        if (JXImManager.McsUser.getInstance().canSendAudio()) {
                            menuLayout.voiceImageView.setVisibility(View.VISIBLE);
                        } else {
                            menuLayout.voiceImageView.setVisibility(View.GONE);
                        }
                    }
                }
                switch (status) {
                    case JXUserSelfQueueListener.USER_STATUS_PENDING:
                        break;
                    case JXUserSelfQueueListener.USER_STATUS_WAITING:
                        menuLayout.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(skillsDisplayName)) {
                            titleTextView.setText(skillsDisplayName);
                        }

                        break;
                    case JXUserSelfQueueListener.USER_STATUS_INSERVICE:
                        if (JXUiHelper.getInstance().getJxCommodity() != null) {
                            sendRichText(JXUiHelper.getInstance().getJxCommodity());
                            JXUiHelper.getInstance().setJxCommodity(null);
                        }
                    case JXUserSelfQueueListener.USER_STATUS_RECALL:
                        menuLayout.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(nickName)) {
                            String title = getString(R.string.jx_agent_nick);
                            titleTextView.setText(String.format(title, nickName));
                        }
                        titleTextView.setCompoundDrawablePadding(JXCommonUtils.dip2px(mContext, 4));
                        titleTextView.setCompoundDrawables(onlineDrawable, null, null, null);
                        titleRightText.setVisibility(View.GONE);
                        iv_right_layout.setVisibility(View.VISIBLE);
                        titleRightView.setImageResource(R.drawable.jx_ic_end_of_the_session);
                        title_right_iv_bottom_tv.setText(R.string.jx_exit);
                        iv_right_layout.setOnClickListener(JXChatFragment.this);
                        deleteSessionTipsMsg(true, true);
                        tipsMessageView.setVisibility(View.GONE);
                        cancelLayout.setVisibility(View.GONE);
                        if (TextUtils.isEmpty(agentNickName)
                                && status == JXUserSelfQueueListener.USER_STATUS_INSERVICE) {
                            String tips = getString(R.string.jx_agent_inservice);
                            JXImManager.McsUser.getInstance().saveEvaluateFeedbackMessage(getString(R.string.jx_admin),
                                    TextUtils.isEmpty(nickName) ? String.format(tips, "")
                                            : String.format(tips, nickName));
                            refreshChatView(false, INVAILD_SELECTION);
                        }
                        agentNickName = nickName;
                        break;
                    case JXUserSelfQueueListener.USER_STATUS_ROBOT:
                        menuLayout.setVisibility(View.VISIBLE);
                        titleTextView.setText(nickName);
                        JXUiHelper.getInstance().setRobotNickname(nickName);
                        deleteSessionTipsMsg(true, true);
                        tipsMessageView.setVisibility(View.GONE);
                        cancelLayout.setVisibility(View.GONE);
                        break;
                    case JXUserSelfQueueListener.USER_STATUS_ENDED:
                        conversation.setEvaluated(false);
                        break;
                }
            }
        });
    }

    @Override
    public void onEnded(final String skillsId, final int reasonCode) {
        if (mHandler == null && mContext != null) {
            return;
        }
        JXLog.d("jxchatfragment , onended reason code = " + reasonCode);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (reasonCode == Mcs.END_NORMAL) {
                    menuLayout.setVisibility(View.GONE);
                    titleTextView.setCompoundDrawables(null, null, null, null);
                    String tips = getString(R.string.jx_agentx_has_ended_this_session);
                    saveTmpSessionMsg(TextUtils.isEmpty(agentNickName)
                            ? getActivity().getString(R.string.jx_agent_has_ended_this_session) : String.format(tips,
                            agentNickName), skillsId);
//                    if (!JXUiHelper.getInstance().isRecEvaluateMessage()) {
//                        JXImManager.McsUser.getInstance().saveMcsEndMessage();
//                    }
                    hiddenInput();
                } else if (reasonCode == Mcs.END_EXCEPTION) {
                    if (mContext != null) {
                        JXCommonUtils.showToast(mContext, getString(R.string.jx_cant_join_session));
                        getActivity().finish();
                        return;
                    }
                    menuLayout.setVisibility(View.GONE);
                } else if (reasonCode == Mcs.END_TIMEOUT) {
                    if (mContext != null) {
//                        menuLayout.setVisibility(View.GONE);
                        JXCommonUtils.showToast(mContext, getString(R.string.jx_agent_busy_try_again));
                        if (JXUiHelper.getInstance().isHasRobot()) {
                            requestCustomer(skillsId, JXUiHelper.TAG_TRANSFER_ROBOT, JXChatFragment.this);
                            tipsMessageView.setVisibility(View.GONE);
                            cancelLayout.setVisibility(View.GONE);
                        } else {
                            getActivity().finish();
                        }
//                        menuLayout.setVisibility(View.GONE);
//                        getActivity().finish();
                    }
                } else if (reasonCode == Mcs.END_CANCEL) {
                    if (mContext != null) {
                        if (JXUiHelper.getInstance().isHasRobot()) {
                            requestCustomer(skillsId, JXUiHelper.TAG_TRANSFER_ROBOT, JXChatFragment.this);
                            tipsMessageView.setVisibility(View.GONE);
                            cancelLayout.setVisibility(View.GONE);
                        } else {
                            getActivity().finish();
                        }
                    }
                } else if (reasonCode == Mcs.AFTER_WORK_NOT_ACCEPT) {
                    requestCustomer(skillsId, JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE, JXChatFragment.this);
                } else if (reasonCode == Mcs.CUSTOMER_CLOSE_SESSION) {
                    menuLayout.setVisibility(View.GONE);
                    titleTextView.setCompoundDrawables(null, null, null, null);
                    saveTmpSessionMsg(getActivity().getString(R.string.jx_this_session_has_ended), skillsId);
                    hiddenInput();
                    conversation.setEvaluated(false);
                } else if (reasonCode == Mcs.AUTO_CLOSE_SESSION) {
                    menuLayout.setVisibility(View.GONE);
                    titleTextView.setCompoundDrawables(null, null, null, null);
                    titleRightView.setVisibility(View.INVISIBLE);
                    hiddenInput();
                } else {
                    menuLayout.setVisibility(View.GONE);
                    titleTextView.setCompoundDrawables(null, null, null, null);
                    hiddenInput();
                }
            }
        });
    }

    @Override
    public void onMcsOffline(String mcsId) {
        if (mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                titleTextView.setCompoundDrawables(null, null, null, null);
            }
        });
    }

    private ProgressDialog progressDialog;

    @Override
    public void showProgress(int requestTag) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            if (requestTag == JXUiHelper.TAG_NOT_TRANSFER) {
                progressDialog.setMessage(getString(R.string.jx_contact_customer));
            } else if (requestTag == JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                progressDialog.setMessage(getString(R.string.jx_transfer_customer));
            } else if (requestTag == JXUiHelper.TAG_TRANSFER_ROBOT) {
                progressDialog.setMessage(getString(R.string.jx_transfer_robot));
            } else if (requestTag == JXUiHelper.TAG_CANCEL_WAIT) {
                progressDialog.setMessage(getString(R.string.jx_cancel_waiting));
            } else {
                progressDialog.setMessage("");
            }
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        } else {
            if (!progressDialog.isShowing()) {
                if (requestTag == JXUiHelper.TAG_NOT_TRANSFER) {
                    progressDialog.setMessage(getString(R.string.jx_contact_customer));
                } else if (requestTag == JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                    progressDialog.setMessage(getString(R.string.jx_transfer_customer));
                } else if (requestTag == JXUiHelper.TAG_TRANSFER_ROBOT) {
                    progressDialog.setMessage(getString(R.string.jx_transfer_robot));
                } else if (requestTag == JXUiHelper.TAG_CANCEL_WAIT) {
                    progressDialog.setMessage(getString(R.string.jx_cancel_waiting));
                } else {
                    progressDialog.setMessage("");
                }
                progressDialog.show();
            }
        }
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected int getChatType() {
        return CHATTYPE_CUSTOMER_SERVICE;
    }

    @Override
    public void onRequestCallback(int code, int tag, boolean isNeedFinishAty, String message) {
        if (mContext == null) {
            return;
        }
        JXLog.d("jxchatfragment,on request callbace " + " , code = " + code);
        if (code == 0) {
            if (tag != JXUiHelper.TAG_NOT_TRANSFER) {
                hideProgress();
            }
            return;
        } else {
            hideProgress();
            if (JXUiHelper.TAG_CANCEL_WAIT == tag) {
                if (code == JXErrorCode.NO_CONNECTION) {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_not_connect_server));
                } else {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_cancel_wait_failed));
                }
                return;
            } else if (JXUiHelper.TAG_CLOSE_SESSION == tag) {
                if (code == JXErrorCode.NO_CONNECTION) {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_not_connect_server));
                } else {
                    JXCommonUtils.showToast(mContext, getString(R.string.jx_close_session_execption));
                }
                return;
            }
        }
        if (code == JXErrorCode.Mcs.NOT_INSERVICE) {
            isNeedFinishAty = false;
            if (tag != JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                menuLayout.setVisibility(View.GONE);
            }
            showLeaveMessageDialog(false, code);
        } else if (code == JXErrorCode.Mcs.SKILLS_ID_NOT_EXIST) {
            JXCommonUtils.showToast(mContext, getString(R.string.jx_mcsid_not_exist));
            getActivity().finish();
        } else if (code == JXErrorCode.NETWORK_TIMEOUT) {
            if (tag == JXUiHelper.TAG_NOT_TRANSFER) {
                JXCommonUtils.showToast(mContext, getString(R.string.jx_request_customer_timeout));
            } else if (tag == JXUiHelper.TAG_TRANSFER_ROBOT) {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_transfer_robot_timeout));
            } else if (tag == JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_transfer_customer_timeout));
                isNeedFinishAty = false;
            }
            if (isNeedFinishAty) {
                getActivity().finish();
            }
        } else if (code == JXErrorCode.Mcs.NOT_INSERVICE_WITH_ROBOT) {
            showLeaveMessageDialog(true, code);
            return;
        } else if (code == JXErrorCode.Mcs.AFTER_WORK_NOT_ACCEPT) {
            if (tag != JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                menuLayout.setVisibility(View.GONE);
            }
            JXImManager.McsUser.getInstance().saveEvaluateFeedbackMessage(getString(R.string.jx_admin), message);
            refreshChatView(false, INVAILD_SELECTION);
            showLeaveMessageDialog(false, code);
        } else if (code == JXErrorCode.Mcs.QUEUE_USER_LIMIT_EXCEEDED) {
            isNeedFinishAty = false;
            if (tag != JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                menuLayout.setVisibility(View.GONE);
            }
            showLeaveMessageDialog(false, code);
        } else {
            if (tag == JXUiHelper.TAG_NOT_TRANSFER) {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_request_customerFailed));
            } else if (tag == JXUiHelper.TAG_TRANSFER_ROBOT) {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_transfer_robotFailed));
            } else if (tag == JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE) {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_transfer_customerFailed));
                isNeedFinishAty = false;
            }
            if (isNeedFinishAty) {
                getActivity().finish();
            }
        }
    }

    /**
     * 显示转接人工或机器人的窗口
     *
     * @param isCustomer
     */
    void showTransferCsDialog(final boolean isCustomer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (isCustomer) {
            builder.setMessage(R.string.jx_transfer_customer_service);
        } else {
            builder.setMessage(R.string.jx_transfer_robot_service);
        }
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int what) {
                dialogInterface.dismiss();
                int requestTag = JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE;
                if (isCustomer) {
                    requestTag = JXUiHelper.TAG_TRANSFER_CUSTOMER_SERVICE;
                } else {
                    requestTag = JXUiHelper.TAG_TRANSFER_ROBOT;
                }
                if (TextUtils.isEmpty(extendData)) {
                    requestCustomer(skillsId, requestTag, JXChatFragment.this);
                } else {
                    requestCustomer(skillsId, extendData, requestTag, JXChatFragment.this);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int what) {
                dialogInterface.dismiss();
                if (!isChatWithRobot) {
                    getActivity().finish();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 显示留言窗口
     */
    public void showLeaveMessageDialog(final boolean hasRobot, int errorCode) {
        String[] optionStr;
        if (hasRobot) {
            optionStr = new String[]{
                    mContext.getString(R.string.jx_leave_message_online),
                    mContext.getString(R.string.jx_transfer_robot_service),
                    mContext.getString(R.string.jx_left_out),
            };
        } else {
            optionStr = new String[]{
                    mContext.getString(R.string.jx_leave_message_online),
                    mContext.getString(R.string.jx_left_out),
            };
        }
        String title = getString(R.string.jx_customer_service_offline);
        if (errorCode == JXErrorCode.Mcs.QUEUE_USER_LIMIT_EXCEEDED) {
            title = getString(R.string.jx_queue_user_limit_exceeded);
        }
        new AlertDialog.Builder(mContext).setTitle(title)
                .setItems(optionStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(mContext, JXLeaveMsgActivity.class).putExtra("skillId",
                                        skillsId));
                                getActivity().finish();
                                break;
                            case 1:
                                if (hasRobot) {
                                    requestCustomer(skillsId, JXUiHelper.TAG_TRANSFER_ROBOT, JXChatFragment.this);
                                } else {
                                    getActivity().finish();
                                }
                                break;
                            case 2:
                                getActivity().finish();
                                break;
                            default:
                                break;
                        }
                    }
                }).create().show();
    }

    @Override
    protected void onMessageDeleteClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setMessage(R.string.jx_delete_messages_tips);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                handleMultiMessageDel();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    @Override
    protected void onTitleRightViewClick() {
        if (isChatWithRobot) {
            showTransferCsDialog(true);
        } else {
            closeSession();
        }
    }

    @Override
    protected void processOnclick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            requestCustomer(skillsId, JXUiHelper.TAG_CANCEL_WAIT, this);
        } else if (v.getId() == R.id.tv_leave_message) {
            requestCustomer(skillsId, JXUiHelper.TAG_CANCEL_WAIT, this);
            startActivity(new Intent(mContext, JXLeaveMsgActivity.class).putExtra("skillId", skillsId));
        } else if (v.getId() == R.id.iv_badge) {
            if (chatGeneralCallback != null) {
                chatGeneralCallback.onMessageBoxClick();
            }
        }
    }

    @Override
    public void showDeleteMessageError(boolean isMultiDel) {
        if (isMultiDel && mContext != null) {
            JXCommonUtils.showToast(mContext, getString(R.string.jx_cant_delet_message));
        }
    }

    @Override
    protected void onTitleRightTextClck() {
    }

    private JXChatGeneralCallback chatGeneralCallback = null;

    public void setJXChatGeneralCallback(JXChatGeneralCallback chatGeneralCallback) {
        this.chatGeneralCallback = chatGeneralCallback;
    }

    @Override
    public void onFunctionItemClick(int position, int drawableResId) {
        if (chatGeneralCallback != null) {
            chatGeneralCallback.onFunctionItemClick(position, drawableResId);
        }
    }

    public void onDisconnectWhenWaiting() {
    }

    @Override
    public void closeOnDisconnect() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                JXCommonUtils.showToast(mContext, getString(R.string.jx_disconnected_please_request_again));
            }
        });
        getActivity().finish();
    }


    public void closeSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.jx_confirm_close_session);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int what) {
                requestCustomer(skillsId, JXUiHelper.TAG_CLOSE_SESSION, JXChatFragment.this);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int what) {

            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    /**
     * 摄像
     */
    public void navToVideo() {
        if (mContext == null) {
            return;
        }
        final String[] optionStr = getResources().getStringArray(R.array.jx_choice_video);
        new AlertDialog.Builder(getActivity())
                .setItems(optionStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_PICK);
                            intent.setType("video/*");
                            intent.putExtra("return-data", true);
                            startActivityForResult(intent, VIDEO_REQUEST_CODE);
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), JXRecorderVideoActivity.class);
                            startActivityForResult(intent, JXChatView.GET_VIDEO_VIA_CAMERA);
                        }
                    }
                }).create().show();
    }

    @Override
    public void onMessageBoxLoaded(final int offset, final List<JXMessage> newMessages) {
        if (mContext == null) {
            return;
        }
        if (chatAdapter == null || mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (newMessages != null) {
                    if (newMessages.isEmpty()) {
                        JXCommonUtils.showToast(mContext,
                                getString(R.string.jx_no_more_lmsg));
                    } else {
                        if (offset == 0) {
                            chatAdapter.refreshMessageList(newMessages);
                        } else {
                            chatAdapter.addMessageList(newMessages);
                        }
                    }
                } else {
                    JXCommonUtils.showToast(mContext,
                            getString(R.string.jx_no_more_lmsg));
                }
            }
        });
    }

    @Override
    public void onMessageBoxLoadFailed() {
        if (mContext == null || mHandler == null) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                JXCommonUtils.showToast(mContext,
                        getString(R.string.jx_get_lmsg_failed));
            }
        });
    }

    @Override
    public void onMessageBoxUpdate() {
        if (mContext == null) {
            return;
        }
        if (mHandler == null) {
            return;
        }
        setUpBadgeUnread();
    }

    /**
     * 当前界面是否为消息中心
     *
     * @return
     */
    public boolean isMessagebox() {
        return type == CHATTYPE_MESSAGE_BOX;
    }

    @Override
    public void onUpdate() {
        setUpBadgeUnread();
    }

    @Override
    public void showErrorTips(int errorCode, String tips) {
        if (mContext == null) {
            return;
        }
        JXCommonUtils.showToast(mContext, tips);
    }

    /**
     * 显示为读数
     */
    private void setUpBadgeUnread() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int messageCount = JXUiHelper.getInstance().getMessageBoxUnreadCount();
                if (messageCount > 99) {
                    if (badgeTextView.getVisibility() != View.VISIBLE) {
                        badgeTextView.setVisibility(View.VISIBLE);
                    }
                    badgeTextView.setText("...");
                } else if (messageCount > 0) {
                    if (badgeTextView.getVisibility() != View.VISIBLE) {
                        badgeTextView.setVisibility(View.VISIBLE);
                    }
                    badgeTextView.setText(String.valueOf(messageCount));
                } else {
                    if (badgeTextView.getVisibility() == View.VISIBLE) {
                        badgeTextView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
