package com.aikucun.akapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.LoginActivity;
import com.aikucun.akapp.api.callback.SyncProductsCallback;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.PayType;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.manager.LiveApiManager;
import com.aikucun.akapp.api.response.SynProductsResp;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.wxapi.WXEntryActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2017/11/16.
 * 底部弹出框管理
 */

public class BottomDialog {
    //事件点击回调
    public interface IBottomListenter {
        void onClick();
    }

    /**
     * 显示弹框
     *
     * @param mContext
     * @param topStrRes       顶部文本（资源文件）
     * @param bottomStrRes    底部文本（资源文件）
     * @param topListenter    顶部点击回调
     * @param bottomListenter 底部点击回调
     */
    public static void showBottomDialog(Context mContext, int topStrRes, int bottomStrRes, IBottomListenter topListenter, IBottomListenter bottomListenter) {
        String topStr = mContext.getResources().getString(topStrRes);
        String bottomStr = AppContext.getInstance().getBaseContext().getResources().getString(bottomStrRes);
        show(mContext, topStr, bottomStr, topListenter, bottomListenter);
    }

    /**
     * 显示弹框
     *
     * @param mContext
     * @param topStr          顶部文本
     * @param bottomStr       底部文本
     * @param topListenter    顶部点击回调
     * @param bottomListenter 底部点击回调
     */
    public static void showBottomDialog(Context mContext, String topStr, String bottomStr, IBottomListenter topListenter, IBottomListenter bottomListenter) {
        show(mContext, topStr, bottomStr, topListenter, bottomListenter);
    }

