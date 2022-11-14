package com.aikucun.akapp.activity.video;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aswife.ui.MaskImageView;
import com.qiyunxin.android.http.adapter.QiYunXinAdapter;

import java.util.ArrayList;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by ak123 on 2017/11/17.
 */

public class VideoAdapter extends QiYunXinAdapter {

    public interface ISelectedListenter {
        void onBack(VideoEntity videoEntity);

        void onRecord();
    }

    MediaMetadataRetriever mmr;
    private Context context;
    private ArrayList<VideoEntity> arrayList;
    private ISelectedListenter mISelectedListenter;

    public VideoAdapter(Context _context, ArrayList<VideoEntity> _arr, ISelectedListenter _ISelectedListenter) {
        mISelectedListenter = _ISelectedListenter;
        mmr = new MediaMetadataRetriever();
        this.context = _context;
        this.arrayList = _arr;
    }

    @Override
    public int getCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList == null ? null : arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VideoAdapterViewHolder viewHolder;
        final VideoEntity videoEntity = arrayList.get(position);
        if (convertView == null) {
            viewHolder = new VideoAdapterViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_video_layout, null);
            viewHolder.imageView = convertView.findViewById(R.id.image);
            viewHolder.duration = convertView.findViewById(R.id.duration);
            viewHolder.selectedIv = convertView.findViewById(R.id.is_selected);
            viewHolder.frist_layout = convertView.findViewById(R.id.frist_layout);
            viewHolder.video_layout = convertView.findViewById(R.id.video_layout);
            convertView.setTag(viewHolder);
            AddRecycleMaskImageView(viewHolder.imageView);
        } else {
            viewHolder = (VideoAdapterViewHolder) convertView.getTag();
        }

        if (TextUtils.isEmpty(videoEntity.getId()) && TextUtils.isEmpty(videoEntity.getVideoName()) && TextUtils.isEmpty(videoEntity.getVideoPath())) {
            viewHolder.frist_layout.setVisibility(View.VISIBLE);
            viewHolder.video_layout.setVisibility(View.GONE);
            viewHolder.frist_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mISelectedListenter != null) {
                        mISelectedListenter.onRecord();
                    }
                }
            });
        } else {
            viewHolder.frist_layout.setVisibility(View.GONE);
            viewHolder.video_layout.setVisibility(View.VISIBLE);
            if (videoEntity.isSelected()) {
                viewHolder.selectedIv.setImageResource(R.drawable.icon_img_selected);
            } else viewHolder.selectedIv.setImageResource(R.drawable.icon_img_not_select);

            if (!TextUtils.isEmpty(videoEntity.getVideoPath()) && videoEntity.getBitmap() != null) {
//                mmr.setDataSource(videoEntity.videoPath);
//                //获取第一帧图像的bitmap对象
//                Bitmap bitmap = mmr.getFrameAtTime();
//                //加载到ImageView控件上
                viewHolder.imageView.setImageBitmap(videoEntity.getBitmap());
//                viewHolder.imageView.SetUrl(videoEntity.videoPath);
            }
            if (!TextUtils.isEmpty(videoEntity.getVideoDuration())) {
                viewHolder.duration.setText(videoEntity.getVideoDuration());
            }

            viewHolder.selectedIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateStatus(videoEntity);
                }
            });
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/11/17 播放该视频
                    JCVideoPlayerStandard.startFullscreen(context, JCVideoPlayerStandard.class, videoEntity.getVideoPath(), "给你看视频");
//                    Bundle bundle = new Bundle();
//                    bundle.putString("url", videoEntity.videoPath);
//                    ActivityUtils.startActivity((Activity) context, PlayVideoActivity.class, bundle, -1);
                }
            });
        }


        return convertView;
    }


    private void updateStatus(VideoEntity videoEntity) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == 0)
                continue;
            if (arrayList.get(i).getVideoPath().equals(videoEntity.getVideoPath())) {
                arrayList.get(i).setSelected(!arrayList.get(i).isSelected());
            } else arrayList.get(i).setSelected(false);
        }
        if (mISelectedListenter != null) {
            mISelectedListenter.onBack(videoEntity);
        }
        notifyDataSetChanged();
    }

    private class VideoAdapterViewHolder {
        private RelativeLayout frist_layout;
        private RelativeLayout video_layout;
        private MaskImageView imageView;
        private TextView duration;
        private ImageView selectedIv;
    }
}
