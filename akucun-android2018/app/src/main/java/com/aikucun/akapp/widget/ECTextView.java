package com.aikucun.akapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.SCLog;

public class ECTextView extends AppCompatTextView
{

    public static final String MD_ICON_FONT = "MaterialIcons-Regular.ttf";

    public static final String FA_ICON_FONT = "Fontawesome.ttf";


    public ECTextView(Context context)
    {
        super(context);
        init();
    }

    public ECTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setViewAttributes(context, attrs, 0);
        init();
    }

    public ECTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setViewAttributes(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {

    }

    private void setViewAttributes(Context context, AttributeSet attrs, int defStyle)
    {
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ECTextView);
        float textSize = array.getDimension(R.styleable.ECTextView_textSize, getResources()
                .getDimension(R.dimen.text_size_icon));

        int fontFamily = array.getInteger(R.styleable.ECTextView_fontFamily, 0);
        String icon = array.getString(R.styleable.ECTextView_textIcon);
        getPaint().setTextSize(textSize);
        setIconFont(this, fontFamily, icon);

        array.recycle();
    }

    private void setIconFont(TextView tv, int fontfamily, String code)
    {
        String path = null;
        switch (fontfamily)
        {
            case 1: // material
                path = MD_ICON_FONT;
                break;
            case 2: // fontawesome
                path = FA_ICON_FONT;
                break;
            default:
                SCLog.error("字体错误");
                break;
        }
        if (null != path)
        {
            Typeface font = Typeface.createFromAsset(tv.getContext().getAssets(), path);
            tv.setTypeface(font);
        }

        if (null != code)
        {
            tv.setText(code);
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
//        mScaleSpring.addListener(mScaleSpringListener);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
//        mScaleSpring.removeListener(mScaleSpringListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return super.onTouchEvent(event);
    }

}
