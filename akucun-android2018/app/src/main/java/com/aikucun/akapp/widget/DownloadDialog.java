package com.aikucun.akapp.widget;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aikucun.akapp.AppConfig;
import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.utils.DownloadUtils;

import java.io.File;


/**
 * 在线更新apk dialog
 */
public class DownloadDialog extends Dialog {

    public DownloadDialog(Context context) {
        super(context);
    }

    public DownloadDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private Dialog mDialog;
        private String md5, hash, apkUrl;
        TextView download_progress_tv;
        ProgressBar download_progressBar;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 设置下载信息
         *
         * @param _md5
         * @param _hash
         * @param _apkUrl
         * @return
         */
        public Builder setDownloadInfo(String _md5, String _hash, String _apkUrl) {
            this.md5 = _md5;
            this.apkUrl = _apkUrl;
            this.hash = _hash;
            return this;
        }

        public void startDownload() {
            downloadApkFile(context, getDialog(), apkUrl, md5, hash, download_progress_tv, download_progressBar);
        }

        public Dialog getDialog() {
            return mDialog;
        }

        @SuppressLint("NewApi")
        public DownloadDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final DownloadDialog dialog = new DownloadDialog(context, R.style.mydialog);
            FrameLayout layout = (FrameLayout) inflater.inflate(
                    R.layout.dialog_downloading_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            download_progress_tv = layout.findViewById(R.id.download_progress_tv);
            download_progressBar = layout.findViewById(R.id.download_progressBar);
            dialog.setContentView(layout);
            mDialog = dialog;
            return dialog;
        }
    }

    /**
     * 下载apk文件进度显示
     *
     * @param apkUrl
     * @param textView
     * @param progressBar
     */
    private static void downloadApkFile(final Context context, final Dialog mDialog, String apkUrl, final String md5, final String hash, final TextView textView, final ProgressBar progressBar) {
        String apk_loacl_path = AppContext.get("apk_loacl_path", "");
        if (!TextUtils.isEmpty(apk_loacl_path)) {
            File file = new File(apk_loacl_path);
            if (!file.exists()) file.delete();
        }
        DownloadUtils.getInstance().downloadApk(apkUrl, new DownloadUtils.IDownloadApkLisenter() {
            @Override
            public void onSuccess(final String apkPath) {
                mDialog.dismiss();
                if (!TextUtils.isEmpty(apkPath)) {
                    AppContext.set("apk_loacl_path", apkPath);
                    String apkMd5 = DownloadUtils.getInstance().fileToMD5(apkPath);
                    String apkHash = DownloadUtils.getInstance().fileToSHA1(apkPath);
                    if (!TextUtils.isEmpty(apkMd5) && !TextUtils.isEmpty(apkHash)) {
                        if (apkMd5.equalsIgnoreCase(md5) && apkHash.equalsIgnoreCase(hash)) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
                            Uri apkUri = FileProvider.getUriForFile(context, AppConfig.PROVIDER_FILE_PATH, new File(apkPath));  //包名.fileprovider
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                        }
                        context.startActivity(intent);
                    }
                    }
                }

            }

            @Override
            public void onError() {

            }

            @Override
            public void onDownloadProgress(int progress) {
                textView.setText(progress + "%");
                progressBar.setProgress(progress);
            }
        });
    }

}
