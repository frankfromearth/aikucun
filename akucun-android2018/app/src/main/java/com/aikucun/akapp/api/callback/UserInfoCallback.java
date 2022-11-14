package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.entity.UserInfo;
import com.alibaba.fastjson.JSON;

/**
 * 用户信息返回数据处理
 * Created by jarry on 16/5/12.
 */
public abstract class UserInfoCallback extends ApiBaseCallback<UserInfo> {

    @Override
    public UserInfo parseResponse(ApiResponse responseData) throws Exception {
        String userInfo = responseData.getJsonObject().getString("userinfo");
        UserInfo user = JSON.parseObject(userInfo, UserInfo.class);
        AppContext.getInstance().setUserInfo(user);
        return user;
    }
}
