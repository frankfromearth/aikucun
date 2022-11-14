package com.aikucun.akapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.fragment.CartRecycleFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by micker on 2017/7/9.
 */

public class CartRecycleActivity extends PopActivity {

    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText.setText("回收清单");

        CartRecycleFragment fragment = new CartRecycleFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.realtabcontent, fragment, "");
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cart_recycle;
    }


    @Override

    @OnClick()
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
        }
    }
}
