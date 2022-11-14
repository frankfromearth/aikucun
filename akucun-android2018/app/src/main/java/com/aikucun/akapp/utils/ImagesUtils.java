package com.aikucun.akapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Base64;
import android.view.View;

import com.qyx.android.weight.utils.QyxWeightDensityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by ak123 on 2017/12/12.
 */

public class ImagesUtils {


    public static int[] getImageWidthAndHeightToDynamic(Context mContext, int pic_width, int pic_height) {
        int w = pic_width;
        int h = pic_height;
        int[] w_h = new int[2];
        int screeWidth = QyxWeightDensityUtils.getScreenWidth((Activity) mContext) / 2;
        int screeHeight = QyxWeightDensityUtils.getScreenHeight((Activity) mContext) / 3;
        if (pic_width < screeWidth && pic_height < screeHeight){
            w = screeWidth;
            h = screeHeight;
        }else{
            if (pic_width == pic_height) {
                w = screeWidth;
                h = screeWidth;
            } else {
                float rs;
                if (pic_width > pic_height && pic_width >= screeWidth) {
                    w = screeWidth;
                    rs = (float) pic_width / (float) screeWidth;
                    h = (int) ((float) pic_height / rs);
                } else if (pic_height > pic_width && pic_height > screeHeight) {
                    h = screeHeight;
                    rs = (float) pic_height / (float) screeHeight;
                    w = (int) ((float) pic_width / rs);
                }
            }
        }


        w_h[0] = w;
        w_h[1] = h;
        return w_h;
    }

    /**
     * 图片转base64
     *
     * @param imagePath
     * @return
     */
    public static String fileToBase64(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        return bitmapToBase64(bitmap);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
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

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public interface ICompressListener {
        void onResult(String path);
    }

    /**
     * 压缩图片
     *
     * @param context
     * @param imagePath
     * @param mICompressListener
     */
    public static void compressImg(Context context, String imagePath, final ICompressListener mICompressListener) {
        Luban.with(context)
                .load(imagePath)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(FileUtils.getImgCompressPath())                        // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        if (file != null) {
                            mICompressListener.onResult(file.getAbsolutePath());
                        } else mICompressListener.onResult("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mICompressListener.onResult("");
                    }
                }).launch();    //启动压缩

    }


    //如果View不可见时，必须调用该方法
    // 然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。
    // 接着就可以创建位图并在上面绘制了：
    public static View layoutView(View v, int width, int height) {
        // 指定整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        return v;
    }

    /**
     * View to image
     *
     * @param view
     */
    public static Bitmap viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);

        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        return cachebmp;
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

//        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }
}
