package com.aikucun.akapp.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.TDevice;

import java.lang.reflect.Field;

/**
 * Created by micker on 2017/7/3.
 */

public class FloatWindowSmallView extends LinearLayout {

    public Button button;
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;

    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight, titleBarHeight, navBarHeight;

    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;

    private float screenWidth, screenHeight;

    public FloatWindowSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        button = (Button)findViewById(R.id.button);
        screenWidth = TDevice.getScreenWidth();
        screenHeight = TDevice.getScreenHeight();
    }

    private void checkStartY() {

        // 手指移动的时候更新小悬浮窗的位置
//        SCLog.debug("" + yInScreen);
//        if (yInScreen <= getTitleBarHeight())
//            yInScreen = getTitleBarHeight();
//        else if (yInScreen >= screenHeight-getStatusBarHeight() - getTitleBarHeight()-getNavBarHeight())
//            yInScreen = screenHeight - getTitleBarHeight()-getNavBarHeight();
    }

    long tempTime = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempTime = System.currentTimeMillis();
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                checkStartY();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                checkStartY();
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                long nowTime = System.currentTimeMillis();
                if (nowTime - tempTime < 150){
                    tempTime = 0;
                    openBigWindow();
                }else{
                    if (xInScreen >= screenWidth/2) {
                        xInScreen = screenWidth - viewWidth;
                    }
                    else {
                        xInScreen = 0;
                    }
                    updateViewPosition();
                }
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
//                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    openBigWindow();
//                }
//                else {
//                    if (xInScreen >= screenWidth/2) {
//                        xInScreen = screenWidth - viewWidth;
//                    }
//                    else {
//                        xInScreen = 0;
//                    }
//                    updateViewPosition();
//                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params
     *            小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        if (onWindowItemListener != null) {
            onWindowItemListener.onEvent(0);
        }
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    private int getTitleBarHeight() {
        if (0 == titleBarHeight) {
            titleBarHeight = (int) TDevice.dpToPixel(48);
        }
        return titleBarHeight;
    }

    private int getNavBarHeight() {
        if (0 == navBarHeight) {
            navBarHeight = (int) TDevice.dpToPixel(48);
        }
        return navBarHeight;
    }

    private  OnSmallWindowItemListener onWindowItemListener;

    public void setOnWindowItemListener(OnSmallWindowItemListener onWindowItemListener) {
        this.onWindowItemListener = onWindowItemListener;
    }

    public interface OnSmallWindowItemListener
    {
        public void onEvent(int event);
    }

}
