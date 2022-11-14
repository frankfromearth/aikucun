package com.aikucun.akapp.adapter.viewholder;


import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.Comment;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

/**
 * Created by micker on 2017/8/13.
 */

public class CommentViewHolder extends BaseViewHolder<Comment>  {

    public TextView contentText;

    private Comment comment;

    public CommentViewHolder(ViewGroup parent)
    {
        super(parent, R.layout.adapter_comment_item);

        contentText = $(R.id.msg_content_text);
    }

    @Override
    public void setData(Comment data)
    {
        this.comment = data;
        StringBuffer content = new StringBuffer(data.getContent());
        if (data.getName() != null)
        {
            content.insert(0, data.getName()+" : ");
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(content);
//            AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(40);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(getContext().getResources().getColor(R.color.color_bg_button));
            spannableStringBuilder.setSpan(colorSpan, 0, data.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentText.setText(spannableStringBuilder);
        }
        else
        {
            contentText.setTextColor(getContext().getResources().getColor(R.color.color_bg_button));
            contentText.setText(content);
        }
    }
}
