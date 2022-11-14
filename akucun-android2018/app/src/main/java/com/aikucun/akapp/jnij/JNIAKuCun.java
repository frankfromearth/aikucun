package com.aikucun.akapp.jnij;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by micker on 2017/9/25.
 */

public class JNIAKuCun {

    static
    {
        System.loadLibrary("JNIAKuCun");
    }

    public native static Bitmap saveTextsToImage(Context context, String[] texts);

    public native static String[] getDataFromImage(Context context);

    public native static String getSafeImageName();

}
