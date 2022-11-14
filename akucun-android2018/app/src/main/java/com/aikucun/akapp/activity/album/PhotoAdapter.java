package com.aikucun.akapp.activity.album;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;

import com.qiyunxin.android.http.adapter.QiYunXinAdapter;
import com.qyx.android.weight.choosemorepic.PhotoAibum;
import com.qyx.android.weight.choosemorepic.PhotoItem;

import java.util.ArrayList;

public class PhotoAdapter extends QiYunXinAdapter {
    private Context context;
    private PhotoAibum aibum;
    private ArrayList<PhotoItem> gl_arr;
    private PhotoGridItem item;

    public PhotoAdapter(Context context, PhotoAibum aibum,
                        ArrayList<PhotoItem> gl_arr) {
        this.context = context;
        this.aibum = aibum;
        this.gl_arr = gl_arr;
    }

    @Override
    public int getCount() {
        if (gl_arr == null) {
            return aibum.getBitList().size();
        } else {
            return gl_arr.size();
        }
    }

    @Override
    public PhotoItem getItem(int position) {
        if (gl_arr == null) {
            return aibum.getBitList().get(position);
        } else {
            return gl_arr.get(position);
        }

    }

    public void setData(PhotoAibum aibum) {
        this.aibum = aibum;
        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            item = new PhotoGridItem(context);
            item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            convertView = item;
            AddRecycleMaskImageView(item.mImageView);
        } else {
            item = (PhotoGridItem) convertView;
        }
        // item.setImgResID(R.drawable.recv_image_defalut);
        final PhotoItem photoItem = aibum.getBitList().get(position);
        final int pos = position;
        if (photoItem != null) {

            if (gl_arr == null) {

                if (item.mImageView != null) {
                    item.mImageView.SetUrl(photoItem.getPath(), mIsScrolling,
                            true);
                }

                boolean flag = photoItem.isSelect();
                item.setChecked(flag);
            } else {
                if (item.mImageView != null) {
                    item.mImageView.SetUrl(photoItem.getPath(), mIsScrolling,
                            true);
                }
            }

            item.mImageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemSelectListener != null) {
                        itemSelectListener.onImgClick(photoItem);
                    }
                }
            });

            item.mSelect.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemSelectListener != null) {
                        itemSelectListener.onItemSelect(pos, photoItem, item);
                    }
                }
            });
        }
        return item;
    }

    private onItemSelectListener itemSelectListener;

    public void setOnItemSelectListener(onItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface onItemSelectListener {
        void onItemSelect(int position, PhotoItem photoItem,
                          PhotoGridItem gridItem);
        void onImgClick(PhotoItem photoItem);
    }
}
