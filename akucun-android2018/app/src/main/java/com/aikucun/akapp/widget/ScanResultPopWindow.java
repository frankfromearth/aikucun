package com.aikucun.akapp.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by micker on 2017/7/2.
 */

public class ScanResultPopWindow extends PopupWindow implements View.OnClickListener {

    private TextView tvContent;
    private TextView tvTip;
    private TextView tvBarcode;
    private TextView tvBackup;
    private ImageView imageView;
    private View rlDone;

    private Context context;
//    private YiFaAdOrderDB yiFaAdOrderDB;
    private CartProduct cartProduct;
    int flag = -1;

    public ScanResultPopWindow(Context context) {
        super(context);
        init(context);
    }

    public ScanResultPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScanResultPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
        View view = View.inflate(context, R.layout.view_scan_result, null);

        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvTip = (TextView) view.findViewById(R.id.tv_tip);
        tvBarcode = (TextView) view.findViewById(R.id.tv_barcode);
        tvBackup = (TextView) view.findViewById(R.id.tv_backup);

        rlDone = view.findViewById(R.id.rl_done_btn);

        imageView = (ImageView) view.findViewById(R.id.imageView);

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

        rlDone.setOnClickListener(this);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public CartProduct getCartProduct()
    {
        return cartProduct;
    }

    public void setCartProduct(CartProduct cartProduct)
    {
        this.cartProduct = cartProduct;

        tvContent.setText(cartProduct.getDesc());
        ProductSKU sku = cartProduct.getSku();
        if (sku != null) {
            tvTip.setText(sku.getChima() + " x1" + cartProduct.getDanwei());
        } else {

            tvTip.setText(cartProduct.getChima() + " x1" + cartProduct.getDanwei());
        }
        tvBarcode.setText(sku.getBarcode());
        tvBackup.setText("备 注：" + cartProduct.getRemark());

        Glide.with(AppContext.getInstance().getApplicationContext()).load(cartProduct.getImageUrl()).diskCacheStrategy
                (DiskCacheStrategy.ALL).into(imageView);
    }

/*
    public void setYiFaAdOrderDB(YiFaAdOrderDB yiFaAdOrderDB) {
        this.yiFaAdOrderDB = yiFaAdOrderDB;

        AdOrder adOrder = this.yiFaAdOrderDB.AdOrder();
        CartProduct product = adOrder.getCartProductWithID(yiFaAdOrderDB.getCartproductid());
        tvContent.setText(product.getDesc());
        ProductSKU sku = product.getSku();
        if (sku != null) {
            tvTip.setText(sku.getChima() + " x1" + product.getDanwei());
        } else {

            tvTip.setText(product.getChima() + " x1" + product.getDanwei());
        }
        tvBarcode.setText(yiFaAdOrderDB.getBarcode());
        tvBackup.setText("备 注：" + yiFaAdOrderDB.getRemark());

        Glide.with(AppContext.getInstance().getApplicationContext()).load(product.getImageUrl()).diskCacheStrategy
                (DiskCacheStrategy.ALL).into(imageView);

    }

    public YiFaAdOrderDB getYiFaAdOrderDB() {
        return yiFaAdOrderDB;
    }
*/

    @Override
    public void dismiss() {
        super.dismiss();
        if (onWindowItemListener != null) {
            onWindowItemListener.onEvent(this, flag);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_done_btn: {
//                YiFaAdOrderManager.getInstance().peiHuo(yiFaAdOrderDB);
                flag = 0;
                this.dismiss();
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
        public void onEvent(ScanResultPopWindow popWindow , int event);
    }

}
