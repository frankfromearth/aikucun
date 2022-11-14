
package com.jxccp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.jxccp.im.JXErrorCode;
import com.jxccp.im.JXErrorCode.Mcs;
import com.jxccp.im.callback.JXLoginCallback;
import com.jxccp.im.chat.manager.JXImManager;
import com.jxccp.im.exception.JXException;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.im.util.log.JXLog.Module;
import com.jxccp.jivesoftware.smack.util.stringencoder.Base64;

import java.util.Locale;
import java.util.Random;


/**
 * IM账号自动创建登录的帮助类, 若app需要自动创建账号则可采用该帮助类.
 *
 */
public class AccountHelper {

    private static final String CLASS_NAME = "AccountHelper";

    private static final String CONFIG = "mcs_config";

    private static final String SP_LOGIN_FLAG = "hasAccount";

    private static final String SP_SHORT_TOKEN_FLAG = "hasShortToken";

    private static final String SP_USERNAMAE = "mcs_username";

    private static final String SP_PASSWORD = "mcs_password";

    private static final String SP_SHORT_TOKEN = "mcs_short_token";

    private static final String SP_CID = "mcs_cid";

    private SharedPreferences sharedPreferences;

    private AsyncTask<Void, Void, Integer> accountTask = null;

    private AccountHelper() {
    }

    private static class SingletonHolder {
        private final static AccountHelper INSTANCE = new AccountHelper();
    }

    public static AccountHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private String username;

    private String password;

    public void initMcsRequest(final Context applicationContext,
                               final OnInitMcsRequestCallback callback){
        if (getCustomerConfigCid() != null) {
            JXLog.d(Module.mcs, CLASS_NAME, "initMcsRequest", "cid not null , init by cid");
            initMcsRequestByConfig(applicationContext, callback);
        }else {
            JXLog.d(Module.mcs, CLASS_NAME, "initMcsRequest", "cid is null , init normal");
            initMcsRequestNomal(applicationContext, callback);
        }
    }

    public void setKeyBoardHeight(int height){
        Editor editor = sharedPreferences.edit();
        editor.putInt("key_board_height", height);
        editor.commit();
    }
    public int getKeyBoardHeight(){
        return sharedPreferences.getInt("key_board_height",0);
    }

