package com.aikucun.akapp.adapter.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.adapter.CommentAdapter;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.utils.DateUtils;
import com.aikucun.akapp.utils.TimeZoneUtils;
import com.aikucun.akapp.view.MultiImageView;
import com.aikucun.akapp.view.SkuGridView;
import com.aikucun.akapp.widget.ExpandTextView;
import com.aikucun.akapp.widget.FastForwardPopWindow;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;

import static com.aikucun.akapp.R.id.recyclerView;


/**
 * 活动中 ViewHolder
 */

public class ActivingViewHolder extends BaseViewHolder<Product> {
    public TextView tvPinpai;
    public ExpandTextView tvContent;
    public TextView tvTime;
    public ImageView imageIcon;

    public ImageView follow_iv;

    public TextView buyBtn;
    public TextView zhuanchangBtn;
    public TextView forwardBtn;

    public RelativeLayout quehuoRl;
    public TextView quehuoView;
    public MultiImageView multiImageView;
    public SkuGridView skuGridView;

    public LinearLayout skuContent;

    public LinearLayout rl_count_down;
    public TextView time_end_tips;
    public TextView time_end_times;
    public TextView time_tips;
    public CountdownView countdownView;

    public RelativeLayout rl_count_down2;
    public TextView time_end_tips2;
    public TextView time_end_times2;
    public TextView time_tips2;
    public CountdownView countdownView2;

    //评论
    public EasyRecyclerView commentRecyclerView;
    public RelativeLayout comment_rl;
    public CommentAdapter commentAdapter;

    private Product product;

    private boolean isMoreImages = false;

    ViewTreeObserver vto;

