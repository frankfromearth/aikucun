package com.aikucun.akapp.adapter.viewholder.discover;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.ImagesUtils;
import com.aikucun.akapp.view.MultiImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak123 on 2017/11/16.
 * 单图
 */

public class DiscoverOneImgViewHolder extends DiscoverBaseViewHolder {
    //图片
    public MultiImageView pic_one_iv;
    private IReplayListener iReplayListener;
    public DiscoverOneImgViewHolder(ViewGroup itemView,IReplayListener replayListener) {
        //加载纯文本布局
        super(itemView, R.layout.item_dynamic_one_image);
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
        pic_one_iv = $(R.id.pic_one_iv);
        send_time_tv = $(R.id.send_time_tv);
        title_tv = $(R.id.title_tv);
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

        if (data.imageHeight !=0 && data.imageWidth !=0){
            int[] w_h = ImagesUtils.getImageWidthAndHeightToDynamic(getContext(), data.imageWidth, data.imageHeight);
            ViewGroup.LayoutParams ivLinearParams = pic_one_iv.getLayoutParams();
            ivLinearParams.height = w_h[1];
            ivLinearParams.width = w_h[0];
            pic_one_iv.setLayoutParams(ivLinearParams);
        }
        List<String> arr = new ArrayList<>();
        arr.add(data.imagesUrl);
//        arr.add(data.imagesUrl+"?x-oss-process=image/resize,w_300,limit_0");
        pic_one_iv.setUrlList(arr);
//        Glide.with(getContext()).load(data.imagesUrl+"?x-oss-process=image/resize,w_300,limit_0").diskCacheStrategy(DiskCacheStrategy
//                    .ALL).into(pic_one_iv);
        pic_one_iv.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // imagesize是作为loading时的图片size
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                        (view.getMeasuredWidth(), view.getMeasuredHeight());
                List<String> photoUrls = new ArrayList<String>();
                photoUrls.add(data.imagesUrl);
                ImagePagerActivity.startImagePagerActivity((getContext()), photoUrls,
                        0, imageSize);
            }
        });
        
        //设置评论数据
        setReplyData(data.comments,0,null);
        replay_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iReplayListener != null){
                    iReplayListener.onReplay(data,v);
                }
            }
        });
        forward_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iReplayListener != null){
                    iReplayListener.onForward(data);
                }
            }
        });
    }
}
