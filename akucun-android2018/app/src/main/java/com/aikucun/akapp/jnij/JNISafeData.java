package com.aikucun.akapp.jnij;

import android.content.Context;

/**
 * Created by micker on 2017/10/12.
 */

public final class JNISafeData {

    public static void createSafeBitmap(Context context) {

//      adb pull /storage/emulated/0/Pictures/akucun_safe/ak.png
        /*
        String[] texts = {"28769828","41b913099b0b48268d42c33b461a29c2"};
        Bitmap bitmap = JNIAKuCun.saveTextsToImage(context,texts);
        File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        File appDir = new File(pictureFolder ,"akucun_safe");
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        File file = new File(appDir, JNIAKuCun.getSafeImageName());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        */
    }
}
