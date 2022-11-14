package com.aikucun.akapp.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

/**
 * Created by micker on 2017/7/3.
 */

public class PopWindowManager {

    public static FloatWindowSmallView  createSmallWindow(FloatWindowSmallView smallWindow,Context context) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);;
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        LayoutParams smallWindowParams;
        if (null == smallWindow) {
            smallWindow = new FloatWindowSmallView(context);
            smallWindowParams = new LayoutParams();
            smallWindowParams.type = LayoutParams.TYPE_APPLICATION;
            smallWindowParams.format = PixelFormat.RGBA_8888;
            smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
            smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
            smallWindowParams.width = FloatWindowSmallView.viewWidth;
            smallWindowParams.height = FloatWindowSmallView.viewHeight;
            smallWindowParams.x = 0;//screenWidth;
            smallWindowParams.y = screenHeight / 2;
            smallWindow.setParams(smallWindowParams);
        } else {
            smallWindowParams = (WindowManager.LayoutParams)smallWindow.getLayoutParams();
        }

        windowManager.addView(smallWindow, smallWindowParams);
        return smallWindow;
    }

    public static void removeSmallWindow(FloatWindowSmallView smallWindow, Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);;
            windowManager.removeView(smallWindow);
        }
    }

    public static boolean isWindowShowing(FloatWindowSmallView smallWindow) {
        return smallWindow.getParent() != null;
    }

    public static void setButtonText(FloatWindowSmallView smallWindow, String text) {
        smallWindow.button.setText(text);
    }


}
