package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Bill;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by ak123 on 2018/1/9.
 */

public class BillListCallback extends ApiBaseCallback<List<Bill>> {
    @Override
    public List<Bill> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("data");
        List<Bill> list = JSON.parseArray(dataList, Bill.class);
        return list;
    }

}
