package com.aikucun.akapp.widget;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.ImagePagerActivity;
import com.aikucun.akapp.activity.ProductForwardSettingActivity;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.SKUListCallback;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.ProductSKU;
import com.aikucun.akapp.api.manager.ProductApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.storage.ExplosionGoodsManager;
import com.aikucun.akapp.storage.LiveInfosManager;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.SystemShareUtils;
import com.aikucun.akapp.utils.TDevice;
import com.aikucun.akapp.view.MultiImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

import static com.lzy.okgo.OkGo.getContext;

/**
 * Created by micker on 2017/7/2.
 */

public class FastForwardPopWindow extends PopupWindow implements View.OnClickListener {

    public static final int FAST_FORWARD_POP_WINDOW_DISMISS = 3;

    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvJumpNO;
    private TextView tvPinpai;
    private TextView forward_text;

    private CheckBox checkBox, more_img_checkbox, more_normal_checkbox;
    private Context context;

    private MultiImageView multiImageView;
    private Product product;
    private View rlPinpai, rlJumpNO, rlJumpNext, rlForward, rlJumpPrevious;
    private ImageView forward_set_iv, close_iv;


    private List<Product> searchProducts;


    public FastForwardPopWindow(Context context) {
        super(context);
        init(context);
    }

    public FastForwardPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastForwardPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {

        this.context = context;
        View view = View.inflate(context, R.layout.view_forward, null);
        LinearLayout ly_myinfo_changeaddress = view.findViewById(R.id.ly_myinfo_changeaddress);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvJumpNO = (TextView) view.findViewById(R.id.tv_jump_no);
        tvPinpai = (TextView) view.findViewById(R.id.tv_pinpai);
        checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        forward_text = view.findViewById(R.id.forward_text);
        more_img_checkbox = (CheckBox) view.findViewById(R.id.more_img_checkbox);
        more_normal_checkbox = (CheckBox) view.findViewById(R.id.more_normal_checkbox);

        rlJumpNO = view.findViewById(R.id.rl_forward_no);
        rlJumpNext = view.findViewById(R.id.rl_forward_next);
        rlJumpPrevious = view.findViewById(R.id.rl_forward_previous);
        rlPinpai = view.findViewById(R.id.rl_pinpai);
        rlForward = view.findViewById(R.id.rl_forward_btn);

        multiImageView = (MultiImageView) view.findViewById(R.id.multiImagView);
        forward_set_iv = view.findViewById(R.id.forward_set_iv);
        close_iv = view.findViewById(R.id.close_iv);
        //??????SelectPicPopupWindow???View
        this.setContentView(view);
        //??????SelectPicPopupWindow??????????????????
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //??????SelectPicPopupWindow??????????????????
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //??????SelectPicPopupWindow?????????????????????
        this.setFocusable(true);
        //??????SelectPicPopupWindow????????????????????????
        // this.setAnimationStyle(R.style.dialogWindowAnim);
        //???????????????ColorDrawable??????????????????
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //??????SelectPicPopupWindow?????????????????????
        this.setBackgroundDrawable(dw);

        rlJumpNext.setOnClickListener(this);
        rlJumpPrevious.setOnClickListener(this);
        rlForward.setOnClickListener(this);
        rlPinpai.setOnClickListener(this);
        rlJumpNO.setOnClickListener(this);
        //?????????????????????????????????
        if (AppContext.get(ProductForwardSettingActivity.FORWARD_IMG_KEY, 1) == 2) {
            more_img_checkbox.setChecked(true);
            checkBox.setChecked(false);
            more_normal_checkbox.setChecked(false);
        } else if (AppContext.get(ProductForwardSettingActivity.FORWARD_IMG_KEY, 1) == 3) {
            more_img_checkbox.setChecked(false);
            checkBox.setChecked(true);
            more_normal_checkbox.setChecked(false);
        } else if (AppContext.get(ProductForwardSettingActivity.FORWARD_IMG_KEY, 1) == 1) {
            more_normal_checkbox.setChecked(true);
            more_img_checkbox.setChecked(false);
            checkBox.setChecked(false);
        }
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        more_img_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                more_img_checkbox.setChecked(isChecked);
                if (more_img_checkbox.isChecked()) {
                    checkBox.setChecked(false);
                    more_normal_checkbox.setChecked(false);
                } else if (!checkBox.isChecked() && !more_img_checkbox.isChecked()) {
                    more_normal_checkbox.setChecked(true);
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox.setChecked(isChecked);
                if (checkBox.isChecked()) {
                    more_img_checkbox.setChecked(false);
                    more_normal_checkbox.setChecked(false);
                } else if (!more_img_checkbox.isChecked() && !more_normal_checkbox.isChecked()) {
                    more_normal_checkbox.setChecked(true);
                }
                //AppContext.set("SHARE_CHECK", isChecked);
            }
        });
        more_normal_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                more_normal_checkbox.setChecked(isChecked);
                if (more_normal_checkbox.isChecked()) {
                    checkBox.setChecked(false);
                    more_img_checkbox.setChecked(false);
                } else if (!checkBox.isChecked() && !more_img_checkbox.isChecked()) {
                    more_normal_checkbox.setChecked(true);
                }
            }
        });
        //checkBox.setChecked(AppContext.get("SHARE_CHECK", false));

        forward_set_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.startActivity((Activity) context, ProductForwardSettingActivity.class);
                dismiss();
            }
        });
        close_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //????????????????????????
        float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        if (addMoney > 0) {
            forward_text.setText(forward_text.getText().toString() + "+" + addMoney + "???");
            forward_text.setTextColor(context.getResources().getColor(R.color.color_accent));
        }

        ly_myinfo_changeaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setProduct(Product product) {
        if (null == product) return;
        this.product = product;

        tvContent.setText(product.getDesc());
        tvPinpai.setText("??? ??????" + product.getPinpai());
        tvJumpNO.setText(String.format("????????? %03d ???", product.getXuhao()));

        initImages();

        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("aikucun", product.getDesc()));
    }

    public Product getProduct() {
        return product;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    private void initImages() {
        final List<String> imageUrls = Product.getImageUrls(product);
        if (imageUrls != null && imageUrls.size() > 0) {
            multiImageView.imageWidth = (int) TDevice.dpToPixel(44);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.setUrlList(imageUrls);
            multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    // imagesize?????????loading????????????size
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                            (view.getMeasuredWidth(), view.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<String>();
                    for (String url : imageUrls) {
                        photoUrls.add(url);
                    }
                    ImagePagerActivity.startImagePagerActivity(FastForwardPopWindow.this.context, photoUrls, position, imageSize);
                }
            });
        } else {
            multiImageView.setVisibility(View.GONE);
        }
    }


    private void goToIndex() {
        rlJumpPrevious.setBackgroundColor(context.getResources().getColor(R.color.white));
        int xuehao = ProductManager.getInstance().getForwardIndex();
        Product product = ProductManager.getInstance().getForwardProduct(xuehao + 1);
        if (null != product) {
            setProduct(product);
            ProductManager.getInstance().setForwardIndex(product.getXuhao());
        } else {
            MToaster.showShort((Activity) context, "????????????????????????!", MToaster.IMG_ALERT);
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (onWindowItemListener != null) {
            onWindowItemListener.onEvent(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_pinpai: {
                showPinPai();
            }
            break;
            case R.id.rl_forward_no: {
                showNo();
            }
            break;
            case R.id.rl_forward_next:
                goToIndex();
                break;
            case R.id.rl_forward_previous:
                goToPrevious();
                break;
            case R.id.rl_forward_btn: {

                if (!product.shouldUpdateSKU()) {
                    saveImagesToDiskAndForward(product);
                    return;
                }
                ProductApiManager.getSKUProduct((Activity) context, product.getId(), new SKUListCallback(product) {

                    @Override
                    public void onApiSuccess(List<ProductSKU> productSKUs, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(productSKUs, call, jsonResponse);
                        product.updateSKU(productSKUs);
                        if (!product.enableForward()) {
                            MToaster.showLong((Activity) context, "?????????????????????", MToaster.IMG_INFO);
                            return;
                        }
                        saveImagesToDiskAndForward(product);
                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (!product.enableForward()) {
                            MToaster.showLong((Activity) context, "?????????????????????", MToaster.IMG_INFO);
                            return;
                        }
                        saveImagesToDiskAndForward(product);
                    }
                });

            }
            break;
        }
    }

    //?????????
    private void goToPrevious() {
        {
            int xuehao = ProductManager.getInstance().getForwardIndex();
            if (xuehao == 1) {
                return;
            }
            Product product = ProductManager.getInstance().getForwardProduct(xuehao - 1);
            if (null != product) {
                setProduct(product);
                ProductManager.getInstance().setForwardIndex(product.getXuhao());
            }
        }
    }

    private void showNo() {
        final EditText remarkEt = new EditText(context);
        remarkEt.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????????????????????3???").setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TDevice.hideSoftKeyboard(remarkEt);
                String remark = remarkEt.getText().toString();
                if (remark.length() > 3) {
                    ((BaseActivity) context).showMessage("????????????????????????3???!");
                    return;
                }
                if (StringUtils.isEmpty(remark)) {
                    ((BaseActivity) context).showMessage("????????????????????????!");
                    return;
                }
                Pattern p = Pattern.compile("[0-9]*");
                Matcher m = p.matcher(remark);
                if (!m.matches()) {
                    ((BaseActivity) context).showMessage("????????????????????????!");
                    return;
                }

                Integer ind = 0;
                try {
                    ind = Integer.parseInt(remark);
                } catch (Exception e) {
                    ((BaseActivity) context).showMessage("????????????????????????!");
                    return;
                }
                Product product = ProductManager.getInstance().getForwardProduct(ind);
                if (null == product) {
                    ((BaseActivity) context).showMessage("????????????????????????!");
                    return;
                } else {
                    setProduct(product);
                    ProductManager.getInstance().setForwardIndex(product.getXuhao());
                }
            }
        });
        builder.show();
    }

    private void showPinPai() {

//        List<LiveInfo> liveInfos = LiveInfosManager.getInstance().getLiveInfos();

        ArrayList<LiveInfo> liveInfos = new ArrayList<>();
        liveInfos.addAll(LiveInfosManager.getInstance().getLiveInfos());
        liveInfos.addAll(ExplosionGoodsManager.getInstance().toLiveInfos());

        BottomDialog.showBrandIndefiniteDialog(context, context.getString(R.string.choose_brand), liveInfos, new BottomDialog.IBrandLiseneter() {
            @Override
            public void onClick(LiveInfo liveInfo) {

                if (null != liveInfo) {
                    String liveId = ProductManager.getInstance().getLiveId();
                    if (liveInfo.hasBegun()) {
                        ProductManager.getInstance().setLiveId(liveInfo.getLiveid());
                        ;
                        Product tmp = ProductManager.getInstance().getForwardProduct(0);
                        //??????xuehao???????????????????????????????????????????????????9????????????????????????10???????????????????????????????????????
                        if (null == tmp) {
                            ProductManager.getInstance().setForwardIndex(0);
                            tmp = ProductManager.getInstance().getForwardProduct(0);
                        }
                        //???????????????????????????????????????????????????
                        if (null == tmp) {
                            ProductManager.getInstance().setLiveId(liveId);
                        }
                        setProduct(tmp);
                    } else {
                        MToaster.showShort((Activity) context, "?????????????????????", MToaster.IMG_ALERT);

                    }
                }

            }
        });


//        final String[] pinpais = LiveInfosManager.getInstance().pinpaiNames(); // LiveManager.getInstance().pinpaiNames();
//        if (pinpais.length == 0) return;
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("??????????????????");
//
//        builder.setItems(pinpais, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                LiveInfo liveInfo = LiveInfosManager.getInstance().getPinpai(which); //LiveManager.getInstance().getPinpai(which);
//                if (null != liveInfo) {
//                    String liveId = ProductManager.getInstance().getLiveId();
//                    if (liveInfo.hasBegun()) {
//                        ProductManager.getInstance().setLiveId(liveInfo.getLiveid());
//                        ;
//                        Product tmp = ProductManager.getInstance().getForwardProduct(0);
//                        //??????xuehao???????????????????????????????????????????????????9????????????????????????10???????????????????????????????????????
//                        if (null == tmp) {
//                            ProductManager.getInstance().setForwardIndex(0);
//                            tmp = ProductManager.getInstance().getForwardProduct(0);
//                        }
//                        //???????????????????????????????????????????????????
//                        if (null == tmp) {
//                            ProductManager.getInstance().setLiveId(liveId);
//                        }
//                        setProduct(tmp);
//                    } else {
//                        MToaster.showShort((Activity) context, "?????????????????????", MToaster.IMG_ALERT);
//
//                    }
//                }
//            }
//        });
//        builder.show();
    }

    private void checkImageDownloadFinish(int count) {
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
        //????????????????????????
        String state = Environment.getExternalStorageState();
        //??????????????????mounted???????????????
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

            Glide.with(context).load(url).asBitmap() //??????
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

                    checkImageDownloadFinish(count);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    // Do nothing.

                    checkImageDownloadFinish(count);
                }


            });

        }
    }

    private int saveCount = 0;
    public List<String> imagesPath = new ArrayList<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (checkBox.isChecked()) {
                //????????????????????????????????????????????? ?????????????????????0??????????????????????????????
                ShareView shareView = new ShareView(getContext());

                final ArrayList<String> imagePath = (ArrayList<String>) imagesPath;

                shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                    @Override
                    public void afterSaveBitmap(String localPath) {

                        ArrayList<String> imagePaths = new ArrayList<String>();
                        imagePaths.add(localPath);
                        if (product.getProductType() == 0)
                            SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, getFaredContent(product.weixinDesc()), imagePaths);
                        else
                            SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, product.weixinDesc(), imagePaths);
                        FastForwardPopWindow.this.dismiss();
                    }
                });
                if (product.getProductType() == 0)
                    shareView.setImageUrls(imagePath, getFaredContent(product.weixinDesc()), product.getPinpaiid());
                else shareView.setImageUrls(imagePath, product.weixinDesc(), product.getPinpaiid());

            } else if (more_img_checkbox.isChecked()) {
                //???????????????????????????
                ShareView shareView = new ShareView(getContext());
                shareView.setShareViewInterface(new ShareView.ShareViewInterface() {
                    @Override
                    public void afterSaveBitmap(String localPath) {
                        imagesPath.add(0, localPath);
                        if (product.getProductType() == 0)
                            SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, getFaredContent(product.weixinDesc()), imagesPath);
                        else
                            SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, product.weixinDesc(), imagesPath);
                    }
                });
                shareView.setImageUrls(null, getFaredContent(product.weixinDesc()), product.getPinpaiid());
            } else {
                if (product.getProductType() == 0)
                    SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, getFaredContent(product.weixinDesc()), imagesPath);
                else
                    SystemShareUtils.shareMultipleImage((Activity) FastForwardPopWindow.this.context, product.weixinDesc(), imagesPath);
                FastForwardPopWindow.this.dismiss();
            }
        }
    };


    private OnWindowItemListener onWindowItemListener;

    public void setOnWindowItemListener(OnWindowItemListener onWindowItemListener) {
        this.onWindowItemListener = onWindowItemListener;
    }

    public interface OnWindowItemListener {
        public void onEvent(int event);
    }

    /**
     * ????????????????????????????????????
     *
     * @param content
     * @return
     */
    public static String getFaredContent(String content) {
        String result = "";
        float addMoney = AppContext.get(ProductForwardSettingActivity.FORWARD_FARE_KEY, 0.0f);
        if (!TextUtils.isEmpty(content) && content.indexOf("??") > -1 && addMoney > 0) {
            // TODO: 2017/12/13
            String tempStr = content.substring(content.indexOf("??") + 1);
            String str2 = "";
            for (int i = 0; i < tempStr.length(); i++) {
                if (tempStr.charAt(i) >= 48 && tempStr.charAt(i) <= 57) {
                    str2 += tempStr.charAt(i);
                } else break;
            }
            if (!TextUtils.isEmpty(str2)) {
                float temp = Integer.valueOf(str2) + addMoney;
                result = content.replace(str2, temp + "");
            }
        } else result = content;
        return result;
    }

    /**
     * ???????????????????????????
     *
     * @param content
     * @return
     */
    public static int getDescAmount(String content) {
        int amount = 0;
        if (!TextUtils.isEmpty(content) && content.indexOf("??") > -1) {
            String tempStr = content.substring(content.indexOf("??") + 1);
            String str2 = "";
            for (int i = 0; i < tempStr.length(); i++) {
                if (tempStr.charAt(i) >= 48 && tempStr.charAt(i) <= 57) {
                    str2 += tempStr.charAt(i);
                } else break;
            }
            if (!TextUtils.isEmpty(str2)) {
                amount = Integer.valueOf(str2) * 100;
            }
        }
        return amount;
    }

}
