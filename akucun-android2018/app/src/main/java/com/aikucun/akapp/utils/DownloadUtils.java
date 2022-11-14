package com.aikucun.akapp.utils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by ak123 on 2018/1/30.
 * 下载apk管理
 */

public class DownloadUtils {

    private static DownloadUtils mDownloadUtils;

    public synchronized static DownloadUtils getInstance() {
        if (mDownloadUtils == null) mDownloadUtils = new DownloadUtils();
        return mDownloadUtils;
    }


    public interface IDownloadApkLisenter {
        void onSuccess(String apkPath);

        void onError();

        void onDownloadProgress(int progress);
    }

    public void downloadApk(String APK_URL, final IDownloadApkLisenter mIDownloadApkLisenter) {
        OkGo.get(APK_URL).tag(APK_URL).execute(new FileCallback() {
            @Override
            public void onSuccess(File file, Call call, Response response) {
                if (file != null)
                    mIDownloadApkLisenter.onSuccess(file.getAbsolutePath());
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                mIDownloadApkLisenter.onDownloadProgress((int) (progress * 100));
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mIDownloadApkLisenter.onError();
            }
        });
    }


    /**
     * 获取apk md5
     *
     * @param filePath
     * @return
     */
    public String fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024 * 30]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] md5Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(md5Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Get the sha1 value of the filepath specified file
     *
     * @param filePath The filepath of the file
     * @return The sha1 value
     */
    public String fileToSHA1(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
            byte[] buffer = new byte[1024]; // The buffer to read the file
            MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
            int numRead = 0; // Record how many bytes have been read
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead); // Update the digest
            }
            byte[] sha1Bytes = digest.digest(); // Complete the hash computing
            return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // Close the InputStream
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Convert the hash bytes to hex digits string
     *
     * @param hashBytes
     * @return The converted hex digits string
     */
    private String convertHashToString(byte[] hashBytes) {
        String returnVal = "";
        for (int i = 0; i < hashBytes.length; i++) {
            returnVal += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
        }
        return returnVal.toLowerCase();
    }
}
