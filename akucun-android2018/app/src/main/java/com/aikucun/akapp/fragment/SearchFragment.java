package com.aikucun.akapp.fragment;

import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.storage.LiveInfosManager;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.BottomDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by micker on 2017/8/28.
 */

public class SearchFragment extends BaseProductFragment implements View.OnClickListener {

    private String keyword;
    private int start = 0;

    @BindView(R.id.rl_pinpai)
    View rl_pinpai;


    TextView tips_guang_text;

    TextView tips_youhuo_text;

    @BindView(R.id.rl_pinpai_tv)
    TextView rl_pinpai_tv;

    private String live_id = "";
    private String live_name = "全部";

    @Override
    public void initView(View view) {
        super.initView(view);
        View emptyView = mInflater.inflate(R.layout.view_search_empty, null);
        tips_guang_text = (TextView) emptyView.findViewById(R.id.tips_guang_text);
        tips_youhuo_text = (TextView) emptyView.findViewById(R.id.tips_youhuo_text);
        tips_guang_text.setOnClickListener(this);
        tips_youhuo_text.setOnClickListener(this);
        recyclerView.setEmptyView(emptyView);
    }

    @Override
    public void initData() {
        super.initData();
        setKeyword("");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onLoadMore() {

        List<Product> result = null;
        if (StringUtils.isEmpty(keyword)) {
            result = new ArrayList<>();
        } else if (keyword.equalsIgnoreCase("光")) {
            result = ProductManager.searchProductsBy(start, true, live_id);
        } else if (keyword.equalsIgnoreCase("有货")) {
            result = ProductManager.searchProductsBy(start, false, live_id);
        } else {
            result = ProductManager.searchProductsBy(keyword, start, live_id);
        }

        final List<Product> updateResult = result;
        updateSKUS(result, new UpdateSKUListener() {
            @Override
            public void onUpdateSuccess(List<Product> products) {
                updateData(products);
            }

            @Override
            public void onUpdateFailed(List<Product> products) {
                updateData(products);

            }
        });
    }

    private void updateData(List<Product> result) {

        productAdapter.addAll(result);
        productAdapter.notifyDataSetChanged();

        start++;
        if (null != listener) {
            listener.onLoadMore(productAdapter.getCount() != 0);
        }
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        doSearch();
    }

    private void doSearch() {
        start = 0;
        productAdapter.clear();
        onLoadMore();
        rl_pinpai_tv.setText("搜索品牌：" + live_name);
    }

    @Override
    @OnClick(R.id.rl_pinpai)
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tips_guang_text: {
                String keyword = "光";
                setKeyword(keyword);
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.SEACH_KEY_WORDS, keyword));
            }
            break;
            case R.id.tips_youhuo_text: {
                String keyword = "有货";
                setKeyword(keyword);
                EventBus.getDefault().post(new AppConfig.MessageEvent(AppConfig.SEACH_KEY_WORDS, keyword));
            }
            break;
            case R.id.rl_pinpai: {
                showChooseBrandDialog();
            }
            break;
        }
    }

//    private void showPinpais() {
//        final String[] pinpais = LiveInfosManager.getInstance().pinpaiAllNames();//LiveManager.getInstance().pinpaiAllNames();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("选择品牌");
//
//        builder.setItems(pinpais, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (0 == which) {
//                    live_id = "";
//                    live_name = "全部";
//                } else {
//                    LiveInfo liveInfo = LiveInfosManager.getInstance().getPinpai(which - 1);// LiveManager.getInstance().getPinpai(which-1);
//                    if (null != liveInfo) {
//                        live_id = liveInfo.getLiveid();
//                        live_name = liveInfo.getPinpaiming();
//                    }
//                }
//                doSearch();
//            }
//        });
//        builder.show();
//    }

    /**
     * 选择品牌
     */
    private void showChooseBrandDialog() {
        List<LiveInfo> liveInfos = LiveInfosManager.getInstance().getLiveInfos();
        BottomDialog.showBrandIndefiniteDialog(getActivity(), getString(R.string.choose_brand), liveInfos, new BottomDialog.IBrandLiseneter() {
            @Override
            public void onClick(LiveInfo liveInfo) {
                if (null != liveInfo) {
                    live_id = liveInfo.getLiveid();
                    live_name = liveInfo.getPinpaiming();
                    doSearch();
                }
            }
        });
    }


    public void setListener(onLoadMoreListener listener) {
        this.listener = listener;
    }

    private onLoadMoreListener listener;

    public interface onLoadMoreListener {
        void onLoadMore(boolean hasData);
    }

}
