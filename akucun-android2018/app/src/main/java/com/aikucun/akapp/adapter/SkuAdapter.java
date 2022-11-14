package com.aikucun.akapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.ProductSKU;

import java.util.List;

/**
 * Created by jarry on 2017/6/8.
 */

public class SkuAdapter extends BaseAdapter
{
    private List<ProductSKU> list;
    private LayoutInflater mInflater;

    private int textColor;

    private onItemClickListener itemClickListener;

    public SkuAdapter(List<ProductSKU> list, Context context)
    {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.list = list;

        this.textColor = context.getResources().getColor(R.color.text_normal);
    }

    public void setList(List<ProductSKU> list)
    {
        this.list = list;
    }

    public void setSelectIndex(int index)
    {
        ProductSKU sku = list.get(index);
        sku.setSelected(true);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final SkuViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.sku_gridview_item, null);
            holder = new SkuViewHolder(convertView);
            convertView.setTag(holder);
        }
        else
        {
            holder = (SkuViewHolder) convertView.getTag();
        }

        final ProductSKU sku = list.get(position);
        holder.setProductSku(sku);

//        SCLog.debug(sku.isSelected() + " --> " + sku.getChima() + " : " + sku.getShuliang());

        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean selected = sku.isSelected();
                sku.setSelected(!selected);
//                holder.layout.setSelected(sku.isSelected());

                if (itemClickListener != null) {
                    itemClickListener.onItemClick(sku, position);
                }
            }
        });

        return convertView;
    }

    public void setItemClickListener(onItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    public class SkuViewHolder
    {
        public TextView skuTextView;
        public LinearLayout layout;

        public SkuViewHolder(View view)
        {
            this.skuTextView = (TextView) view.findViewById(R.id.skuText);
            this.layout = (LinearLayout) view.findViewById(R.id.skuLayout);
        }

        public void setProductSku(ProductSKU sku)
        {
            skuTextView.setText(sku.getChima());
            skuTextView.setEnabled((sku.getShuliang() > 0));

//            layout.setEnabled((sku.getShuliang() > 0));
//            layout.setSelected(sku.isSelected());

            if (sku.getShuliang() > 0)
            {
                layout.setEnabled(true);
                skuTextView.setEnabled(true);
                if (sku.isSelected())
                {
                    layout.setBackgroundResource(R.drawable.sku_item_bg_selected);
                    skuTextView.setTextColor(Color.WHITE);
                }
                else {
                    layout.setBackgroundResource(R.drawable.sku_item_bg_normal);
                    skuTextView.setTextColor(textColor);
                }
            }
            else
            {
                layout.setEnabled(false);
                skuTextView.setEnabled(false);
                layout.setBackgroundResource(R.drawable.sku_item_bg_disabled);
            }
        }
    }

    public interface onItemClickListener
    {
        public void onItemClick(ProductSKU sku, int position);
    }
}
