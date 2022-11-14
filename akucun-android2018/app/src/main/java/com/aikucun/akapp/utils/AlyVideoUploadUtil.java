package com.aikucun.akapp.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.activity.video.AlyVideoConstant;
import com.aikucun.akapp.api.callback.ApiResponse;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.DiscoverApiManager;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadCallback;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClient;
import com.alibaba.sdk.android.vod.upload.VODSVideoUploadClientImpl;
import com.alibaba.sdk.android.vod.upload.model.SvideoInfo;
import com.alibaba.sdk.android.vod.upload.session.VodHttpClientConfig;
import com.alibaba.sdk.android.vod.upload.session.VodSessionCreateInfo;

import java.io.File;

import okhttp3.Call;

/**
 * Created by ak123 on 2017/12/1.
 * 阿里云视频上传
 */

public class AlyVideoUploadUtil {

    VODSVideoUploadClient vodsVideoUploadClient;

    public interface IVideoUploadListener {
        void onSuccessResult(String videoId, String imageUrl);

        void onUploadProgress(long uploadedSize, long totalSize);
    }

    /**
     * 阿里云视频上传工具
     *
     * @param context
     */
    public AlyVideoUploadUtil(Context context) {
        vodsVideoUploadClient = new VODSVideoUploadClientImpl(context.getApplicationContext());
        vodsVideoUploadClient.init();
    }

    /**
     * 上传视频到阿里云
     *
     * @param videoPath
     * @param coverPath
     * @param mIVideoUploadListener
     */
    public void uploadVideo(String videoPath, final String akId, final String akSecret, final String securityToken, final String expriedTime, String coverPath, final IVideoUploadListener mIVideoUploadListener) {
//        final String akId = AppContext.get(AlyVideoConstant.ALY_AKID, "");
//        final String akSecret = AppContext.get(AlyVideoConstant.ALY_AKSECRET, "");
//        final String securityToken = AppContext.get(AlyVideoConstant.ALY_SECTOKEN, "");
//        final String expriedTime = AppContext.get(AlyVideoConstant.ALY_EXPIRATION, "");
        final String requestID = "";//传空或传递访问STS返回的requestID
        if (TextUtils.isEmpty(akId) || TextUtils.isEmpty(akSecret) || TextUtils.isEmpty(securityToken) || TextUtils.isEmpty(expriedTime)) {
            return;
        }
        VodHttpClientConfig vodHttpClientConfig = new VodHttpClientConfig.Builder()
                .setMaxRetryCount(2)
                .setConnectionTimeout(15 * 1000)
                .setSocketTimeout(15 * 1000)
                .build();
        SvideoInfo svideoInfo = new SvideoInfo();
        svideoInfo.setTitle(new File(videoPath).getName());
        svideoInfo.setDesc("");
        svideoInfo.setCateId(1);
        VodSessionCreateInfo vodSessionCreateInfo = new VodSessionCreateInfo.Builder()
                .setImagePath(coverPath)
                .setVideoPath(videoPath)
                .setAccessKeyId(akId)
                .setAccessKeySecret(akSecret)
                .setSecurityToken(securityToken)
                .setRequestID(requestID)
                .setExpriedTime(expriedTime)
                .setIsTranscode(true)
                .setSvideoInfo(svideoInfo)
                .setVodHttpClientConfig(vodHttpClientConfig)
                .build();
        vodsVideoUploadClient.uploadWithVideoAndImg(vodSessionCreateInfo, new VODSVideoUploadCallback() {
            @Override
            public void onUploadSucceed(String videoId, String imageUrl) {
                Log.e("上传文件成功：", videoId + "|" + imageUrl);
                mIVideoUploadListener.onSuccessResult(videoId, imageUrl);
            }

            @Override
            public void onUploadFailed(String code, String message) {
                Log.e("上传文件错误：", code + "|" + message);
                mIVideoUploadListener.onSuccessResult("", "");
            }

            @Override
            public void onUploadProgress(long uploadedSize, long totalSize) {
                Log.e("上传文件进度：", uploadedSize + "|" + totalSize);
                mIVideoUploadListener.onUploadProgress(uploadedSize, totalSize);
//                Log.e(TAG,"onUploadProgress" + uploadedSize * 100 / totalSize);
//                progress = uploadedSize * 100 / totalSize;
//                handler.sendEmptyMessage(0);
            }

            @Override
            public void onSTSTokenExpried() {
                Log.e("上传文件onSTSTokenExpried：", "onSTSTokenExpried");
                //STS token过期之后刷新STStoken，如正在上传将会断点续传
                vodsVideoUploadClient.refreshSTSToken(akId, akSecret, securityToken, expriedTime);
            }

            @Override
            public void onUploadRetry(String code, String message) {
                Log.e("上传文件onUploadRetry：", code + "|" + message);
                //上传重试的提醒
            }

            @Override
            public void onUploadRetryResume() {
                //上传重试成功的回调.告知用户重试成功
                Log.e("上传文件RetryResume：", "|");
            }
        });
    }

