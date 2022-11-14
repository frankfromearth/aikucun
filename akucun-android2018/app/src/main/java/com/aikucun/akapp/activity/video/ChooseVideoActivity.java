package com.aikucun.akapp.activity.video;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.base.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by ak123 on 2017/11/17.
 * 选视频
 */

public class ChooseVideoActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolBar;
    @BindView(R.id.photo_gridview)
    GridView gv;
    @BindView(R.id.btn_right)
    Button btn_right;
    private VideoEntity mVideoEntity;

    private VideoAdapter videoAdapter;
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    @Override
    public void initView() {
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitleText = (TextView) findViewById(R.id.tv_title);
        mTitleText.setText(R.string.video);
        btn_right.setVisibility(View.INVISIBLE);
    }

    @Override
    public void initData() {
        new LoadVideoTask().execute();
        initListenter();
    }

    private void recordVideo(){
//        Intent intent = new Intent(ChooseVideoActivity.this, VideoRecorderActivity.class);
//        startActivityForResult(intent,102);
    }

    /**
     * 发送视频
     */
//    private void sendVideo() {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        getWindowManager().getDefaultDisplay()
//                .getMetrics(dm);
//        int h = 960;
//        if (dm.heightPixels < 960) {
//            h = dm.heightPixels;
//        }
//        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
//                .doH264Compress(true)
//                .smallVideoWidth(480)
////                .smallVideoWidth(dm.widthPixels)
//                .smallVideoHeight(360)
//                .recordTimeMax(6 * 1000)
//                .maxFrameRate(20)
//                .minFrameRate(8)
//                .captureThumbnailsTime(1)
//                .recordTimeMin((int) (1.5 * 1000))
//                .build();
//        Intent intent = new Intent(ChooseVideoActivity.this, MediaRecorderActivity.class);
//        intent.putExtra("media_recorder_config_key", config);
//        startActivityForResult(intent, 101);
//    }

    private void initListenter() {
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoEntity != null) {
                    compressVideo();
                }
            }
        });
    }

    private void compressVideo(){
//
//        LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
//        final LocalMediaConfig config = buidler
//                .setVideoPath(mVideoEntity.getVideoPath())
//                .captureThumbnailsTime(1)
//                .doH264Compress(new AutoVBRMode(30))
//                .setFramerate(15)
//                .setScale(1.0f)
//                .build();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                       showProgress("视频压缩中...");
//                    }
//                });
//               final OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        cancelProgress();
//                        try {
//                            Log.e("压缩结果：",onlyCompressOverBean.isSucceed()+"");
//                            long size1 = getFileSize(new File(mVideoEntity.getVideoPath()));
//                            Log.e("源文件大小：",FormetFileSize(size1));
//                            long size2 = getFileSize(new File(onlyCompressOverBean.getVideoPath()));
//                            Log.e("压缩文件大小：",FormetFileSize(size2));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                Intent intent = new Intent();
//                // 由于intent不能传递大于40K的bitmap所以这里将它设置为null
//                mVideoEntity.setBitmap(null);
//                mVideoEntity.setVideoPath(onlyCompressOverBean.getVideoPath());
//                intent.putExtra("video_entity", mVideoEntity);
//                setResult(RESULT_OK, intent);
//                finish();
//
//            }
//        }).start();
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
    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_choose_video;
    }


    private String getTime(int time) {
        int hour = time / 3600;
        int minute = time / 60 % 60;
        int second = time % 60;
        return hour * 60 + minute + ":" + second;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 100) {
                // TODO: 2017/11/20 获取拍摄视频结果
                VideoEntity videoEntity = data.getParcelableExtra("video_entity");
                Intent intent = new Intent();
                intent.putExtra("video_entity", videoEntity);
                setResult(RESULT_OK, intent);
                finish();
            } else if (requestCode == 101) {
//                //视频地址
//                String path = data.getStringExtra(MediaRecorderActivity.VIDEO_URI);
//                //视频封面地址
//                int duration = data.getIntExtra(MediaRecorderActivity.VIDEO_DURATION, 0);
//                VideoEntity videoEntity = new VideoEntity();
//                videoEntity.setVideoDuration(getTime(duration / 1000));
//                videoEntity.setVideoPath(path);
//                Intent intent = new Intent();
//                intent.putExtra("video_entity", videoEntity);
//                setResult(RESULT_OK, intent);
//                finish();
            }else if (requestCode == 102){
//                //视频地址
//                String path = data.getStringExtra(VideoRecorderActivity.RECORDER_VIDEO_PATH);
//                //视频时长
//                int duration = data.getIntExtra(VideoRecorderActivity.RECORDER_VIDEO_DURATION, 0);
//                VideoEntity videoEntity = new VideoEntity();
//                videoEntity.setVideoDuration(getTime(duration / 1000));
//                videoEntity.setVideoPath(path);
//                Intent intent = new Intent();
//                intent.putExtra("video_entity", videoEntity);
//                setResult(RESULT_OK, intent);
//                finish();
            }
        }
    }

    class LoadVideoTask extends AsyncTask<Void, Void, Void> {

        ArrayList<VideoEntity> listVideo;
        @Override
        protected Void doInBackground(Void... arg0) {
            listVideo = new ArrayList<>();
            // 扫描外部设备中的视频
            String str[] = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
            Cursor cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null,
                    null, null);

            while (cursor.moveToNext()) {
                VideoEntity videoEntity = new VideoEntity();
                videoEntity.setId(cursor.getString(0));
                videoEntity.setVideoName(cursor.getString(1));
                videoEntity.setVideoPath(cursor.getString(2));
                videoEntity.setVideoSize(cursor.getString(3));
                //时长
                int time = Integer.valueOf(cursor.getString(4));
                String duration = getTime(time / 1000);
                videoEntity.setVideoDuration(duration);
                if (!TextUtils.isEmpty(videoEntity.getVideoPath())) {
                    mmr.setDataSource(videoEntity.getVideoPath());
                    //获取第一帧图像的bitmap对象
                    Bitmap bitmap = mmr.getFrameAtTime();
                    videoEntity.setBitmap(bitmap);
                }

                listVideo.add(videoEntity);
            }
            listVideo.add(0, new VideoEntity());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            videoAdapter = new VideoAdapter(ChooseVideoActivity.this, listVideo, new VideoAdapter.ISelectedListenter() {
                @Override
                public void onBack(VideoEntity videoEntity) {
                    if (videoEntity != null && videoEntity.isSelected()) {
                        mVideoEntity = videoEntity;
                        btn_right.setVisibility(View.VISIBLE);
                    } else {
                        mVideoEntity = null;
                        btn_right.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onRecord() {
                    // TODO: 2017/11/17 录制视频
                    recordVideo();
//                sendVideo();
                }
            });
            gv.setAdapter(videoAdapter);
        }
    }
}
