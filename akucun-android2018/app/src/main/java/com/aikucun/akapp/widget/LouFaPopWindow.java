package com.aikucun.akapp.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.view.MultiImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by micker on 2017/7/2.
 */

public class LouFaPopWindow extends PopupWindow implements View.OnClickListener {


    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvJumpNO;
    private TextView tvPinpai;

    private Context context;

    private MultiImageView multiImageView;
    private View rl_forward_done,rl_forward_cancel;

    private CartProduct cartProduct;

    public LouFaPopWindow(Context context) {
        super(context);
        init(context);
    }

    public LouFaPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LouFaPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
        View view = View.inflate(context, R.layout.view_loufa, null);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvJumpNO = (TextView) view.findViewById(R.id.tv_jump_no);
        tvPinpai = (TextView) view.findViewById(R.id.tv_pinpai);

        rl_forward_cancel = view.findViewById(R.id.rl_forward_cancel);
        rl_forward_done = view.findViewById(R.id.rl_forward_done);

        multiImageView = (MultiImageView) view.findViewById(R.id.multiImagView);

        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        rl_forward_done.setOnClickListener(this);
        rl_forward_cancel.setOnClickListener(this);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void setCartProduct(CartProduct cartProduct) {
        if (null == cartProduct) return;
        this.cartProduct = cartProduct;

        tvContent.setText(cartProduct.getDesc());
        tvPinpai.setText(cartProduct.getSku().getChima() + "  x1");

        initImages();
    }

    public CartProduct getCartProduct() {
        return cartProduct;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void initImages() {
        final List<String> imageUrls = cartProduct.akucunImageUrls();
        if (imageUrls != null && imageUrls.size() > 0)
        {
            multiImageView.imageWidth = (int) TDevice.dpToPixel(75);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setUrlList(imageUrls);
            multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener()
            {
                @Override
                public void onItemClick(View view, int position)
                {
                    // imagesize是作为loading时的图片size
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                            (view.getMeasuredWidth(), view.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<String>();
                    for (String url : imageUrls)
                    {
                        photoUrls.add(url);
                    }
                    ImagePagerActivity.startImagePagerActivity(LouFaPopWindow.this.context, photoUrls, position, imageSize);
                }
            });
        }
        else
        {
            multiImageView.setVisibility(View.GONE);
        }
    }



    @Override
    public void dismiss() {
        super.dismiss();
        if (onWindowItemListener != null) {
            onWindowItemListener.onEvent(this,0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_forward_cancel: {
                LouFaPopWindow.this.dismiss();
            }
            break;
            case R.id.rl_forward_done: {
                //漏发申诉
                if (onWindowItemListener != null) {
                    onWindowItemListener.onEvent(this,1);
                }
            }
            break;
        }
    }


    private  OnWindowItemListener onWindowItemListener;

    public void setOnWindowItemListener(OnWindowItemListener onWindowItemListener) {
        this.onWindowItemListener = onWindowItemListener;
    }

    public interface OnWindowItemListener
    {
        public void onEvent(LouFaPopWindow popWindow, int event);
    }

}
