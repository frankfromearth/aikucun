package com.aikucun.akapp.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.album.PhotoActivity;
import com.aikucun.akapp.activity.discover.CameraActivity;
import com.aikucun.akapp.activity.discover.ImageSize;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.entity.CartProduct;
import com.aikucun.akapp.api.manager.AfterSaleApiManager;
import com.aikucun.akapp.base.BaseActivity;
import com.aikucun.akapp.utils.ActivityUtils;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.widget.BottomDialog;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qyx.android.weight.choosemorepic.PhotoItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import rx.functions.Action1;

import static com.aikucun.akapp.R.id.ll_question_desc;

/**
 * 退款／售后 商品列表界面
 * Created by jarry on 17/6/30.
 */
public class AfterSaleServiceActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.productImage)
    ImageView productImage;
    @BindView(R.id.contentTv)
    TextView contentTv;
    @BindView(R.id.skuTv)
    TextView skuTv;
    @BindView(R.id.amountTv)
    TextView amountTv;


    @BindView(R.id.tv_question)
    TextView tvQuestion;
    @BindView(R.id.problem0)
    TextView problem0;
    @BindView(R.id.problem1)
    TextView problem1;
    @BindView(R.id.problem2)
    TextView problem2;
    @BindView(R.id.problem3)
    TextView problem3;
    @BindView(R.id.problem4)
    TextView problem4;
    @BindView(R.id.problem5)
    TextView problem5;
    @BindView(R.id.tv_service)
    TextView tvService;
    @BindView(R.id.problem20)
    TextView problem20;
    @BindView(R.id.problem21)
    TextView problem21;
    @BindView(R.id.problem22)
    TextView problem22;
    @BindView(R.id.ll_service)
    LinearLayout ll_service;
    @BindView(R.id.edite_view)
    EditText editeView;

    @BindView(R.id.imageView_pinzhen)
    ImageView imageViewPinzhen;
    @BindView(R.id.tv_pinzhen)
    TextView tvPinzhen;
    @BindView(R.id.iv_takepicture)
    ImageView ivTakepicture;
    @BindView(ll_question_desc)
    LinearLayout llQuestionDesc;
    @BindView(R.id.save_button)
    Button saveButton;


    private int problem = -1;
    private int service = -1;

    private Bitmap mBitmap;

    private String cartProductId;
    private CartProduct cartProduct;

    private static final int PAIS = 101;
    private static final int PIC = 102;
    private ArrayList<ImageSize> imgSizes = new ArrayList<>();
    private ArrayList<PhotoItem> select_gl_arr = new ArrayList<>();
    private String imagePath;

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.apply_for_after_service);

    }

    @Override
    public void initData() {
        {
            cartProductId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID);

            String productId = getIntent().getStringExtra(AppConfig.BUNDLE_KEY_PRODUCT_ID);
            if (productId == null || productId.length() == 0) {
                return;
            }

            cartProduct = (CartProduct) (getIntent().getSerializableExtra(AppConfig.BUNDLE_KEY_PRODUCT));
        }

        {
            if (cartProduct != null) {

                Glide.with(this).load(cartProduct.getImageUrl()).diskCacheStrategy(DiskCacheStrategy
                        .ALL).placeholder(R.color.color_bg_image).into(productImage);

                contentTv.setText(cartProduct.getDesc());
                if (cartProduct.getSku() != null) {
                    skuTv.setText(cartProduct.getSku().getChima() + "  x1" + cartProduct.getDanwei());
                } else {
                    skuTv.setText(cartProduct.getChima() + "  x1" + cartProduct.getDanwei());
                }
                amountTv.setText("结算价：" + StringUtils.getPriceString(cartProduct.getJiesuanjia()));
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aftersale_service;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_bottom_out);
    }

    private void doSendRequest() {

        String text = editeView.getText().toString();

        if (problem < 0) {
            MToaster.showLong(this, "请选择问题类型！", MToaster.IMG_ALERT);
            return;
        }

        if (service < 0) {
            MToaster.showLong(this, "请选择您期望的服务！", MToaster.IMG_ALERT);
            return;
        }

        if (StringUtils.isEmpty(text)) {
            MToaster.showLong(this, "请填写问题描述！", MToaster.IMG_ALERT);
            return;
        }
        showProgress("");
        AfterSaleApiManager.afterSaleApply(this,
                cartProductId,
                problem,
                problem == 0 ? 2 : 4,
                text,
                bitmapToBase64(mBitmap), new JsonDataCallback() {
                    @Override
                    public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                        super.onApiSuccess(jsonObject, call, jsonResponse);

                        cancelProgress();
//                saveButton.setEnabled(true);
                        MToaster.showShort(AfterSaleServiceActivity.this, "售后申请已提交 ！", MToaster.IMG_INFO);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.putExtra("PROBLEM_TYPE", problem);
                                intent.putExtra(AppConfig.BUNDLE_KEY_CARTPROD_ID, cartProductId);
                                setResult(RESULT_OK, intent);
                                onBackPressed();
                            }
                        }, 600);


                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        cancelProgress();
