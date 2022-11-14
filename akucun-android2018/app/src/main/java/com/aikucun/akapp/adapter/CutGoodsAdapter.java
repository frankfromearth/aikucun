package com.aikucun.akapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import com.aikucun.akapp.activity.PinpaiActivity;
import com.aikucun.akapp.adapter.viewholder.CutGoodsViewHolder;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.SKUListCallback;
import com.aikucun.akapp.api.entity.Comment;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.SCLog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * 切货 List Adapter
 */

public class CutGoodsAdapter extends RecyclerArrayAdapter<Product> {
    public static final int PRODUCT_EVENT_BUY = 1;
    public static final int PRODUCT_EVENT_SAVEIMAGE = 2;
    public static final int PRODUCT_EVENT_FORWARD = 3;
    public static final int PRODUCT_EVENT_COMMENT = 4;
    public static final int PRODUCT_EVENT_COMMENT_REPLY = 5;
    public static final int PRODUCT_EVENT_COMMENT_DELETE = 6;
    public static final int PRODUCT_EVENT_HIDE_PROGRESS = 7;
    public static final int PRODUCT_EVENT_FOLLOW_ACTION = 8;

    public boolean singleZhuanChang = false;
    public boolean hideForward = false;

    public SkuAdapter skuAdapter;

    private ProductSKU selectSku;

    private OnItemEventListener onItemEventListener;

