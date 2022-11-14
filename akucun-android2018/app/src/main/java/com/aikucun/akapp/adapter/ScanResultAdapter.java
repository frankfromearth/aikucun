package com.aikucun.akapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.adapter.viewholder.ScanResultViewHolder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleSubmit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductASaleTuihuoPending;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusInit;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusWeifahuo;
import static com.aikucun.akapp.api.entity.CartProduct.ProductStatusYifahuo;

/**
 * 申请售后扫码结果列表
 */
public class ScanResultAdapter extends RecyclerArrayAdapter<CartProduct> {

    public static final int Product_EVENTS_CHANGE = 5;      // 更换尺码
    public static final int Product_EVENTS_TUIKUAN = 6;     // 申请退款 不发货
    public static final int PRODUCT_EVENTS_TUIHUO = 7;      // 退货 退款
    public static final int PRODUCT_EVENTS_HUANHUO = 8;     // 换货
    public static final int PRODUCT_EVENTS_REMARK = 9;      // 标注
    public static final int PRODUCT_EVENTS_LOUFA = 10;       // 漏发
    public static final int PRODUCT_EVENTS_SHOUHOU_APPLY = 11;       // 申请售后
    public static final int PRODUCT_EVENTS_SHOUHOU_AFTER = 12;       // 售后进度

    public int outaftersale;

    private String adOrderId;

    private ScanResultAdapter.OnItemEventListener onItemEventListener;

    public ScanResultAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        ScanResultViewHolder viewHolder = new ScanResultViewHolder(parent);
        viewHolder.adOrderId = adOrderId;
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position) {
        super.OnBindViewHolder(holder, position);

        final CartProduct product = getItem(position);
        final ScanResultViewHolder viewHolder = (ScanResultViewHolder) holder;


        if (outaftersale > 0) {
            viewHolder.cancelButton.setVisibility(View.GONE);
            viewHolder.productButton.setVisibility(View.GONE);
        }

        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int event = 0;
                int statusValue = product.getShangpinzhuangtai();
                if (statusValue == ProductStatusWeifahuo) {
                    event = Product_EVENTS_TUIKUAN;
                }
//                else if (statusValue == ProductStatusYifahuo || statusValue == ProductStatusFahuo) {
//                    event = PRODUCT_EVENTS_HUANHUO;
//                }

                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(event, product, position);
                }
            }
        });

        viewHolder.productButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int event = 0;
                int statusValue = product.getShangpinzhuangtai();
                if (statusValue == ProductStatusInit || statusValue == ProductStatusWeifahuo) {
                    event = Product_EVENTS_CHANGE;
                } else if (statusValue == ProductStatusYifahuo) {
//                    event = PRODUCT_EVENTS_LOUFA;
                    event = PRODUCT_EVENTS_SHOUHOU_APPLY;
                } else if (statusValue >= ProductASaleSubmit && statusValue <= ProductASaleTuihuoPending) {
                    event = PRODUCT_EVENTS_SHOUHOU_AFTER;
                }

                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(event, product, position);
                }
            }
        });

        viewHolder.remarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(PRODUCT_EVENTS_REMARK, product, position);
                }
            }
        });
        viewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // imagesize是作为loading时的图片size
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                        (viewHolder.productImage.getMeasuredWidth(), viewHolder.productImage.getMeasuredHeight());
                List<String> photoUrls = new ArrayList<String>();
                photoUrls.add(product.getImageUrl());

                ImagePagerActivity.startImagePagerActivity(ScanResultAdapter.this.getContext(), photoUrls, 0, imageSize);
            }
        });
    }

    public void setOnItemEventListener(ScanResultAdapter.OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener {
        public void onEvent(int event, CartProduct product, int position);
    }

    public void setAdOrderId(String adOrderId) {
        this.adOrderId = adOrderId;
    }

}
