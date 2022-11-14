//package com.aikucun.akapp.api.callback;
//
//import com.aikucun.akapp.api.entity.LiveInfo;
//import com.aikucun.akapp.api.response.LiveStateResp;
//import com.aikucun.akapp.storage.LiveManager;
//import com.aikucun.akapp.storage.ProductManager;
//import com.aikucun.akapp.utils.StringUtils;
//import com.alibaba.fastjson.JSON;
//
//import java.util.Date;
//import java.util.List;
//
//import okhttp3.Call;
//
///**
// * Created by jarry on 2017/6/10.
// */
//
//public class LiveStateCallback extends ApiBaseCallback<LiveStateResp>
//{
//    @Override
//    public void onApiSuccess(LiveStateResp liveStateResp, Call call, ApiResponse jsonResponse)
//    {
//
//        LiveManager.getInstance().state = liveStateResp.getState();
//        LiveManager.getInstance().liveStateResp = liveStateResp;
//
//        long nowTime = new Date().getTime() / 1000;
//        StringBuilder builder = new StringBuilder();
//        int index = 0;
//        for (LiveInfo liveInfo:liveStateResp.getLiveinfos()) {
//            if (liveInfo.getBegintimestamp() < nowTime && StringUtils.isEmpty(ProductManager.getInstance().getLiveId())) {
//                ProductManager.getInstance().setLiveId(liveInfo.getLiveid());;
//            }
//            if (index++ > 0) {
//                builder.append(",");
//            }
//            builder.append(liveInfo.getPinpai());
//        }
//        LiveManager.getInstance().setLiveLiveIds(builder.toString());
//
//    }
//
//    @Override
//    public LiveStateResp parseResponse(ApiResponse responseData) throws Exception
//    {
//        String jsonString = responseData.getJsonObject().toJSONString();
//        LiveStateResp response = JSON.parseObject(jsonString, LiveStateResp.class);
//
//        // 下架的商品列表处理
//        if (response.getProducts().size() > 0)
//        {
//            List<String> products = response.getProducts();
//            for (String productId : products)
//            {
//                ProductManager.getInstance().deleteProduct(productId);
//            }
//        }
//
//        return response;
//    }
//}
