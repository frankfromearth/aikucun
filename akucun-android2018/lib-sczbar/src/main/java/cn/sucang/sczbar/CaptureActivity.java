package cn.sucang.sczbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import cn.sucang.sczbar.camera.CameraManager;
import cn.sucang.sczbar.decode.DecodeThread;
import cn.sucang.sczbar.decode.InactivityTimer;
import cn.sucang.sczbar.view.ViewfinderView;

/**
 * 条码扫描
 * Created by jarry on 16/10/26.
 *
 * 权限设置：
 * <uses-permission android:name="android.permission.VIBRATE"/>
 * <uses-permission android:name="android.permission.CAMERA"/>
 * <uses-permission android:name="android.permission.FLASHLIGHT"/>
 * <uses-feature android:name="android.hardware.camera"/>
 * <uses-feature android:name="android.hardware.camera.autofocus"/>
 *
 * 调用方法：
 * startActivityForResult(new Intent(this, CaptureActivity.class), 0);
 *
 * 获取扫描结果
 * protected void onActivityResult(int requestCode, int resultCode, Intent data)
 * {
 *      super.onActivityResult(requestCode, resultCode, data);
 *      if (resultCode == CaptureActivity.ZBAR_SCAN_RESULT_CODE_OK)
 *      {
 *          Bundle bundle = data.getExtras();
 *          String scanResult = bundle.getString(CaptureActivity.ZBAR_SCAN_RESULT_NAME);
 *          handleScan(scanResult);
 *      }
 * }
 */
