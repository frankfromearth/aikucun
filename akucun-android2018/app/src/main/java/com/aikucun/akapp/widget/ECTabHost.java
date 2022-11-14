package com.aikucun.akapp.widget;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

/**
 * Tab Host
 * Created by jarry on 16/3/11.
 */
public class ECTabHost extends FragmentTabHost
{
    private String mCurrentTag;

    private String mNoTabChangedTag;

    public ECTabHost(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onTabChanged(String tag)
    {
        if (tag.equals(mNoTabChangedTag))
        {
            setCurrentTabByTag(mCurrentTag);
            
        } else
        {
            super.onTabChanged(tag);
            mCurrentTag = tag;
        }
    }

    public void setNoTabChangedTag(String tag)
    {
        this.mNoTabChangedTag = tag;
    }
}
