package com.jxccp.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ak123 on 2017/12/21.
 * 计算软键盘高度
 */

public class ResizeLayout extends RelativeLayout {
    private ResizeLayout.OnResizeListener mListener;

    public void setOnResizeListener(ResizeLayout.OnResizeListener l) {
        this.mListener = l;
    }

    public ResizeLayout(Context context) {
        super(context);
    }

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.mListener != null) {
            this.mListener.OnResize(w, h, oldw, oldh);
        }

    }

    public interface OnResizeListener {
        void OnResize(int var1, int var2, int var3, int var4);
    }
}