//                saveButton.setEnabled(true);
                    }
                });
    }

    @Override
    @OnClick({R.id.iv_takepicture, R.id.problem0,
            R.id.problem1, R.id.problem2,
            R.id.problem3, R.id.problem4,
            R.id.problem5, R.id.problem20,
            R.id.problem21, R.id.save_button, R.id.imageView_pinzhen})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save_button: {

                doSendRequest();
            }
            break;
            case R.id.imageView_pinzhen:
                if (!TextUtils.isEmpty(imagePath)) {
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize
                            (v.getMeasuredWidth(), v.getMeasuredHeight());
                    List<String> photoUrls = new ArrayList<String>();
                    photoUrls.add(imagePath);
                    ImagePagerActivity.startImagePagerActivity(this, photoUrls,
                            0, imageSize);
                }

                break;
            case R.id.iv_takepicture: {
                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission
                        .WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            doPickPhotoAction();
                        } else {
                            showApplicationSettingDetails(getString(R.string.camera));
                        }
                    }
                });
            }
            break;
            case R.id.problem0:
            case R.id.problem1:
            case R.id.problem2:
            case R.id.problem3:
            case R.id.problem4:
            case R.id.problem5: {
                String tag = (String) v.getTag();
                int iTag = Integer.valueOf(tag);

                ll_service.setVisibility(View.VISIBLE);
                tvService.setVisibility(View.VISIBLE);
                tvQuestion.setVisibility(View.GONE);

                problem20.setText((1000 == iTag) ? "漏发补发" : "退货并补发");
                problem21.setText((1000 == iTag) ? "漏发退款" : "退货退款");


                if (problem > -1) {
                    TextView oldSelectTextView =  getRootView().findViewWithTag("" + (1000 + problem));

                    oldSelectTextView.setBackground(getResources().getDrawable(R.drawable.sku_item_bg_normal));
                    oldSelectTextView.setTextColor(getResources().getColor(R.color.gray));
                }

                {
                    TextView newSelectTextView = (TextView) v;
                    newSelectTextView.setBackground(getResources().getDrawable(R.drawable.btn_bg_buy_pressed));
                    newSelectTextView.setTextColor(getResources().getColor(R.color.white));

                }

                if (service > -1) {
                    TextView oldSelectTextView =  getRootView().findViewWithTag("" + (2000 + service));
                    oldSelectTextView.setBackground(getResources().getDrawable(R.drawable.sku_item_bg_normal));
                    oldSelectTextView.setTextColor(getResources().getColor(R.color.gray));
                    service = -1;
                }

                problem = iTag - 1000;


            }
            break;

            case R.id.problem20:
            case R.id.problem21:
            case R.id.problem22: {

                tvService.setVisibility(View.GONE);
                llQuestionDesc.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);

                String tag = (String) v.getTag();
                int iTag = Integer.valueOf(tag);

                if (service > -1) {
                    TextView oldSelectTextView =  getRootView().findViewWithTag("" + (2000 + service));

                    oldSelectTextView.setBackground(getResources().getDrawable(R.drawable.sku_item_bg_normal));
                    oldSelectTextView.setTextColor(getResources().getColor(R.color.gray));
                }

                {
                    TextView newSelectTextView = (TextView) v;
                    newSelectTextView.setBackground(getResources().getDrawable(R.drawable.btn_bg_buy_pressed));
                    newSelectTextView.setTextColor(getResources().getColor(R.color.white));

                }

                service = iTag - 2000;

            }
            break;

            default:
                break;
        }
    }


    /*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 3023;

    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    /*拍照的照片存储位置*/
//    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
//    private File mCurrentPhotoFile;//照相机拍照得到的图片

    private void doPickPhotoAction() {


        BottomDialog.showBottomDialog(AfterSaleServiceActivity.this, R.string.take_phone, R.string.choose_send_image, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                takePhone();
            }
        }, new BottomDialog.IBottomListenter() {
            @Override
            public void onClick() {
                chooseImgs();
            }
        });


