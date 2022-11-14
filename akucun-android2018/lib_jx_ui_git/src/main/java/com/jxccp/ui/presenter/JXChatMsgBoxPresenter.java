package com.jxccp.ui.presenter;

import android.os.AsyncTask;

import com.jxccp.im.callback.JXEventListner;
import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.TextMessage;
import com.jxccp.im.chat.manager.JXEventNotifier;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.exception.JXException;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.model.JXRequestCusServiceTask.RequestCusServiceCallback;
import com.jxccp.ui.view.JXChatView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Date: 2016年11月10日
 * @author xugs
 * </p>
 */
public class JXChatMsgBoxPresenter implements JXChatPresenter, JXEventListner{

    private static final int DEFAULT_PAGE = 5;
    private static final int INIT_OFFSET = -5;

    private int offset = INIT_OFFSET;

    private JXChatView mChatView;

    public JXChatMsgBoxPresenter(JXChatView chatView){
        this.mChatView = chatView;
        JXImManager.Message.getInstance().registerEventListener(this);
    }

    @Override
    public void loadMoreMessages(String lastMesageId) {
        new AsyncTask<Void, Void, List<JXMessage>>() {
            @Override
            protected List<JXMessage> doInBackground(Void... params) {
                try {
                    offset = offset+DEFAULT_PAGE;
                    List<JXMessage> jxMessages = JXImManager.McsUser.getInstance().getMessagesInBox(offset, DEFAULT_PAGE);
                    if(jxMessages == null){
                        jxMessages = new ArrayList<JXMessage>();
                    }
                    JXUiHelper.getInstance().setMessageBoxUnreadCount(0);
                    if(offset == 0){
                        Collections.reverse(jxMessages);
                    }
                    return jxMessages;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(java.util.List<JXMessage> result) {
                if(mChatView != null){
                    mChatView.interruptRefresh();
                    if(result == null){
                        mChatView.onMessageBoxLoadFailed();
                    }else{
                        mChatView.onMessageBoxLoaded(offset, result);
                    }
                }
                if(result == null){
                    offset -= DEFAULT_PAGE;
                } else if(result != null && result.isEmpty()){
                    offset -= DEFAULT_PAGE;
                }else{
                    setMessagesAsRead();
                }
            };
        }.execute();
    }

    private void setMessagesAsRead(){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    JXImManager.McsUser.getInstance().setMessagesAsRead();
                } catch (JXException e) {
                    e.printStackTrace();
                }
                return null;
            }
            protected void onPostExecute(Void result) {
                if(mChatView != null){
                    mChatView.refreshChatView(false, JXChatView.INVAILD_SELECTION);
                }
            };
        }.execute();
    }
    
    @Override
    public void onEvent(JXEventNotifier eventNotifier) {
        if(eventNotifier.getEvent() == JXEventNotifier.Event.MESSAGE_PUSH){
            offset = INIT_OFFSET;
            loadMoreMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        JXImManager.Message.getInstance().removeEventListener(this);
    }

    @Override
    public void sendTextMessage(String text) {
    }

    @Override
    public void sendImageMessage(String filePath) {
    }

    @Override
    public void sendVoiceMessage(String filePath, int voiceDur) {
    }

    @Override
    public void sendVideoMessage(String filePath, int videoDur) {
    }

    @Override
    public void resendMessage(JXMessage jxMessage) {
    }

    @Override
    public void copyTextMessage(TextMessage message) {
    }

    @Override
    public void deleteMessage(JXMessage jxMessage) {
    }

    @Override
    public void requestCustomer(String skillsId, int Tag, RequestCusServiceCallback callback) {
    }

    @Override
    public void requestCustomer(String skillsId, String extendData, int Tag,
            RequestCusServiceCallback callback) {
    }

}
