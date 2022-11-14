package cn.sucang.sczbar.decode;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.CountDownLatch;

import cn.sucang.sczbar.CaptureActivity;

public final class DecodeThread extends Thread
{
    private final CaptureActivity activity;
    private Handler handler;
    private final CountDownLatch handlerInitLatch;

    public DecodeThread(CaptureActivity activity)
    {
        this.activity = activity;
        handlerInitLatch = new CountDownLatch(1);
    }

    public Handler getHandler()
    {
        try
        {
            handlerInitLatch.await();
        }
        catch (InterruptedException ie)
        {
            // continue?
        }
        return handler;
    }

    @Override
    public void run()
    {
        Looper.prepare();
        handler = new DecodeHandler(activity);
        handlerInitLatch.countDown();
        Looper.loop();
    }

}
