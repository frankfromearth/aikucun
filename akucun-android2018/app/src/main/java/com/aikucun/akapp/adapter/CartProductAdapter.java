package com.aikucun.akapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.adapter.viewholder.CartViewHolder;
import com.aikucun.akapp.api.entity.CartProduct;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jarry on 2017/6/9.
 */
public class CartProductAdapter extends RecyclerArrayAdapter<CartProduct> implements StickyRecyclerHeadersAdapter
{
    public static final int PRODUCT_EVENT_DELETE = 10;
    public static final int PRODUCT_EVENT_REMAKR = 11;
    public static final int PRODUCT_EVENT_ITEM = 12;
    public static final int PRODUCT_EVENT_BUY_AGAIN = 13;

    private boolean isCartRecycle = false;

    private OnItemEventListener onItemEventListener;

    public CartProductAdapter(Context context)
    {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType)
    {
        CartViewHolder viewHolder = new CartViewHolder(parent);
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position)
    {
        super.OnBindViewHolder(holder, position);

        final CartProduct product = getItem(position);
        final CartViewHolder viewHolder = (CartViewHolder) holder;
        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(PRODUCT_EVENT_DELETE, product, position);
                }
            }
        });
        viewHolder.remarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(PRODUCT_EVENT_REMAKR, product, position);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(PRODUCT_EVENT_ITEM, product, position);
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

                ImagePagerActivity.startImagePagerActivity(CartProductAdapter.this.getContext(), photoUrls, 0, imageSize);
            }
        });

        viewHolder.reBuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null)
                {
                    onItemEventListener.onEvent(PRODUCT_EVENT_BUY_AGAIN, product, position);
                }
            }
        });

        if (this.isCartRecycle) {
            viewHolder.setCartRecycle();
        }
    }

    public void setCartRecycle(boolean cartRecycle) {
        isCartRecycle = cartRecycle;
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener)
    {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener
    {
        public void onEvent(int event, CartProduct product, int position);
    }


    @Override
    public long getHeaderId(int position) {
//        if (position == 0)
//            return -1;
        return getItem(position).getPinpaiHash();
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new ItemHolder(parent);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewholder, final int position) {

        ((ItemHolder) viewholder).setData(getItem(position));
    }

    public class ItemHolder extends BaseViewHolder {

        TextView textView;
        ImageView imageView;
        public ItemHolder(ViewGroup parent) {
            super(parent, R.layout.layout_cart_section_item);

            textView = (TextView) itemView.findViewById(R.id.pinpai_tv);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

        public void setData(CartProduct product) {
            textView.setText(product.getPinpai());
            imageView.setImageResource(product.isAllSelected()?R.drawable.right:R.drawable.circle);
        }

    }
}
