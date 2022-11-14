/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.jxccp.im.callback.JXEventListner;
import com.jxccp.im.chat.manager.JXEventNotifier;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.ui.JXConstants;
import com.jxccp.ui.R;
import com.jxccp.ui.entities.JXCommodity;
import com.jxccp.ui.listeners.JXChatGeneralCallback;
import com.jxccp.ui.utils.NotificationUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JXChatUIActivity extends FragmentActivity implements JXEventListner, JXChatGeneralCallback{

    public static final String TAG = "chat";

    private JXChatFragment chatFragment;
    
    private List<Integer> functionImages = new ArrayList<>();
    
    private List<String> functionName =new ArrayList<>();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jx_activity_chat);
        // 聊天界面加载JXChatFragment
        chatFragment = new JXChatFragment();
        Bundle args = new Bundle();
        // 设置相应属性,CHAT_KEY为聊天的对象,访客这里为技能组的ID，CHAT_SKILLS_DISPLAYNAME为技能组的昵称
        args.putInt(JXChatView.CHAT_TYPE, getIntent().getIntExtra(JXChatView.CHAT_TYPE, 0));
        args.putString(JXChatFragment.CHAT_KEY, getIntent().getStringExtra(JXChatView.CHAT_KEY));
        args.putString(JXChatFragment.CHAT_SKILLS_DISPLAYNAME, getIntent().getStringExtra(JXChatView.CHAT_SKILLS_DISPLAYNAME));
        // 扩展信息
        args.putString(JXChatView.EXTEND_DATA, getIntent().getStringExtra(JXChatView.EXTEND_DATA));
        
        configFuntionRes();

//         args.putIntArray(JXChatFragment.FUNCTION_PANEL_DRAWABLES, functionImages);
//         args.putStringArray(JXChatFragment.FUNCTION_PANEL_NAMES, functionName);
        chatFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, chatFragment, TAG)
                .commit();
        chatFragment.configFunctionPanel(functionImages, functionName);
        /**添加扩展功能面板点击事件**/

        chatFragment.setJXChatGeneralCallback(this);
