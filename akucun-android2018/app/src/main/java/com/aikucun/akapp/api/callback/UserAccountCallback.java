package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.api.entity.UserAccount;
import com.aikucun.akapp.api.entity.UserInfo;
import com.alibaba.fastjson.JSON;

/**
 * Created by jarry on 2017/6/22.
 */

public class UserAccountCallback extends ApiBaseCallback<UserAccount>
{
    @Override
    public UserAccount parseResponse(ApiResponse responseData) throws Exception
    {
        String accountInfo = responseData.getJsonObject().getString("account");
        UserAccount account = JSON.parseObject(accountInfo, UserAccount.class);
        // FIXME: 2018/1/4 
        UserInfo userInfo = AppContext.getInstance().getUserInfo();
//        if (userInfo != null) {
//            userInfo.setAccount(account);
//        }

        return account;
    }
}
