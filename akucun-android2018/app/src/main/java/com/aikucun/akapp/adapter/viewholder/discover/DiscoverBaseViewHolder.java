package com.aikucun.akapp.adapter.viewholder.discover;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.api.entity.Discover;
import com.aikucun.akapp.api.entity.Reply;
import com.aikucun.akapp.widget.ExpandTextView;
import com.aikucun.akapp.widget.RoundImageView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by ak123 on 2017/11/16.
 */

public class DiscoverBaseViewHolder extends BaseViewHolder<Discover> {
    /**
     * 评论回调
     */
    public interface IReplayListener{void onReplay(Discover discover,View view);void onForward(Discover discover);}
    public interface IPlayVideoListener{void onPlayVideo(String videoId);}
    //发送内容
    public ExpandTextView send_content_tv;
    //发送者名字
    public TextView send_nick_tv;
    //发送者头像
    public RoundImageView head_image;
    //删除
    public TextView del_tv;
    //转发按钮
    public TextView forward_tv;
    //评论按钮
    public TextView replay_tv;
    //评论内容
    public LinearLayout bg_reply_content_layout;
    //赞列表
    public LinearLayout praise_ll;
    //赞成员
    public TextView praise_tv;
    //评论列
    public LinearLayout reply_content;
    //发布时间
    public TextView send_time_tv;
    //标题
    public TextView title_tv;

    public DiscoverBaseViewHolder(View itemView) {
        super(itemView);
    }

    public DiscoverBaseViewHolder(ViewGroup parent, @LayoutRes int res) {
        super(parent, res);
    }

    protected void setReplyData(ArrayList<Reply> arrayList, int poistion, EditText editText){
        if (arrayList != null && arrayList.size() > 0){
            reply_content.removeAllViews();
            for (int i = 0,size = arrayList.size();i<size;i++){
                View view = DiscoverReplyHandler.getReplyTextView(getContext(), poistion, editText, arrayList.get(i), i, reply_content, new DiscoverReplyHandler.ITextLinkListener() {
                    @Override
                    public void onTextLink(int dypos, int status, String openId, String pOpenId, String pNickName, int deleteReplyIndx, LinearLayout linearLayout) {

                    }
                });
                reply_content.addView(view);
            }
            bg_reply_content_layout.setVisibility(View.VISIBLE);
        }else{
            bg_reply_content_layout.setVisibility(View.GONE);
        }
    }

}
