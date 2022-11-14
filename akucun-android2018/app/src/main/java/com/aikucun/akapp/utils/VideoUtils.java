package com.aikucun.akapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import com.aliyun.common.logger.Logger;
import com.aliyun.common.utils.StorageUtils;
import com.aliyun.demo.crop.AliyunVideoCrop;
import com.aliyun.demo.recorder.AliyunVideoRecorder;
import com.aliyun.struct.common.VideoQuality;
import com.aliyun.struct.recorder.CameraType;
import com.aliyun.struct.recorder.FlashType;
import com.aliyun.struct.snap.AliyunSnapVideoParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ak123 on 2017/12/1.
 * 视频相关
 */

public class VideoUtils {
    public static String SD_DIR, QU_DIR, QU_NAME = "video";
    private static String[] eff_dirs;

    /**
     * 开始录制
     *
     * @param context
     * @param requestCode
     */
    public static void startRecordVideo(Activity context, int requestCode) {

        int gop = 5;
        VideoQuality videoQuality = VideoQuality.SD;
        AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                //设置录制分辨率，目前支持360p，480p，540p，720p
                .setResulutionMode(AliyunSnapVideoParam.RESOLUTION_360P)
                //设置视频比例，目前支持1:1,3:4,9:16
                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)
                .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO) //设置录制模式，目前支持按录，点录和混合模式
