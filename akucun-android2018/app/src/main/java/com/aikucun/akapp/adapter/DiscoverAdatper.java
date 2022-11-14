package com.aikucun.akapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.aikucun.akapp.adapter.viewholder.discover.DiscoverBaseViewHolder;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverMultiViewHolder;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverOneImgViewHolder;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverSignleTextViewHolder;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverType;
import com.aikucun.akapp.adapter.viewholder.discover.DiscoverVideoViewHolder;
import com.aikucun.akapp.api.entity.Discover;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.util.List;

/**
 * Created by ak123 on 2017/11/16.
 * 发现适配器
 */

public class DiscoverAdatper extends RecyclerArrayAdapter<Discover> {
    private DiscoverBaseViewHolder.IReplayListener replayListener;
    private DiscoverBaseViewHolder.IPlayVideoListener iPlayVideoListener;
    public DiscoverAdatper(Context context, DiscoverBaseViewHolder.IReplayListener _replaylistener, DiscoverBaseViewHolder.IPlayVideoListener _IPlayVideoListener) {
        super(context);
        iPlayVideoListener = _IPlayVideoListener;
        replayListener = _replaylistener;
    }

    @Override
    public int getViewType(int position) {
        List<Discover> lists = getAllData();
        Discover discover = lists.get(position);
        if (discover.type == 0){
            if (!TextUtils.isEmpty(discover.imagesUrl)){
                if (discover.imagesUrl.indexOf(",") > 0){
                    //多图
                    return DiscoverType.MULITE_IMG;
                }else   return  DiscoverType.ONE_IMG;
            }else return DiscoverType.SINGLE_TEXT;
        }else {
            // TODO: 2017/11/20 视频
            return DiscoverType.VIDEO;
        }
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == DiscoverType.SINGLE_TEXT)
            return new DiscoverSignleTextViewHolder(parent,replayListener);
        else if (viewType == DiscoverType.MULITE_IMG)
            return new DiscoverMultiViewHolder(parent,replayListener);
        else if (viewType == DiscoverType.ONE_IMG)
            return new DiscoverOneImgViewHolder(parent,replayListener);
        else if (viewType == DiscoverType.VIDEO)
            return new DiscoverVideoViewHolder(parent,replayListener,iPlayVideoListener);
        else return null;
    }
}
