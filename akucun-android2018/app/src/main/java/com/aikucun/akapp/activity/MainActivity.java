package com.aikucun.akapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.discover.SendImgTextActivity;
import com.aikucun.akapp.activity.discover.SendVideoActivity;
import com.aikucun.akapp.api.callback.ActivingCallBack;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.CutGoodsCallBack;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.LatestIdeosListCallback;
import com.aikucun.akapp.api.entity.CutGoods;
import com.aikucun.akapp.api.entity.LatestIdeosList;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.DiscoverApiManager;
import com.aikucun.akapp.api.manager.IdeoManager;
import com.aikucun.akapp.api.manager.LiveApiManager;
import com.aikucun.akapp.api.manager.SystemApiManager;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.base.BaseFragment;
import com.aikucun.akapp.fragment.CartlistFragment;
import com.aikucun.akapp.fragment.DiscoverFragment;
import com.aikucun.akapp.fragment.FollowFragment;
import com.aikucun.akapp.fragment.MainFragment;
import com.aikucun.akapp.fragment.MyInfoFragment;
import com.aikucun.akapp.service.GeTuiIntentPushService;
import com.aikucun.akapp.service.GeTuiPushService;
import com.aikucun.akapp.storage.ExplosionGoodsManager;
import com.aikucun.akapp.storage.LiveInfosManager;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.ColorFilterImageView;
import com.aikucun.akapp.widget.ECTabHost;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igexin.sdk.PushManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 主界面
 * Created by jarry on 16/5/16.
 */
public class MainActivity extends PopActivity implements OnTabChangeListener {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(android.R.id.tabhost)
    ECTabHost mTabHost;

    @BindView(R.id.btn_left)
    ImageButton mButtonLeft;

    @BindView(R.id.btn_right)
    ImageButton mButtonRight;

    private static boolean versionCheck = false;


    private List<String> mTitles = new ArrayList<>();

    private BaseFragment mFragment;
    private Timer timer = null;


    private DrawerLayout drawerLayout;
    //    private SystemBarTintManager tintManager;
    private NavigationView navigationView;
    //    private ListView brand_list;
    private LinearLayout brand_layout;
    private List<LiveInfo> liveInfos = null;
    private List<CutGoods> cutGoodsList = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        //
        mToolBar.setTitle("");
        //        mToolBar.setNavigationIcon();
        setSupportActionBar(mToolBar);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.app_name);

        // Set up TabHost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        mTabHost.setOnTabChangedListener(this);

        //初始化推送接口
        configPushInfo();
        configWindow(mTabHost);

        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        navigationView = (NavigationView) findViewById(R.id.nav);
        View headerView = navigationView.getHeaderView(0);//获取头布局
//        brand_list = headerView.findViewById(R.id.brand_list);
        brand_layout = headerView.findViewById(R.id.brand_layout);
        initLisenter();
        getLiveStateOnService();
