package com.aikucun.akapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by micker on 2017/8/23.
 */

public class LiveContentLayout extends LinearLayout {

    private View headerView;
    // 能滚动的view
    private View contentView;
    // 头部高度
    private int headerViewHeight;
    // listview scrollView...
    private View mTarget;


    public LiveContentLayout(Context context) {
        super(context);
    }

    public LiveContentLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveContentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getDefaultSize(0, widthMeasureSpec);
        // 一个头一个容器
        measureChild(headerView, widthMeasureSpec, heightMeasureSpec);
        measureChild(contentView, widthMeasureSpec, heightMeasureSpec);
        headerViewHeight = headerView.getMeasuredHeight();
        int measuredHeight = getDefaultSize(0, heightMeasureSpec) + headerViewHeight;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }
}
