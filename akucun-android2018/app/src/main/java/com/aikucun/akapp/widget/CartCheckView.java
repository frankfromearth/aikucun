package com.aikucun.akapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aikucun.akapp.R;

/**
 * Created by micker on 2017/8/24.
 */

public class CartCheckView extends LinearLayout {
    private Context context;

    private EditText tvContent;

    public CartCheckView(Context context) {
        super(context);
        init(context);
    }

    public CartCheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CartCheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
        View view = View.inflate(context, R.layout.view_buy_check, null);
        tvContent = (EditText) view.findViewById(R.id.comment_et);
//        InputMethodUtils.showInputKeyboard(context,tvContent);
    }

    public EditText getTvContent() {
        return tvContent;
    }
}
