package com.aikucun.akapp.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.ImagesUtils;
import com.aikucun.akapp.utils.RSAUtils;
import com.aikucun.akapp.utils.TDevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by micker on 2017/10/22.
 */

public class ShareView {

    private Context context;
    private ArrayList<String> mImagesPath;

    public ShareView(Context context) {
        this.context = context;
    }

    public void setImageUrls(ArrayList<String> imagesPath, String context, String url) {
//        if (imagesPath == null || imagesPath.size() == 0)
//            return;

        mImagesPath = imagesPath;
        if (mImagesPath == null || mImagesPath.size() == 0) {
            Bitmap screenshot = creatCodeBitmap(context);
            savePic(screenshot, url);
        } else {
            Bitmap screenshot = drawBitmap(context);
            savePic(screenshot, url);
        }

    }

    private Bitmap doGetBitmap(int position) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(mImagesPath.get(position));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fis);
    }

    private Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();

        Bitmap cropBitmap = origin;
        float ratio = newHeight * 1.0f / newWidth;
        float oRatio = height * 1.0f / width;
        if (oRatio < ratio) {
            int cropX = width - (int) (height * 1.0f / ratio);
            cropBitmap = Bitmap.createBitmap(origin, cropX / 2, 0, width - cropX, height, null, false);
            recycleBitmap(origin);
            width = width - cropX;
        } else if (oRatio > ratio) {
            int cropY = height - (int) (width * 1.0f * ratio / 2);
            cropBitmap = Bitmap.createBitmap(origin, 0, cropY / 2, width, height - cropY, null, false);
            recycleBitmap(origin);
            height = height - cropY;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(cropBitmap, 0, 0, width, height, matrix, false);
        recycleBitmap(origin);
        return newBM;
    }

    private Bitmap drawBitmap(String content) {
        Bitmap bitmap0 = null;
        Bitmap bitmap11 = null;
        Bitmap bitmap12 = null;
        Bitmap bitmap13 = null;
        for (int i = 0; i < mImagesPath.size(); i++) {
            if (i == 0) {
                bitmap0 = doGetBitmap(0);
            } else if (i == 1) {
                bitmap11 = doGetBitmap(1);
            } else if (i == 2) {
                bitmap12 = doGetBitmap(2);
            } else if (i == 3) {
                bitmap13 = doGetBitmap(3);
            }
        }
        int padding = 10;
        Bitmap bitmap3 = null;
        if (bitmap13 != null) {
            bitmap3 = scaleBitmap(bitmap13, bitmap0.getWidth(), bitmap0.getHeight());
            recycleBitmap(bitmap13);
        }

        Bitmap bitmap1 = scaleBitmap(bitmap11, bitmap0.getWidth() / 2 - padding / 2, bitmap0.getHeight() / 2);
        recycleBitmap(bitmap11);
        Bitmap bitmap2 = null;
        if (bitmap12 != null) {
            bitmap2 = scaleBitmap(bitmap12, bitmap1.getWidth(), bitmap1.getHeight());
            recycleBitmap(bitmap12);
        }

        Bitmap screenshot = Bitmap.createBitmap(bitmap0.getWidth() * 2 + 3 * padding, bitmap0.getHeight() + bitmap1.getHeight() + 3 * padding, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(screenshot);
        canvas.drawRGB(255, 255, 255);

        canvas.drawBitmap(bitmap0, padding * 1, padding, null);
        recycleBitmap(bitmap0);

        if (bitmap3 != null) {
            canvas.drawBitmap(bitmap3, padding * 2 + bitmap0.getWidth(), padding, null);
            recycleBitmap(bitmap3);
        }

        if (bitmap1 != null) {
            canvas.drawBitmap(bitmap1, padding * 2 + bitmap0.getWidth(), padding * 2 + bitmap0.getHeight(), null);
            recycleBitmap(bitmap1);
        }

        if (bitmap2 != null) {
            canvas.drawBitmap(bitmap2, (int) (padding * 2.5) + bitmap0.getWidth() + bitmap0.getWidth() / 2, padding * 2 + bitmap0.getHeight(), null);
            recycleBitmap(bitmap2);
        }

        int textSize = 20;
        TextPaint textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setLinearText(false);

        int tWidth = bitmap0.getWidth() - 2 * padding;
        int cHeight = bitmap1.getHeight();
        boolean out = false;
        int tempTH = 0;
        while (!out && (textSize < 60 || textSize > 10)) {
            textPaint.setTextSize(textSize);
            Rect bounds = new Rect();
            textPaint.getTextBounds(content, 0, content.length(), bounds);
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int tHeight = (fontMetrics.bottom - fontMetrics.top + (int) textPaint.getFontSpacing()) * bounds.width() / (tWidth - 2 * textSize);

            out = true;
            if (tHeight > cHeight && tHeight - cHeight > -3 * textSize) {
                textSize--;
                out = false;
            }

            if (tHeight < cHeight && tHeight - cHeight < -1 * textSize) {
                textSize++;
                out = false;
            }
            tempTH = tHeight;
        }

        textPaint.setAntiAlias(true);
        StaticLayout layout = new StaticLayout(content, textPaint, bitmap0.getWidth() - 2 * padding, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        canvas.save();
        canvas.translate(padding * 2, padding * 3 + bitmap0.getHeight());
        layout.draw(canvas);
        canvas.restore();

        int amount = FastForwardPopWindow.getDescAmount(content);
        if (amount > 0) {
            TextPaint priceTp = new TextPaint();
            priceTp.setColor(Color.RED);
            priceTp.setLinearText(false);
            priceTp.setTextSize(textSize + 10);
            priceTp.setAntiAlias(true);
            String priceContent = "活动价：¥" + amount / 100;
            layout = new StaticLayout(priceContent, priceTp, bitmap0.getWidth() - 2 * padding, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            canvas.save();
            canvas.translate(bitmap0.getWidth() / 3 - padding * 2, bitmap0.getHeight() + tempTH - padding * 4);
            layout.draw(canvas);
            canvas.restore();
        }

        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        canvas.drawRect(padding, padding * 2 + bitmap0.getHeight(), padding + bitmap0.getWidth(), padding * 2 + bitmap0.getHeight() + bitmap1.getHeight(), paint);
        return screenshot;
    }

    public Bitmap creatCodeBitmap(String contents) {
        //文本转图片View
        return ImagesUtils.viewSaveToImage(getTextToImageForwardLayout(contents));
//        TextPaint textPaint = new TextPaint();
//
//        // textPaint.setARGB(0x31, 0x31, 0x31, 0);
//        textPaint.setColor(Color.BLACK);
//        textPaint.setAntiAlias(true);//抗锯齿
//        textPaint.setTextSize(16);
//        StaticLayout layout = new StaticLayout(contents, textPaint, 450,
//                Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, true);
//        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth() + 20,
//                layout.getHeight() + 20, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.translate(10, 10);
//        canvas.drawColor(Color.WHITE);
//
//        layout.draw(canvas);
//        return bitmap;
    }

    private void savePic(Bitmap b, String url) {

        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        final String saveName = RSAUtils.md5String("" + System.currentTimeMillis()) + ".jpg";
        final File file = new File(dir, saveName);
        if (file.exists()) {
            file.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recycleBitmap(b);
        if (null != getShareViewInterface()) {
            getShareViewInterface().afterSaveBitmap(file.getAbsolutePath());
        }
    }

    private void recycleBitmap(Bitmap b) {
        if (null != b && !b.isRecycled()) {
            b = null;
        }
    }

    public interface ShareViewInterface {
        public void afterSaveBitmap(String localPath);
    }

    private ShareViewInterface shareViewInterface;

    public ShareViewInterface getShareViewInterface() {
        return shareViewInterface;
    }

    public void setShareViewInterface(ShareViewInterface shareViewInterface) {
        this.shareViewInterface = shareViewInterface;
    }


    /**
     * 获取分享的布局
     *
     * @param content
     * @return
     */
    private View getTextToImageForwardLayout(String content) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_forward_text_to_img_layout, null);
        TextView content_tv = view.findViewById(R.id.content_tv);
        content_tv.setText(content);
        LinearLayout linearLayout = view.findViewById(R.id.forward_layout);
        DisplayMetrics metric = TDevice.getDisplayMetrics();
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        ImagesUtils.layoutView(linearLayout, width, height);
        TextView activity_price_tv = view.findViewById(R.id.activity_price_tv);
        int amount = FastForwardPopWindow.getDescAmount(content);
        if (amount == 0) {
            activity_price_tv.setVisibility(View.GONE);
        } else {
            activity_price_tv.setVisibility(View.VISIBLE);
            activity_price_tv.setText("活动价：¥" + amount / 100);
        }
        return linearLayout;
    }

}