    /**
     * 显示弹框
     *
     * @param mContext
     * @param topText
     * @param bottomText
     * @param mTopBottomLis
     * @param mBtBottomLis
     */
    private static void show(Context mContext, String topText, String bottomText, final IBottomListenter mTopBottomLis, final IBottomListenter mBtBottomLis) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_dynamic);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        LinearLayout bottomLayout = dialog.findViewById(R.id.bottom_layout);
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);

        TextView topLayoutTv = dialog.findViewById(R.id.top_layout_tv);
        if (!TextUtils.isEmpty(topText))
            topLayoutTv.setText(topText);
        TextView bottomLayoutTv = dialog.findViewById(R.id.bottom_layout_tv);
        if (!TextUtils.isEmpty(bottomText))
            bottomLayoutTv.setText(bottomText);


        if (topLayout != null) {
            topLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mTopBottomLis.onClick();
                }
            });
        }
        if (bottomLayout != null) {
            bottomLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mBtBottomLis.onClick();
                }
            });
        }
        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public interface IClickListener {
        void onClick(String content, int index);
    }

    /**
     * 自定义长度
     *
     * @param mContext
     * @param contents
     * @param topUnClick
     * @param mIClickListener
     */
    public static void showIndefiniteDialog(Context mContext, final ArrayList<String> contents, final boolean topUnClick, final IClickListener mIClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_indefinite);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        View topLineView = dialog.findViewById(R.id.top_view);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        TextView top_textview = dialog.findViewById(R.id.top_textview);
        //设置顶部标题
        top_textview.setText(contents.get(0));
        //如果第一项可以点击标题不可见
        if (!topUnClick) {
            topLineView.setVisibility(View.GONE);
            top_textview.setVisibility(View.GONE);
        }
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        topLayout.removeAllViews();
        for (int i = 0, size = contents.size(); i < size; i++) {
            if (topUnClick && i == 0) continue;
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_dialog_indefinite, null);
            TextView textView = view.findViewById(R.id.content_textview);
            final String content = contents.get(i);
            final int index = i;
            textView.setText(content);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (mIClickListener != null)
                        mIClickListener.onClick(content, index);
                }
            });
            topLayout.addView(view);
        }

        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 自定义长度选择框
     *
     * @param mContext
     * @param title
     * @param contents
     * @param mIClickListener
     */
    public static void showIndefiniteDialog(Context mContext, final String title, final ArrayList<String> contents, final IClickListener mIClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_indefinite);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        TextView top_textview = dialog.findViewById(R.id.top_textview);
        //设置顶部标题
        top_textview.setText(title);
        //如果第一项可以点击标题不可见

        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        topLayout.removeAllViews();
        for (int i = 0, size = contents.size(); i < size; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_dialog_indefinite, null);
            TextView textView = view.findViewById(R.id.content_textview);
            final String content = contents.get(i);
            final int index = i;
            textView.setText(content);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (mIClickListener != null)
                        mIClickListener.onClick(content, index);
                }
            });
            topLayout.addView(view);
        }

        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public interface IEdittextDialogLisenter {
        void onResult(String content);
    }

    /**
     * 输入框的提示框
     *
     * @param context
     * @param titleStr
     * @param hintStr
     * @param mIEdittextDialogLisenter
     */
    public static void showEdittextDailog(Context context, int maxLength, String titleStr, String hintStr, final IEdittextDialogLisenter mIEdittextDialogLisenter) {
        showEdittextDialog(context, maxLength, titleStr, hintStr, context.getResources().getString(R.string.cancel), context.getString(R.string.sure), mIEdittextDialogLisenter);
    }

    /**
     * 输入框edittext
     *
     * @param context
     * @param titleStr
     * @param hintStr
     * @param cancelStr
     * @param sureStr
     * @param mIEdittextDialogLisenter
     */
    private static void showEdittextDialog(Context context, int maxLength, String titleStr, String hintStr, String cancelStr, String sureStr, final IEdittextDialogLisenter mIEdittextDialogLisenter) {
        final EditText remarkEt = new EditText(context);
        remarkEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        remarkEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        if (!TextUtils.isEmpty(hintStr)) remarkEt.setHint(hintStr);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleStr).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(cancelStr, null);
        builder.setPositiveButton(sureStr, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String content = remarkEt.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    mIEdittextDialogLisenter.onResult(content);
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    public interface IBrandLiseneter {
        void onClick(LiveInfo liveInfo);
    }

    /**
     * 品牌选择弹框
     *
     * @param mContext
     * @param titleText
     * @param arrayList
     * @param mIClickListener
     */
    public static void showBrandIndefiniteDialog(final Context mContext, String titleText, final List<LiveInfo> arrayList, final IBrandLiseneter mIClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_indefinite);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        View topLineView = dialog.findViewById(R.id.top_view);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        TextView top_textview = dialog.findViewById(R.id.top_textview);
        //设置顶部标题
        top_textview.setText(titleText);
        //如果第一项可以点击标题不可见
        if (TextUtils.isEmpty(titleText)) {
            topLineView.setVisibility(View.GONE);
            top_textview.setVisibility(View.GONE);
        }
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        topLayout.removeAllViews();
        for (int i = 0, size = arrayList.size(); i < size; i++) {
            final LiveInfo liveInfo = arrayList.get(i);

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_brand_pop_dialog_indefinite, null);
            ColorFilterImageView imageView = view.findViewById(R.id.headImage);
            Glide.with(mContext).load(liveInfo.getPinpaiurl()).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(imageView);
            TextView textView = view.findViewById(R.id.brand_name_tv);
            LinearLayout brand_layout = view.findViewById(R.id.brand_layout);
            textView.setText(liveInfo.getPinpaiming());
            brand_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    getBrandInfosByLiveId(mContext,mIClickListener,liveInfo);
//                    if (mIClickListener != null)
//                        mIClickListener.onClick(liveInfo);
                }
            });
            topLayout.addView(view);
        }

        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 加载这个品牌下的商品
     * @param mContext
     * @param mIClickListener
     * @param liveInfo
     */
    private  static void getBrandInfosByLiveId(final Context mContext, final IBrandLiseneter mIClickListener,final  LiveInfo liveInfo) {

        if (liveInfo != null) {
            List<Product> products = ProductManager.loadProducts(0, liveInfo.getLiveid());
            if (products == null || products.size() == 0){
                Product product = ProductManager.getInstance().getLastXuHaoProduct(liveInfo.getLiveid());
                int lastId = 0;
                if (product != null) lastId = product.getLastxuhao();
                LiveApiManager.syncProductsByLiveId((Activity) mContext, liveInfo.getLiveid(), lastId + "", new SyncProductsCallback() {
                    @Override
                    public void onSuccess(SynProductsResp synProductsResp, Call call, Response response) {
                        super.onSuccess(synProductsResp, call, response);
                        if (mIClickListener != null){
                            mIClickListener.onClick(liveInfo);
                        }
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                    }
                });
            }else{
                if (mIClickListener != null){
                    mIClickListener.onClick(liveInfo);
                }
            }

        }
    }

    public interface ISuAccountLisenter {
        void onClick(LoginActivity.SubAccountInfo mSubAccountInfo);
    }

    /**
     * 选择子账户
     *
     * @param mContext
     * @param title
     * @param arrayList
     * @param mIClickListener
     */
    public static void showChooseSubAccountDialog(Context mContext, final String title, final ArrayList<LoginActivity.SubAccountInfo> arrayList, final ISuAccountLisenter mIClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_indefinite);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        TextView top_textview = dialog.findViewById(R.id.top_textview);
        //设置顶部标题
        top_textview.setText(title);
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        topLayout.removeAllViews();
        for (int i = 0, size = arrayList.size(); i < size; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_pop_dialog_choose_subaccount, null);
            TextView nametv = view.findViewById(R.id.name_tv);
            RoundImageView avatariv = view.findViewById(R.id.head_image);
            TextView account_text = view.findViewById(R.id.account_text);
            final LoginActivity.SubAccountInfo accountInfo = arrayList.get(i);
            if (!TextUtils.isEmpty(accountInfo.shoujihao) && accountInfo.shoujihao.length() == 11) {
                String tempPhoneNum = accountInfo.shoujihao.substring(0, 3) + "****" + accountInfo.shoujihao.substring(accountInfo.shoujihao.length() - 4, accountInfo.shoujihao.length());
                account_text.setText(tempPhoneNum);
            }
            if (accountInfo.islogin == 1 && !TextUtils.isEmpty(accountInfo.devicename)) {
                String temp = account_text.getText().toString();
                account_text.setText(temp + MessageFormat.format(mContext.getString(R.string.app_login), accountInfo.devicename));
            }
            nametv.setText(accountInfo.subusername);
            Glide.with(mContext).load(accountInfo.avatar).error(R.drawable.icon_default_avatar).diskCacheStrategy(DiskCacheStrategy
                    .ALL).into(avatariv);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIClickListener.onClick(accountInfo);
                    dialog.dismiss();
                }
            });
            topLayout.addView(view);
        }

        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public interface ISelectedPayTypeListener {
        void onResult(PayType mPayType);
    }

    /**
     * 选择支付类型
     *
     * @param mContext
     * @param arrayList
     * @param mIClickListener
     */
    public static void showPayTypeDialog(Context mContext, final List<PayType> arrayList, final ISelectedPayTypeListener mIClickListener) {
        final Dialog dialog = new Dialog(mContext, R.style.pop_dialog);
        dialog.setContentView(R.layout.pop_dialog_indefinite);
        Window win = dialog.getWindow();
        win.setGravity(Gravity.BOTTOM);
        win.setWindowAnimations(R.style.dialogWindowAnim);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        win.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout topLayout = dialog.findViewById(R.id.top_layout);
        TextView top_textview = dialog.findViewById(R.id.top_textview);
        TextView cancel_layout_tv = dialog.findViewById(R.id.cancel_layout_tv);
        //设置顶部标题
        top_textview.setText(R.string.choose_pay_type);
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        cancel_layout_tv.setText(R.string.sure);
        initPayType(mContext, topLayout, arrayList);

        cancelLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PayType mPayType = getSelectPayType(arrayList);
                if (mPayType != null) {
                    dialog.dismiss();
                    mIClickListener.onResult(mPayType);
                }

            }
        });
    }

    private static PayType getSelectPayType(final List<PayType> arrayList) {
        PayType mPayType = null;
        for (int i = 0, size = arrayList.size(); i < size; i++) {
            if (arrayList.get(i).getFlag() == 1) {
                mPayType = arrayList.get(i);
                break;
            }
        }
        return mPayType;
    }

    private static void initPayType(final Context mContext, final LinearLayout topLayout, final List<PayType> arrayList) {
        topLayout.removeAllViews();
        for (int i = 0, size = arrayList.size(); i < size; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_pay_layout, null);
            TextView payName = view.findViewById(R.id.pay_name_tv);
            TextView payContent = view.findViewById(R.id.pay_content_tv);
            ImageView iconIv = view.findViewById(R.id.icon_pay_logo);
            ImageView selectedIv = view.findViewById(R.id.icon_right_iv);
            final PayType payType = arrayList.get(i);
            payName.setText(payType.getName());
            payContent.setText(payType.getContent());
            if (payType.getPaytype() == 11 || payType.getPaytype() == AppConfig.PAY_TYPE_ALIPAY) {
                //支付宝
                iconIv.setImageResource(R.drawable.logo_alipay);
            } else if (payType.getPaytype() == 12 || payType.getPaytype() == AppConfig.PAY_TYPE_WXPAY) {
                iconIv.setImageResource(R.drawable.logo_wxpay);//微信支付
            } else if (payType.getPaytype() == 13) {
                iconIv.setImageResource(R.drawable.icon_union_pay);//银联支付
            } else {
                iconIv.setImageResource(R.drawable.icon_unknow_pay_type);//APP未知支付
            }
            if (payType.getFlag() == 1) {
                selectedIv.setVisibility(View.VISIBLE);
            } else selectedIv.setVisibility(View.GONE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelectedPayType(mContext, topLayout, payType, arrayList);
                }
            });
            topLayout.addView(view);
        }
    }

    private static void updateSelectedPayType(Context mContext, LinearLayout topLayout, PayType payType, final List<PayType> arrayList) {
        for (int i = 0, size = arrayList.size(); i < size; i++) {
            if (arrayList.get(i).getPaytype() == payType.getPaytype()) {
                arrayList.get(i).setFlag(1);
            } else arrayList.get(i).setFlag(0);
        }
        initPayType(mContext, topLayout, arrayList);
    }
}