public class CaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    private static final String TAG = "ZBar";

    public static final String BUNDLE_KEY_ZBAR_SCAN_TITLE = "BUNDLE_KEY_ZBAR_SCAN_TITLE";
    public static final String BUNDLE_KEY_ZBAR_SCAN_TIP = "BUNDLE_KEY_ZBAR_SCAN_TIP";

    public static final int ZBAR_SCAN_RESULT_CODE_OK = 101;
    public static final int ZBAR_SCAN_RESULT_CODE_CANCELED = 102;
    public static final String ZBAR_SCAN_RESULT_NAME = "result";

    public static final int SCAN_MESSAGE_AUTOFOCUS = 1;
    public static final int SCAN_MESSAGE_DECODE = 2;
    public static final int SCAN_MESSAGE_DECODE_SUCCEED = 3;
    public static final int SCAN_MESSAGE_DECODE_FAILED = 4;
    public static final int SCAN_MESSAGE_RESTART_PREVIEW = 5;
    public static final int SCAN_MESSAGE_QUIT = 6;

    private enum State
    {
        PREVIEW,
        SUCCESS,
        DONE
    }

    private State state = State.SUCCESS;

    private SurfaceView mSurfaceView;
    private ViewfinderView mViewfinderView;

    private DecodeThread decodeThread;
    private CaptureHandler mHandler;

    private boolean hasSurface = false;

    private TextView tvTitle;
    private ImageView iv_flashlight;

    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private boolean flag = true;

    private InactivityTimer inactivityTimer;
    private ScreenOffReceiver mScreenOffReceiver = new ScreenOffReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.zbar_activity_capture);

        CameraManager.init(getApplication());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //
        mSurfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        iv_flashlight = (ImageView) findViewById(R.id.iv_flashlight);
        iv_flashlight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                light();
            }
        });

        tvTitle = (TextView) findViewById(R.id.tv_title);
        String title = getIntent().getStringExtra(BUNDLE_KEY_ZBAR_SCAN_TITLE);
        if (title != null && title.length() > 0)
        {
            tvTitle.setText(title);
        }
        String tipText = getIntent().getStringExtra(BUNDLE_KEY_ZBAR_SCAN_TIP);
        if (tipText != null && tipText.length() > 0)
        {
            mViewfinderView.setTipText(tipText);
        }

        this.hasSurface = false;
        this.inactivityTimer = new InactivityTimer(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(this.mScreenOffReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        if (mHandler != null)
        {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().offLight();
        inactivityTimer.onPause();
        CameraManager.get().closeDriver();
        if (!hasSurface)
        {
            SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume()
    {
        super.onResume();

        mHandler = null;

        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (hasSurface)
        {
            initCamera(surfaceHolder);
        }
        else
        {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
        {
            playBeep = false;
        }
        initBeepSound();
        inactivityTimer.onResume();
        vibrate = true;
    }

    @Override
    protected void onDestroy()
    {
        inactivityTimer.shutdown();
        unregisterReceiver(this.mScreenOffReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            CameraManager.get().openDriver(surfaceHolder);
        }
        catch (IOException ioe)
        {
            return;
        }
        catch (RuntimeException e)
        {
            return;
        }

        if (mHandler == null)
        {
            mHandler = new CaptureHandler();
        }

        decodeThread = new DecodeThread(this);
        decodeThread.start();

        CameraManager.get().startPreview();
        startPreviewAndDecode();
    }

    private void startPreviewAndDecode()
    {
        if (state == State.SUCCESS)
        {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), SCAN_MESSAGE_DECODE);
            CameraManager.get().requestAutoFocus(mHandler, SCAN_MESSAGE_AUTOFOCUS);
            mViewfinderView.drawViewfinder();
        }
    }

    public ViewfinderView getViewfinderView()
    {
        return mViewfinderView;
    }

    public Handler getHandler()
    {
        return mHandler;
    }

    public void drawViewfinder()
    {
        mViewfinderView.drawViewfinder();
    }

    public void handleDecode(String result)
    {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Intent intent = new Intent();

        if (result.length() == 13 && "0".equalsIgnoreCase(result.substring(0,1))) {
            result = result.substring(1);
        }

        intent.putExtra("result", result);
        setResult(ZBAR_SCAN_RESULT_CODE_OK, intent);
        finish();
    }

    public void light()
    {
        if (this.flag)
        {
            CameraManager.get().openLight();
            this.flag = false;
            this.iv_flashlight.setImageDrawable(getResources().getDrawable(R.drawable
                    .icon_light_hover));
            return;
        }
        CameraManager.get().offLight();
        this.flag = true;
        this.iv_flashlight.setImageDrawable(getResources().getDrawable(R.drawable.icon_light));
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        setResult(ZBAR_SCAN_RESULT_CODE_CANCELED, intent);
        finish();
    }

    private class CaptureHandler extends Handler
    {
        public CaptureHandler()
        {
            state = State.SUCCESS;
        }

        @Override
        public void handleMessage(Message message)
        {
            switch (message.what)
            {
                case SCAN_MESSAGE_AUTOFOCUS:
                    if (state == State.PREVIEW)
                    {
                        CameraManager.get().requestAutoFocus(this, SCAN_MESSAGE_AUTOFOCUS);
                    }
                    break;

                case SCAN_MESSAGE_RESTART_PREVIEW:
                    Log.d(TAG, "Got restart preview message");
                    startPreviewAndDecode();
                    break;

                case SCAN_MESSAGE_DECODE_SUCCEED:
                    String strResult = (String) message.obj;
                    Log.d(TAG, "Decode Result : " + strResult);
                    state = State.SUCCESS;
                    handleDecode(strResult);
                    /***********************************************************************/
                    break;

                case SCAN_MESSAGE_DECODE_FAILED:
                    state = State.PREVIEW;
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
                            SCAN_MESSAGE_DECODE);
                    break;

                //                case ZBA_SCAN_MESSAGE_SCAN_RESULT:
                //                    Log.d(TAG, "Got return scan result message");
                //                    activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                //                    activity.finish();
                //                    break;
            }
        }

        public void quitSynchronously()
        {
            state = State.DONE;
            CameraManager.get().stopPreview();
            Message quit = Message.obtain(decodeThread.getHandler(), SCAN_MESSAGE_QUIT);
            quit.sendToTarget();
            try
            {
                decodeThread.join();
            }
            catch (InterruptedException e)
            {
                // continue
            }
            removeMessages(SCAN_MESSAGE_DECODE_SUCCEED);
            removeMessages(SCAN_MESSAGE_DECODE_FAILED);
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context arg0, Intent arg1)
        {
            Log.d("CaptureActivity", "CaptureActivity receive screen off command ++");
            CaptureActivity.this.finish();
        }
    }


    private static final float BEEP_VOLUME = 0.20f;
    private static final long VIBRATE_DURATION = 200L;

    private final OnCompletionListener beepListener = new OnCompletionListener()
    {
        public void onCompletion(MediaPlayer mediaPlayer)
        {
            mediaPlayer.seekTo(0);
        }
    };

    private void initBeepSound()
    {
        if (playBeep && mediaPlayer == null)
        {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try
            {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file
                        .getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            }
            catch (IOException e)
            {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate()
    {
        if (playBeep && mediaPlayer != null)
        {
            mediaPlayer.start();
        }
        if (vibrate)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
}