    private int saveCount = 0;
    public List<String> imagesPath = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //
            if (onItemEventListener != null) {
                onItemEventListener.onEvent(PRODUCT_EVENT_FORWARD, (Product) msg.obj, imagesPath, 0);
            }
        }
    };

    public CutGoodsAdapter(Context context) {
        super(context);
    }

    private int getMaxSkuLength(Product product) {
        int maxLength = 0;
        for (ProductSKU sku : product.getSkus()) {
            sku.setSelected(false);
            if (maxLength < sku.getChima().length()) {
                maxLength = sku.getChima().length();
            }
        }
        return maxLength;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
//        skuAdapter = new SkuAdapter(new ArrayList<ProductSKU>(), getContext());
        CutGoodsViewHolder viewHolder = new CutGoodsViewHolder(parent);
        viewHolder.forwardBtn.setVisibility(hideForward ? View.GONE : View.VISIBLE);
        return viewHolder;
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof CutGoodsViewHolder) {
            CutGoodsViewHolder mCutGoodsViewHolder = (CutGoodsViewHolder) holder;
            mCutGoodsViewHolder.refreshTime();
        }

    }

    @Override
    public void onViewDetachedFromWindow(BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof CutGoodsViewHolder) {
            CutGoodsViewHolder mCutGoodsViewHolder = (CutGoodsViewHolder) holder;
            mCutGoodsViewHolder.stopTime();
        }
    }

    private void goToPinPai(Product product) {
        if (1 == product.getProductType()) {
            MToaster.showShort((Activity) getContext(), "该品牌活动暂未开始", MToaster.IMG_ALERT);
            return;
        }
        if (2 == product.getProductType()) {
            if (!product.hasBegun()) {
                MToaster.showShort((Activity) getContext(), "该品牌活动暂未开始", MToaster.IMG_ALERT);
                return;
            }
        }
        Intent intent = new Intent(getContext(), PinpaiActivity.class);
        intent.putExtra("notice",product.getContent());
        intent.putExtra("pinpai", product);
        getContext().startActivity(intent);
    }

    @Override
    public void OnBindViewHolder(BaseViewHolder holder, final int position) {
        super.OnBindViewHolder(holder, position);

        final Product product = getItem(position);
        final CutGoodsViewHolder viewHolder = (CutGoodsViewHolder) holder;

        viewHolder.tvPinpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPinPai(product);
            }
        });
        viewHolder.imageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPinPai(product);
            }
        });


        viewHolder.follow_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(PRODUCT_EVENT_FOLLOW_ACTION, product, null, position);
                }
            }
        });
        viewHolder.zhuanchangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == product.getProductType()) {
                    showComment(product, position);
                } else {
                    goToPinPai(product);
                }
            }
        });


        viewHolder.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectSku == null) {
                    SCLog.error("!! Select SKU == null");
                }
                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(PRODUCT_EVENT_BUY, product, selectSku, position);
                }
            }
        });

        viewHolder.forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemEventListener != null) {
                    onItemEventListener.onEvent(PRODUCT_EVENT_SAVEIMAGE, product, null, position);
                }

                if (product.getId() == null) {
                    saveImagesToDiskAndForward(product);
                    return;
                }
                if (product.hasNoSku()) {
                    saveImagesToDiskAndForward(product);
                    return;
                }

                if (!product.shouldUpdateSKU()) {
                    saveImagesToDiskAndForward(product);
                    return;
                }
                ProductApiManager.getSKUProduct((Activity) getContext(), product.getId(), new SKUListCallback(product) {

                    @Override
                    public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(productSKUs, call, jsonResponse);
                        product.updateSKU(productSKUs);
                        if (!product.enableForward()) {
                            MToaster.showLong((Activity) getContext(), "该商品已卖光了", MToaster.IMG_INFO);
                            if (onItemEventListener != null) {
                                onItemEventListener.onEvent(PRODUCT_EVENT_HIDE_PROGRESS, product, null, position);
                            }
                            return;
                        }
                        saveImagesToDiskAndForward(product);
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (onItemEventListener != null) {
                            onItemEventListener.onEvent(PRODUCT_EVENT_HIDE_PROGRESS, product, null, position);
                        }
                        if (!product.enableForward()) {
                            MToaster.showLong((Activity) getContext(), "该商品已卖光了", MToaster.IMG_INFO);
                            return;
                        }
                        saveImagesToDiskAndForward(product);
                    }
                });

               /* Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        saveImagesToDisk(product);
                        //
                        Message message = new Message();
                        message.what = 1;
                        message.obj = product;
                        mHandler.sendMessage(message);
                    }
                };
                Thread payThread = new Thread(runnable);
                payThread.start();*/
            }
        });

        if (0 != product.getProductType()) {
            viewHolder.zhuanchangBtn.setVisibility((2 == product.getProductType() && !singleZhuanChang) ? View.VISIBLE : View.GONE);
            return;
        }
        int maxSkuLength = getMaxSkuLength(product);
        if (maxSkuLength < 5) {
            viewHolder.skuGridView.setNumColumns(4);
        } else if (maxSkuLength < 8) {
            viewHolder.skuGridView.setNumColumns(3);
        } else {
            viewHolder.skuGridView.setNumColumns(2);
        }

        final SkuAdapter skuAdapter = new SkuAdapter(product.getSkus(), getContext());
        viewHolder.skuGridView.setAdapter(skuAdapter);

        skuAdapter.setItemClickListener(new SkuAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ProductSKU sku, int position) {
                viewHolder.buyBtn.setEnabled(sku.isSelected());

                if (sku != selectSku) {
                    if (selectSku != null) {
                        selectSku.setSelected(false);
                    }
                    if (sku.isSelected()) {
                        selectSku = sku;
                    }
                }
                skuAdapter.notifyDataSetChanged();
            }
        });

        viewHolder.commentAdapter.setOnItemEventListener(new CommentAdapter.OnItemEventListener() {
            @Override
            public void onEvent(int event, Comment comment, Object object, int position) {

                if (CommentAdapter.COMMENT_EVENT_REPLY == event) {
                    if (onItemEventListener != null) {
                        onItemEventListener.onEvent(PRODUCT_EVENT_COMMENT_REPLY, product, comment, position);
                    }

                } else if (CommentAdapter.COMMENT_EVENT_DELETE == event) {
                    if (onItemEventListener != null) {
                        onItemEventListener.onEvent(PRODUCT_EVENT_COMMENT_DELETE, product, comment, position);
                    }
                }

            }
        });
    }

    private void showComment(Product product, final int position) {
        if (onItemEventListener != null) {
            onItemEventListener.onEvent(PRODUCT_EVENT_COMMENT, product, null, position);
        }

    }


    private void checkImageDownloadFinish(Product product, int count) {
        saveCount++;
        if (saveCount == count) {
            //
            Message message = new Message();
            message.what = 1;
            message.obj = product;
            mHandler.sendMessage(message);
        }
    }


    public void saveImagesToDiskAndForward(final Product product) {
        saveCount = 0;
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }

        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        List<String> imageUrls = Product.getImageUrls(product);
        imagesPath.clear();
        final int count = imageUrls.size();
        for (int i = 0; i < count; i++) {
            String url = imageUrls.get(i);

            final String saveName = "pic" + RSAUtils.md5String(url) + ".jpg";
            final File file = new File(dir, saveName);
            if (file.exists()) {
                file.delete();
            }
            imagesPath.add(dir + saveName);

            Glide.with(getContext()).load(url).asBitmap() //必须
                    .centerCrop().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target
                    .SIZE_ORIGINAL) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap>
                        glideAnimation) {
                    try {
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    checkImageDownloadFinish(product, count);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    // Do nothing.

                    checkImageDownloadFinish(product, count);
                }
            });
        }
    }

    public void setOnItemEventListener(OnItemEventListener onItemEventListener) {
        this.onItemEventListener = onItemEventListener;
    }

    public interface OnItemEventListener {
        public void onEvent(int event, Product product, Object object, int position);
    }


}
