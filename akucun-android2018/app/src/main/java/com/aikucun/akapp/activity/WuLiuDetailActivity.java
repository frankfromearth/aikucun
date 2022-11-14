package com.aikucun.akapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.HttpConfig;
import com.aikucun.akapp.utils.StringUtils;


/**
 * Created by micker on 2017/7/12.
 */

public class WuLiuDetailActivity extends WebViewActivity {

    private String wuliu;

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mTitleText.setText(intent.getStringExtra("title"));
        wuliu = intent.getStringExtra("wuliu");

        final String wulius[] = wuliu.split(",");
        if (wulius.length > 0) {
            showMultitDingdan();
        } else {
            gotoWuliu(wuliu);
        }

    }

    private void gotoWuliu(String danhao) {
        if (StringUtils.isEmpty(danhao))
            return;
        webView.loadUrl(HttpConfig.WULIU_URL_PREFER + danhao);
    }

    private void showMultitDingdan() {
        final String wulius[] = wuliu.split(",");
        if (wulius.length > 0) {

            //默认进入第一个物流单号
            gotoWuliu(wulius[0]);

            btn_right.setVisibility(View.VISIBLE);
            btn_right.setOnClickListener(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("选择快递单号");
            builder.setItems(wulius, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    String wuliu = wulius[which];
                    gotoWuliu(wuliu);
                }
            });
            builder.show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_right:{
                showMultitDingdan();
            }
            break;
        }
    }
}
