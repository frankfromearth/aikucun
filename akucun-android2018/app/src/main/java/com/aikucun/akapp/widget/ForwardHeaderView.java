package com.aikucun.akapp.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;

/**
 * Created by micker on 2017/8/31.
 */

public class ForwardHeaderView extends LinearLayout {

    TextView yizhuanfaText;
    TextView jiangliText;
    TextView kedikouText;
    TextView yidikouText;
    TextView userNoTextView;

    public ForwardHeaderView(Context context) {
        super(context);
        init(context);
    }

    public ForwardHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ForwardHeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.header_forward, null);

        yizhuanfaText = view.findViewById(R.id.header_yizhuanfa);
        jiangliText = view.findViewById(R.id.header_yijiangli);
        kedikouText = view.findViewById(R.id.header_kedikou);
        yidikouText = view.findViewById(R.id.header_yidikou);
        userNoTextView = view.findViewById(R.id.header_user_no);
    }

    public void updateData() {
        // FIXME: 2018/1/4
//        UserInfo userInfo = AppContext.getInstance().getUserInfo();
//        if (userInfo != null)
//        {
//            userNoTextView.setText("代购编号： " + userInfo.getYonghubianhao());
//            yizhuanfaText.setText("已转发： " + userInfo.getForwardcount());
//            int jiangli = userInfo.getKeyongdikou() + userInfo.getYiyongdikou();
//            jiangliText.setText("已奖励： " + jiangli);
//            kedikouText.setText("可抵扣余额： " + userInfo.getKeyongdikou());
//            yidikouText.setText("已抵扣金额： " + userInfo.getYiyongdikou());
//        }
    }
}
