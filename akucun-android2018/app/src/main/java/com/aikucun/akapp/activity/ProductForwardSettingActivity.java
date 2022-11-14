package com.aikucun.akapp.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.widget.BottomDialog;

import java.text.MessageFormat;
import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by ak123 on 2017/12/12.
 */

public class ProductForwardSettingActivity extends BaseActivity {

    //默认转发图片选项 1：多图普通模式，只转发商品图片 2：多图图文模式商品描述转图片再转发 3：单图模式商品描述和图片合成一张图
    public static final String FORWARD_IMG_KEY = "forward_img_key";
    //是否转发缺货尺寸
    public static final String FORWARD_OUTSTOCK_KEY = "forward_outstock_key";
    //转发加价
    public static final String FORWARD_FARE_KEY = "forward_fare_key";


    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.choose_text_tv)
    TextView choose_text_tv;
    @BindView(R.id.choose_img_reslut_intro)
    TextView choose_img_reslut_intro;
    @BindView(R.id.forward_out_of_stock)
    TextView forward_out_of_stock;
    @BindView(R.id.outstock_intro)
    TextView outstock_intro;
    @BindView(R.id.forward_fare_tv)
    TextView forward_fare_tv;
    @BindView(R.id.fare_intro_tv)
    TextView fare_intro_tv;

    @Override
    public void initView() {

        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.forward_product_setting);
    }

    @Override
    public void initData() {
        initLisetner();
        /**图片模式**/
        int type = AppContext.get(FORWARD_IMG_KEY, 1);
        String content = "";
        if (type == 1) { //普通
            choose_img_reslut_intro.setText(R.string.forward_product_normal_intro_more_img_no_text);
            content = getResources().getString(R.string.forward_img_more_img_normal);
        } else if (type == 2) {
            //文字转图片
            content = getResources().getString(R.string.forward_img_more_img);
            choose_img_reslut_intro.setText(R.string.forward_product_normal_intor_more_img_and_text);
        } else {
            //文字和图片合为一张图
            content = getResources().getString(R.string.forward_img_one_img);
            choose_img_reslut_intro.setText(R.string.forward_product_normal_intor_one_img);
        }
        choose_text_tv.setText(content.substring(0, content.indexOf("（")));
        /***缺货情况***/
        int outStockType = AppContext.get(FORWARD_OUTSTOCK_KEY, 1);
        initOutStockInfo(outStockType);
        forward_out_of_stock.setText(getOutStockData().get(outStockType));
        /**转发加价**/
        float fareValue = AppContext.get(FORWARD_FARE_KEY, 0.0f);
        if (fareValue == 0) {
            //不加价
            forward_fare_tv.setText(R.string.forward_product_fare_no);
            fare_intro_tv.setText(R.string.forward_product_fare_no_intro);
        } else {
            forward_fare_tv.setText("+" + fareValue + "元");
            //始终转发
            fare_intro_tv.setText(MessageFormat.format(getResources().getString(R.string.forward_product_fare_intro), "+" + fareValue + "元"));
        }
    }

    private void initLisetner() {
        findViewById(R.id.forward_img_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForwardImgDialog();
            }
        });
        findViewById(R.id.outstock_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOutStockDialog();
            }
        });
        findViewById(R.id.forward_fare_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFareDialog();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_forward_setting_layout;
    }

    /**
     * 图片选项
     */
    private void showForwardImgDialog() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.forward_img_list_title));
        arrayList.add(getResources().getString(R.string.forward_img_more_img_normal));
        arrayList.add(getResources().getString(R.string.forward_img_more_img));
        arrayList.add(getResources().getString(R.string.forward_img_one_img));
        BottomDialog.showIndefiniteDialog(ProductForwardSettingActivity.this, arrayList, true, new BottomDialog.IClickListener() {
            @Override
            public void onClick(String content, int index) {
                choose_text_tv.setText(content.substring(0, content.indexOf("（")));
                AppContext.set(FORWARD_IMG_KEY, index);
                if (index == 1) {
                    //多图普通模式，只转发商品图片
                    choose_img_reslut_intro.setText(R.string.forward_product_normal_intro_more_img_no_text);
                } else if (index == 2) {
                    //多图图文模式商品描述转图片再转发
                    choose_img_reslut_intro.setText(R.string.forward_product_normal_intor_more_img_and_text);
                } else if (index == 3) {
                    //单图模式商品描述和图片合成一张图
                    choose_img_reslut_intro.setText(R.string.forward_product_normal_intor_one_img);
                }
            }
        });

    }

    private void initOutStockInfo(int index) {
        if (index == 1) {
            //不转发
            outstock_intro.setText(R.string.forward_product_outstock_no_intro);
        } else if (index == 2) {
            //始终转发
            outstock_intro.setText(R.string.forward_product_outstock_always_intro);
        } else if (index == 3) {
            //活动一小时内转发
            outstock_intro.setText(R.string.forward_product_outstock_certain_time_onehour);
        } else if (index == 4) {
            //活动两小时内转发
            outstock_intro.setText(R.string.forward_product_outstock_certain_time_twohour);
        }
    }

    private ArrayList<String> getOutStockData() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.forward_product_outstock_dialog_title));
        arrayList.add(getResources().getString(R.string.forward_product_outstock_no));
        arrayList.add(getResources().getString(R.string.forward_product_outstock_always));
        arrayList.add(MessageFormat.format(getResources().getString(R.string.forward_product_outstock_certain_time), 1));
        arrayList.add(MessageFormat.format(getResources().getString(R.string.forward_product_outstock_certain_time), 2));
        return arrayList;
    }

    private void showOutStockDialog() {

        BottomDialog.showIndefiniteDialog(ProductForwardSettingActivity.this, getOutStockData(), true, new BottomDialog.IClickListener() {
            @Override
            public void onClick(String content, int index) {
                forward_out_of_stock.setText(content);
                AppContext.set(FORWARD_OUTSTOCK_KEY, index);
                initOutStockInfo(index);
            }
        });
    }

    /**
     * 商品转发加价
     */
    private void showFareDialog() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(getResources().getString(R.string.forward_product_fare_title));
        arrayList.add(getResources().getString(R.string.forward_product_fare_no));
        arrayList.add(getResources().getString(R.string.forward_product_fare_add_five));
        arrayList.add(getResources().getString(R.string.forward_product_fare_add_ten));
        arrayList.add(getResources().getString(R.string.forward_product_fare_add_custom));
        BottomDialog.showIndefiniteDialog(ProductForwardSettingActivity.this, arrayList, true, new BottomDialog.IClickListener() {
            @Override
            public void onClick(String content, int index) {

                if (index == 1) {
                    AppContext.set(FORWARD_FARE_KEY, (float) 0);
                    forward_fare_tv.setText(content);
                    //不加价
                    fare_intro_tv.setText(R.string.forward_product_fare_no_intro);
                } else if (index == 2 || index == 3) {
                    if (index == 2) AppContext.set(FORWARD_FARE_KEY, (float) 5);
                    else AppContext.set(FORWARD_FARE_KEY, (float) 10);
                    forward_fare_tv.setText(content.substring(content.indexOf("+"), content.length()));
                    //始终转发
                    fare_intro_tv.setText(MessageFormat.format(getResources().getString(R.string.forward_product_fare_intro), content.substring(content.indexOf("+"), content.length())));
                } else if (index == 4) {
                    //输入自定义金额
                    showEditDialog();
                }
            }
        });
    }

    private void showEditDialog() {
        BottomDialog.showEdittextDailog(this, 5, "自定义加价金额", "请输入加价金额（元）", new BottomDialog.IEdittextDialogLisenter() {
            @Override
            public void onResult(String content) {
                AppContext.set(FORWARD_FARE_KEY, Float.valueOf(content));
                forward_fare_tv.setText("+" + content + "元");
                fare_intro_tv.setText(MessageFormat.format(getResources().getString(R.string.forward_product_fare_intro), "+" + content));
            }
        });
    }
}
