package com.jxccp.ui.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jxccp.im.chat.common.message.JXMessage;
import com.jxccp.im.chat.common.message.JXMessageAttribute;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.R;
import com.jxccp.ui.utils.JXCommonUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JXRecorderVideoActivity extends Activity
        implements OnClickListener, SurfaceHolder.Callback, OnErrorListener, OnInfoListener, OnTouchListener {
    private static final String TAG = "RecorderVideoActivity";

    private final static String CLASS_LABEL = "RecordActivity";

    private PowerManager.WakeLock mWakeLock;

    private TextView pressBtn;//点击录制按钮
    
    private  TextView moveUpHintText;//上滑取消提示
    
    private TextView cancelHintText;//取消提示
    
    private ProgressBar recorderTimePb;

    private MediaRecorder mediaRecorder;// 录制视频的类

    private VideoView mVideoView;// 显示视频的控件

    String localPath = "";// 录制的视频路径

    private Camera mCamera;

    // 预览的宽高
    private int previewWidth = 480;

    private int previewHeight = 480;

    private int frontCamera = 0;// 0是后置摄像头，1是前置摄像头

    Parameters cameraParameters = null;

    private SurfaceHolder mSurfaceHolder;

    int defaultVideoFrameRate = -1;
    
    boolean recording = false;
    
    ImageView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 选择支持半透明模式，在有surfaceview的activity中使用
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.jx_activity_recorder);
        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();
        initViews();
    }

    private void initViews() {
        mVideoView = (VideoView)findViewById(R.id.mVideoView);
        pressBtn = (TextView)findViewById(R.id.tv_press);
        pressBtn.setVisibility(View.INVISIBLE);
        moveUpHintText = (TextView)findViewById(R.id.tv_up_hint);
        cancelHintText = (TextView)findViewById(R.id.tv_cancel_hint);
        recorderTimePb = (ProgressBar)findViewById(R.id.pb_recorder_time);
        recorderTimePb.setMax(maxTime);
        backView = (ImageView)findViewById(R.id.iv_left);
        backView.setOnClickListener(this);
        pressBtn.setOnClickListener(this);
        pressBtn.setOnTouchListener(this);
        mSurfaceHolder = mVideoView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void back(View view) {
        releaseRecorder();
        releaseCamera();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            // 获取唤醒锁,保持屏幕常亮
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            mWakeLock.acquire();
        }
        // if (!initCamera()) {
        // showFailDialog();
        // }
    }

    @SuppressLint("NewApi")
    private boolean initCamera() {
        try {
            if (frontCamera == 0) {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            }
            Camera.Parameters camParams = mCamera.getParameters();
            mCamera.lock();
            mSurfaceHolder = mVideoView.getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mCamera.setDisplayOrientation(90);
        } catch (RuntimeException ex) {
            JXLog.e("video,init Camera fail ", ex);
            return false;
        }
        return true;
    }

    private void handleSurfaceChanged() {
        if (mCamera == null) {
            finish();
            return;
        }
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters()
                .getSupportedPreviewFrameRates();
        if (supportedPreviewFrameRates != null && supportedPreviewFrameRates.size() > 0) {
            Collections.sort(supportedPreviewFrameRates);
            for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);

                if (supportRate == 15) {
                    hasSupportRate = true;
                }

            }
            if (hasSupportRate) {
                defaultVideoFrameRate = 15;
            } else {
                defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
            }

        }
        // 获取摄像头的所有支持的分辨率
        List<Camera.Size> resolutionList = getResolutionList(mCamera);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new ResolutionComparator());
            Camera.Size previewSize = null;
            boolean hasSize = false;
            // 如果摄像头支持640*480，那么强制设为640*480
            for (int i = 0; i < resolutionList.size(); i++) {
                Size size = resolutionList.get(i);
                if (size != null && size.width == 640 && size.height == 480) {
                    previewSize = size;
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                    hasSize = true;
                    break;
                }
            }
            // 如果不支持设为中间的那个
            if (!hasSize) {
                int mediumResolution = resolutionList.size() / 2;
                if (mediumResolution >= resolutionList.size())
                    mediumResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(mediumResolution);
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_left) {
            back(null);
        }
    }
    
  //相机参数的初始化设置
    private void reInitCamera() {
        cameraParameters = mCamera.getParameters();
        cameraParameters.setPictureFormat(PixelFormat.JPEG);
        // parameters.setPictureSize(surfaceView.getWidth(),
        // surfaceView.getHeight()); // 部分定制手机，无法正常识别该方法。
//        cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
        setDispaly(cameraParameters, mCamera);
        mCamera.setParameters(cameraParameters);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上
    }
    
    // 控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }

    // 实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try {
            downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] {
                    int.class
            });
            if (downPolymorphic != null) {
                downPolymorphic.invoke(camera, new Object[] {
                        i
                });
            }
        } catch (Exception e) {
            JXLog.e("Came_e", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
        mSurfaceHolder = holder;
        mCamera.autoFocus(new AutoFocusCallback() {
            
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){
                        reInitCamera();//实现相机的参数初始化
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                   }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        pressBtn.setVisibility(View.VISIBLE);
        if (mCamera == null) {
            if (!initCamera()) {
                showFailDialog();
                return;
            }

        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            handleSurfaceChanged();
        } catch (Exception e1) {
            JXLog.e("video,start preview fail", e1);
            showFailDialog();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        JXLog.d("video , surfaceDestroyed");
    }

    public boolean startRecording() {
        initRecorder();
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseRecorder();
            return false;
        }
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.start();
        recording = true;
//        pbHandler.sendEmptyMessage(START);
        return true;
    }

    @SuppressLint("NewApi")
    private boolean initRecorder() {
        if (!JXCommonUtils.isExitsSdcard()) {
            showNoSDCardDialog();
            return false;
        }

        if (mCamera == null) {
            if (!initCamera()) {
                showFailDialog();
                return false;
            }
        }
        mVideoView.setVisibility(View.VISIBLE);
        // TODO init button
        mCamera.stopPreview();
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }else {
            mediaRecorder.reset();
        }
        try {
            mCamera.unlock();
        } catch (Exception e) {
            Log.e("JXRecorderVideoActivity", "unlock camera failed", e);
            showFailDialog();
            return false;
        }
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        // 设置录制视频源为Camera（相机）
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (frontCamera == 1) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }
        // 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置录制的视频编码h263 h264
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        System.out.println("width = "+previewWidth+" , height="+previewHeight);
        mediaRecorder.setVideoSize(previewWidth, previewHeight);
        // 设置视频的比特率
        mediaRecorder.setVideoEncodingBitRate(384 * 1024);
        // // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        if (defaultVideoFrameRate != -1) {
            mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
        }
        // 设置视频文件输出的路径
        File dir = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mcs_agent/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        localPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/mcs_agent/"+ System.currentTimeMillis() + ".mp4";
        mediaRecorder.setOutputFile(localPath);
        mediaRecorder.setMaxDuration(maxTime);
        mediaRecorder.setMaxFileSize(JXImManager.Config.getInstance().getFileMsgMaxSize(JXMessage.Type.VIDEO)*1024);
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        return true;
    }

    public void stopRecording(boolean releaseCamera) {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            try {
                if (releaseCamera) {
                    mediaRecorder.stop();
                }else {
                    mediaRecorder.reset();
                }
            } catch (IllegalStateException e) {
                JXLog.e("video , stopRecording error", e);
            }
        }

        if (mCamera != null && releaseCamera) {
            mCamera.stopPreview();
            releaseRecorder();
            releaseCamera();
        }
        recording  = false;
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    protected void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    MediaScannerConnection msc = null;

    ProgressDialog progressDialog = null;

    public void sendVideo(View view) {
        if (TextUtils.isEmpty(localPath)) {
            JXLog.e("Recorderrecorder fail please try again!");
            return;
        }
        if (msc == null)
            msc = new MediaScannerConnection(this, new MediaScannerConnectionClient() {

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    JXLog.d("scanner completed");
                    msc.disconnect();
                    progressDialog.dismiss();
                    setResult(RESULT_OK, getIntent().putExtra("uri", uri));
                    finish();
                }

                @Override
                public void onMediaScannerConnected() {
                    msc.scanFile(localPath, "video/*");
                }
            });

        showProgress(true);
        msc.connect();

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        JXLog.d("video , onInfo");
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            JXLog.d("video ,reach max limit , limit is "+what);
            reachLimit = true;
            stopRecording(true);
            if (localPath == null) {
                return;
            }
            sendVideo(null);
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        JXLog.e("video , recording onError:");
        stopRecording(true);
        Toast.makeText(this, "Recording error has occurred. Stopping the recording",
                Toast.LENGTH_SHORT).show();

    }

    public void saveBitmapFile(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "a.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRecorder();
        releaseCamera();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }

    }

    @Override
    public void onBackPressed() {
        back(null);
    }

    private void showFailDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.jx_prompt)
                .setMessage(R.string.jx_open_the_equipment_failure)
                .setPositiveButton(R.string.jx_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setCancelable(false).show();

    }

    private void showNoSDCardDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.jx_prompt).setMessage("No sd card!")
                .setPositiveButton(R.string.jx_confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                }).setCancelable(false).show();
    }

    public static List<Camera.Size> getResolutionList(Camera camera) {
        Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes;
    }

    public static class ResolutionComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            if (lhs.height != rhs.height)
                return lhs.height - rhs.height;
            else
                return lhs.width - rhs.width;
        }
    }
    
    public static int MOVE_UP_CANCEL = 0;
    public static int VIDEO_TOO_SHORT = 1;
    public static int VIDEO_SUCCESS= 2;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pbHandler.sendEmptyMessageDelayed(START, 500);
                moveUpHintText.setVisibility(View.VISIBLE);
                // 重置其他
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    moveUpHintText.setVisibility(View.GONE);
                    cancelHintText.setVisibility(View.VISIBLE);
                } else {
                    moveUpHintText.setVisibility(View.VISIBLE);
                    cancelHintText.setVisibility(View.GONE);
                }
                return true;
            case MotionEvent.ACTION_UP:
                moveUpHintText.setVisibility(View.GONE);
                cancelHintText.setVisibility(View.GONE);
                if (reachLimit) {
                    return true;
                }
                Message message = new Message();
                message.what = STOP;
                if (event.getY()<0) {
                    message.arg1 = MOVE_UP_CANCEL;
                    pbHandler.sendMessage(message);
                    return true;
                }
                if (currDuration < 2000) {
                    message.arg1 = VIDEO_TOO_SHORT;
                    pbHandler.sendMessage(message);
                }else {
                    message.arg1 = VIDEO_SUCCESS;
                    pbHandler.sendMessage(message);
                }
                break;
            default:
                break;
        }
        return false;
    }
    
    public static final int START = 0;
    
    public static final int INCREASE = 1;
    
    public static final int CANCEL = 2;
    
    public static final int STOP = 3;
    
    public boolean reachLimit = false;
    
    public int maxTime = JXImManager.Config.getInstance().getVideoMsgDuration()*1000;
    
    public int currDuration =0;
    
    Handler pbHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case START:
                    if (!startRecording())
                        break;
                    currDuration = 0;
                    pbHandler.sendEmptyMessageDelayed(INCREASE,100);
                    break;
                case INCREASE:
                    pbHandler.sendEmptyMessageDelayed(INCREASE, 100);
                    currDuration = currDuration+100;
                    recorderTimePb.setProgress(currDuration);
                    break;
                case CANCEL:
                    recorderTimePb.setProgress(0);
                    currDuration = 0;
                    pbHandler.removeMessages(INCREASE);
                    break;
                case STOP:
                    pbHandler.removeMessages(START);
                    pbHandler.removeMessages(INCREASE);
                    recorderTimePb.setProgress(0);
                    if (recording) {
                        stopRecording(false);
                    }
                    if (msg.arg1 == MOVE_UP_CANCEL || msg.arg1 == VIDEO_TOO_SHORT) {
                        if (currDuration>500 && msg.arg1 == VIDEO_TOO_SHORT) {
                            JXCommonUtils.showToast(JXRecorderVideoActivity.this, getString(R.string.jx_recording_video_too_short));
                        }
                        if (localPath != null) {
                            File file = new File(localPath);
                            if (file.exists())
                                file.delete();
                        }
                    }else {
                        sendVideo(null);
                    }
                    currDuration = 0;
                    break;
                default:
                    break;
            }
        };
    };
    
    public void showProgress(final boolean show){
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.jx_recorder_processing));
            progressDialog.setCancelable(false);
        }
        runOnUiThread(new Runnable() {
            public void run() {
                if (show) {
                    progressDialog.show();
                }else {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
