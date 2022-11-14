
package com.aikucun.akapp.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * [简要描述]: 显示和隐藏软键盘 控制
 * [详细描述]:
 *
 * @author [Jarry]
 * @date [Created 2014-2-24]
 * @package [com.easycity.manager.utils]
 * @see [InputMethodUtils]
 * @since [EasyCityManager]
 */
public class InputMethodUtils
{
    public static void showInputKeyboard(Context context, View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, 0);
    }

    public static void hideInputKeyboard(Context context, View view)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
