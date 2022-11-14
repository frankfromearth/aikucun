package com.aikucun.akapp.adapter.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.KefuMsgItem;
import com.aikucun.akapp.api.entity.UserInfo;
import com.aikucun.akapp.utils.GlideCircleTransform;
import com.aikucun.akapp.utils.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import static com.igexin.sdk.GTServiceManager.context;


/**
 * Created by jarry on 17/6/30.
 */

public class KefuMsgViewHolder extends BaseViewHolder<KefuMsgItem>
{
    private View left_item, right_item;

    public KefuMsgViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_kefu_msg_item);

        left_item = $(R.id.left_item);
        right_item = $(R.id.right_item);
    }

    @Override
    public void setData(KefuMsgItem data)
    {

        View current = null;
        if (data.isKefu()) {
            current = left_item;
            right_item.setVisibility(View.GONE);
            left_item.setVisibility(View.VISIBLE);
        } else {
            current = right_item;
            right_item.setVisibility(View.VISIBLE);
            left_item.setVisibility(View.GONE);
        }

        TextView contentText = (TextView) current.findViewById(R.id.msg_content_text);
        ImageView imageView = (ImageView) current.findViewById(R.id.icon_message);

        contentText.setText(data.getContent());

        if (data.isKefu()) {
            imageView.setImageResource(R.drawable.ic_round_launcher);
        } else {
            UserInfo userInfo = AppContext.getInstance().getUserInfo();
            // FIXME: 2018/1/4
            if (userInfo!=null && !StringUtils.isEmpty(userInfo.getAvator())) {
                Glide.with(getContext()).load(userInfo.getAvator()).transform(new GlideCircleTransform(context)).diskCacheStrategy(DiskCacheStrategy
                        .ALL).placeholder(R.color.color_bg_image).into(imageView);
            } else {
                imageView.setImageResource(R.color.color_bg_image);
            }
        }
    }
}