    public ActivingViewHolder(ViewGroup parent) {
        super(parent, R.layout.adapter_product_item);

        tvPinpai = $(R.id.pinpaiTv);
        follow_iv = $(R.id.follow_iv);
        tvContent = $(R.id.contentTv);
        tvTime = $(R.id.timeTv);
        imageIcon = $(R.id.headImage);
        buyBtn = $(R.id.buyBtn);
        zhuanchangBtn = $(R.id.zhuanchang);
        forwardBtn = $(R.id.forwardBtn);

        quehuoView = $(R.id.quehuoTv);
        multiImageView = $(R.id.multiImagView);
        skuGridView = $(R.id.skuGridview);
        skuContent = $(R.id.skuContent);

        quehuoRl = $(R.id.quehuoRl);
        rl_count_down = $(R.id.rl_count_down);
        time_tips = $(R.id.time_tips);
        time_end_tips = $(R.id.time_end_tips);
        time_end_times = $(R.id.time_end_times);
        countdownView = $(R.id.countdown);

        rl_count_down2 = $(R.id.rl_count_down2);
        time_tips2 = $(R.id.time_tips2);
        time_end_tips2 = $(R.id.time_end_tips2);
        time_end_times2 = $(R.id.time_end_times2);
        countdownView2 = $(R.id.countdown2);

        vto = multiImageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ViewGroup.LayoutParams params = quehuoRl.getLayoutParams();
                params.width = multiImageView.getMeasuredHeight();
                params.height = multiImageView.getMeasuredWidth();
                quehuoRl.setLayoutParams(params);
                return true;
            }
        });

        comment_rl = $(R.id.comment_rl);
        commentRecyclerView = $(recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentRecyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(this.getContext());
        commentRecyclerView.setAdapter(commentAdapter);

/*
        ViewStub viewStub = $(R.id.viewStub);
        if (viewStub == null)
        {
            throw new IllegalArgumentException("viewStub is null...");
        }
        viewStub.setLayoutResource(R.layout.viewstub_imgtest);
        View subView = viewStub.inflate();
        MultiImageView multiImageView = (MultiImageView) subView.findViewById(R.id.multiImagView);
        if (multiImageView != null)
        {
            this.multiImageView = multiImageView;
        }
*/
    }

    @Override
    public void setData(Product data) {
        this.product = data;
        tvPinpai.setText(data.getPinpai());
        tvContent.setText(data.getDesc());
        tvTime.setText(DateUtils.friendlyTime(data.getShangjiashuzishijian() * 1000));

        buyBtn.setEnabled(false);

        //图片
        Glide.with(getContext()).load(data.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                .ALL).into(imageIcon);

        final List<String> imageUrls = Product.getImageUrls(data);
        if (imageUrls != null && imageUrls.size() > 0) {
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setUrlList(imageUrls);
            multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // imagesize是作为loading时的图片size
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                            (view.getMeasuredWidth(), view.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<String>();
                    for (String url : imageUrls) {
                        photoUrls.add(url);
                    }
                    ImagePagerActivity.startImagePagerActivity((getContext()), photoUrls,
                            position, imageSize);
                }
            });
        } else {
            multiImageView.setVisibility(View.GONE);
        }

        comment_rl.setVisibility(View.GONE);
        if (data.getComments() != null) {
            comment_rl.setVisibility(data.getComments().size() > 0 ? View.VISIBLE : View.GONE);
            commentAdapter.clear();
            commentAdapter.addAll(data.getComments());
        }

        zhuanchangBtn.setVisibility(View.GONE);
        if (0 == data.getProductType()) {
            tvContent.setShowLines(10);
            int bohuojia = FastForwardPopWindow.getDescAmount(data.getDesc());
            if (AppContext.getInstance().getUserInfo().getViplevel() > 0 && bohuojia > 0) {
                float temp = (bohuojia - data.getJiesuanjia()) / 100;
                String endStr = MessageFormat.format(AppContext.getInstance().getResources().getString(R.string.purchase_fee), temp);

                String content = data.getDesc() + endStr;
                SpannableStringBuilder style = new SpannableStringBuilder(content);
                style.setSpan(new ForegroundColorSpan(AppContext.getInstance().getResources().getColor(R.color.color_accent)), content.length() - endStr.length(), content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                tvContent.setText(style);
            } else tvContent.setText(data.getDesc());

            forwardBtn.setEnabled(data.enableForward());
            quehuoRl.setVisibility(data.isQuehuo() ? View.VISIBLE : View.GONE);
            multiImageView.isGrayImage = false;
            //隐藏评论 fixme
            zhuanchangBtn.setVisibility(View.GONE);
            zhuanchangBtn.setText("评论");
            skuContent.setVisibility(View.VISIBLE);
            rl_count_down.setVisibility(View.GONE);
            rl_count_down2.setVisibility(View.GONE);

            //处理本次商品上架已完成，活动时间一晚上截止，请及时下单采购
            {
                if (product.hasNoSku()) {
                    quehuoRl.setVisibility(View.GONE);
                    skuContent.setVisibility(View.GONE);
                    forwardBtn.setEnabled(true);
                }
            }
        } else {
            tvContent.setShowLines(5);
            multiImageView.isGrayImage = false;
            forwardBtn.setEnabled(true);

            quehuoRl.setVisibility(View.GONE);
            skuContent.setVisibility(View.GONE);

            time_end_tips.setVisibility(View.GONE);
            time_end_times.setVisibility(View.GONE);
            time_end_tips2.setVisibility(View.GONE);
            time_end_times2.setVisibility(View.GONE);

            //活动
            if (2 == data.getProductType()) {
                time_end_tips.setVisibility(View.VISIBLE);
                time_end_times.setVisibility(View.VISIBLE);
                time_end_tips2.setVisibility(View.VISIBLE);
                time_end_times2.setVisibility(View.VISIBLE);
                zhuanchangBtn.setText(R.string.special_event);
                zhuanchangBtn.setVisibility(View.VISIBLE);
            } else {
            }

            if (imageUrls.size() > 4) {
                isMoreImages = true;
                rl_count_down.setVisibility(View.GONE);
                rl_count_down2.setVisibility(View.VISIBLE);
                refreshTime(countdownView2, time_tips2, time_end_times2);
            } else {
                rl_count_down.setVisibility(View.VISIBLE);
                rl_count_down2.setVisibility(View.GONE);
                refreshTime(countdownView, time_tips, time_end_times);
            }
        }


        follow_iv.setVisibility(View.GONE);

        if (0 == product.getProductType()) {
            if (product.getFollow() == 1) {
                follow_iv.setBackground(getContext().getResources().getDrawable(R.drawable
                        .icon_heart_selected));
            } else {
                follow_iv.setBackground(getContext().getResources().getDrawable(R.drawable
                        .icon_heart));
            }
            if (null != product.getSkus() && 0 != product.getSkus().size()) {
                follow_iv.setVisibility(View.VISIBLE);
            }
        }

    }

    private void configLiveTime(CountdownView countView, TextView timeTips, TextView timeEnd) {
        Date date = new Date();
        long now = date.getTime() / 1000;
        countView.setVisibility(View.GONE);

        timeEnd.setText(TimeZoneUtils.longToString(product.getEndtimestamp() * 1000));
        if (product.getBegintimestamp() > now) {
            timeTips.setText("即将开始");
            timeTips.setTextColor(getContext().getResources().getColor(R.color.black));
            if (product.getBegintimestamp() - now < 86400) {
                countView.customTimeShow(false, true, true, true, false);

            } else {
                countView.customTimeShow(true, false, false, false, false);
            }
            refreshTime(countView, 1000 * (product.getBegintimestamp() - now));
            countView.setVisibility(View.VISIBLE);
        } else if (product.getEndtimestamp() > now) {
            if (product.getEndtimestamp() - now < 86400) {
                timeTips.setText("距活动结束");
                countView.customTimeShow(false, true, true, true, false);

            } else {
                timeTips.setText("距活动结束");
                countView.customTimeShow(true, false, false, false, false);
            }
            refreshTime(countView, 1000 * (product.getEndtimestamp() - now));
            countView.setVisibility(View.VISIBLE);
            timeTips.setTextColor(getContext().getResources().getColor(R.color.color_accent));
        } else {
            timeTips.setText("活动已经结束");
        }

    }

    private void configTrailerTime(CountdownView countView, TextView timeTips) {
        Date date = new Date();
        long now = date.getTime() / 1000;
        countView.setVisibility(View.GONE);
        if (product.getBegintimestamp() > now) {
            timeTips.setText("即将开始");
            timeTips.setTextColor(getContext().getResources().getColor(R.color.black));
            if (product.getBegintimestamp() - now < 86400) {
                countView.customTimeShow(false, true, true, true, false);
            } else {
                countView.customTimeShow(true, false, false, false, false);
            }
            refreshTime(countView, 1000 * (product.getBegintimestamp() - now));
            countView.setVisibility(View.VISIBLE);
        }
    }

    public void refreshTime(CountdownView countView, TextView timeTips, TextView timeEnd) {
        if (product == null) {
            return;
        }
        if (1 == product.getProductType()) {
            configTrailerTime(countView, timeTips);

        } else if (2 == product.getProductType()) {
            configLiveTime(countView, timeTips, timeEnd);
        }
    }

    public void refreshTime() {
        if (isMoreImages) {
            refreshTime(countdownView2, time_tips2, time_end_times2);
        } else {
            refreshTime(countdownView, time_tips, time_end_times);
        }
    }

    public void stopTime() {
        if (isMoreImages) {
            countdownView2.stop();
        } else {
            countdownView.stop();
        }
    }

    private void refreshTime(CountdownView countView, long leftTime) {
        if (leftTime > 0) {
            countView.start(leftTime);
            countView.forceLayout();
        } else {
            countView.stop();
            countView.allShowZero();
        }
    }

}
