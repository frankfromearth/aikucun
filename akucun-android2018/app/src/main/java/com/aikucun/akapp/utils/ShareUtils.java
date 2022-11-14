package com.aikucun.akapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 微信分享工具类
 * Created by jarry on 2017/6/8.
 */
public class ShareUtils
{
    /**
     * 不实用微信的SDK分享文字到好友
     *
     * @param context
     * @param path
     */
    public static void shareTextToFriendNoSDK(Context context, String text)
    {
        if (!isInstallWeChart(context))
        {
            MToaster.showShort((Activity)context,"您没有安装微信",MToaster.IMG_ALERT);

            return;
        }
        if (StringUtils.isEmpty(text)) {
            MToaster.showShort((Activity)context,"分享内容不能为空",MToaster.IMG_ALERT);

            return;
        }

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools" + "" +
                ".ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }

    /**
     * 不实用微信的SDK分享图片到好友
     *
     * @param context
     * @param path
     */
    public static void sharePicToFriendNoSDK(Context context, String path)
    {
        if (!isInstallWeChart(context))
        {
            Toast.makeText(context, "您没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools" + "" +
                ".ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.setType("image/*");
        // intent.setFlags(0x3000001);
        File f = new File(path);
        if (f.exists())
        {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
        }
        else
        {
            MToaster.showShort((Activity)context,"文件不存在",MToaster.IMG_ALERT);

            return;
        }
        context.startActivity(intent);
    }


    /**
     * 分享9图到朋友圈
     *
     * @param context
     * @param Kdescription 9图上边输入框中的文案
     * @param paths        本地图片的路径
     */
    public static void share9PicsToWXCircle(Context context, String Kdescription, List<String>
            paths)
    {
        if (!isInstallWeChart(context))
        {
            Toast.makeText(context, "您没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        //
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(Manifest
                .permission.READ_EXTERNAL_STORAGE, "com.tencent.mm"));
        if (!permission)
        {
            SCLog.logv("微信没有读写权限");
//            Toast.makeText(context, "没有读写权限", Toast.LENGTH_SHORT).show();
//            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui" +
                "" + ".tools.ShareToTimeLineUI");
        intent.setComponent(componentName);
        intent.setType("image/*");

        ArrayList<Uri> imageList = new ArrayList<Uri>();
        for (String picPath : paths)
        {
            File f = new File(picPath);
            if (f.exists())
            {
                if (permission)
                {
                    imageList.add(Uri.fromFile(f));
                }
                else
                {
                    Uri uri = FileProvider.getUriForFile(context, "com.akucun.fileprovider", f);
                    imageList.add(uri);
                }
            }
        }
        if (imageList.size() == 0)
        {
            MToaster.showShort((Activity)context,"图片不存在",MToaster.IMG_ALERT);

            return;
        }
        intent.putExtra(Intent.EXTRA_TITLE, "转发朋友圈");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageList);
        intent.putExtra("Kdescription", Kdescription); //微信分享页面，图片上边的描述
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);    //这一步很重要。给目标应用一个临时的授权。
//        context.startActivity(intent);
        context.startActivity(Intent.createChooser(intent,"分享"));
    }

    /**
     * 不使用微信sdk 检查是否安装微信
     *
     * @param context
     * @return
     */
    public static boolean isInstallWeChart(Context context)
    {
        PackageInfo packageInfo = null;
        try
        {
            packageInfo = context.getPackageManager().getPackageInfo("com.tencent.mm", 0);
        }
        catch (Exception e)
        {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


}
