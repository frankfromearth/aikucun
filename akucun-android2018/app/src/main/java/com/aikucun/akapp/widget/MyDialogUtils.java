package com.aikucun.akapp.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.aikucun.akapp.R;
import com.aikucun.akapp.api.callback.JsonDataCallback;
import com.aikucun.akapp.api.manager.UsersApiManager;
import com.aikucun.akapp.utils.MToaster;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ak123 on 2017/11/16.
 * 自定义弹框设置
 */

public class MyDialogUtils {

    public interface IDialogListenter {
        void onClick();
    }

    /**
     * @param context
     * @param dialog_title      标题
     * @param dialog_content    内容
     * @param sureBtnListener   确定回调
     * @param cancelBtnListener 取消回调
     */
    public static void showNormalDialog(Context context, String dialog_title, String dialog_content, IDialogListenter sureBtnListener, IDialogListenter cancelBtnListener) {
        show(context, dialog_title, dialog_content, sureBtnListener, cancelBtnListener);
    }

    public static void showNormalDialog(Context context, int titleRes, int contentRes, IDialogListenter sureBtnListenter, IDialogListenter cancelBtnListenter) {
        String title = context.getResources().getString(titleRes);
        String content = context.getResources().getString(contentRes);
        show(context, title, content, sureBtnListenter, cancelBtnListenter);
    }

    /**
     * 显示弹框
     *
     * @param context
     * @param dialog_title      标题
     * @param dialog_content    内容
     * @param sureBtnListener   确定回调
     * @param cancelBtnListener 取消回调
     */
    private static void show(Context context, String dialog_title, String dialog_content, final IDialogListenter sureBtnListener, final IDialogListenter cancelBtnListener) {
        MyDialog.Builder alertDialog = new MyDialog.Builder(context);
        alertDialog.setTitle(dialog_title);
        alertDialog.setMessage(dialog_content);
        alertDialog.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (sureBtnListener != null)
                            sureBtnListener.onClick();
                    }
                });

        alertDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (cancelBtnListener != null)
                            cancelBtnListener.onClick();
                    }
                });
        alertDialog.create().show();
    }

    /**
     * 显示下载框
     *
     * @param context
     * @param md5
     * @param hash
     * @param apkUrl
     */
    public static void showDownloadDialog(Context context, String md5, String hash, String apkUrl) {
        DownloadDialog.Builder alertDialog = new DownloadDialog.Builder(context);
        alertDialog.setDownloadInfo(md5, hash, apkUrl);
        DownloadDialog mDownloadDialog = alertDialog.create();
        alertDialog.startDownload();
        mDownloadDialog.setCanceledOnTouchOutside(false);
        mDownloadDialog.show();
    }

    /**
     * 默认取消，确定按钮提示框
     *
     * @param context
     * @param contentId
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, int contentId, final IDialogListenter mIDialogListenter) {
        showNormalDialog(context, "", context.getString(contentId), R.string.cancel, R.string.sure, true, mIDialogListenter);
    }

    /**
     * @param context
     * @param title
     * @param contentId
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, int title, int contentId, final IDialogListenter mIDialogListenter) {
        showNormalDialog(context, context.getString(title), context.getString(contentId), R.string.cancel, R.string.sure, true, mIDialogListenter);
    }

    public static void showV7NormalDialog(Context context, int title, String content, final IDialogListenter mIDialogListenter) {
        showNormalDialog(context, context.getString(title), content, R.string.cancel, R.string.sure, true, mIDialogListenter);
    }

    /**
     * @param context
     * @param title
     * @param contentId
     * @param btnId
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, int title, int contentId, int btnId, final IDialogListenter mIDialogListenter) {
        showNormalDialog(context, context.getString(title), context.getString(contentId), R.string.cancel, btnId, true, mIDialogListenter);
    }

    /**
     * 默认取消，确定按钮提示框
     *
     * @param context
     * @param content
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, String content, final IDialogListenter mIDialogListenter) {
        showNormalDialog(context, "", content, R.string.cancel, R.string.sure, true, mIDialogListenter);
    }

    /**
     * 确定按钮提示框
     *
     * @param context
     * @param content
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, String title, String content, final IDialogListenter mIDialogListenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(content).setPositiveButton
                (R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIDialogListenter.onClick();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * @param context
     * @param contentId
     * @param cancelBtnResourceId
     * @param sureBtnResourceId
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, int contentId, int cancelBtnResourceId, String sureBtnResourceId, final IDialogListenter mIDialogListenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(contentId)).setNegativeButton(cancelBtnResourceId, null).setPositiveButton
                (sureBtnResourceId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIDialogListenter.onClick();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    /**
     * @param context
     * @param contentId
     * @param cancelBtnResourceId
     * @param sureBtnResourceId
     * @param cancelOutside
     * @param mIDialogListenter
     */
    public static void showV7NormalDialog(Context context, int contentId, int cancelBtnResourceId, String sureBtnResourceId, boolean cancelOutside, final IDialogListenter mIDialogListenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(contentId)).setNegativeButton(cancelBtnResourceId, null).setPositiveButton
                (sureBtnResourceId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIDialogListenter.onClick();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(cancelOutside);
        dialog.show();
    }

    /**
     * 提示框
     *
     * @param context
     * @param title
     * @param content
     * @param cancelBtnResourceId
     * @param sureBtnResourceId
     * @param cancelOutside
     * @param mIDialogListenter
     */
    private static void showNormalDialog(Context context, String title, String content, int cancelBtnResourceId, int sureBtnResourceId, boolean cancelOutside, final IDialogListenter mIDialogListenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(content).setNegativeButton(cancelBtnResourceId, null).setPositiveButton
                (sureBtnResourceId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mIDialogListenter != null)
                            mIDialogListenter.onClick();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(cancelOutside);
        dialog.show();
    }

    public interface IProductForwardListener {
        void onResult(boolean singleSelected, boolean moreSelected);

        void onCancel();
    }

    /**
     * 显示转发弹框
     *
     * @param context
     * @param message
     * @param showSignleImg
     * @param showMoreImg
     * @param mIProductForwardListener
     */
    public static void showProductForwardDialog(final Context context, String message, String btnStr, boolean showSignleImg, boolean showMoreImg, final IProductForwardListener mIProductForwardListener) {
        final ProductForwardDialog.Builder productForwardDialog = new ProductForwardDialog.Builder(context);
        productForwardDialog.setMessage(message);
        productForwardDialog.setPositiveButton(btnStr,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mIProductForwardListener != null) {
                            mIProductForwardListener.onResult(productForwardDialog.getSingleImgSelected(), productForwardDialog.getMoreImgSelected());
                        }
                    }
                });
        productForwardDialog.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (mIProductForwardListener != null) mIProductForwardListener.onCancel();
                    }
                });
        productForwardDialog.setIsShowMoreImgLayout(showMoreImg);
        productForwardDialog.setIsShowSignleImgLayout(showSignleImg);
        ProductForwardDialog dialog = productForwardDialog.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mIProductForwardListener != null) mIProductForwardListener.onCancel();
            }
        });
        dialog.show();
    }

    /**
     * 显示权限提示框
     *
     * @param context
     * @param serviceName
     */
    public static void showPermissionsDialog(final Context context, String serviceName) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.runing_permissions) + (StringUtils.isEmpty(serviceName) ? "" : ("\n" + serviceName)))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(intent);
                    }
                }).show();
    }

    /**
     * 非VIP弹框
     *
     * @param context
     */
    public static void showNotVipDialog(final Activity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.not_vip_title)
                .setMessage(R.string.buy_vip_intro)
                .setNeutralButton(R.string.cancel, null)
                .setNegativeButton(R.string.input_invitation_code, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showInviteCode(context);
                    }
                });
