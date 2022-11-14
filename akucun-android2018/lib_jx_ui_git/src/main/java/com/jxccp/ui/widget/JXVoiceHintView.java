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

package com.jxccp.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxccp.im.util.VoiceRecorder;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.R;


public class JXVoiceHintView extends RelativeLayout {
    
    protected Context context;
    
    public ImageView hintBgImageView ;
    
    public ImageView voiceAmplitudeImageView;
    
    public TextView hintTextView;
    
    private Drawable [] voiceImages;
    
    private JXVoiceHintView mView;
    
    private VoiceRecorder voiceRecorder;
    
    public JXVoiceHintView(Context context){
        super(context);
        initChildView(context);
    }
    
    public JXVoiceHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChildView(context);
    }

    public void initChildView(Context context) {
        this.context = context;
        mView = this;
        View convertView = LayoutInflater.from(context).inflate(R.layout.jx_voice_record_hint, this);
        hintBgImageView = (ImageView)convertView.findViewById(R.id.iv_recording_hint_bg);
        voiceAmplitudeImageView = (ImageView)convertView.findViewById(R.id.iv_voic_amplitude);
        hintTextView = (TextView)convertView.findViewById(R.id.tv_recording_hint_text);
        
        voiceImages = new Drawable [] {
                getResources().getDrawable(R.drawable.jx_ic_voic_amplitude_1),
                getResources().getDrawable(R.drawable.jx_ic_voic_amplitude_2),
                getResources().getDrawable(R.drawable.jx_ic_voic_amplitude_3),
                getResources().getDrawable(R.drawable.jx_ic_voic_amplitude_4)
        };
        
        voiceRecorder = new VoiceRecorder(voiceImageHandler, 0);
    }
    
    private boolean recordOverMax = false ;
    int amplitude = 0 ;
    int dur = 0;
    private String recordingPath = null ;
    private int recordDur =0;
    
    private Handler voiceImageHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            amplitude = msg.arg1/1000;
            dur = msg.arg2;
            /**
             * 大于60秒自动截断发送
             */
            if (dur>60000) {
                recordOverMax = true ;
                mView.setVisibility(View.INVISIBLE);
                try {
                    recordDur = voiceRecorder.stopRecord();
                } catch (Exception e) {
                    JXLog.e("recording execption",e);
                }
                recorderCallBack.recordSuccess(recordingPath , recordDur);
                JXLog.e("recording path = "+recordingPath);
            }
            switch (amplitude) {
                case 0:
                    voiceAmplitudeImageView.setImageDrawable(voiceImages[0]);
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    voiceAmplitudeImageView.setImageDrawable(voiceImages[1]);
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    voiceAmplitudeImageView.setImageDrawable(voiceImages[2]);
                    break;
                default:
                    voiceAmplitudeImageView.setImageDrawable(voiceImages[3]);
                    break;
            }
        };
    };
    
    public voiceRecorderCallBack recorderCallBack = null;
    
    public boolean pressToSpeak(View v , MotionEvent event , voiceRecorderCallBack voiceRecorderCallBack){
        recorderCallBack = voiceRecorderCallBack;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setPressed(true);
                this.setVisibility(View.VISIBLE);
                hintBgImageView.setImageResource(R.drawable.jx_ic_mic);
                hintTextView.setText(R.string.jx_sliding_to_cancel);
                this.bringToFront();
                try {
                    recordingPath = voiceRecorder.startRecord(context);
                } catch (Exception e) {
                    JXLog.e("recording execption",e);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    hintTextView.setText(getResources().getString(R.string.jx_canel_send_voice));
                } else {
                    hintTextView.setText(getResources().getString(R.string.jx_sliding_to_cancel));
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                if (recordOverMax) {
                    recordOverMax = false;
                    return true ;
                }
                this.setVisibility(View.INVISIBLE);
                if (event.getY()<0) {
                    voiceRecorder.abandonRecord();
                    if(voiceRecorderCallBack != null){
                        voiceRecorderCallBack.recordAbandon();
                    }
                    return true;
                }
                try {
                    recordDur = voiceRecorder.stopRecord();
                } catch (Exception e) {
                    JXLog.e("recording execption",e);
                }
                if (recordDur<1000) {
                    this.setVisibility(View.VISIBLE);
                    voiceAmplitudeImageView.setVisibility(View.INVISIBLE);
                    hintBgImageView.setImageResource(R.drawable.jx_ic_recording_too_short_hint);
                    hintTextView.setText(R.string.jx_recording_too_short);
                    this.bringToFront();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            voiceAmplitudeImageView.setVisibility(View.VISIBLE);
                            mView.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                    return true;
                }
//                sendVoiceMessage(recordingPath , recordDur);
                if(voiceRecorderCallBack != null){
                    voiceRecorderCallBack.recordSuccess(recordingPath , recordDur);
                }
                JXLog.e("recording path = "+recordingPath);
                return true;
            default:
                this.setVisibility(View.INVISIBLE);
                if(voiceRecorderCallBack != null){
                    voiceRecorderCallBack.recordAbandon();
                }
                if (voiceRecorder!=null) {
                    voiceRecorder.abandonRecord();
                }
                return false;
        }
    }
    
    public interface voiceRecorderCallBack{
        public void recordSuccess(String recordingPath , int recordDur);
        public void recordAbandon();
    }
    
    

}
