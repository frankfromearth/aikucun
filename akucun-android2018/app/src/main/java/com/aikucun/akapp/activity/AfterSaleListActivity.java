package com.aikucun.akapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.fragment.AfterSaleListFragment;

import butterknife.BindView;

/**
 * Created by jarry on 2017/6/11.
 */

public class AfterSaleListActivity extends BaseActivity
{
    private AfterSaleListFragment fragment;

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_after_sale_list;
    }


    @Override
    public void initView()
    {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.after_sales_record);

        fragment = new AfterSaleListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.realtabcontent, fragment, "");
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public void initData()
    {
    }

}