    public interface IGetVideoTokenListener {
        void onResult(String akId, String akSecret, String token, String exTime);
    }

    /**
     * 获取视频相关数据
     *
     * @param activity
     * @param mIGetVideoTokenListener
     */
    public static void getVideoToken(final Activity activity, final IGetVideoTokenListener mIGetVideoTokenListener) {

        long nowTime = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String exTime = AppContext.get(AlyVideoConstant.ALY_EXPIRATION, "");
        if (TextUtils.isEmpty(exTime)) {
            getUploadVideoToken(activity, mIGetVideoTokenListener);
        } else {
            long lastTime = Long.valueOf(exTime) / 1000;
            //一小时更新一次video token
            if (lastTime - nowTime < 60 * 5) {
                getUploadVideoToken(activity, mIGetVideoTokenListener);
            } else {
                String akId = AppContext.get(AlyVideoConstant.ALY_AKID, "");
                String akSecret = AppContext.get(AlyVideoConstant.ALY_AKSECRET, "");
                String token = AppContext.get(AlyVideoConstant.ALY_SECTOKEN, "");
                // String exTime = AppContext.get(AlyVideoConstant.ALY_EXPIRATION, "");
                mIGetVideoTokenListener.onResult(akId, akSecret, token, exTime);
            }
        }

    }

    /**
     * 获取网络上的video token
     *
     * @param activity
     * @param mIGetVideoTokenListener
     */
    private static void getUploadVideoToken(final Activity activity, final IGetVideoTokenListener mIGetVideoTokenListener) {
        DiscoverApiManager.getUploadVideoToken(activity, new JsonDataCallback() {
            @Override
            public void onApiSuccess(JSONObject jsonObject, Call call, ApiResponse jsonResponse) {
                super.onApiSuccess(jsonObject, call, jsonResponse);
                if (jsonObject != null) {
                    String akId = jsonObject.getString("akId");
                    String akSecret = jsonObject.getString("akSecret");
                    String expiration = jsonObject.getString("expiration");
                    String secToken = jsonObject.getString("secToken");
                    AppContext.set(AlyVideoConstant.ALY_AKID, akId);
                    AppContext.set(AlyVideoConstant.ALY_AKSECRET, akSecret);
                    AppContext.set(AlyVideoConstant.ALY_EXPIRATION, expiration);
                    AppContext.set(AlyVideoConstant.ALY_SECTOKEN, secToken);
//                    AppContext.set(AlyVideoConstant.LOCA_ALY_TOKEN_EXPIRATION, System.currentTimeMillis() / 1000);
                    if (!TextUtils.isEmpty(akId) && !TextUtils.isEmpty(akSecret) && !TextUtils.isEmpty(expiration) && !TextUtils.isEmpty(secToken)) {
                        mIGetVideoTokenListener.onResult(akId, akSecret, secToken, expiration);
                    } else {
                        mIGetVideoTokenListener.onResult("", "", "", "");
                        MToaster.showShort(activity, R.string.get_alytoken_error, MToaster.NO_IMG);
                    }
                }
            }

            @Override
            public void onApiFailed(String message, int code) {
                super.onApiFailed(message, code);
                mIGetVideoTokenListener.onResult("", "", "", "");
                if (!TextUtils.isEmpty(message)) {
                    MToaster.showShort(activity, message, MToaster.NO_IMG);
                }
            }
        });
    }

}
