/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui;

/**
 * @Description: TODO
 * @date 2015-9-7 下午7:26:07
 */
public class JXConstants {

    public static final String EXTRA_INTENT_TYPE = "intentType";
    public static final String EXTRA_CHAT_KEY = "EXTRA_CHAT_KEY";
    public static final String EXTRA_CHAT_TITLE_KEY = "EXTRA_CHAT_TITLE_KEY";
    public static final String EXTRA_MSG_BOX_KEY = "EXTRA_MSG_BOX_KEY";
    //public static final String FILE_PROVIDER_AUTHORITIRS = "com.akucun.akfileprovider";
    public static final String FILE_PROVIDER_AUTHORITIRS = JXConstants.APP_PACKAGE+".akfileprovider";
    //发送者头像地址
    public static String SEND_MSG_USER_AVATAR_URL = "";
    //APP包名
    public static String APP_PACKAGE = "com.aikucun.akapp";

    private static String providerName ="com.aikucun.akapp";
    public static void setProviderName(String name){
        providerName = name;
    }
    public static String getProviderName(){
        return providerName +".akfileprovider";
    }
}
