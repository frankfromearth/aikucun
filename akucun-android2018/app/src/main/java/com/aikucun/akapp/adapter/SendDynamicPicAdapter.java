package com.aikucun.akapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.album.PictureShowActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aswife.ui.MaskImageView;

import java.util.ArrayList;

/**
 * @author SL
 * @E-mail:
 * @date 创建时间：2016-6-24 下午7:55:54
 * @version 1.0
 * @parameter
 * @Function 动态图片
 */
public class SendDynamicPicAdapter extends BaseAdapter {

	public interface IDeleteListener {
		void onDelete(int index);
	}

	public ArrayList<String> picArraylist = new ArrayList<String>();
	private Context mContext;
	private OnClickListener mOnClickListener;
	private IDeleteListener mIDeleteListener;

	public SendDynamicPicAdapter(Context _Context,
                                 ArrayList<String> _picArraylist, OnClickListener _OnClickListener,
                                 IDeleteListener _IDeleteListener) {
		picArraylist = _picArraylist;
		mContext = _Context;
		mIDeleteListener = _IDeleteListener;
		mOnClickListener = _OnClickListener;
	}

	public void setData(ArrayList<String> _picArraylist) {
		picArraylist = _picArraylist;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return picArraylist == null ? 0 : picArraylist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return picArraylist == null ? 0 : picArraylist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		DynamicPicViewHolder mDynamicPicViewHolder = null;
		if (arg1 == null) {
			mDynamicPicViewHolder = new DynamicPicViewHolder();
			arg1 = View.inflate(mContext, R.layout.item_send_dynamic_img, null);
			mDynamicPicViewHolder.pic = (MaskImageView) arg1
					.findViewById(R.id.send_img);
			mDynamicPicViewHolder.deleteIv = (ImageView) arg1
					.findViewById(R.id.del_img);
			arg1.setTag(mDynamicPicViewHolder);
		} else {
			mDynamicPicViewHolder = (DynamicPicViewHolder) arg1.getTag();
		}

		mDynamicPicViewHolder.deleteIv.setVisibility(View.GONE);
		if (!TextUtils.isEmpty(picArraylist.get(arg0))) {
			mDynamicPicViewHolder.pic.SetUrl(picArraylist.get(arg0));
			mDynamicPicViewHolder.deleteIv.setVisibility(View.VISIBLE);
			mDynamicPicViewHolder.pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ArrayList<String> tempArr = new ArrayList<String>();
					for (int i = 0, size = picArraylist.size(); i < size; i++) {
						if (!picArraylist.get(i).equals("")) {
							tempArr.add(picArraylist.get(i));
						}
					}
					Bundle bundle = new Bundle();
					bundle.putString("content", picArraylist.get(arg0));
					bundle.putStringArrayList("imgList", tempArr);
					bundle.putStringArrayList("imgListSmall", tempArr);
					ActivityUtils.startActivity((Activity)mContext,PictureShowActivity.class,bundle,-1);
				}
			});

			mDynamicPicViewHolder.deleteIv
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							mIDeleteListener.onDelete(arg0);
						}
					});
		} else {
			mDynamicPicViewHolder.deleteIv.setVisibility(View.GONE);
			mDynamicPicViewHolder.pic.SetUrl("");
			mDynamicPicViewHolder.pic.setOnClickListener(mOnClickListener);
		}

		return arg1;
	}

	private class DynamicPicViewHolder {
		private MaskImageView pic;
		private ImageView deleteIv;
	}

}
