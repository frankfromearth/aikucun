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

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessage.Direction;
import com.jxccp.im.chat.common.message.VoiceMessage;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.R;
import com.jxccp.ui.view.adapter.JXChatAdapter;

import java.io.File;

public class JXVoicePlayListener implements OnClickListener{
    
    VoiceMessage voiceMessage;
    JXChatAdapter adapter;
    Context context;
    ImageView unreadDot;
    ImageView voicePlayingView;
    private AnimationDrawable voiceAnimation = null;
    
    MediaPlayer mediaPlayer =  null;
    public static boolean isPlaying = false;
    public static JXVoicePlayListener currentPlayListener = null;

    public JXVoicePlayListener(JXMessage message , ImageView unreadDot ,ImageView voicePlaying ,JXChatAdapter adapter , Context context){
        voiceMessage = (VoiceMessage)message;
        this.adapter = adapter;
        this.context = context;
        this.unreadDot = unreadDot;
        this.voicePlayingView = voicePlaying;
    }
    
    @Override
    public void onClick(View v) {
        JXLog.d("voice local url  = "+ voiceMessage.getLocalUrl());
        if (!voiceMessage.isRead() && unreadDot != null && unreadDot.getVisibility() == View.VISIBLE) {
            unreadDot.setVisibility(View.INVISIBLE);
            voiceMessage.setAsRead();
        }
        
        if (isPlaying) {
            if (JXUiHelper.getInstance().getPlayingMsgID() != null && JXUiHelper.getInstance().getPlayingMsgID().equals(voiceMessage.getMessageId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }
        
        playVoice(voiceMessage.getLocalUrl());
        
    }
    
    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        JXUiHelper.getInstance().setPlayingMsgID(voiceMessage.getMessageId());
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); 
                }

            });
            currentPlayListener = this;
            mediaPlayer.start();
            showAnim();
            isPlaying = true;
        } catch (Exception e) {
        }
    }

    public void stopPlayVoice() {
        if (voiceMessage.getDirect() == Direction.SEND) {
            voicePlayingView.setImageResource(R.drawable.voice_send_playing_a3);
        } else {
            voicePlayingView.setImageResource(R.drawable.voice_rec_playing_a3);
        }
        if (voiceAnimation != null){
            voiceAnimation.stop();
        }
     // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        JXUiHelper.getInstance().setPlayingMsgID(null);
    }
    
    public void showAnim(){
        if (voiceMessage.getDirect() == Direction.SEND) {
            voicePlayingView.setImageResource(R.anim.jx_voice_send);
        } else {
            voicePlayingView.setImageResource(R.anim.jx_voic_recive);
        }
        voiceAnimation = (AnimationDrawable) voicePlayingView.getDrawable();
        voiceAnimation.start();
    }

}
