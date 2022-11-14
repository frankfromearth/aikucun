package com.aikucun.akapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.UserInfo;
import com.jxccp.im.chat.common.entity.JXCustomerConfig;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.exception.JXException;
import com.jxccp.ui.JXConstants;
import com.jxccp.ui.JXUiHelper;
import com.jxccp.ui.view.JXInitActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by ak123 on 2017/12/19.
 * 启动佳信客服
 */

public class JxChatUtils {
    //售前
    public static String jx_channel_num_presale = "10002";
    //售后
    public static String jx_channel_num_customer = "10003";

    /**
     * 启动会话
     *
     * @param context
     */
    public static void startChat(final Activity context, String channelNumber) {
        String jxChannelNum = AppContext.get("jx_channel_num", "");
        if (!TextUtils.isEmpty(jxChannelNum) && !channelNumber.equals(jxChannelNum) && !TextUtils.isEmpty(JXUiHelper.getInstance().getSkillsId())) {
            try {
                JXImManager.McsUser.getInstance().closeSession(JXUiHelper.getInstance().getSkillsId());
            } catch (JXException e) {
                e.printStackTrace();
            }
        }

        UserInfo userInfo = AppContext.getInstance().getUserInfo();
        //登录用户的头像
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getAvator()))
            JXConstants.SEND_MSG_USER_AVATAR_URL = userInfo.getAvator();
        JXCustomerConfig jxCustomerConfig = new JXCustomerConfig();
        if (!TextUtils.isEmpty(userInfo.getYonghubianhao()))
            jxCustomerConfig.setName(userInfo.getName()+"("+ userInfo.getYonghubianhao()+")");
        else jxCustomerConfig.setName(userInfo.getName());
        Map<String, String> extendMap = new HashMap<>();
        extendMap.put("extend1", userInfo.getYonghubianhao());
        extendMap.put("extend2", userInfo.getViplevel() + "");
        jxCustomerConfig.setExtendFields(extendMap);
        jxCustomerConfig.setCid(userInfo.getUserid());
        JXImManager.Config.getInstance().setCustomerConfig(jxCustomerConfig);
        JXImManager.getInstance().setChannelNumber(channelNumber);
        AppContext.set("jx_channel_num", channelNumber);
        RxPermissions rxPermissions = new RxPermissions(context);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean granted) {
                if (granted) {
                    if (TDevice.isCameraAvailable()) {
                        // 启动客服
                        JXImManager.Config.getInstance().enableMessageGroupByChannelNo(true);
                        context.startActivity(new Intent(context, JXInitActivity.class));
                    } else {
                        MToaster.showShort(context, R.string.camera_is_not_available, MToaster.IMG_INFO);
                    }
                } else {
                    showApplicationSettingDetails(context, "相机/录音");
                }
            }
        });
    }


    private static void showApplicationSettingDetails(final Activity context, String serviceName) {

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
}
