package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Message;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class MsgListCallback extends ApiBaseCallback<List<Message>>
{

    @Override
    public List<Message> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("msg");
        List<Message> msgs = JSON.parseArray(dataList, Message.class);
        return msgs;
    }
}
