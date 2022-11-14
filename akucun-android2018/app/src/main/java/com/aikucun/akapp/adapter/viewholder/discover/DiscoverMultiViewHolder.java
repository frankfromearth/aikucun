package com.aikucun.akapp.adapter.viewholder.discover;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.view.MultiImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak123 on 2017/11/16.
 * 多图布局
 */

public class DiscoverMultiViewHolder extends DiscoverBaseViewHolder {

    //图片列表
    private IReplayListener iReplayListener;
    private MultiImageView multiImagView;

    public DiscoverMultiViewHolder(ViewGroup itemView, IReplayListener _iReplayListener) {
        //加载纯文本布局
        super(itemView, R.layout.item_dynamic_multi_pic);
        this.iReplayListener = _iReplayListener;
        send_content_tv = $(R.id.send_content_tv);
        multiImagView = $(R.id.multiImagView);
        send_nick_tv = $(R.id.send_nick_tv);
        title_tv = $(R.id.title_tv);
        head_image = $(R.id.head_image);
        del_tv = $(R.id.del_tv);
        replay_tv = $(R.id.replay_tv);
        forward_tv = $(R.id.forward_tv);
        bg_reply_content_layout = $(R.id.bg_reply_content_layout);
        praise_ll = $(R.id.praise_ll);
        praise_tv = $(R.id.praise_tv);
        reply_content = $(R.id.reply_content);
        send_time_tv = $(R.id.send_time_tv);
    }

    @Override
    public void setData(final Discover data) {
        // TODO: 2017/11/16 设置数据
        title_tv.setText(data.title);
        send_nick_tv.setText(data.username);
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
        else
            head_image.setImageResource(R.drawable.ic_launcher);

        String[] strs = data.imagesUrl.split(",");
        final ArrayList<String> arrayList = new ArrayList<>();
        for (String str : strs) {
            //列表加载小图
            arrayList.add(str + "?x-oss-process=image/resize,w_300,limit_0");
        }
        multiImagView.setUrlList(arrayList);
        multiImagView.setVisibility(View.VISIBLE);
        multiImagView.isGrayImage = false;

        multiImagView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // imagesize是作为loading时的图片size
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                        (view.getMeasuredWidth(), view.getMeasuredHeight());
                List<String> photoUrls = new ArrayList<String>();
                for (String url : arrayList) {
                    //显示大图
                    photoUrls.add(url.replace("?x-oss-process=image/resize,w_300,limit_0", ""));
                }
                ImagePagerActivity.startImagePagerActivity((getContext()), photoUrls,
                        position, imageSize);
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
