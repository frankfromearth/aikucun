package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.response.SynCommentsResp;
import com.aikucun.akapp.storage.ProductManager;
import com.aikucun.akapp.utils.SCLog;
import com.alibaba.fastjson.JSON;

import okhttp3.Call;

/**
 * 同步商品返回数据处理
 * Created by jarry on 2017/6/7.
 */
public class SyncCommentsCallback extends ApiBaseCallback<SynCommentsResp>
{
    @Override
    public void onApiSuccess(SynCommentsResp synCommentsResp, Call call, ApiResponse jsonResponse)
    {
        SCLog.log("Sync Time : " + synCommentsResp.lastupdate + "\nProduct Count : " +
                synCommentsResp.comments.size() + "\nhasMore : " + synCommentsResp.hasmore);
    }

    @Override
    public SynCommentsResp parseResponse(ApiResponse responseData) throws Exception
    {
        String jsonString = responseData.getJsonObject().toJSONString();
        SynCommentsResp response = JSON.parseObject(jsonString, SynCommentsResp.class);

        // Save
        if (response.lastupdate > ProductManager.getInstance().commentUpdate) {
            ProductManager.getInstance().commentUpdate = response.lastupdate;
        }
        ProductManager.getInstance().insertComments(response.comments);
        return response;
    }
}
