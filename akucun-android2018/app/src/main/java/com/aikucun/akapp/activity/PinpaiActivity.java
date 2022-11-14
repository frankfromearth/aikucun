package com.aikucun.akapp.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.adapter.BrandAdapter;
import com.aikucun.akapp.api.callback.ApiBaseCallback;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.callback.ProductsSKUListCallback;
import com.aikucun.akapp.api.callback.ResultCallback;
import com.aikucun.akapp.api.callback.SKUListCallback;
import com.aikucun.akapp.api.callback.SyncProductsCallback;
import com.aikucun.akapp.api.entity.Comment;
import com.aikucun.akapp.api.entity.CutGoods;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.api.manager.CommentsApiManager;
import com.aikucun.akapp.api.manager.LiveApiManager;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.api.response.SynProductsResp;
import com.aikucun.akapp.fragment.BaseProductFragment;
import com.aikucun.akapp.storage.ExplosionGoodsManager;
import com.aikucun.akapp.storage.LiveInfosManager;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.DisplayUtils;
import com.aikucun.akapp.utils.InputMethodUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.SCLog;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.SystemShareUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.aikucun.akapp.widget.CommentPopWindow;
import com.aikucun.akapp.widget.ExpandTextView;
import com.aikucun.akapp.widget.FastForwardPopWindow;
import com.aikucun.akapp.widget.MyDialogUtils;
import com.aikucun.akapp.widget.ShareView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.androidadvance.topsnackbar.TSnackbar;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_BUY;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT_DELETE;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_COMMENT_REPLY;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_FOLLOW_ACTION;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_FORWARD;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_HIDE_PROGRESS;
import static com.aikucun.akapp.adapter.ProductAdapter.PRODUCT_EVENT_SAVEIMAGE;

/**
 * Created by micker on 2017/7/9.
 */

public class PinpaiActivity extends PopActivity implements SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnLoadMoreListener, BrandAdapter.OnItemEventListener {

    @BindView(R.id.tv_title)
    TextView mTitleText;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.btn_right)
    ImageButton btnRight;
    //    @BindView(R.id.realtabcontent)