//                .setFilterList(eff_dirs) //设置滤镜地址列表,具体滤镜接口接收的是一个滤镜数组
                .setBeautyLevel(80) //设置美颜度
                .setBeautyStatus(true) //设置美颜开关
                .setCameraType(CameraType.BACK) //设置前后置摄像头
                .setFlashType(FlashType.AUTO) // 设置闪光灯模式
                .setNeedClip(true) //设置是否需要支持片段录制
                .setMaxDuration(1000 * 60) //设置最大录制时长 单位毫秒
                .setMinDuration(1000 * 3) //设置最小录制时长 单位毫秒
                .setVideQuality(videoQuality) //设置视频质量
                .setGop(gop) //设置关键帧间隔
                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                .build();
        AliyunVideoRecorder.startRecordForResult(context, requestCode, recordParam);
    }

    /**
     * 选择视频
     * @param context
     * @param requestCode
     */
    public static void chooseVideo(Activity context,int requestCode){
        int gop = 5;
        VideoQuality videoQuality = VideoQuality.SD;
        AliyunSnapVideoParam mCropParam = new AliyunSnapVideoParam.Builder()
                .setFrameRate(25) //设置帧率(20-30)
                .setGop(gop) //设置关键帧间隔
                .setCropMode(AliyunVideoCrop.SCALE_FILL) //设置裁剪模式，目前支持有黑边和无黑边两种
                .setVideQuality(videoQuality) //设置裁剪质量
                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                .setCropUseGPU(true) //设置裁剪方式，是否使用gpu进行裁剪，不设置则默认使用cpu来裁剪
                .setResulutionMode(AliyunSnapVideoParam.RESOLUTION_360P) //设置分辨率，目前支持360p，480p，540p，720p
                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)//设置裁剪比例 目前支持1:1,3:4,9:16
                .setNeedRecord(true)//设置是否需要开放录制入口
                .setMinVideoDuration(4000) //设置过滤的视频最小长度 单位毫秒
                .setMaxVideoDuration(29 * 1000) //设置过滤的视频最大长度 单位毫秒
                .setMinCropDuration(3000) //设置视频最小裁剪时间 单位毫秒
                .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//只显示视频
                .build();
        AliyunVideoCrop.startCropForResult(context,requestCode,mCropParam);
    }

    /**
     * 选择视频
     * @param context
     * @param requestCode
     * @param _eff_dirs   滤镜地址列表
     */
    public static void startRecordVideo(Activity context, int requestCode, String[] _eff_dirs) {

        int gop = 5;
        VideoQuality videoQuality = VideoQuality.SD;
        AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
                //设置录制分辨率，目前支持360p，480p，540p，720p
                .setResulutionMode(AliyunSnapVideoParam.RESOLUTION_360P)
                //设置视频比例，目前支持1:1,3:4,9:16
                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)
                .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO) //设置录制模式，目前支持按录，点录和混合模式
                .setFilterList(_eff_dirs) //设置滤镜地址列表,具体滤镜接口接收的是一个滤镜数组
                .setBeautyLevel(80) //设置美颜度
                .setBeautyStatus(true) //设置美颜开关
                .setCameraType(CameraType.FRONT) //设置前后置摄像头
                .setFlashType(FlashType.ON) // 设置闪光灯模式
                .setNeedClip(true) //设置是否需要支持片段录制
                .setMaxDuration(200000) //设置最大录制时长 单位毫秒
                .setMinDuration(5) //设置最小录制时长 单位毫秒
                .setVideQuality(videoQuality) //设置视频质量
                .setGop(gop) //设置关键帧间隔
                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
                .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
                .build();
        AliyunVideoRecorder.startRecordForResult(context, requestCode, recordParam);
    }

    /**
     * 获取文件大小
     *
     * @param filePath
     * @return
     */
    public static String getFileSize(String filePath) {
        File file = new File(filePath);
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("获取文件大小", "文件不存在!");
            return "0";

        }
        return FormetFileSize(size);
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取视频文件时长
     *
     * @param videoPath 视频地址
     * @return
     */
    public static String getVideoDuration(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
        mmr.release();
        return getTime(Integer.valueOf(duration) / 1000);
    }

    public static String getTime(int time) {
        int hour = time / 3600;
        int minute = time / 60 % 60;
        int second = time % 60;
        return hour * 60 + minute + ":" + second;
    }

    /**
     * 获取视频的封面地址
     *
     * @param videoPath 视频路径
     * @return
     */
    public static String getVideoCover(String videoPath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        //获取第一帧图像的bitmap对象
        Bitmap bitmap = mmr.getFrameAtTime();
        return saveBitmap(bitmap);
    }

    /**
     * 保存bitmap到本地
     *
     * @param bm
     * @return
     */
    private static String saveBitmap(Bitmap bm) {
        String coverPath = "";
        File file = getFileByTime("jpg");

        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.e("保存文件提示", "已经保存");
            coverPath = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return coverPath;
    }

    /**
     * 删除文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) file.delete();
        }
    }

    public interface IInitListenter {
        void onResult(String[] eff_dirs);
    }

    public static void initAssetPath(Context context, IInitListenter mIInitListenter) {
        String path = StorageUtils.getCacheDirectory(context).getAbsolutePath() + File.separator + QU_NAME + File.separator;
        eff_dirs = new String[]{
                null,
                path + "filter/chihuang",
                path + "filter/fentao",
                path + "filter/hailan",
                path + "filter/hongrun",
                path + "filter/huibai",
                path + "filter/jingdian",
                path + "filter/maicha",
                path + "filter/nonglie",
                path + "filter/rourou",
                path + "filter/shanyao",
                path + "filter/xianguo",
                path + "filter/xueli",
                path + "filter/yangguang",
                path + "filter/youya",
                path + "filter/zhaoyang"
        };
        copyAssets(context, mIInitListenter);
    }

    private static void copyAssets(final Context context, final IInitListenter mIInitListenter) {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                copyAll(context);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                // TODO: 2017/12/1
                mIInitListenter.onResult(eff_dirs);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private static void copyAll(Context cxt) {
        SD_DIR = StorageUtils.getCacheDirectory(cxt).getAbsolutePath() + File.separator;
        QU_DIR = SD_DIR + QU_NAME + File.separator;
        File dir = new File(QU_DIR);
        copySelf(cxt, QU_NAME);
        dir.mkdirs();
        unZip();

    }

    private static void unZip() {
        File[] files = new File(SD_DIR + QU_NAME).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name != null && name.endsWith(".zip")) {
                    return true;
                }
                return false;
            }
        });
        for (final File file : files) {
            int len = file.getAbsolutePath().length();
            if (!new File(file.getAbsolutePath().substring(0, len - 4)).exists()) {
                try {
                    UnZipFolder(file.getAbsolutePath(), SD_DIR + QU_NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {

                File file = new File(outPathString + File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }

    private static void copySelf(Context cxt, String root) {
        try {
            String[] files = cxt.getAssets().list(root);
            if (files.length > 0) {
                File subdir = new File(SD_DIR + root);
                if (!subdir.exists())
                    subdir.mkdirs();
                for (String fileName : files) {
                    if (new File(SD_DIR + root + File.separator + fileName).exists()) {
                        continue;
                    }
                    copySelf(cxt, root + "/" + fileName);
                }
            } else {
                Logger.getDefaultLogger().d("copy...." + SD_DIR + root);
                OutputStream myOutput = new FileOutputStream(SD_DIR + root);
                InputStream myInput = cxt.getAssets().open(root);
                byte[] buffer = new byte[1024 * 8];
                int length = myInput.read(buffer);
                while (length > 0) {
                    myOutput.write(buffer, 0, length);
                    length = myInput.read(buffer);
                }

                myOutput.flush();
                myInput.close();
                myOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static File getFileByTime(String suffix) {
        String file_path = createSysFileHolder();
        File file = new File(file_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        Time time = new Time();
        time.setToNow();
        return new File(file_path, time.format("%Y%m%d%H%M%S") + "." + suffix);
    }


    private static String createSysFileHolder() {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(file_path + file_path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }


    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
}
