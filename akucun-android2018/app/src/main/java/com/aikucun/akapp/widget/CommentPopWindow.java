package com.aikucun.akapp.widget;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by micker on 2017/8/13.
 */

public class CommentPopWindow extends PopupWindow {

    private Context context;

    private EditText tvContent;
    private TextView sendBtn;
//    private Product product;
    private Object object;

    public CommentPopWindow(Context context) {
        super(context);
        init(context);
    }

    public CommentPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommentPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
        View view = View.inflate(context, R.layout.view_comment, null);

        tvContent = (EditText) view.findViewById(R.id.comment_et);
        sendBtn = (TextView)view.findViewById(R.id.sendBtn);

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//        		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x40000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        tvContent.setFocusable(true);
        tvContent.setFocusableInTouchMode(true);
//        tvContent.requestFocus();
        this.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        tvContent.setOnEditorActionListener(editorActionListener);

        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.FASTWINDOW_HIDE));

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAction();
            }
        });

        Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodUtils.showInputKeyboard(CommentPopWindow.this.context,tvContent);
            }
        },150);
    }

//    public void setProduct(Product product) {
//        if (null == product) return;
//        this.product = product;
//
//    }
    public void setObject(Object _object){
        if (_object == null) return;
        this.object = _object;
    }

    public void setHint(String hint) {
        if (!StringUtils.isEmpty(hint)) {
            tvContent.setHint(hint);
        }
    }

    public Object getObject() {
        return object;
    }

    private final EditText.OnEditorActionListener editorActionListener =
            new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                        //业务代码
                        sendAction();
                    }
                    return true;
                }
            };

    private void sendAction() {
        String content = tvContent.getText().toString();
        if (StringUtils.isEmpty(content))
            return;
        if (null != listener) {
            listener.onSendComment(object,content);
        }
        dismiss();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.FASTWINDOW_SHOW));
    }

    public void setListener(OnCommentEventListener listener) {
        this.listener = listener;
    }

    private OnCommentEventListener listener;
    public interface OnCommentEventListener
    {
        public void onSendComment(Object object, String content);
    }
}