    /**
     * 自动创建账号并登录IM用户
     * @param applicationContext
     * @param callback
     */
    public void initMcsRequestNomal(final Context applicationContext,
                                    final OnInitMcsRequestCallback callback) {
        accountTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    sharedPreferences = applicationContext.getSharedPreferences(CONFIG,
                            Context.MODE_PRIVATE);
                    final Editor editor = sharedPreferences.edit();
                    boolean hasAccount = sharedPreferences.getBoolean(
                            JXImManager.getInstance().getAppKey() + SP_LOGIN_FLAG, false);
                    if (hasAccount) {
                        if (JXImManager.Login.getInstance().isConnected()) {
                            if (callback != null) {
                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                            }
                        } else {
                            String encodeUsername = sharedPreferences.getString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_USERNAMAE,
                                    "");
                            String encodePassword = sharedPreferences.getString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_PASSWORD,
                                    "");
                            username = getUsernameFromPrefixName(
                                    Base64.decodeToString(encodeUsername));
                            password = getUsernameFromPrefixName(
                                    Base64.decodeToString(encodePassword));
                            JXImManager.Login.getInstance().login(username, password,
                                    new JXLoginCallback() {
                                        @Override
                                        public void onSuccess() {
                                            if (callback != null) {
                                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                            }
                                        }

                                        @Override
                                        public void onError(int code, String reason) {
                                            if (callback != null) {
                                                callback.onInitMcsResult(code);
                                            }
                                        }
                                    });
                        }
                    } else {
                        String username = newUserName();
                        password = randomString();
                        boolean res = JXImManager.Login.getInstance()
                                .createAccountFromServer(username, password);
                        if (res) {
                            String encodeUsername = Base64.encode(username);
                            String encodePassword = Base64.encode(password);
                            editor.putString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_USERNAMAE,
                                    encodeUsername);
                            editor.putString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_PASSWORD,
                                    encodePassword);
                            editor.putBoolean(
                                    JXImManager.getInstance().getAppKey() + SP_LOGIN_FLAG,
                                    true);
                            editor.commit();
                            JXImManager.Login.getInstance().login(username, password,
                                    new JXLoginCallback() {
                                        @Override
                                        public void onSuccess() {
                                            if (callback != null) {
                                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                            }
                                        }

                                        @Override
                                        public void onError(int code, String reason) {
                                            if (callback != null) {
                                                callback.onInitMcsResult(code);
                                            }
                                        }
                                    });
                        } else {
                            editor.putBoolean(
                                    JXImManager.getInstance().getAppKey() + SP_LOGIN_FLAG,
                                    false);
                            editor.commit();
                            if (callback != null) {
                                callback.onInitMcsResult(JXErrorCode.OTHER);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof JXException) {
                        if (callback != null) {
                            callback.onInitMcsResult(((JXException)e).getErrorCode());
                        }
                    } else {
                        if (callback != null) {
                            callback.onInitMcsResult(JXErrorCode.OTHER);
                        }
                    }
                }
                return null;
            }

        }.execute();
    }

    public void initMcsRequestByCustomId(final Context applicationContext, final String customid,
                                         final OnInitMcsRequestCallback callback) {
        accountTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                JXImManager.Login.getInstance().loginByCustomId(customid,
                        new JXLoginCallback() {
                            @Override
                            public void onSuccess() {
                                if (callback != null) {
                                    callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                }
                            }

                            @Override
                            public void onError(int code, String reason) {
                                if (callback != null) {
                                    callback.onInitMcsResult(code);
                                }
                            }
                        });
                return null;
            }

        }.execute();
    }

    public void initMcsRequestByToken(final Context applicationContext, final String customId,
                                      final String username,final String token, final OnInitMcsRequestCallback callback) {
        accountTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                JXImManager.Login.getInstance().loginByToken(customId, username, token,
                        new JXLoginCallback() {
                            @Override
                            public void onSuccess() {
                                if (callback != null) {
                                    callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                }
                            }

                            @Override
                            public void onError(int code, String reason) {
                                if (callback != null) {
                                    callback.onInitMcsResult(code);
                                }
                            }
                        });
                return null;
            }
        }.execute();
    }

    public void initMcsRequestByConfig(final Context applicationContext,
                                       final OnInitMcsRequestCallback callback) {
        accountTask = new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    sharedPreferences = applicationContext.getSharedPreferences(CONFIG,
                            Context.MODE_PRIVATE);
                    final Editor editor = sharedPreferences.edit();
                    boolean hasAccount = sharedPreferences.getBoolean(
                            JXImManager.getInstance().getAppKey() + SP_SHORT_TOKEN_FLAG, false);
                    boolean isSameUser = sharedPreferences.getString(JXImManager.getInstance().getAppKey() + SP_CID,"jxtest123").equals(getCustomerConfigCid());
                    if (hasAccount && isSameUser) {
                        if (JXImManager.Login.getInstance().isConnected()) {
                            if (callback != null) {
                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                            }
                        } else {
                            String encodeShortToken = sharedPreferences.getString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_SHORT_TOKEN, "");
                            String shortToken = getUsernameFromPrefixName(
                                    Base64.decodeToString(encodeShortToken));
                            JXImManager.Login.getInstance().loginWithShortToken(shortToken,
                                    new JXLoginCallback() {
                                        @Override
                                        public void onSuccess() {
                                            if (callback != null) {
                                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                            }
                                        }

                                        @Override
                                        public void onError(int code, String reason) {
                                            if (callback != null) {
                                                callback.onInitMcsResult(code);
                                            }
                                        }
                                    });
                        }
                    } else {
                        String shortToken = JXImManager.Login.getInstance()
                                .registerWithConfig();
                        if (!TextUtils.isEmpty(shortToken)) {
                            String encodeShortToken = Base64.encode(shortToken);
                            editor.putString(
                                    JXImManager.getInstance().getAppKey() + "_" + SP_SHORT_TOKEN,
                                    encodeShortToken);
                            editor.putString(JXImManager.getInstance().getAppKey() + SP_CID,
                                    getCustomerConfigCid());
                            editor.putBoolean(JXImManager.getInstance().getAppKey() + SP_SHORT_TOKEN_FLAG,
                                    true);
                            editor.commit();
                            JXImManager.Login.getInstance().loginWithShortToken(shortToken,
                                    new JXLoginCallback() {
                                        @Override
                                        public void onSuccess() {
                                            if (callback != null) {
                                                callback.onInitMcsResult(Mcs.MCS_USER_INIT_SUCCESS);
                                            }
                                        }

                                        @Override
                                        public void onError(int code, String reason) {
                                            if (callback != null) {
                                                callback.onInitMcsResult(code);
                                            }
                                        }
                                    });
                        } else {
                            editor.putBoolean(JXImManager.getInstance().getAppKey() + SP_SHORT_TOKEN_FLAG,
                                    false);
                            editor.commit();
                            if (callback != null) {
                                callback.onInitMcsResult(JXErrorCode.OTHER);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof JXException) {
                        if (callback != null) {
                            callback.onInitMcsResult(((JXException)e).getErrorCode());
                        }
                    } else {
                        if (callback != null) {
                            callback.onInitMcsResult(JXErrorCode.OTHER);
                        }
                    }
                }
                return null;
            }

        }.execute();
    }

    public void cancelTask(){
        if(accountTask != null){
            accountTask.cancel(true);
            accountTask = null;
        }
    }

    public static String getUsernameFromPrefixName(String prefixUsername) {
        return prefixUsername.substring(prefixUsername.indexOf("_") + 1);
    }

    private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz")
            .toCharArray();

    private static Random randGen = new Random();

    private static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }

    /**
     * @Description: 生成新的userName
     */
    public static String newUserName() {
        /*
         * 生成规则：3个随机字符+时间戳
         */
        return randomString(3) + System.currentTimeMillis();
    }

    /*
     * 用户生成mcs用户的密码
     */
    public static String randomString() {
        return randomString(8);
    }

    /**
     * Mcs用户端初始化的回调接口
     *
     */
    public interface OnInitMcsRequestCallback {

        /**
         * 初始化后的回调结果
         */
        public void onInitMcsResult(int code);
    }

    public String getCustomerConfigCid(){
        if (JXImManager.Config.getInstance().getCustomerConfig() != null && !TextUtils.isEmpty(JXImManager.Config.getInstance().getCustomerConfig().getCid())) {
            return JXImManager.Config.getInstance().getCustomerConfig().getCid();
        }
        return null;
    }
}
