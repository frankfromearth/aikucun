package com.aikucun.akapp.activity.album;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.qyx.android.weight.choosemorepic.PhotoAibum;

import java.util.List;

public class PhotoAibumAdapter extends BaseAdapter {
	private List<PhotoAibum> aibumList;
	private Context context;
	private ViewHolder holder;

	private LayoutInflater inflater;

	public PhotoAibumAdapter(List<PhotoAibum> list, Context context) {
		this.aibumList = list;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return aibumList.size();
	}

	@Override
	public Object getItem(int position) {
		return aibumList.get(position);
	}
	
	
	public void setData(List<PhotoAibum> list){
		this.aibumList=list;
		notifyDataSetChanged();
		
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.list_dir_item, parent,false);
			holder = new ViewHolder();
			holder.iv = convertView.findViewById(R.id.id_dir_item_image);
			holder.name_tv =convertView.findViewById(R.id.id_dir_item_name);
			holder.count_tv = convertView.findViewById(R.id.id_dir_item_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PhotoAibum aibum = aibumList.get(position);

		Bitmap bitmap = Thumbnails.getThumbnail(
				context.getContentResolver(), aibum.getBitmap(),
				Thumbnails.MICRO_KIND, null);
		holder.iv.setImageBitmap(bitmap);

		holder.name_tv.setText(aibum.getName() + " ( " + aibum.getCount()
				+ " )");
		holder.count_tv.setText(aibum.getCount());
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (clickListener != null) {
					clickListener.onItemClickListener(aibum);
				}
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView iv;
		TextView name_tv;
		TextView count_tv;
	}

	private OnItemClickListener2 clickListener;

	public void setOnItemClickListener(OnItemClickListener2 clickListener) {
		this.clickListener = clickListener;
	}

	public interface OnItemClickListener2 {

		public void onItemClickListener(PhotoAibum aibum);

	}

}