//        getLiveState();
//        UserInfo userInfo = AppContext.getInstance().getUserInfo();
//        RoundImageView userImage = headerView.findViewById(R.id.user_iv);
//        TextView user_name = headerView.findViewById(R.id.user_name);
//        if (userInfo != null) {
//            if (!TextUtils.isEmpty(userInfo.getAvatar()))
//                Glide.with(this).load(userInfo.getAvatar()).diskCacheStrategy(DiskCacheStrategy
//                        .ALL).into(userImage);
//            if (!TextUtils.isEmpty(userInfo.getName()))
//                user_name.setText(userInfo.getName());
//        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //item.setChecked(true);
                Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }

    private android.os.Handler MyHandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int messageCount = msg.arg1;
            TextView textView = mTabHost.getTabWidget()
                    .getChildTabViewAt(4).findViewById(R.id.unread_count);
            if (messageCount < 1) {
                textView.setVisibility(View.GONE);
            } else {
                if (messageCount > 99) {
                    textView.setText("99+");
                } else textView.setText(messageCount + "");
                textView.setVisibility(View.VISIBLE);
            }
            return false;
        }
    });

    @Override
    public void initData() {
        initTabs();
        mTabHost.setCurrentTab(0);
        timer = new Timer();
        timer.schedule(refreshDiscoverMsgTask, 1000, 1000 * 30); // 1s后执行task,经过1s再次执行
//示例
//       ImageView imageView = mTabHost.getTabWidget()
//                .getChildTabViewAt(2).findViewById(R.id.unread_count_discover);
//       imageView.setVisibility(View.VISIBLE);
        int messageCount = AppContext.get("unread_msg_count", 0);
        Message message = MyHandler.obtainMessage();
        message.arg1 = messageCount;
        message.what = 1;
        MyHandler.sendMessage(message);
        activeUpload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNotVipMsg();
        if (!versionCheck && AppContext.getInstance().shouldCheckUpgrade()) {
            versionCheck = true;
            checkVersionUpdate(false);
        }
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (null != userInfo) {
//            mButtonLeft.setImageResource(userInfo.getUnreadnum() > 0 ? R.drawable.icon_message_selected : R.drawable.icon_message);
        } else {
            startActivity(new Intent(this, WXEntryActivity.class));
        }
        setCartNewMsg();
        setFollowCount();
    }

    private void showNotVipMsg() {
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        if (null != userInfo) {
            if (0 != userInfo.getViplevel()) {
                imageDialog();
            }
        }
    }

    //图片对话框，只有每天的第一次才展示
    private void imageDialog() {
        String currentTime = DateUtils.timeStamp2Date2Day(System.currentTimeMillis());
        String saveTime = AppContext.get(AppConfig.LOGIN_DATE_TIME, null);
        //当每天第一次的时候获取图片并弹窗
        if (!currentTime.equals(saveTime)) {
            getLatestIdeosList();
            AppContext.set(AppConfig.LOGIN_DATE_TIME, currentTime);
        }
    }

    //获取获取弹窗图片
    private void getLatestIdeosList() {
        IdeoManager.getLatestIdeosList(this, 1, new LatestIdeosListCallback() {
            @Override
            public void onApiSuccess(LatestIdeosList data, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(data, call, jsonResponse);
                if(!TextUtils.isEmpty(data.getData())){
                    MyDialogUtils.showImageDialog(MainActivity.this,data.getData());
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }



    private void configPushInfo() {
        PushManager.getInstance().initialize(MainActivity.this.getApplicationContext(), GeTuiPushService.class);
        PushManager.getInstance().registerPushIntentService(MainActivity.this.getApplicationContext(), GeTuiIntentPushService.class);
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        SystemApiManager.requestReportPush(null, PushManager.getInstance().getClientid(this), null);
        if (null != userInfo) {
            // FIXME: 2018/1/4
            PushManager.getInstance().bindAlias(getApplicationContext(), userInfo.getUserid());
        }
    }


    private void initTabs() {
        //
//        BaseFragment liveFragment = new LiveFragment();
//        liveFragment.setTitleName("爱库存");
        BaseFragment mainFragment = new MainFragment();
        mainFragment.setTitleName(getString(R.string.app_name));
        createTabContent("home", getString(R.string.home_page), R.drawable.tab_icon_home, mainFragment);
        createTabContent("follow", getString(R.string.followed_page), R.drawable.tab_icon_follow, new FollowFragment());
        //发现
        createTabContent("discover", getResources().getString(R.string.discover), R.drawable.tab_icon_discover, new DiscoverFragment());
        createTabContent("cart", getString(R.string.cart_page), R.drawable.tab_icon_cart, new CartlistFragment());
        createTabContent("my", getString(R.string.my_page), R.drawable.tab_icon_my, new MyInfoFragment());
    }

    private void createTabContent(String tag, String title, int iconId, BaseFragment fragment) {
        TabHost.TabSpec tab = mTabHost.newTabSpec(tag);
        View indicator = LayoutInflater.from(getApplicationContext()).inflate(R.layout
                .tab_indicator, null);
        TextView tvTitle = indicator.findViewById(R.id.tab_title);
        ImageView iconImage = indicator.findViewById(R.id.tab_icon);

        tvTitle.setText(title);
        iconImage.setImageResource(iconId);

        tab.setIndicator(indicator);
        tab.setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return new View(MainActivity.this);
            }
        });

        if (fragment.getTitleName() != null) {
            mTitles.add(fragment.getTitleName());
        } else {
            mTitles.add(title);
        }

        mTabHost.addTab(tab, fragment.getClass(), null);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event) {
        super.onMessageEvent(event);

        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_FOLLOW_CLEAR_STATUS)) {
            //收藏商品数量
            AppContext.set("follow_product_count", 0);
            setFollowCount();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_UNFOLLOW_STATUS)) {
            //收藏商品数量
            int count = AppContext.get("follow_product_count", 0);
            --count;
            AppContext.set("follow_product_count", count);
            setFollowCount();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_FOLLOW_STATUS)) {
            //收藏商品数量
            int count = AppContext.get("follow_product_count", 0);
            count = count + 1;
            AppContext.set("follow_product_count", count);
            setFollowCount();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_CLEAR_CART)) {
            //购物车数量
            //AppContext.set("cart_new_msg_count", 0);
            AppContext.getInstance().setCartMsgCount(0);
            setCartNewMsg();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_JX_NEW_MSG_COUNT)) {
            //佳信客服新消息数量
            int messageCount = (int) event.content;
            Message message = MyHandler.obtainMessage();
            message.arg1 = messageCount;
            message.what = 1;
            MyHandler.sendMessage(message);
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_ADD_CART)) {
            //购物车数量
            int count = AppContext.getInstance().getCartMsgCount();  //AppContext.get("cart_new_msg_count", 0);
            count++;
            //AppContext.set("cart_new_msg_count", count);
            AppContext.getInstance().setCartMsgCount(count);
            setCartNewMsg();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_REFRESH_CART)) {
            //购物车数量
            setCartNewMsg();
        } else if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_HIDE_DISCOVER_NEW_MSG)) {
            //发现小红点
            ImageView imageView = mTabHost.getTabWidget()
                    .getChildTabViewAt(2).findViewById(R.id.unread_count_discover);
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        final int size = mTabHost.getTabWidget().getTabCount();
        final int currentIndex = mTabHost.getCurrentTab();
        for (int i = 0; i < size; i++) {
            View v = mTabHost.getTabWidget().getChildAt(i);
            if (i == currentIndex) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }

        supportInvalidateOptionsMenu();

        if (currentIndex == 4) {
            mButtonRight.setImageResource(R.drawable.selector_icon_config);
        } else if (currentIndex == 2) {
            // FIXME: 2018/1/4
            if (AppContext.getInstance().getUserInfo().getAllowUpload() == 1) {
                mButtonRight.setImageResource(R.drawable.selector_icon_btn_add);
            } else {
                mButtonRight.setImageResource(R.drawable.selector_icon_search);
            }
        } else {
            mButtonRight.setImageResource(R.drawable.selector_icon_search);
        }

        showOrHidePopWindow(0 == currentIndex || 1 == currentIndex);

        //
        /*final String tag = tabId;
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
                        .findFragmentByTag(tag);
                int rightButton = fragment.rightButton();
                if (rightButton > 0)
                {
                    mButtonRight.setImageResource(rightButton);
                    mButtonRight.setVisibility(View.VISIBLE);
                }
                else
                {
                    mButtonRight.setVisibility(View.INVISIBLE);
                }
            }
        }, 100);*/

        //
        setTitleText(mTitles.get(currentIndex));
        if (currentIndex == 4) {
            //关闭侧滑栏
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //修改左上角图片为新消息
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            if (null != userInfo) {
                mButtonLeft.setImageResource(userInfo.getUnreadnum() > 0 ? R.drawable.icon_message_selected : R.drawable.icon_message);
            } else {
                startActivity(new Intent(this, WXEntryActivity.class));
            }
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mButtonLeft.setImageResource(R.drawable.selector_icon_sideslip_screen);
        }
    }

    @Override
    @OnClick({R.id.btn_left, R.id.btn_right})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_left: {
                if (mTabHost.getCurrentTab() == 4) {
                    Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                    startActivity(intent);
                } else {
                    getLiveState();
                    drawerLayout.openDrawer(navigationView);
                }
            }
            break;

            case R.id.btn_right: {
/*
                // TODO TEST SCAN
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA).subscribe(new Action1<Boolean>()
                {
                    @Override
                    public void call(Boolean granted)
                    {
                        if (granted)
                        {
                            if (TDevice.isCameraAvailable())
                            {
                                // 开启扫描
                                startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class), 0);
                            }
                            else
                            {
                                showMessage("摄像头不可用或已被禁用 !");
                            }
                        }
                        else
                        {
                            showApplicationSettingDetails("相机");
                        }
                    }
                });
*/
                int currentIndex = mTabHost.getCurrentTab();
                if (currentIndex == 2) {
                    // FIXME: 2018/1/4
                    if (AppContext.getInstance().getUserInfo().getAllowUpload() == 1) {
                        // TODO: 2017/11/16 发现发布动态
                        showDialog();
                    } else {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (currentIndex < 4) {
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }

            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        String tag = mTabHost.getCurrentTabTag();
        BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (JCVideoPlayer.backPress()) {
            return;
        }
        if (fragment != null && fragment.onBackPressed()) {
            return;
        }

        exitApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showDialog() {
        BottomDialog.showBottomDialog(MainActivity.this, R.string.small_video, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                //拍小视频
                ActivityUtils.startActivity(MainActivity.this, SendVideoActivity.class);
            }
        }, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                // 从相册中选择
                ActivityUtils.startActivity(MainActivity.this, SendImgTextActivity.class);
            }
        });
    }

    /**
     * 定时器获取发现模块是否存在新消息
     */
    TimerTask refreshDiscoverMsgTask = new TimerTask() {

        @Override
        public void run() {
            // TODO: 2017/11/23 获取新动态
            if (!isRequestNewDiscoverMsg)
                checkDiscoverHasNewMsg();
        }
    };

    private boolean isRequestNewDiscoverMsg = false;

    /**
     * 检查是否有新的发现消息
     */
    private void checkDiscoverHasNewMsg() {
        isRequestNewDiscoverMsg = true;
        long lasttime = AppContext.get("discover_new_msg_lasttime", (long) 0);
        DiscoverApiManager.checkUpdate(this, lasttime, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                AppContext.set("discover_new_msg_lasttime", System.currentTimeMillis() / 1000);
                if (jsonObject != null) {
                    int status = jsonObject.getIntValue("status");
                    if (status == 1) {
                        //存在新的发现消息
                        ImageView imageView = mTabHost.getTabWidget()
                                .getChildTabViewAt(2).findViewById(R.id.unread_count_discover);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
                isRequestNewDiscoverMsg = false;
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                AppContext.set("discover_new_msg_lasttime", System.currentTimeMillis() / 1000);
                isRequestNewDiscoverMsg = false;
            }
        });
    }

    /**
     * 设置购物车数量
     */
    private void setCartNewMsg() {
        int count = AppContext.getInstance().getCartMsgCount();// AppContext.get("cart_new_msg_count", 0);
        TextView textView = mTabHost.getTabWidget()
                .getChildTabViewAt(3).findViewById(R.id.unread_count);
        if (count < 1) {
            textView.setVisibility(View.GONE);
        } else {
            if (count > 99) {
                textView.setText("99+");
            } else textView.setText(count + "");
            textView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 收藏商品数量
     */
    private void setFollowCount() {
        int count = AppContext.get("follow_product_count", 0);
        TextView textView = mTabHost.getTabWidget()
                .getChildTabViewAt(1).findViewById(R.id.unread_count);
        if (count < 1) {
            textView.setVisibility(View.GONE);
        } else {
            if (count > 99) {
                textView.setText("99+");
            } else textView.setText(count + "");
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        timer.purge();
        timer.cancel();
        timer = null;
        super.onDestroy();
    }

    /**
     * 获取品牌信息
     */
    private void getLiveState() {
        if (cutGoodsList == null || cutGoodsList.size() == 0) {
            if (ExplosionGoodsManager.getInstance().getCutGoodsList() != null) {
                cutGoodsList = ExplosionGoodsManager.getInstance().getCutGoodsList();
            }
        }
        if (liveInfos == null || liveInfos.size() == 0) {
            if (LiveInfosManager.getInstance().getLiveInfos() != null)
                liveInfos = LiveInfosManager.getInstance().getLiveInfos();
            if (liveInfos != null && liveInfos.size() > 0) initBrandInfo(liveInfos);
        }
    }

    private void initBrandInfo(List<LiveInfo> liveInfos) {
        brand_layout.removeAllViews();
        View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.header_activing_layout, null);
        brand_layout.addView(view1);
        for (int i = 0, size = liveInfos.size(); i < size; i++) {
            brand_layout.addView(getBrandView(liveInfos.get(i)));
        }

        View view2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.header_activing_layout, null);
        TextView textview = view2.findViewById(R.id.textview);
        textview.setText(R.string.cut_goods);

        if (cutGoodsList != null && cutGoodsList.size() > 0) {
            brand_layout.addView(view2);
            for (int i = 0, size = cutGoodsList.size(); i < size; i++) {
                brand_layout.addView(getBrandViewByCutGoods(cutGoodsList.get(i)));
            }
        }
    }

    private View getBrandViewByCutGoods(final CutGoods cutGoods) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_screen_brand_layout, null);
        TextView nameTv = view.findViewById(R.id.brand_name_tv);
        ColorFilterImageView heardIv = view.findViewById(R.id.headImage);
        if (!TextUtils.isEmpty(cutGoods.getPinpaiurl())) {
            Glide.with(MainActivity.this).load(cutGoods.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(heardIv);
            heardIv.setVisibility(View.VISIBLE);
        } else {
            heardIv.setVisibility(View.GONE);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(navigationView);
                Product product = CutGoods.fromCutGoods(cutGoods);
                if (!product.hasBegun()) {
                    MToaster.showShort(MainActivity.this, "该品牌活动暂未开始", MToaster.IMG_ALERT);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("notice", cutGoods.getContent());
                bundle.putSerializable("pinpai", product);
                ActivityUtils.startActivity(MainActivity.this, PinpaiActivity.class, bundle);
            }
        });
        nameTv.setText(cutGoods.getPinpaiming());
        return view;
    }

    /**
     * 获取品牌布局信息
     *
     * @param liveInfo
     * @return
     */
    private View getBrandView(final LiveInfo liveInfo) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_screen_brand_layout, null);
        TextView nameTv = view.findViewById(R.id.brand_name_tv);
        ColorFilterImageView heardIv = view.findViewById(R.id.headImage);
        if (!TextUtils.isEmpty(liveInfo.getPinpaiurl())) {
            Glide.with(MainActivity.this).load(liveInfo.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(heardIv);
            heardIv.setVisibility(View.VISIBLE);
        } else {
            heardIv.setVisibility(View.GONE);
        }
        nameTv.setText(liveInfo.getPinpaiming());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(navigationView);
                Bundle bundle = new Bundle();
                Product product = Product.fromLiveInfo(liveInfo);
                bundle.putSerializable("pinpai", product);
                ActivityUtils.startActivity(MainActivity.this, PinpaiActivity.class, bundle);
            }
        });
        return view;
    }

    private void initLisenter() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //隐藏悬浮按钮
                showOrHidePopWindow(false);
                getLiveState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                final int currentIndex = mTabHost.getCurrentTab();
                //首页时显示悬浮按钮
                if (currentIndex == 0) showOrHidePopWindow(true);
            }
        });
    }


    private void getLiveStateOnService() {
        LiveApiManager.getActivityToday(this, new ActivingCallBack() {
            @Override
            public void onSuccess(List<LiveInfo> liveInfos, Call call, Response response) {
                super.onSuccess(liveInfos, call, response);
                LiveInfosManager.getInstance().setLiveInfos(liveInfos);
            }
        });

        LiveApiManager.getCutGoodsLiving(this, new CutGoodsCallBack() {
            @Override
            public void onSuccess(List<CutGoods> cutGoods, Call call, Response response) {
                super.onSuccess(cutGoods, call, response);
                ExplosionGoodsManager.getInstance().setCutGoodsList(cutGoods);
            }
        });
//        LiveApiManager.getLiveState(MainActivity.this, 0, new LiveStateCallback() {
//            @Override
//            public void onApiSuccess(LiveStateResp liveStateResp, Call call, ApiResponse
//                    jsonResponse) {
//                super.onApiSuccess(liveStateResp, call, jsonResponse);
//            }
//
//            @Override
//            public LiveStateResp parseResponse(ApiResponse responseData) throws Exception {
//                return super.parseResponse(responseData);
//            }
//        });
    }

    private void activeUpload() {
//        String lastTime = AppContext.get("last_upload_active_time", "");
//        boolean isUpload = false;
//        if (TextUtils.isEmpty(lastTime)) {
//            isUpload = true;
//        }
//        final String nowTime = DateUtils.timeStamp2Date2Day(System.currentTimeMillis());
//        if (nowTime.equalsIgnoreCase(lastTime)) {
//            isUpload = false;
//        }
//        if (isUpload) {
        UsersApiManager.activeUpload(this, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
            }
        });
//        }
    }

    @Override
    public boolean isSupportSwipeBack() {
        return false;
    }


}
