//package com.aikucun.akapp.fragment;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.Typeface;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.AlertDialog;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.aikucun.akapp.AppContext;
//import com.aikucun.akapp.R;
//import com.aikucun.akapp.activity.MemberActivity;
//import com.aikucun.akapp.adapter.ProductAdapter;
//import com.aikucun.akapp.api.callback.ApiResponse;
//import com.aikucun.akapp.api.callback.JsonDataCallback;
//import com.aikucun.akapp.api.callback.LiveStateCallback;
//import com.aikucun.akapp.api.callback.TrackProductsCallback;
//import com.aikucun.akapp.api.entity.Product;
//import com.aikucun.akapp.api.entity.UserInfo;
//import com.aikucun.akapp.api.manager.CommentsApiManager;
//import com.aikucun.akapp.api.manager.LiveApiManager;
//import com.aikucun.akapp.api.manager.UsersApiManager;
//import com.aikucun.akapp.api.response.LiveStateResp;
//import com.aikucun.akapp.api.response.TrackProductsResp;
//import com.aikucun.akapp.storage.LiveManager;
//import com.aikucun.akapp.storage.ProductManager;
//import com.aikucun.akapp.utils.MToaster;
//import com.aikucun.akapp.utils.SCLog;
//import com.aikucun.akapp.utils.StringUtils;
//import com.aikucun.akapp.utils.TDevice;
//import com.aikucun.akapp.widget.MyDialogUtils;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.androidadvance.topsnackbar.TSnackbar;
//import com.bumptech.glide.Glide;
//import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import butterknife.BindView;
//import okhttp3.Call;
//import okhttp3.Response;
//
///**
// * 直播首页
// * Created by jarry on 2017/6/1.
// */
//public class LiveFragment extends BaseProductFragment implements SwipeRefreshLayout.OnRefreshListener,
//        RecyclerArrayAdapter.OnLoadMoreListener, ProductAdapter.OnItemEventListener, View.OnClickListener {
//    @BindView(R.id.live_layout)
//    View viewLayout;
//
//    private ArrayList<Product> headerProduct = null;
//
//    private boolean isForward;
//
//    private String liveId;
//    private int lastOffset;
//    //所有直播商品
//    @BindView(R.id.preparation_type_all)
//    TextView preparation_type_all;
//    //预告直播商品
//    @BindView(R.id.preparation_type_ncotice)
//    TextView preparation_type_ncotice;
//    //进行中直播商品
//    @BindView(R.id.preparation_type_progress)
//    TextView preparation_type_progress;
//    @BindView(R.id.top_layout)
//    LinearLayout top_layout;
//    //已选中的直播商品类型
//    private int selectedPreparationType = 1;
//    private TextView selectTypeText;
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_live;
//    }
//
//    @Override
//    public void initView(View view) {
//        super.initView(view);
//        recyclerView.setRefreshListener(this);
//
//        productAdapter.singleZhuanChang = !StringUtils.isEmpty(this.liveId);
//
//        productAdapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
//            @Override
//            public void onErrorShow() {
//                productAdapter.resumeMore();
//            }
//
//            @Override
//            public void onErrorClick() {
//                productAdapter.resumeMore();
//            }
//        });
//    }
//
//
//    @Override
//    public void initData() {
//        super.initData();
//
//        this.headerProduct = null;
//        this.titleName = "爱库存";
//        List<Product> products = ProductManager.loadProducts(0, this.liveId);
//        productAdapter.clear();
//        productAdapter.addAll(products);
//
//        File path = Glide.getPhotoCacheDir(getActivity());
//        SCLog.debug(path.getAbsolutePath());
//
//        initNoticeView();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showNotVipMsg();
//            }
//        }, 2000);
//        preparation_type_all.setOnClickListener(this);
//        preparation_type_ncotice.setOnClickListener(this);
//        preparation_type_progress.setOnClickListener(this);
//        //默认选中所有
//        selectTypeText = preparation_type_all;
//        selectTypeText.setSelected(true);
//        selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//
//    }
//
//    private void initNoticeView() {
//
//        try {
////            if (AppContext.getInstance().isVip()) {
////                noticeTextView.setVisibility(View.VISIBLE);
////                String notice = LiveManager.getInstance().getNotice(this.liveId);
////                noticeTextView.setText(notice);
////                int color = noticeTextView.getContext().getResources().getColor(R.color.color_accent);
////                noticeTextView.getContentText().setTextColor(color);
//////                noticeTextView.getTextPlus().setTextColor(color);
////            }
//
//            //处理预告、活动内容
////            int currPosition = Integer.MAX_VALUE;
////            RecyclerView.LayoutManager lm =  recyclerView.getRecyclerView().getLayoutManager();
////            if (lm != null && lm instanceof  LinearLayoutManager) {
////                currPosition = ((LinearLayoutManager)lm).findFirstVisibleItemPosition();
////            }
////               setHeaderProduct(LiveManager.getInstance().getYugaoProducts(this.liveId));
////            if (currPosition == 0)
////                recyclerView.getRecyclerView().scrollToPosition(0);
//        } catch (Exception e) {
//            SCLog.error(e.getLocalizedMessage());
//        }
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if (isForward) {
//            isForward = false;
//
//        } else {
//            getLiveState();
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (!isForward) {
//            stopTimer();
//        }
//    }
//
//    @Override
//    public void onLoadMore() {
//        int start = productAdapter.getCount() - insertUpdateGoodsPosition();
//        List<Product> products = ProductManager.loadProducts(start, this.liveId);
//        productAdapter.addAll(products);
//    }
//
//    @Override
//    public void onRefresh() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                getLiveState();
//            }
//        }, 300);
//    }
//
//    @Override
//    protected void forwardProduct(Product product, Object object) {
//        isForward = true;
//        super.forwardProduct(product, object);
//    }
//
//
//    private void getLiveState() {
//        LiveApiManager.getLiveState(getActivity(), 0, new LiveStateCallback() {
//            @Override
//            public void onApiSuccess(LiveStateResp liveStateResp, Call call, ApiResponse
//                    jsonResponse) {
//                super.onApiSuccess(liveStateResp, call, jsonResponse);
//                //
//
//                initNoticeView();
//                trackProducts();
//            }
//
//            @Override
//            public LiveStateResp parseResponse(ApiResponse responseData) throws Exception {
//                {
//                    JSONArray pictures = responseData.getJsonObject().getJSONArray("pictures");
//
//                    //http://www.cnblogs.com/whoislcj/p/5565012.html
//                    //TODO 删除图片
//                    //Glide 以 url、viewwidth、viewheight、屏幕的分辨率等做为联合key，官方api中没有提供删除图片缓存的函数，官方提供了signature()方法，将一个附加的数据加入到缓存key当中，多媒体存储数据，可用MediaStoreSignature类作为标识符，会将文件的修改时间、mimeType等信息作为cacheKey的一部分，通过改变key来实现图片失效相当于软删除。
//                    //会较为麻烦，暂不支持
//                    //较为简单的方法是，全部清空，重新下载
//                    /*
//                    for (int k = 0; k < pictures.size(); k++)
//                    {
//                        String url = pictures.getJSONObject(k).toString();
//                        FutureTarget<File> future = Glide.with(LiveFragment.this.getActivity()).load(url).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//
//                    }
//                    */
//                }
//                return super.parseResponse(responseData);
//            }
//        });
//    }
//
//    private void trackProducts() {
//        long productUpdate = ProductManager.getInstance().productUpdate;
//        long skuUpdate = ProductManager.getInstance().skuUpdate;
//        LiveApiManager.trackProducts(getActivity(), productUpdate, skuUpdate, new
//                TrackProductsCallback() {
//                    @Override
//                    public void onApiSuccess(TrackProductsResp trackProductsResp, Call call, ApiResponse
//                            jsonResponse) {
//                        super.onApiSuccess(trackProductsResp, call, jsonResponse);
//
//                        recyclerView.setRefreshing(false);
//                        //
//                        List<Product> products = trackProductsResp.getLiveProducts(LiveFragment.this.liveId);
//                        if (products.size() > 0) {
//                            //新数据刷新时会滚动到顶部
//                            productAdapter.insertAll(products, insertUpdateGoodsPosition());
//                            showMessageTip("已更新 " + products.size() + " 件商品");
//                        }
//
//                        long time = LiveManager.periodTime();
//
//                        trackComments();
//                        startTimer(time);
//                    }
//
//                    @Override
//                    public void onApiFailed(String message, int code) {
//                        super.onApiFailed(message, code);
//                        recyclerView.setRefreshing(false);
//                    }
//                });
//    }
//
//    private void trackComments() {
//        CommentsApiManager.trackComments(getActivity(), ProductManager.getInstance().commentUpdate, new JsonDataCallback() {
//            @Override
//            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
//                super.onApiSuccess(jsonObject, call, jsonResponse);
//            }
//
//            @Override
//            public void onApiFailed(String message, int code) {
//                super.onApiFailed(message, code);
//            }
//        });
//    }
//
//    private void showMessageTip(String msg) {
//        if (null != viewLayout) {
//            try {
//                TSnackbar snackbar = TSnackbar.make(viewLayout, msg, TSnackbar.LENGTH_LONG);
//                View snackbarView = snackbar.getView();
//                snackbarView.setBackgroundColor(Color.parseColor("#fd643c"));
//                TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R
//                        .id.snackbar_text);
//                textView.setTextColor(Color.WHITE);
//                snackbar.show();
//            } catch (Exception e) {
//                SCLog.error(e.getLocalizedMessage());
//            }
//        }
//    }
//
//
//    /**
//     * 定时跟踪任务
//     */
//    private Timer timer;
//    private Handler timerHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            //
//            stopTimer();
//            //
//            getLiveState();
//        }
//    };
//
//    private void startTimer(long time) {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//        timer = new Timer();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                Message message = new Message();
//                message.what = 1;
//                timerHandler.sendMessage(message);
//            }
//        };
//
//        timer.schedule(task, time);
//    }
//
//    private void stopTimer() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }
//
//
//    public void setLiveId(String liveId) {
//        this.liveId = liveId;
//    }
//
//    private int insertUpdateGoodsPosition() {
//        if (getHeaderProduct() != null)
//            return getHeaderProduct().size();
//        return 0;
//    }
//
//    public ArrayList<Product> getHeaderProduct() {
//        return headerProduct;
//    }
//
//    boolean isAdd = false;
//
//    public void setHeaderProduct(ArrayList<Product> headerProduct) {
//        headerProduct = sortProducts(headerProduct);
////        if (this.headerProduct != null && productAdapter.getCount() >= this.headerProduct.size()) {
////            for (int i = 0; i < this.headerProduct.size(); i++) {
////                productAdapter.remove(0);
////            }
////        }
////        this.headerProduct = headerProduct;
////        if (this.headerProduct != null && this.headerProduct.size() != 0) {
////            //设置不刷新
//////            productAdapter.setNotifyOnChange(false);
////            productAdapter.insertAll(this.headerProduct,0);
////        }
//
//        if (isAdd && this.headerProduct.size() > 0) {
//            List<Product> array = productAdapter.getAllData();
//            for (int i = 0, size = array.size(); i < size; i++) {
//                for (int j = 0, len = headerProduct.size(); j < len; j++) {
//                    if (array.get(i).getId().equals(headerProduct.get(j).getId())) {
//                        productAdapter.update(headerProduct.get(j), i);
//                        break;
//                    }
//                }
//            }
//        } else {
//            if (this.headerProduct != null && productAdapter.getCount() >= this.headerProduct.size()) {
//                for (int i = 0; i < this.headerProduct.size(); i++) {
//                    productAdapter.remove(0);
//                }
//            }
//            this.headerProduct = headerProduct;
//            if (this.headerProduct != null && this.headerProduct.size() != 0) {
//                //设置不刷新 todo
//                productAdapter.insertAll(this.headerProduct, 0);
//                isAdd = true;
//            }
//
//        }
//    }
//
//    private void showNotVipMsg() {
//
//        UserInfo userInfo = AppContext.getInstance().getUserInfo();
//        if (null != userInfo) {
//            // FIXME: 2018/1/4
//            if (0 == userInfo.getViplevel()) {
//                MyDialogUtils.showNotVipDialog(getActivity());
//            }
//        }
//    }
//
//
//    private ArrayList<Product> sortProducts(ArrayList<Product> headerProduct) {
//        ArrayList<Product> arrayList = headerProduct;
//        Comparator<Product> comparator = new Comparator<Product>() {
//            public int compare(Product s1, Product s2) {
//                return (int) (s2.getEndtimestamp() - s1.getEndtimestamp());
//            }
//        };
//        Collections.sort(arrayList, comparator);
//        return arrayList;
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//        final int id = v.getId();
//        if (selectTypeText != null) {
//            selectTypeText.setSelected(false);
//            selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
//        }
//        switch (id) {
//            case R.id.preparation_type_all:
//                selectedPreparationType = 1;
//                selectTypeText = preparation_type_all;
//                break;
//            case R.id.preparation_type_ncotice:
//                selectedPreparationType = 2;
//                selectTypeText = preparation_type_ncotice;
//                break;
//            case R.id.preparation_type_progress:
//                selectedPreparationType = 3;
//                selectTypeText = preparation_type_progress;
//                break;
//        }
//        selectTypeText.setSelected(true);
//        selectTypeText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//    }
//
//
//}