//        new AlertDialog.Builder(AfterSaleServiceActivity.this).setItems(
//                new String[]{"拍摄照片", "从相册选择"},
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0: {
//                                String status = Environment.getExternalStorageState();
//                                if (status.equals(Environment.MEDIA_MOUNTED)) {//判断是否有SD卡
//                                    doTakePhoto();// 用户点击了从照相机获取
//                                } else {
//                                    MToaster.showShort(AfterSaleServiceActivity.this, "没有SD卡", MToaster.IMG_ALERT);
//                                }
//                            }
//                            break;
//                            case 1:
//                                doPickPhotoFromGallery();// 从相册中去获取
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                }).show();
    }

    /**
     * 拍照
     */
    private void takePhone() {
        imgSizes.clear();
        imgSizes.add(new ImageSize(960, 960, ""));
        Bundle bundle = new Bundle();
        bundle.putSerializable("size", imgSizes);
        ActivityUtils.startActivity(AfterSaleServiceActivity.this,
                CameraActivity.class, bundle, PAIS);
    }

    /**
     * 选择图片
     */
    private void chooseImgs() {
        Bundle bundle = new Bundle();
        bundle.putInt("selected_count", 0);
        bundle.putInt("max_select_count", 1);
        ActivityUtils.startActivity(AfterSaleServiceActivity.this,
                PhotoActivity.class, bundle, PIC);
    }

//    /**
//     * 拍照获取图片
//     */
//    protected void doTakePhoto() {
//        try {
//            // Launch camera to take photo for selected contact
//            PHOTO_DIR.mkdirs();// 创建照片的存储目录
//            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
//            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
//            startActivityForResult(intent, CAMERA_WITH_DATA);
//        } catch (ActivityNotFoundException e) {
//            MToaster.showShort(AfterSaleServiceActivity.this, "没有相机", MToaster.IMG_ALERT);
//
//        }
//    }
//
//    public static Intent getTakePickIntent(File f) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//        return intent;
//    }
//
//    /**
//     * 用当前时间给取得的图片命名
//     */
//    private String getPhotoFileName() {
//        Date date = new Date(System.currentTimeMillis());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
//        return dateFormat.format(date) + ".jpg";
//    }
//
//    // 请求Gallery程序
//    protected void doPickPhotoFromGallery() {
//        try {
//            // Launch picker to choose photo for selected contact
//            final Intent intent = getPhotoPickIntent();
//            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//        } catch (ActivityNotFoundException e) {
//            MToaster.showShort(AfterSaleServiceActivity.this, "没有相册", MToaster.IMG_ALERT);
//
//        }
//    }
//
//    // 封装请求Gallery的intent
//    public static Intent getPhotoPickIntent() {
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        return intent;
//    }

    // 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PIC:

                try {
                    if (data == null) {
                        return;
                    }
                    select_gl_arr.clear();
                    select_gl_arr = (ArrayList<PhotoItem>) data
                            .getSerializableExtra("gl_arr");

                    if (select_gl_arr.size() > 0) {
                        for (int i = 0; i < select_gl_arr.size(); i++) {
                            imagePath = select_gl_arr.get(i).getPath();
                            mBitmap = readSample(imagePath);
                            imageViewPinzhen.setImageBitmap(mBitmap);
                            tvPinzhen.setVisibility(View.GONE);
                            imageViewPinzhen.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case PAIS:

                if (data == null) {
                    return;
                }
                try {
                    imagePath = data.getStringExtra("big_pic_filename");
                    mBitmap = readSample(imagePath);
                    imageViewPinzhen.setImageBitmap(mBitmap);
                    tvPinzhen.setVisibility(View.GONE);
                    imageViewPinzhen.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
//            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
//                mBitmap = data.getParcelableExtra("data");
//                if (mBitmap == null) {
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//                    c.moveToFirst();
//                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                    String imagePath = c.getString(columnIndex);
//                    mBitmap = readSample(imagePath);
//                }
//                imageViewPinzhen.setImageBitmap(mBitmap);
//                tvPinzhen.setVisibility(View.GONE);
//                imageViewPinzhen.setVisibility(View.VISIBLE);
//                break;
//            }
//            case CAMERA_WITH_DATA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
//
//                mBitmap = readSample(mCurrentPhotoFile.getAbsolutePath());
//                imageViewPinzhen.setImageBitmap(mBitmap);
//                tvPinzhen.setVisibility(View.GONE);
//                imageViewPinzhen.setVisibility(View.VISIBLE);
//                break;
//            }
        }
    }


    private Bitmap readSample(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        //整个图像，下采样
        bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;

    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
