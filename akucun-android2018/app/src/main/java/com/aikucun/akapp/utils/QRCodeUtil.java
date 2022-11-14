package com.aikucun.akapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.aikucun.akapp.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qyx.android.weight.utils.QyxWeightDensityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * @Author: SL
 * @Parameter: TODO
 * @Function: 二维码管理
 */
public class QRCodeUtil {

    private static ImageView sweepIV;
    private static int QR_WIDTH = 400, QR_HEIGHT = 400;

    /**
     * @return void
     * @throws
     * @方法功能说明: 生成二维码图片, 实际使用时要初始化sweepIV, 不然会报空指针错误
     * @参数: @param url 要转换的地址或字符串,可以是中文
     */

    // 要转换的地址或字符串,可以是中文
    public static void createQRImage(ImageView iv, String url) {
        sweepIV = iv;
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            // 显示到一个ImageView上面
            sweepIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成二维码Bitmap
     *
     * @param content   内容
     * @param widthPix  图片宽度
     * @param heightPix 图片高度
     * @param logoBm    二维码中心的Logo图标（可以为null）
     * @param filePath  用于存储二维码图片的文件路径
     * @return 生成二维码及保存文件是否成功
     */
    public static boolean createQRImage(String content, int widthPix,
                                        int heightPix, Bitmap logoBm, String filePath) {
        if (content == null || "".equals(content)) {
            return false;
        }
        // 配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0); // default is 4

        // 图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(content,
                    BarcodeFormat.QR_CODE, widthPix, heightPix, hints);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int[] pixels = new int[widthPix * heightPix];
        // 下面这里按照二维码的算法，逐个生成二维码的图片，
        // 两个for循环是图片横列扫描的结果
        for (int y = 0; y < heightPix; y++) {
            for (int x = 0; x < widthPix; x++) {
                if (bitMatrix.get(x, y)) {
                    pixels[y * widthPix + x] = 0xff000000;
                } else {
                    pixels[y * widthPix + x] = 0xffffffff;
                }
            }
        }

        // 生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

        if (logoBm != null) {
            bitmap = addLogo(bitmap, logoBm);
        }

        // 必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
        try {
            return bitmap != null
                    && bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }

        if (logo == null) {
            return src;
        }

        // 获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();

        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }

        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        // logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight,
                Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2,
                    (srcHeight - logoHeight) / 2, null);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;
    }


    public interface ICreateQRImg {
        void onFilePath(String filePath);
    }

    /**
     * 生成二维码图片
     *
     * @param content
     * @param context
     * @param iv
     */
    public static void createQRImg(final String content, final Activity context, final boolean showCenterIcon, final ImageView iv, final ICreateQRImg mICreateQRImg) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        context.getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        final int size = dm.widthPixels - QyxWeightDensityUtils.dp2px(context, 120);
        final String filePath = getFileRoot(context) + File.separator + "qr_"
                + System.currentTimeMillis() + ".jpg";
        // 二维码图片较大时，生成图片、保存文件的时间可能较长，因此放在新线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = createQRImage(content, size,
                        size, showCenterIcon ? BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.icon_app_logo_white) : null, filePath);

                if (success) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iv != null) {
                                iv.setImageBitmap(BitmapFactory
                                        .decodeFile(filePath));
                            }
                            mICreateQRImg.onFilePath(filePath);
                        }
                    });
                }
            }
        }).start();
    }

    // 文件存储根目录
    @SuppressLint("NewApi")
    private static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

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
