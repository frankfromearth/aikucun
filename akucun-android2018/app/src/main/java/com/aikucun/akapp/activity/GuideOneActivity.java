package com.aikucun.akapp.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.LatestIdeosListCallback;
import com.aikucun.akapp.api.entity.LatestIdeosList;
import com.aikucun.akapp.api.manager.IdeoManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 引导页
 */
public class GuideOneActivity extends BaseActivity {
    Handler handler = new Handler();
    @BindView(R.id.guide_one_image)
    ImageView mGuideOneImage;

    boolean isError;

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        isError = getIntent().getBooleanExtra("isError", false);
        IdeoManager.getLatestIdeosList(this, 0, new LatestIdeosListCallback() {
            @Override
            public void onApiSuccess(LatestIdeosList data, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(data, call, jsonResponse);
                if (TextUtils.isEmpty(data.getData())) {
                    Log.e("LatestIdeosList","data"+data.getData());
                    goToNextPage(0);
                } else {
                    Glide.with(GuideOneActivity.this).load(data.getData()).into(mGuideOneImage);
                    goToNextPage(3000);
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                if(isError){
                    Intent intent = new Intent(GuideOneActivity.this, WXEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    finish();
                }else{
                    goToMain(0);
                }
            }
        });
    }

    private void goToNextPage(long time) {
        if (isError) {
            goToWeChat(time);
        } else {
            goToMain(time);
        }
    }


    private void goToWeChat(long time) {
        //三秒之后跳到微信
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideOneActivity.this, WXEntryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                finish();
            }
        }, time);
    }

    private void goToMain(long time) {
        //三秒之后跳到首页
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideOneActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                finish();
            }
        }, time);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_one;
    }
}
