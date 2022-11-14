package com.aikucun.akapp.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by micker on 2017/7/1.
 */

public class SystemShareUtils {

    public static final int SHARE_RESULT_CODE = 1703;    // 分享请求代码


    //分享文字
    public static void shareText(Context context, String shareText) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");

        //设置分享列表的标题，并且每次都显示分享列表

        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    //分享单张图片
    public static void shareSingleImage(Context context, String path) {
        String imagePath = Environment.getExternalStorageDirectory() + File.separator + path;
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    /**
     * 分享单张图片及文本
     * @param context
     * @param Kdescription
     * @param path
     */
    public static void shareSingleImageAndText(Context context, String Kdescription, String path) {
        String imagePath = path;
        //由文件得到uri
        Uri imageUri = Uri.fromFile(new File(imagePath));
        Log.d("share", "uri:" + imageUri);  //输出：file:///storage/emulated/0/test.jpg
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("aikucun", Kdescription));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra("Kdescription", Kdescription); //微信分享页面，图片上边的描述
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Kdescription);
        shareIntent.setType("image/*, text/plain");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }


    //分享多张图片
    public static void shareMultipleImage(Activity context, String Kdescription, List<String>
            paths) {

        ArrayList<Uri> imageList = new ArrayList<Uri>();
        for (String picPath : paths)
        {
            File f = new File(picPath);
            if (f.exists())
            {
                imageList.add(Uri.fromFile(f));
            }
        }
        if (imageList.size() == 0)
        {
            MToaster.showShort((Activity)context,"图片不存在",MToaster.IMG_ALERT);
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("aikucun", Kdescription));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageList);
        shareIntent.putExtra("Kdescription", Kdescription); //微信分享页面，图片上边的描述
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, Kdescription);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("image/*, text/plain");

        context.startActivityForResult(Intent.createChooser(shareIntent, "分享到"), SHARE_RESULT_CODE);
    }

    static ArrayList<String> arrayList = new ArrayList<>();
    static int saveCount = 0;
    public static ArrayList<String> shareImgs(final Activity context,final String content, final List<String> imageUrls){
        saveCount = 0;
        arrayList = new ArrayList<>();
        //获取内部存储状态
        String state = Environment.getExternalStorageState();
        //如果状态不是mounted，无法读写
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        final String dir = Environment.getExternalStorageDirectory() + "/akucun/";
        File fileDir = new File(dir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        final int count = imageUrls.size();
        for (int i = 0; i < count; i++) {
            String url = imageUrls.get(i);
            final String saveName = "pic" + RSAUtils.md5String(url) + ".jpg";
            final File file = new File(dir, saveName);
            if (file.exists()) {
                file.delete();
            }

            arrayList.add(dir + saveName);

            Glide.with(context).load(url).asBitmap() //必须
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

                    checkImageDownloadFinish(context,count, content);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    // Do nothing.
                    checkImageDownloadFinish(context,count,content);
                }


            });

        }
        return arrayList;
    }


    private static void checkImageDownloadFinish(Activity context,int count, String content) {
        saveCount++;
        if (saveCount == count) {
            SystemShareUtils.shareMultipleImage(context, content, arrayList);
            //
//            Message message = new Message();
//            message.what = 1;
//            message.obj = product;
//            mHandler.sendMessage(message);
        }
    }
}