//                        .setPositiveButton(R.string.go_to_vip, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(new Intent(getActivity(), MemberActivity.class));
//                            }
//                        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 输入邀请码弹框
     *
     * @param context
     */
    private static void showInviteCode(final Activity context) {
        final EditText remarkEt = new EditText(context);
        remarkEt.setHint(R.string.input_invitation_code);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.invitation_code).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                TDevice.hideSoftKeyboard(remarkEt);
                String remark = remarkEt.getText().toString();

                if (remark.length() < 6) {
                    showV7NormalDialog(context, R.string.msg_text_title, R.string.invite_code_invalid, null);
                    return;
                }

                UsersApiManager.sendInviteCode(context, remark, new JsonDataCallback() {
                    @Override
                    public void onSuccess(JSONObject jsonObject, Call call, Response response) {
                        super.onSuccess(jsonObject, call, response);
                        if (jsonObject != null) {
                            int code = jsonObject.getIntValue("code");
                            if (code == 0) {
                                UsersApiManager.userGetInfo(context, null);
                                showV7NormalDialog(context, R.string.alread_opend_vip_title, R.string.already_opened_vip, null);
                            } else {
                                MToaster.showShort(context, R.string.invite_code_invalid, MToaster.IMG_INFO);
                            }
                        }

                    }

                    @Override
                    public void onApiFailed(String message, int code) {
                        super.onApiFailed(message, code);
                        if (!TextUtils.isEmpty(message)) {
                            MToaster.showShort(context, message, MToaster.IMG_INFO);
                        }
                    }
                });


            }
        });
        builder.show();
    }

    public interface ISetRemarkLisenter {
        void onBack(String content);
    }

    /**
     * 显示设置备注弹框
     * @param context
     * @param mISetRemarkLisenter
     */
    public static void showSetRemarkDialog(Context context, final ISetRemarkLisenter mISetRemarkLisenter) {
        final EditText remarkEt = new EditText(context);
        remarkEt.setMaxLines(3);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.input_remark_info).setIcon(android.R.drawable.ic_dialog_info).setView(remarkEt)
                .setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String remark = remarkEt.getText().toString();
                if (!StringUtils.isEmpty(remark)) {
                    mISetRemarkLisenter.onBack(remark.replaceAll("\\n", ""));
                }
            }
        });
        builder.show();
    }

    public static void showImageDialog(Activity activity,String url){
        final Dialog dia = new Dialog(activity, R.style.edit_AlertDialog_style);
        View view= LayoutInflater.from(activity).inflate(R.layout.dialog_image_show,null);
        dia.setContentView(view);
        ImageView imageView = view.findViewById(R.id.dialog_image_show);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        if(lp!=null){
            lp.width = (int) TDevice.getScreenWidth()*4/5;
            lp.height = lp.width*4/3;
            imageView.setLayoutParams(lp);
        }
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy
                .ALL).into(imageView);
        //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dia.dismiss();
                    }
                });
        dia.show();
    }

}
