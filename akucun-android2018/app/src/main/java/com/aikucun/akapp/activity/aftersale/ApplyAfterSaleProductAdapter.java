package com.aikucun.akapp.activity.aftersale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.api.entity.CartProduct;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak123 on 2018/1/23.
 */

public class ApplyAfterSaleProductAdapter extends RecyclerArrayAdapter<CartProduct> {


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

    private OnItemEventListener onItemEventListener;

    public ApplyAfterSaleProductAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        ApplyAfterSaleProductViewHolder viewHolder = new ApplyAfterSaleProductViewHolder(parent);
        viewHolder.adOrderId = adOrderId;
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position) {
        super.OnBindViewHolder(holder, position);

        final CartProduct product = getItem(position);
        final ApplyAfterSaleProductViewHolder viewHolder = (ApplyAfterSaleProductViewHolder) holder;

        viewHolder.remarkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(PRODUCT_EVENTS_REMARK, product, position);
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

                ImagePagerActivity.startImagePagerActivity(getContext(), photoUrls, 0, imageSize);
            }
        });
        viewHolder.apply_after_sale_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemEventListener.onEvent(1, product, position);
                // TODO: 2018/2/2 申请售后
//                Intent intent = new Intent(getContext(), AfterSaleServiceActivity.class);
//                intent.putExtra(AppConfig.BUNDLE_KEY_ORDER_ID, orderId);
//                intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT, product);
//                intent.putExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID, product.getId());
//                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, product.getCartproductid());
//                ((Activity)getContext()).startActivityForResult(intent, AFTER_SALE_SERVICE_APPLY_REQUEST);
//                ((Activity)getContext()).overridePendingTransition(R.anim.anim_bottom_in, R.anim.anim_fade_out);
            }
        });
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener {
        public void onEvent(int event, CartProduct product, int position);
    }

    public void setAdOrderId(String adOrderId) {
        this.adOrderId = adOrderId;
    }
}