//        chatFragment.setJXCommodity(DemoHelper.getInstance().getCommodity());

        JXImManager.Message.getInstance().registerEventListener(this);

    }
    
    private void configFuntionRes() {
        // 聊天界面功能面板的图片资源的id,这里数组的顺序与下面配置的string的顺序对应
        functionImages.add(R.drawable.bg_image_click);
        functionImages.add(R.drawable.bg_take_photo_click);
        if (JXImManager.McsUser.getInstance().canSendVideo()) {
            functionImages.add(R.drawable.bg_function_video);
        }

        functionName.add(getString(R.string.jx_image));
        functionName.add(getString(R.string.jx_take_photo));
        if (JXImManager.McsUser.getInstance().canSendVideo()) {
            functionName.add(getString(R.string.jx_videomsg));
        }

        if (!JXImManager.McsUser.getInstance().getQuickQuestions().isEmpty()) {
            functionImages.add(R.drawable.bg_quick_question_click);
            functionName.add(getString(R.string.jx_function_quick_question));
        }
        if (JXImManager.McsUser.getInstance().isVisitorSatisfyOpen()) {
            functionImages.add(R.drawable.bg_evaluate_click);
            functionName.add(getString(R.string.jx_satisfaction_evaluate));
        }
    }

    /**发送图文消息**/
    public void sendRichTextMessage(JXCommodity commodity){
        chatFragment.sendRichText(commodity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationUtils.cancelAllNotifty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JXImManager.Message.getInstance().removeEventListener(this);
    }

    public boolean isVisiable(){
        if(chatFragment != null){
            return chatFragment.isVisiable;
        }
        return false;
    }
    
    /**
     * 当前界面是否为消息中心界面
     * @return
     */
    public boolean isMessagebox(){
        if(messageBoxFragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("messageBox");
            if(fragment != null){
                return true;
            }
        }
        if(chatFragment != null){
            return chatFragment.isMessagebox();
        }
        return false;
    }
    
    @Override
    public void onFunctionItemClick(int position, int drawableResId) {
        if (drawableResId == R.drawable.bg_image_click) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra("return-data", true);
            chatFragment.startActivityForResult(intent, JXChatView.IMAGE_REQUEST_CODE);
        } else if (drawableResId == R.drawable.bg_take_photo_click) {
            if(chatFragment.isChatWithRobot){
                return;
            }
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
//                chatFragment.cameraPhoto = getFile();
//                Intent getImageByCamera = new Intent(
//                        MediaStore.ACTION_IMAGE_CAPTURE);
//                    getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
//                            Uri.fromFile(chatFragment.cameraPhoto));
//                getImageByCamera.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                    startActivityForResult(getImageByCamera,JXChatView.GET_IMAGE_VIA_CAMERA);

                try {
                    File dir = JXChatUIActivity.this.getExternalFilesDir("JXCameraPhoto");
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    chatFragment.cameraPhoto = new File(dir , System.currentTimeMillis() + ".jpg");
                    Uri u;
                    if (Build.VERSION.SDK_INT<=Build.VERSION_CODES.M){
                        u = Uri.fromFile(chatFragment.cameraPhoto);
                    }else {
                        Log.e("使用拍照：",JXConstants.getProviderName());
                        u = FileProvider.getUriForFile(this, JXConstants.getProviderName(),chatFragment.cameraPhoto);
                    }
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    chatFragment.startActivityForResult(intent, JXChatView.GET_IMAGE_VIA_CAMERA);
                } catch (Exception e) {
                    Log.e("拍照异常信息：",e.getMessage());
                    Toast.makeText(JXChatUIActivity.this, getString(R.string.jx_storage_dir_not_found), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(JXChatUIActivity.this, getString(R.string.jx_sdcard_not_found), Toast.LENGTH_LONG).show();
            }
        }else if (drawableResId == R.drawable.bg_function_video) {
            chatFragment.navToVideo();
        } else if (drawableResId == R.drawable.bg_evaluate_click) {
            chatFragment.showEvaluateWindow(null,false);
        } else if (drawableResId == R.drawable.bg_quick_question_click) {
            chatFragment.showQuickQuestionWindows();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (chatFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onEvent(JXEventNotifier eventNotifier) {
        NotificationUtils.vibrate(this);
    }
    

    @Override
    public void onChatSession(boolean isWithRobot) {
        if(chatFragment != null){
            if(isWithRobot){
                List<Integer> robotFunction = new ArrayList<>();
                List<String> robotFunctionName = new ArrayList<>();
                robotFunction.add(R.drawable.bg_quick_question_click);
                robotFunctionName.add(getString(R.string.jx_function_quick_question));
                chatFragment.configFunctionPanel(robotFunction, robotFunctionName);
            }else{
                chatFragment.configFunctionPanel(functionImages, functionName);
            }
        }
    }

    private JXChatFragment messageBoxFragment =null;
    @Override
    public void onMessageBoxClick() {
        messageBoxFragment = new JXChatFragment();
        Bundle args = new Bundle();
        args.putInt(JXChatView.CHAT_TYPE, JXChatView.CHATTYPE_MESSAGE_BOX);
        args.putString(JXChatFragment.CHAT_SKILLS_DISPLAYNAME, getString(R.string.jx_message_center));
        messageBoxFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, messageBoxFragment, "messageBox")
        .addToBackStack("messageBox")
        .commit();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        JXChatFragment chatFragment = new JXChatFragment();
        Bundle args = new Bundle();
        // 设置相应属性,CHAT_KEY为聊天的对象,访客这里为技能组的ID，CHAT_SKILLS_DISPLAYNAME为技能组的昵称
        int chatType = intent.getIntExtra(JXChatView.CHAT_TYPE, 0);
        if(chatType == JXChatView.CHATTYPE_MESSAGE_BOX){
            args.putInt(JXChatView.CHAT_TYPE, chatType);
            args.putString(JXChatFragment.CHAT_KEY, intent.getStringExtra(JXChatView.CHAT_KEY));
            args.putString(JXChatFragment.CHAT_SKILLS_DISPLAYNAME, intent.getStringExtra(JXChatView.CHAT_SKILLS_DISPLAYNAME));
            chatFragment.setArguments(args);
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag("messageBox");
            if(fragment != null){
                fragmentManager.popBackStackImmediate();
                fragmentManager.beginTransaction().remove(fragment).commit();
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, chatFragment, "messageBox")
                .addToBackStack("messageBox")
                .commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, chatFragment, "messageBox")
                .commit();
            }
        }
    }

    private File getFile(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String timeStamp = format.format(new Date());
        String imageFileName = "akc_" + timeStamp + ".jpg";
        File imageFile = new File(this.getAlbumDir(), imageFileName);
        return imageFile;
    }

    public File getAlbumDir() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),  "akc/big");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }
}
