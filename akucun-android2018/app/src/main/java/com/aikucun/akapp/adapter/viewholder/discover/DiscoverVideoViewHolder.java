package com.aikucun.akapp.adapter.viewholder.discover;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.ImagesUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.view.MultiImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by ak123 on 2017/11/16.
 * 视频布局
 */

public class DiscoverVideoViewHolder extends DiscoverBaseViewHolder {
    RelativeLayout video_layout;
    //视频封面
    public MultiImageView video_iv;
    private IReplayListener iReplayListener;
    private IPlayVideoListener iPlayVideoListener;

    public DiscoverVideoViewHolder(ViewGroup itemView, IReplayListener replayListener, IPlayVideoListener _iPlayVideoListener) {
        //加载纯文本布局
        super(itemView, R.layout.item_dynamic_video);
        this.iPlayVideoListener = _iPlayVideoListener;
        this.iReplayListener = replayListener;
        send_content_tv = $(R.id.send_content_tv);
        send_nick_tv = $(R.id.send_nick_tv);
        head_image = $(R.id.head_image);
        del_tv = $(R.id.del_tv);
        replay_tv = $(R.id.replay_tv);
        forward_tv = $(R.id.forward_tv);
        bg_reply_content_layout = $(R.id.bg_reply_content_layout);
        praise_ll = $(R.id.praise_ll);
        praise_tv = $(R.id.praise_tv);
        reply_content = $(R.id.reply_content);
        video_iv = $(R.id.video_iv);
        title_tv = $(R.id.title_tv);
        send_time_tv = $(R.id.send_time_tv);
        video_layout = $(R.id.video_layout);
    }

    @Override
    public void setData(final Discover data) {
        // TODO: 2017/11/16 设置数据
        send_nick_tv.setText(data.username);
        title_tv.setText(data.title);
        if (!TextUtils.isEmpty(data.content)) {
            send_content_tv.setVisibility(View.VISIBLE);
            send_content_tv.setText(data.content);
        } else {
            send_content_tv.setVisibility(View.GONE);
        }
        send_time_tv.setText(DateUtils.friendlyTime(data.createtime * 1000));

        if (!TextUtils.isEmpty(data.avatar))
            Glide.with(getContext()).load(data.avatar).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(head_image);

        if (data.imageHeight != 0 && data.imageWidth != 0) {
            int[] w_h = ImagesUtils.getImageWidthAndHeightToDynamic(getContext(), data.imageWidth, data.imageHeight);
            ViewGroup.LayoutParams ivLinearParams = video_iv.getLayoutParams();
            ivLinearParams.height = w_h[1];
            ivLinearParams.width = w_h[0];
            video_iv.setLayoutParams(ivLinearParams);

            ViewGroup.LayoutParams videoLayoutP = video_layout.getLayoutParams();
            videoLayoutP.height = w_h[1];
            videoLayoutP.width = w_h[0];
            video_layout.setLayoutParams(videoLayoutP);
        } else {
            ViewGroup.LayoutParams ivLinearParams = video_iv.getLayoutParams();
            ivLinearParams.height = (int) TDevice.dpToPixel(200.0f);
            ivLinearParams.width = (int) TDevice.dpToPixel(200.0f);
            video_iv.setLayoutParams(ivLinearParams);

            ViewGroup.LayoutParams videoLayoutP = video_layout.getLayoutParams();
            videoLayoutP.height = (int) TDevice.dpToPixel(200.0f);
            videoLayoutP.width = (int) TDevice.dpToPixel(200.0f);
            video_layout.setLayoutParams(videoLayoutP);

//            int w = video_iv.getMeasuredWidth();
//            int h = video_iv.getMeasuredHeight();
//            ViewGroup.LayoutParams lp = video_layout.getLayoutParams();
//            lp.width = w;
//            lp.height = h;
//            video_layout.setLayoutParams(lp);
        }
        List<String> arr = new ArrayList<>();
        arr.add(data.imagesUrl);
        video_iv.setUrlList(arr);
        video_iv.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!TextUtils.isEmpty(data.videoUrl)) {
                    JCVideoPlayerStandard.startFullscreen(getContext(), JCVideoPlayerStandard.class, data.videoUrl, "");
                } else if (iPlayVideoListener != null) iPlayVideoListener.onPlayVideo(data.videoId);
            }
        });

        //设置评论数据
        setReplyData(data.comments, 0, null);
        replay_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iReplayListener != null) {
                    iReplayListener.onReplay(data, v);
                }
            }
        });
        forward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iReplayListener != null) {
                    iReplayListener.onForward(data);
                }
            }
        });
    }

}
