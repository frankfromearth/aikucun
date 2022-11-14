package com.aikucun.akapp.adapter.viewholder.discover;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.utils.DateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by ak123 on 2017/11/16.
 * 纯文本布局
 */

public class DiscoverSignleTextViewHolder extends DiscoverBaseViewHolder {

    private IReplayListener replayListener;
    public DiscoverSignleTextViewHolder(ViewGroup itemView,IReplayListener _IReplayListener ) {
        //加载纯文本布局
        super(itemView, R.layout.item_dynamic_signle_text);
        replayListener = _IReplayListener;
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
        title_tv = $(R.id.title_tv);
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

        //设置评论数据
        setReplyData(data.comments,0,null);
        replay_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replayListener != null){
                    replayListener.onReplay(data,v);
                }
            }
        });
        forward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (replayListener != null){
                    replayListener.onForward(data);
                }
            }
        });
    }
}
