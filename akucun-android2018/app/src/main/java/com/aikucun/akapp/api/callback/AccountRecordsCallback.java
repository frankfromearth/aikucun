package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.AccountRecord;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 2017/6/22.
 */

public class AccountRecordsCallback extends ApiBaseCallback<List<AccountRecord>>
{

    @Override
    public List<AccountRecord> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("records");
        List<AccountRecord> records = JSON.parseArray(dataList, AccountRecord.class);
        return records;
    }
}
