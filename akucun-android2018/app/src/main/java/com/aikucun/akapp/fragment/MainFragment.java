package com.aikucun.akapp.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.MyViewPagerAdapter;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.ScaleTransitionPagerTitleView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by ak123 on 2018/1/3.
 */

public class MainFragment extends BaseFragment implements TabLayout.OnTabSelectedListener {

    private ViewPager viewPager;
    private MyViewPagerAdapter viewPagerAdapter;
    //TabLayout标签
    private String[] titles;
    private List<Fragment> fragments = new ArrayList<>();
    View view = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main_layout, container, false);
            ButterKnife.bind(this, view);
            init();
            initMagicIndicator2();
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    private void init() {
        titles = new String[]{getResources().getString(R.string.main_tab_activity_have_hand), "", getResources().getString(R.string.main_tab_trailer)};
        viewPager = view.findViewById(R.id.view_pager);
        fragments.add(new ActivingFragment());
        fragments.add(new CutGoodsFragment());
        fragments.add(new TrailerFragment());
        viewPagerAdapter = new MyViewPagerAdapter(getActivity().getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(viewPagerAdapter);
        showNotVipMsg();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }


    private void initMagicIndicator2() {
        MagicIndicator magicIndicator = view.findViewById(R.id.magic_indicator);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                if(index==1){
                    simplePagerTitleView.setBackgroundResource(R.drawable.textview_image_bg);
                }
                simplePagerTitleView.setText(titles[index]);
                simplePagerTitleView.setTextSize(18);
                simplePagerTitleView.setNormalColor(getResources().getColor(R.color.gray));
                simplePagerTitleView.setSelectedColor(getResources().getColor(R.color.color_accent));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (index == 0) {
                            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_ACTIVING_LIVES));
                        } else if (index == 1) {
                            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_WHOLESALE));
                        } else if (index == 2) {
                            EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_REFRESH_TRAILER));
                        }
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setStartInterpolator(new AccelerateInterpolator());
                indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
                indicator.setYOffset(UIUtil.dip2px(context, 39));
                indicator.setLineHeight(UIUtil.dip2px(context, 1));
                indicator.setColors(getResources().getColor(R.color.transparent));
                return indicator;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                if (index == 0) {
                    return 1.0f;
                } else if (index == 1) {
                    return 1.0f;
                } else {
                    return 1.0f;
                }
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }


    private void showNotVipMsg() {

        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (null != userInfo) {
            if (0 == userInfo.getViplevel()) {
                MyDialogUtils.showNotVipDialog(getActivity());
            }
        }
    }
}