package com.aikucun.akapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.TintContextWrapper;

/**
 * Created by micker on 2017/8/26.
 */

public class ContextHelper {

    public static Activity getActivity(Context context) {
        if (context instanceof Application) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof TintContextWrapper) {
            return getActivity(((TintContextWrapper) (context)).getBaseContext());
        }
        if (context instanceof ContextThemeWrapper) {
            return getActivity(((ContextThemeWrapper) (context)).getBaseContext());
        }

        if(context instanceof android.view.ContextThemeWrapper){
            return getActivity(((android.view.ContextThemeWrapper)context).getBaseContext());
        }

        return null;
    }
}
