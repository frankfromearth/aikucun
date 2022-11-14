package cn.sucang.sczbar.decode;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import cn.sucang.sczbar.CaptureActivity;

final class DecodeHandler extends Handler
{
    private static final String TAG = DecodeHandler.class.getSimpleName();
    private final CaptureActivity activity;
    private ImageScanner scanner;

//    static
//    {
//        System.loadLibrary("iconv");
//    }

    public DecodeHandler(CaptureActivity activity)
    {
        this.activity = activity;
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
    }

    @Override
    public void handleMessage(Message message)
    {
        switch (message.what)
        {
            case CaptureActivity.SCAN_MESSAGE_DECODE:
                decode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case CaptureActivity.SCAN_MESSAGE_QUIT:
                Looper.myLooper().quit();
                break;
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The preview frame.
     * @param width  The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height)
    {
        Image barcode = new Image(width, height, "Y800");
        Rect scanImageRect = activity.getViewfinderView().getScanImageRect(height, width);
        barcode.setCrop(scanImageRect.top, scanImageRect.left, scanImageRect.bottom,
                scanImageRect.right);
        barcode.setData(data);

        int result = scanner.scanImage(barcode);
        String strResult = "";
        if (result != 0)
        {
            SymbolSet syms = scanner.getResults();
            for (Symbol sym : syms)
            {
                strResult = sym.getData().trim();
                if (!strResult.isEmpty())
                {
                    break;
                }
            }
        }

        if (!strResult.isEmpty())
        {
            Message message = Message.obtain(activity.getHandler(), CaptureActivity.SCAN_MESSAGE_DECODE_SUCCEED,
                    strResult);
            Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        }
        else
        {
            Message message = Message.obtain(activity.getHandler(), CaptureActivity.SCAN_MESSAGE_DECODE_FAILED);
            message.sendToTarget();
        }
    }

}