//    FrameLayout realtabcontent;
//    @BindView(R.id.base_layout)
//    LinearLayout baseLayout;
    @BindView(R.id.view_layout)
    CoordinatorLayout view_layout;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.noticeTv)
    ExpandTextView noticeTextView;

    //    private Product product;
    private String liveId;
    private List<LiveInfo> liveInfos;
    private BrandAdapter brandAdapter;
    private ProductSKU selectSKU;
    private boolean isShowData = false;
    private Timer timer = null;
    private MyTimerTask myTimerTask;
    private String notice;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        if (intent.hasExtra("notice")) {
            notice = intent.getStringExtra("notice");
        }
        Product product = (Product) intent.getSerializableExtra("pinpai");
        liveId = product.getLiveid();
        mTitleText.setText(product.getPinpai());
        ProductManager.getInstance().setLiveId(liveId);
        brandAdapter = new BrandAdapter(this);
        brandAdapter.setOnItemEventListener(this);
        brandAdapter.setMore(R.layout.view_load_more, this);
        brandAdapter.setNoMore(R.layout.view_nomore);
        recyclerView.setRefreshListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, DisplayUtils.dip2px(this, 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(true);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(brandAdapter);
//        LiveFragment fragment = new LiveFragment();
//        fragment.setLiveId(product.getLiveid());
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.base_layout, fragment, "");
//        fragmentTransaction.commitAllowingStateLoss();
        configWindow(null);
        getData(0);
        syncProducts();
        initNoticeView();
        // getLiveState(false);
    }

    @Override
    public void initData() {
//        ProductManager.getInstance().setLiveId(product.getLiveid());
//        onInitialZhuanFa();

        mToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProducts();
            }
        });

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                if (newState == 0){
//                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                    if (layoutManager instanceof LinearLayoutManager) {
//                        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
//                        //获取最后一个可见view的位置
//                        int lastItemPosition = linearManager.findLastVisibleItemPosition();
//                        //获取第一个可见view的位置
//                        int firstItemPosition =linearManager.findFirstVisibleItemPosition();
////                        MToaster.showShort(PinpaiActivity.this,lastItemPosition+"",MToaster.IMG_INFO);
////                        MToaster.showShort(PinpaiActivity.this,firstItemPosition+"",MToaster.IMG_INFO);
////                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastItemPosition + 1 == brandAdapter.getItemCount()) {
////                        //最后一个itemView的position为adapter中最后一个数据时,说明该itemView就是底部的view了
////                        //需要注意position从0开始索引,adapter.getItemCount()是数据量总数
////                    }
////                    //同理检测是否为顶部itemView时,只需要判断其位置是否为0即可
////                    if (newState == RecyclerView.SCROLL_STATE_IDLE && firstItemPosition == 0) {}
//                    }
//                }
//
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pinpai;
    }


    private void syncProducts() {
        Product product = ProductManager.getInstance().getLastXuHaoProduct(liveId);
        int lastId = 0;
        if (product != null) lastId = product.getLastxuhao();
        LiveApiManager.syncProductsByLiveId(this, liveId, lastId + "", new SyncProductsCallback() {
            @Override
            public void onSuccess(SynProductsResp synProductsResp, Call call, Response response) {
                super.onSuccess(synProductsResp, call, response);
                recyclerView.setRefreshing(false);
                if (isShowData) getData(0);
                isShowData = false;
                if (synProductsResp.products != null && synProductsResp.products.size() > 0) {
                    showMessageTip("已更新 " + synProductsResp.products.size() + " 件商品");
                }
                startTime(synProductsResp.period);
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }


    private void showMessageTip(String msg) {
        if (null != view_layout) {
            try {
                TSnackbar snackbar = TSnackbar.make(view_layout, msg, TSnackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.parseColor("#fd643c"));
                TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R
                        .id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.show();
            } catch (Exception e) {
                SCLog.error(e.getLocalizedMessage());
            }
        }
    }

    private void initNoticeView() {

        try {
//            if (AppContext.getInstance().isVip()) {
            noticeTextView.setVisibility(View.VISIBLE);
            //String notice = LiveManager.getInstance().getNotice(this.liveId);
            if (TextUtils.isEmpty(notice)) {
                notice = LiveInfosManager.getInstance().getNotice(this.liveId);
            }
            noticeTextView.setText(notice);
            int color = noticeTextView.getContext().getResources().getColor(R.color.color_accent);
            noticeTextView.getContentText().setTextColor(color);
//                noticeTextView.getTextPlus().setTextColor(color);
//            }

            //处理预告、活动内容
            int currPosition = Integer.MAX_VALUE;
            RecyclerView.LayoutManager lm = recyclerView.getRecyclerView().getLayoutManager();
            if (lm != null && lm instanceof LinearLayoutManager) {
                currPosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
            }
//               setHeaderProduct(LiveManager.getInstance().getYugaoProducts(this.liveId));
            if (currPosition == 0)
                recyclerView.getRecyclerView().scrollToPosition(0);
        } catch (Exception e) {
            SCLog.error(e.getLocalizedMessage());
        }
    }

    @Override

    @OnClick({R.id.btn_right})
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_right:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
    }

    private void getProducts() {
        if (liveInfos == null) {
            getLiveState(true);
        } else {
            showChooseBrandDialog();
        }
    }

    /**
     * 选择品牌
     */
    private void showChooseBrandDialog() {

        BottomDialog.showBrandIndefiniteDialog(PinpaiActivity.this, getString(R.string.choose_brand), liveInfos, new BottomDialog.IBrandLiseneter() {
            @Override
            public void onClick(LiveInfo liveInfo) {
                updateSelectedProduct(liveInfo);
            }
        });
    }

    private void updateSelectedProduct(LiveInfo liveInfo) {

        if (!TextUtils.isEmpty(liveInfo.getLiveid())) {
            mTitleText.setText(liveInfo.getPinpaiming());
            liveId = liveInfo.getLiveid();
            ProductManager.getInstance().setLiveId(liveId);
            //ProductManager.getInstance().setLiveId(liveInfo.getLiveid());
            getData(0);
//            LiveFragment fragment = new LiveFragment();
//            fragment.setLiveId(liveInfo.getLiveid());
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.base_layout, fragment, "");
//            fragmentTransaction.commitAllowingStateLoss();
//            ProductManager.getInstance().setLiveId(liveInfo.getLiveid());
//            onInitialZhuanFa();

        }
    }

    /**
     * 获取品牌信息
     *
     * @param isShowChoose
     */
    private void getLiveState(final boolean isShowChoose) {
        if (liveInfos == null || liveInfos.size() == 0) {
            //liveInfos = LiveManager.getInstance().liveStateResp.getLiveinfos();

            ArrayList<LiveInfo> liveList = new ArrayList<>();
            liveList.addAll(LiveInfosManager.getInstance().getLiveInfos());
            liveList.addAll(ExplosionGoodsManager.getInstance().toLiveInfos());
            liveInfos = liveList;

//            liveInfos = LiveInfosManager.getInstance().getLiveInfos();
            if (liveInfos != null && liveInfos.size() > 0) {
                if (isShowChoose) getProducts();
            }
        }

    }

    private void getData(int start) {
        List<Product> products = ProductManager.loadProducts(start, liveId);
        if (start == 0) {
            brandAdapter.clear();
            if (products == null || products.size() == 0) {
                recyclerView.setRefreshing(true);
                isShowData = true;
            }
        }
        recyclerView.setRefreshing(false);
        brandAdapter.addAll(products);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTime();
    }

    @Override
    public void onRefresh() {
        recyclerView.setRefreshing(true);
        getData(0);
    }

    @Override
    public void onLoadMore() {
        getData(brandAdapter.getCount());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppConfig.MessageEvent event)
    {
        super.onMessageEvent(event);

        if (event.messageId.equalsIgnoreCase(AppConfig.MESSAGE_EVENT_SYNC_LIVEDATA))
        {
            String liveId = (String) event.content;
            if (liveId.matches(this.liveId)) {
                syncProducts();
            }
        }
    }

    @Override
    public void onEvent(int event, final Product product, final Object object, int position) {
        switch (event) {
            case PRODUCT_EVENT_SAVEIMAGE: {
                showProgress("");
            }
            break;
            case PRODUCT_EVENT_HIDE_PROGRESS: {
                cancelProgress();
            }
            break;

            case PRODUCT_EVENT_BUY: {
                ProductSKU sku = (ProductSKU) object;
                SCLog.debug("-> Buy : " + sku.getChima());
                selectSKU = sku;

                boolean isDirectBuy = false;
                CutGoods cutGoods = ExplosionGoodsManager.getInstance().getLiveInfo(product.getLiveid());
                if (cutGoods != null) {
                    isDirectBuy = (cutGoods.getBuymodel() > 0);
                }
                else {
                    LiveInfo liveInfo = LiveInfosManager.getInstance().getLiveInfo(product.getLiveid());
                    isDirectBuy = (liveInfo != null && liveInfo.getBuymodel() > 0);
                }

                // LiveInfo liveInfo = LiveManager.getInstance().getLiveById(product.getLiveid());
                if (isDirectBuy) {
                    // 直购模式
                    Intent intent = new Intent(PinpaiActivity.this, DirectBuyActivity.class);
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
                    intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_SKU, sku.getId());
                    startActivityForResult(intent, 0);
//                    overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
//                    PinpaiActivity.this.startActivity(intent);
                    break;
                }

                requestBuyProduct(product, sku);
            }
            break;
            case PRODUCT_EVENT_FORWARD: {
                showProductForwardDialog(product, object);
            }
            break;
            case PRODUCT_EVENT_COMMENT: {
                sendComment(product, null);
            }
            break;
            case PRODUCT_EVENT_COMMENT_REPLY: {
//
//                屏蔽回复，
//                final Comment comment = (Comment)object;
//                sendComment(product,comment.getName());
            }
            break;
            case PRODUCT_EVENT_COMMENT_DELETE: {
                final Comment comment = (Comment) object;
                deleteComment(product, comment);
            }
            break;

            case PRODUCT_EVENT_FOLLOW_ACTION: {
                if (product.getFollow() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PinpaiActivity.this);
                    builder.setTitle("取消关注该商品?").setNegativeButton(R.string.cancel, null).setPositiveButton
                            (R.string.sure, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    toggleFollowProduct(product, object);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                } else {
                    toggleFollowProduct(product, object);
                }
            }
            break;
        }
    }

    protected void toggleFollowProduct(final Product product, Object object) {
        final int follow = product.getFollow() == 1 ? 0 : 1;

        ProductApiManager.followProduct(PinpaiActivity.this, product.getId(), follow, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                product.setFollow(follow);
                ProductManager.getInstance().updateProduct(product);
                MToaster.showShort(PinpaiActivity.this, (1 == follow) ? "已添加关注 ！" : "已取消关注 ！", MToaster.IMG_INFO);
                if (1 == follow) {
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_FOLLOW_STATUS, product));
                } else {
                    EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_UNFOLLOW_STATUS, product));

                    if (isNeedToRemoveUnfollowdProduct()) {
                        brandAdapter.remove(product);
                    }
                }
                brandAdapter.notifyDataSetChanged();
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    protected void updateSKUS(final List<Product> products, final BaseProductFragment.UpdateSKUListener listener) {

        if (products == null || listener == null || products.size() <= 0) {
            if (listener != null) {
                listener.onUpdateSuccess(products);
            }
            return;
        }

        ArrayList<String> ids = new ArrayList<>();

        for (Product product : products) {
            ids.add(product.getId());
        }

        ProductApiManager.updateSKUProduct(PinpaiActivity.this, ids, new ProductsSKUListCallback(products) {
            @Override
            public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(productSKUs, call, jsonResponse);
                if (listener != null) {
                    listener.onUpdateSuccess(getResultProducts());
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if (listener != null) {
                    listener.onUpdateFailed(getResultProducts());
                }
            }
        });
    }


    protected void deleteComment(final Product product, final Comment comment) {
        if (comment.getPinglunzheID().equalsIgnoreCase(AppContext.getInstance().getUserId())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PinpaiActivity.this);
            builder.setTitle("删除评论").setNegativeButton(R.string.cancel, null).setPositiveButton
                    (R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            product.removeComment(comment);
                            CommentsApiManager.cancelComment(PinpaiActivity.this, product.getId(), comment.getId(), new ApiBaseCallback() {
                                @Override
                                public void onApiSuccess(Object o, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(o, call, jsonResponse);
                                }
                            });
                            brandAdapter.notifyDataSetChanged();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }


    /**
     * 显示转发对话框
     *
     * @param product
     * @param object
     */
    private void showProductForwardDialog(final Product product, final Object object) {
        boolean showSingleImgSelected = false, showMoreImgSelected = false;
        final ArrayList<String> imagePath = (ArrayList<String>) object;
        if (imagePath.size() == 9 || imagePath.size() == 0) {
            showSingleImgSelected = false;
            showMoreImgSelected = false;
        } else if (imagePath.size() > 4 && imagePath.size() < 9) {
            showSingleImgSelected = false;
            showMoreImgSelected = true;
        } else {
            showSingleImgSelected = true;
            showMoreImgSelected = true;
        }
        final float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        String btnStr = getResources().getString(R.string.forward);
        //商品类型0：普通商品2：活动商品
        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
            btnStr = getResources().getString(R.string.forward) + "+" + addMoney + "元";
        }

        ClipboardManager cm = (ClipboardManager) PinpaiActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(FastForwardPopWindow.getFaredContent(product.weixinDesc()));
        cm.setPrimaryClip(ClipData.newPlainText("akucun", FastForwardPopWindow.getFaredContent(product.weixinDesc())));

        MyDialogUtils.showProductForwardDialog(PinpaiActivity.this, "1、点击'去分享'后若无微信和QQ选项，可在'更多'中打开\n2、商品描述已复制，可以长按'粘贴'", btnStr, showSingleImgSelected, showMoreImgSelected, new MyDialogUtils.IProductForwardListener() {
            @Override
            public void onResult(boolean singleSelected, boolean moreSelected) {
                if (!singleSelected && !moreSelected) {
                    forwardProduct(product, object);
                } else {
                    if (singleSelected) {
                        final ArrayList<String> imagePaths = new ArrayList<String>();
                        ShareView shareView = new ShareView(PinpaiActivity.this);
                        shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                            @Override
                            public void afterSaveBitmap(String localPath) {
                                imagePaths.add(localPath);
                                forwardProduct(product, imagePaths);
                            }
                        });
                        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
                            shareView.setImageUrls(imagePath, FastForwardPopWindow.getFaredContent(product.weixinDesc()), product.getPinpaiid());
                        } else
                            shareView.setImageUrls(imagePath, product.weixinDesc(), product.getPinpaiid());

                    } else {
                        // TODO: 2017/12/8 多文字变图片
                        ShareView shareView = new ShareView(PinpaiActivity.this);
                        shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                            @Override
                            public void afterSaveBitmap(String localPath) {
                                imagePath.add(0, localPath);
                                forwardProduct(product, imagePath);
                            }
                        });
                        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
                            shareView.setImageUrls(null, FastForwardPopWindow.getFaredContent(product.weixinDesc()), product.getPinpaiid());
                        } else
                            shareView.setImageUrls(null, product.weixinDesc(), product.getPinpaiid());

                    }

                }
            }

            @Override
            public void onCancel() {
                cancelProgress();
            }
        });
    }

    public boolean isNeedToRemoveUnfollowdProduct() {
        return false;
    }

    protected void forwardProduct(Product product, Object object) {
        cancelProgress();
        List<String> imagesPath = (List<String>) object;
        if (PinpaiActivity.this instanceof PopActivity) {
            ((PopActivity) PinpaiActivity.this).setShareProduct(product);
        }
        float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        if (product.weixinDesc().indexOf("¥") > -1 && addMoney > 0.0 && product.getProductType() == 0) {
            SystemShareUtils.shareMultipleImage(PinpaiActivity.this, FastForwardPopWindow.getFaredContent(product.weixinDesc()), imagesPath);
        } else {
            SystemShareUtils.shareMultipleImage(PinpaiActivity.this, product.weixinDesc(), imagesPath);
        }


        if (null == product.getId() || product.hasNoSku()) {//非商品
            return;
        }
        ProductManager.getInstance().setForwardIndex(product.getXuhao());
        ProductManager.getInstance().setLiveId(product.getLiveid());
    }

    protected void sendComment(Product product, String hint) {

        if (!AppContext.getInstance().isVip()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PinpaiActivity.this);
            builder.setTitle("非会员不支持评论，请先购买会员资格")
                    .setNegativeButton("不，谢谢", null);
            builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // startActivity(new Intent(PinpaiActivity.this, MemberActivity.class));
                }
            });
            builder.show();
            return;
        }


        CommentPopWindow popWindow = new CommentPopWindow(PinpaiActivity.this);
        popWindow.showAtLocation(recyclerView, Gravity.BOTTOM, 0, 0);
        popWindow.setObject(product);
        if (!StringUtils.isEmpty(hint)) {
            popWindow.setHint("回复：" + hint);
        }

        popWindow.setListener(new CommentPopWindow.OnCommentEventListener() {
            @Override
            public void onSendComment(final Object object, final String content) {
                final Product mProduct = (Product) object;
                CommentsApiManager.sendComment(PinpaiActivity.this, mProduct.getId(), content, new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);
                        MToaster.showShort(PinpaiActivity.this, R.string.replay_success, MToaster.IMG_INFO);

                        UserInfo userInfo = AppContext.getInstance().getUserInfo();
                        Comment comment = new Comment();
                        comment.setId(jsonObject.getString("commentid"));
                        comment.setContent(content);
                        comment.setName(userInfo.getName());
                        comment.setPinglunzheID(AppContext.getInstance().getUserId());
                        mProduct.addComment(comment);
                        brandAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                    }
                });
            }
        });
    }


    /* 添加商品到购物车 */
    protected void requestBuyProduct(final Product product, final ProductSKU sku) {
        //
        if (!AppContext.get(AppConfig.UDK_REMARK_DATA, true)) {
            // new AppGoodsManager(brandAdapter, product, sku, "", PinpaiActivity.this).requestBuyProduct(sku, null);
            addCart(product, "", sku);
            return;
        }
        final View view = View.inflate(PinpaiActivity.this, R.layout.view_buy_check, null);
        final EditText editText = (EditText) view.findViewById(R.id.comment_et);

        AlertDialog.Builder builder = new AlertDialog.Builder(PinpaiActivity.this);
        builder.setTitle("确认加入购物车？").setView(view)
                .setNegativeButton("不下单", null);
        builder.setPositiveButton("下单", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String remark = editText.getText().toString();
                addCart(product, remark, sku);
//                new AppGoodsManager(brandAdapter, product, sku, remark, PinpaiActivity.this).requestBuyProduct(sku, null);
            }
        });
        builder.show();
        InputMethodUtils.showInputKeyboard(PinpaiActivity.this, editText);

    }

    /**
     * 添加到购物车
     *
     * @param product
     * @param remark
     * @param sku
     */
    private void addCart(final Product product, final String remark, final ProductSKU sku) {
        showProgress(getString(R.string.loading));
        ProductApiManager.buyProduct(PinpaiActivity.this, sku.getProductid(), sku.getId(), remark, "", new
                ResultCallback(PinpaiActivity.this) {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(apiResponse, call, jsonResponse);

                        cancelProgress();

                        int count = selectSKU.getShuliang();
                        count--;
                        selectSKU.setShuliang(count);
                        brandAdapter.notifyDataSetChanged();

                        MToaster.showShort(PinpaiActivity.this, "已添加到购物车", MToaster.IMG_INFO);
                        EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.MESSAGE_EVENT_ADD_CART));
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (60018 == code) {
                            //纠正库存
                            ProductApiManager.getSKUProduct(PinpaiActivity.this, sku.getProductid(), new SKUListCallback(product) {
                                @Override
                                public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                                    super.onApiSuccess(productSKUs, call, jsonResponse);
                                    brandAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
    }

    private void startTime(final long time) {
        if (timer != null) {
            stopTime();
        }
        if (myTimerTask != null) {
            myTimerTask.cancel();
        }
        myTimerTask = new MyTimerTask();
        timer = new Timer();
        trackSkus();
        timer.schedule(myTimerTask, time * 1000, 1000 * time);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            syncProducts();
        }

    }


    private void stopTime() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    private void trackSkus() {
        long syncsku = AppContext.get("sync_sku", (long) 0);
        LiveApiManager.trackSkus(this, liveId, syncsku, new JsonDataCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                super.onSuccess(jsonObject, call, response);
                if (jsonObject != null) {
                    long tempsku = jsonObject.getLong("lastupdate");
                    AppContext.set("sync_sku", tempsku);
                    String dataList = responseData.getJsonObject().getString("skus");
                    List<ProductSKU> msgs = JSON.parseArray(dataList, ProductSKU.class);
                    updateSku(msgs);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
            }
        });
    }

    private void updateSku(List<ProductSKU> skus) {
        if (skus == null) {
            Log.e("商品的sku为空：", "-------------->");
            return;
        }
        for (int i = 0, size = skus.size(); i < size; i++) {
            Product product = ProductManager.getInstance().findProductById(skus.get(i).getProductid());
            if (product != null && product.getId().equals(skus.get(i).getProductid())) {
                product.updateSKU(skus);
            }
        }
    }

}
