package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.InviteInfo;
import com.alibaba.fastjson.JSON;

/**
 * Created by ak123 on 2018/1/19.
 * 邀请信息返回
 */

public class InviteInfoCallBack extends ApiBaseCallback<InviteInfo> {
    @Override
    public InviteInfo parseResponse(ApiResponse responseData) throws Exception {
        InviteInfo mInviteInfo = JSON.parseObject(responseData.getJsonObject().toString(), InviteInfo.class);
        return mInviteInfo;
    }
}
