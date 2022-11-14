package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Address;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by micker on 2017/7/16.
 */

public class AddressListCallback extends ApiBaseCallback<List<Address>> {

    @Override
    public List<Address> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("addrs");
        List<Address> msgs = JSON.parseArray(dataList, Address.class);
        return msgs;
    }
}
