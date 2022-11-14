package com.aikucun.akapp.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aikucun.akapp.R;

import java.text.MessageFormat;


/**
 * Created by micker on 2017/4/10.
 */

public class MToaster {
    public static final int NO_IMG = 0;
    public static final int IMG_ALERT = android.R.drawable.ic_dialog_alert;
    public static final int IMG_INFO = android.R.drawable.ic_dialog_info;

    private static void show(final Activity activity, final int resId, final int imgResId, final int duration) {
        if (activity == null)
            return;
        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater mInflater = LayoutInflater.from(context);
                mInflater = mInflater.cloneInContext(context);
                View view = mInflater.inflate(R.layout.base_toaster_layout, null);
                TextView mTextView = (TextView) view.findViewById(R.id.toast_text);
                ImageView mImageView = (ImageView) view.findViewById(R.id.toast_img);
                if (imgResId == NO_IMG) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.setImageResource(imgResId);
                }
                mTextView.setText(context.getResources().getString(resId));
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                mTextView.setMaxWidth(dm.widthPixels - 100);
                Toast toast = new Toast(context);
                // 设置Toast的位置
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(duration);
                // 让Toast显示为我们自定义的样子
                toast.setView(view);
                toast.show();
            }
        });
    }

    private static void show(final Activity activity, final String message, final int imgResId, final int duration) {
        if (activity == null)
            return;
        if (TextUtils.isEmpty(message))
            return;
        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = LayoutInflater.from(context).inflate(R.layout.base_toaster_layout, null);
                TextView mTextView = (TextView) view.findViewById(R.id.toast_text);
                ImageView mImageView = (ImageView) view.findViewById(R.id.toast_img);
                if (imgResId == NO_IMG) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                    mImageView.setImageResource(imgResId);
                }
                mTextView.setText(message);
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                mTextView.setMaxWidth(dm.widthPixels - 100);
                Toast toast = new Toast(context);
                // 设置Toast的位置
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setDuration(duration);
                // 让Toast显示为我们自定义的样子
                toast.setView(view);
                toast.show();
            }
        });
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     */
    public static void showLong(final Activity activity, int resId, int imgResId) {
        show(activity, resId, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     */
    public static void showShort(final Activity activity, final int resId, int imgResId) {
        show(activity, resId, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     */
    public static void showLong(final Activity activity, final String message, int imgResId) {
        show(activity, message, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     */
    public static void showShort(final Activity activity, final String message, int imgResId) {
        show(activity, message, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showLong(final Activity activity, final String message, final int imgResId, final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, imgResId, Toast.LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showShort(final Activity activity, final String message, final int imgResId, final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, imgResId, Toast.LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showLong(final Activity activity, final int resId, final int imgResId, final Object... args) {
        if (activity == null)
            return;
        String message = activity.getString(resId);
        showLong(activity, message, imgResId, args);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showShort(final Activity activity, final int resId, final int imgResId, final Object... args) {
        if (activity == null)
            return;
        String message = activity.getString(resId);
        showShort(activity, message, imgResId, args);
    }
}
