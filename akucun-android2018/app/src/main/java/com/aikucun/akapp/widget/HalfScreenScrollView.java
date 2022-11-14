package com.aikucun.akapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ScrollView;

import com.aikucun.akapp.utils.TDevice;

/**
 * Created by ak123 on 2017/12/27.
 * 占屏幕一半的scrollview
 */

public class HalfScreenScrollView extends ScrollView {

    public HalfScreenScrollView(Context context) {
        super(context);
    }

    public HalfScreenScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public HalfScreenScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            DisplayMetrics metric = TDevice.getDisplayMetrics();
            int height = metric.heightPixels;   // 屏幕高度（像素）
            //设置scrollview高度为屏幕的一半
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height / 5 * 3, MeasureSpec.AT_MOST);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}