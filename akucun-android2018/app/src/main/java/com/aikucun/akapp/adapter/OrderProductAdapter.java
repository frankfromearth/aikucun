package com.aikucun.akapp.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.adapter.viewholder.OrderProdViewHolder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.utils.MToaster;
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
 * Created by jarry on 2017/6/9.
 */
public class OrderProductAdapter extends RecyclerArrayAdapter<CartProduct>
{
    public static final int Product_EVENTS_CHANGE = 5;      // 更换尺码
    public static final int Product_EVENTS_TUIKUAN = 6;     // 申请退款 不发货
    public static final int PRODUCT_EVENTS_TUIHUO = 7;      // 退货 退款
    public static final int PRODUCT_EVENTS_HUANHUO = 8;     // 换货
    public static final int PRODUCT_EVENTS_REMARK = 9;      // 标注
    public static final int PRODUCT_EVENTS_LOUFA= 10;       // 漏发
    public static final int PRODUCT_EVENTS_SHOUHOU_APPLY= 11;       // 申请售后
    public static final int PRODUCT_EVENTS_SHOUHOU_AFTER= 12;       // 售后进度

    public int outaftersale;

    private String adOrderId;

    private OnItemEventListener onItemEventListener;

    public OrderProductAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        OrderProdViewHolder viewHolder = new OrderProdViewHolder(parent);
        viewHolder.adOrderId = adOrderId;
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        final CartProduct product = getItem(position);
        final OrderProdViewHolder viewHolder = (OrderProdViewHolder) holder;


        if (outaftersale > 0)
        {
            viewHolder.cancelButton.setVisibility(View.GONE);
            viewHolder.productButton.setVisibility(View.GONE);
        }

        viewHolder.cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int event = 0;
                int statusValue = product.getShangpinzhuangtai();
                if (statusValue == ProductStatusWeifahuo)
                {
                    event = Product_EVENTS_TUIKUAN;
                }
//                else if (statusValue == ProductStatusYifahuo || statusValue == ProductStatusFahuo) {
//                    event = PRODUCT_EVENTS_HUANHUO;
//                }

                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(event, product, position);
                }
            }
        });
        viewHolder.copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(product.getExtrainfo());
                cm.setPrimaryClip(ClipData.newPlainText("akucun", product.getExtrainfo()));
                MToaster.showShort((Activity) getContext(), R.string.copy_success, MToaster.IMG_INFO);
            }
        });
        viewHolder.productButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int event = 0;
                int statusValue = product.getShangpinzhuangtai();
                if (statusValue == ProductStatusInit || statusValue == ProductStatusWeifahuo)
                {
                    event = Product_EVENTS_CHANGE;
                }
                else if (statusValue == ProductStatusYifahuo) {
//                    event = PRODUCT_EVENTS_LOUFA;
                    event = PRODUCT_EVENTS_SHOUHOU_APPLY;
                } else if (statusValue >= ProductASaleSubmit && statusValue <= ProductASaleTuihuoPending) {
                    event = PRODUCT_EVENTS_SHOUHOU_AFTER;
                }

                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(event, product, position);
                }
            }
        });

        viewHolder.remarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
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

                ImagePagerActivity.startImagePagerActivity(OrderProductAdapter.this.getContext(), photoUrls, 0, imageSize);
            }
        });
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener
    {
        public void onEvent(int event, CartProduct product, int position);
    }

    public void setAdOrderId(String adOrderId) {
        this.adOrderId = adOrderId;
    }
}
