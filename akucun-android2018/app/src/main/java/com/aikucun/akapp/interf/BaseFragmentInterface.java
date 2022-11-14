package com.aikucun.akapp.interf;

import android.view.View;

/**
 * 基类fragment实现接口
 * Created by jarry on 16/6/3.
 */
public interface BaseFragmentInterface
{
    /**
     * View 初始化
     */
    public void initView(View view);

    /**
     * 数据初始化
     */
    public void initData();
}
