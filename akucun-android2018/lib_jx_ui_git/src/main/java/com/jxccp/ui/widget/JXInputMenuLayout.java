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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jxccp.im.chat.common.message.JXChatStateExtension.JXChatState;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.ui.R;
import com.jxccp.ui.utils.JXCommonUtils;
import com.jxccp.ui.utils.JXSmileUtils;
import com.jxccp.ui.view.adapter.JXChatFunctionPanelAdapter;
import com.jxccp.ui.view.adapter.JXExpressionAdapter;
import com.jxccp.ui.view.adapter.JXExpressionPagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JXInputMenuLayout extends FrameLayout
        implements TextWatcher, View.OnClickListener, View.OnTouchListener , ViewPager.OnPageChangeListener{

    public static final int COMPOSING_STATE = 0;

    public static final int VOICE_RECORDING_STATE = 1;

    public static final int STOP_STATE = 2;

    public ImageView voiceImageView;

    public Button voicePressButton;

    public EditText contentText;

    public ImageView expressionImageView;

    public ImageView moreImageView;

    private ViewPager expressionPager;

    public LinearLayout dotsLayout;

    public FrameLayout expressionLayout;

    public GridView funcPanelView;

    public TextView sendMsgView;
    
    public String stateContent;

    private List<JXCircleImageView> dotViews = new ArrayList<>();

    private int currDot = 0;

    public JXInputMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChildView(context);
    }

    private void initChildView(Context context) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.jx_widget_inputmenu, this);
        voiceImageView = (ImageView)convertView.findViewById(R.id.iv_voice);
        voicePressButton = (Button)convertView.findViewById(R.id.btn_press_to_speak);
        expressionImageView = (ImageView)convertView.findViewById(R.id.iv_express);
        moreImageView = (ImageView)convertView.findViewById(R.id.more_option);
        contentText = (EditText)convertView.findViewById(R.id.textinput);
        expressionPager = (ViewPager)convertView.findViewById(R.id.pager_expression);
        dotsLayout = (LinearLayout)convertView.findViewById(R.id.ll_dots);
        expressionLayout = (FrameLayout)convertView.findViewById(R.id.fl_expression);
        funcPanelView = (GridView)convertView.findViewById(R.id.function_panel);
        sendMsgView = (TextView)findViewById(R.id.textSendButton);
        contentText.addTextChangedListener(this);
        reslist = getExpressionRes(35);
        // 初始化表情viewpager
        List<View> expressionViews = new ArrayList<View>();
        View pageOne = getGridChildView(1);
        View pageTwo = getGridChildView(2);
        expressionViews.add(pageOne);
        expressionViews.add(pageTwo);
        dotViews.get(currDot).setImageResource(R.color.grey800);
        JXExpressionPagerAdapter expressionAdapter = new JXExpressionPagerAdapter(expressionViews);
        expressionPager.setAdapter(expressionAdapter);
        expressionPager.addOnPageChangeListener(this);
        moreImageView.setOnClickListener(this);
        expressionImageView.setOnClickListener(this);
        voiceImageView.setOnClickListener(this);
        voicePressButton.setOnTouchListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        stateContent = s.toString();
        if (TextUtils.isEmpty(s)) {
            if (sendMsgView.getVisibility() == View.VISIBLE) {
                sendMsgView.setVisibility(View.INVISIBLE);
                moreImageView.setVisibility(View.VISIBLE);
            }
            sendStateMsgHandler.removeMessages(COMPOSING_STATE);
            sendStateMsgHandler.sendEmptyMessage(STOP_STATE);
        } else {
            if (sendMsgView.getVisibility() == View.INVISIBLE) {
                sendMsgView.setVisibility(View.VISIBLE);
                moreImageView.setVisibility(View.INVISIBLE);
            }
            sendStateMsgHandler.sendEmptyMessageDelayed(COMPOSING_STATE, 1000);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
    
    /**
     * 表情的list
     */
    private List<String> reslist;

    private List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 1; x <= getSum; x++) {
            String filename = "jx_expression_" + x;
            reslist.add(filename);
        }
        return reslist;
    }

    private void addIndicator(){
        int margin = JXCommonUtils.dip2px(getContext(), 4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(margin, 
                margin);
        params.gravity = Gravity.CENTER;
        params.setMargins(margin, margin, margin, margin*2);
        JXCircleImageView dot = new JXCircleImageView(getContext());
        dot.setImageResource(R.color.grey500);
        dotsLayout.addView(dot, params);
        dotViews.add(dot);
    }
    /**
     * 获取一页表情的view
     * 
     * @param i 页号，当前只有两页表情
     * @return
     */
    private View getGridChildView(int i) {
        View view = View.inflate(getContext(), R.layout.jx_expression_gridview, null);
        JXExpandGridView gv = (JXExpandGridView)view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 21);
            list.addAll(list1);
        } else if (i == 2) {
            list.addAll(reslist.subList(21, reslist.size()));
        }
        addIndicator();
        final JXExpressionAdapter expressionAdapter = new JXExpressionAdapter(getContext(), 1,
                list);
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (contentText.getVisibility() == View.VISIBLE) {
                    String filename = expressionAdapter.getItem(position);
                    @SuppressWarnings("rawtypes")
                    Class clz;
                    try {
                        clz = Class.forName("com.jxccp.ui.utils.JXSmileUtils");
                        Field field = clz.getField(filename);
                        contentText.append(
                                JXSmileUtils.getSmiledText(getContext(), (String)field.get(null)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    /**
     * 处理back事件
     * 
     * @return
     */
    public boolean onBackEvent() {
        if (funcPanelView.getVisibility() == View.VISIBLE) {
            funcPanelView.setVisibility(View.GONE);
            return true;
        }
        if (expressionLayout.getVisibility() == View.VISIBLE) {
            expressionLayout.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private JXChatFunctionPanelAdapter panelAdapter;

    public void configureFuncPanel(List<Drawable> functionImages, List<String> functionName) {
        panelAdapter = new JXChatFunctionPanelAdapter(getContext(), functionImages, functionName);
        funcPanelView.setAdapter(panelAdapter);
    }

    @Override
    public void onClick(View v) {
        if (R.id.more_option == v.getId()) {
            if (funcPanelView.getVisibility() == View.VISIBLE) {
                funcPanelView.setVisibility(View.GONE);
            } else {
                funcPanelView.setVisibility(View.VISIBLE);
                if (expressionLayout.getVisibility() == View.VISIBLE) {
                    expressionLayout.setVisibility(View.GONE);
                }
            }
            hiddenInput();
        } else if (v.getId() == R.id.iv_express) {
            if (expressionLayout.getVisibility() == View.VISIBLE) {
                expressionLayout.setVisibility(View.GONE);
            } else {
                if (funcPanelView.getVisibility() == View.VISIBLE) {
                    funcPanelView.setVisibility(View.GONE);
                }
                expressionLayout.setVisibility(View.VISIBLE);
            }
            hiddenInput();
        } else if (v.getId() == R.id.iv_voice) {
            if (expressionLayout.getVisibility() == View.VISIBLE) {
                expressionLayout.setVisibility(View.GONE);
            }
            if (funcPanelView.getVisibility() == View.VISIBLE) {
                funcPanelView.setVisibility(View.GONE);
            }
            if (contentText.getVisibility() == View.VISIBLE) {
                contentText.setVisibility(View.INVISIBLE);
                expressionImageView.setVisibility(View.INVISIBLE);
                voicePressButton.setVisibility(View.VISIBLE);
                voiceImageView.setImageResource(R.drawable.jx_ic_chat_keyboard);
                hiddenInput();
            } else {
                contentText.setVisibility(View.VISIBLE);
                expressionImageView.setVisibility(View.VISIBLE);
                voicePressButton.setVisibility(View.GONE);
                voiceImageView.setImageResource(R.drawable.jx_ic_chat_voice);
            }
        }

    }

    private InputMethodManager inputMethodManager;

    /**
     * 隐藏输入法
     * 
     * @param event
     */
    private void hiddenInput() {
        if (inputMethodManager == null) {
            inputMethodManager = (InputMethodManager)getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(contentText.getWindowToken(), 0);
        }
    }

    /**
     * 设置输入布局高度
     * 
     * @param menuHeight
     */
    public void setInputMenuHeight(int menuHeight) {
        if (menuHeight < 0) {
            return;
        }
        if (voiceImageView != null) {
            ViewGroup.LayoutParams voiceParams = voiceImageView.getLayoutParams();
            voiceParams.height = menuHeight;
            voiceImageView.setLayoutParams(voiceParams);
        }
        ViewGroup.LayoutParams moreParams = moreImageView.getLayoutParams();
        moreParams.height = menuHeight;
        moreImageView.setLayoutParams(moreParams);
        ViewGroup.LayoutParams expressionParams = expressionImageView.getLayoutParams();
        expressionParams.height = menuHeight;
        expressionImageView.setLayoutParams(expressionParams);
        ViewGroup.LayoutParams sendBtnParams = sendMsgView.getLayoutParams();
        sendBtnParams.height = menuHeight;
        sendMsgView.setLayoutParams(sendBtnParams);
        contentText.setMinimumHeight(menuHeight);
    }

    /**
     * 设置功能面板高度
     * 
     * @param height
     */
    public void setFunctionPanelHeight(int height) {
        if (height < 0) {
            return;
        }
        ViewGroup.LayoutParams functionParams = funcPanelView.getLayoutParams();
        functionParams.height = height;
        funcPanelView.setLayoutParams(functionParams);
    }

    /**
     * 设置表情面板高度
     * 
     * @param height
     */
    public void setExpressionPanelHeight(int height) {
        if (height < 0) {
            return;
        }
        ViewGroup.LayoutParams expressionPanelParams = expressionPager.getLayoutParams();
        expressionPanelParams.height = height;
        expressionPager.setLayoutParams(expressionPanelParams);
    }

    public JXInputLayoutListener listener;

    public void setJXInputLayoutListener(JXInputLayoutListener listener) {
        this.listener = listener;
    }

    public interface JXInputLayoutListener {

        /** 长按录音发送语音 **/
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (listener != null) {
            return listener.onPressToSpeakBtnTouch(v, event);
        }
        return false;
    }

    Handler sendStateMsgHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case COMPOSING_STATE:
                    sendChatStateMessage(JXChatState.composing);
                    break;
                case VOICE_RECORDING_STATE:
                    break;
                case STOP_STATE:
                    sendChatStateMessage(JXChatState.gone);
                    break;
                default:
                    break;
            }
        };
    };

    public void sendChatStateMessage(String state) {
        if (JXImManager.McsUser.getInstance().isPrepareMsgFlagOpen()) {
            JXImManager.McsUser.getInstance().sendChatStateMessage(state, stateContent);
            sendStateMsgHandler.removeMessages(COMPOSING_STATE);
        }
    }
    
    public void onDestory(){
        if (!TextUtils.isEmpty(stateContent)) {
            sendChatStateMessage(JXChatState.gone);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {
        dotViews.get(currDot).setImageResource(R.color.grey500);
        dotViews.get(position).setImageResource(R.color.grey800);
        currDot = position;
    }
}
