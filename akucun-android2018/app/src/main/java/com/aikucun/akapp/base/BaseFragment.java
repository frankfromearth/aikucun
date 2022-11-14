package com.aikucun.akapp.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.interf.BaseFragmentInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Base Fragment
 * Created by jarry on 16/3/11.
 */
public class BaseFragment extends Fragment implements View.OnClickListener, BaseFragmentInterface
{
    protected LayoutInflater mInflater;

    protected String titleName;

    protected String extraData;

    public AppContext getApplication()
    {
        return (AppContext) getActivity().getApplication();
    }
    View view = null;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
//            savedInstanceState)
//    {
//        this.mInflater = inflater;
//        View view = inflater.inflate(getLayoutId(), container, false);
//        return view;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            this.mInflater = inflater;
            view = inflater.inflate(getLayoutId(), container, false);
            ButterKnife.bind(this, view);
            initView(view);
            initData();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState)
//    {
//        super.onViewCreated(view, savedInstanceState);
//        Log.e("执行了onviewcreated方法","----------->>>>");
//        ButterKnife.bind(this, view);
////        initView(view);
////        initData();
//    }


    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {

    }


    protected int getLayoutId()
    {
        return R.layout.fragment_null;
    }

    protected View inflateView(int resId)
    {
        return this.mInflater.inflate(resId, null);
    }

    public boolean onBackPressed()
    {
        return false;
    }


    @Override
    public void initView(View view)
    {
    }

    @Override
    public void initData()
    {
    }

    @Override
    public void onClick(View v)
    {
    }

    public String getTitleName()
    {
        return titleName;
    }

    public void setTitleName(String titleName)
    {
        this.titleName = titleName;
    }

    public void updateData()
    {
    }

    public String getExtraData()
    {
        return extraData;
    }

    public void setExtraData(String extraData)
    {
        this.extraData = extraData;
    }

}
