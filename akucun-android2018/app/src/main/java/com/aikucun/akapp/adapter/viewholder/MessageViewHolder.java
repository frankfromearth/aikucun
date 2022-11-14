package com.aikucun.akapp.adapter.viewholder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Message;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by jarry on 17/6/30.
 */

public class MessageViewHolder extends BaseViewHolder<Message>
{
    public TextView titleText;
    public TextView contentText;
    public TextView timeText;
    public ImageView imageView;

    public MessageViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_msg_item);

        titleText = $(R.id.msg_title_text);
        contentText = $(R.id.msg_content_text);
        timeText = $(R.id.msg_time_text);

        imageView = $(R.id.icon_message);

    }

    @Override
    public void setData(Message data)
    {
        titleText.setText(data.getTitle());
        contentText.setText(data.getContent());
        timeText.setText(data.getMessagetime());

        imageView.setImageResource(((data.getReadflag() == 1)) ?
                (R.drawable.icon_msg_read):
                (R.drawable.icon_msg_unread));
    }
}
