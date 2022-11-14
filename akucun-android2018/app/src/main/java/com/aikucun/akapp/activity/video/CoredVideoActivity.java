package com.aikucun.akapp.activity.video;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

/**
 * Created by ak123 on 2017/11/30.
 */

public class CoredVideoActivity extends BaseActivity {

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCored();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.cored_video_layout;
    }

    private void startCored(){
//        AliyunSnapVideoParam recordParam = new AliyunSnapVideoParam.Builder()
//                //设置录制分辨率，目前支持360p，480p，540p，720p
//                .setResulutionMode(AliyunSnapVideoParam.RESOLUTION_360P)
//                //设置视频比例，目前支持1:1,3:4,9:16
//                .setRatioMode(AliyunSnapVideoParam.RATIO_MODE_9_16)
//                .setRecordMode(AliyunSnapVideoParam.RECORD_MODE_AUTO) //设置录制模式，目前支持按录，点录和混合模式
//                .setBeautyLevel(80) //设置美颜度
//                .setBeautyStatus(true) //设置美颜开关
//                .setCameraType(CameraType.FRONT) //设置前后置摄像头
//                .setFlashType(FlashType.ON) // 设置闪光灯模式
//                .setNeedClip(true) //设置是否需要支持片段录制
//                .setMaxDuration(10) //设置最大录制时长 单位毫秒
//                .setMinDuration(3) //设置最小录制时长 单位毫秒
//                .setVideoBitrate(2000) //设置视频码率，如果不设置则使用视频质量videoQulity参数计算出码率
//                .setSortMode(AliyunSnapVideoParam.SORT_MODE_VIDEO)//设置导入相册过滤选择视频
//                .build();
//        AliyunVideoRecorder.startRecordForResult(this,100,recordParam);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){

            if(resultCode == Activity.RESULT_OK && data!= null){
//                int type = data.getIntExtra(AliyunVideoRecorder.RESULT_TYPE,0);
//                if(type ==  AliyunVideoRecorder.RESULT_TYPE_CROP){
//                    String path = data.getStringExtra(CropKey.RESULT_KEY_CROP_PATH);
//                    Toast.makeText(this,"文件路径为 "+ path + " 时长为 " + data.getLongExtra(CropKey.RESULT_KEY_DURATION,0),Toast.LENGTH_SHORT).show();
//                }else if(type ==  AliyunVideoRecorder.RESULT_TYPE_RECORD){
//                    String path =data.getStringExtra(AliyunVideoRecorder.OUTPUT_PATH);
//                    long size1 = 0;
//                    try {
//                        size1 = getFileSize(new File(path));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Log.e("文件大小：",FormetFileSize(size1));
//                    Log.e("文件路径为：",path);
//                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this,"用户取消录制",Toast.LENGTH_SHORT).show();
            }

        }
    }
    public long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }
    /**
     * 转换文件大小
     * @param fileS
     * @return
     *
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
}
