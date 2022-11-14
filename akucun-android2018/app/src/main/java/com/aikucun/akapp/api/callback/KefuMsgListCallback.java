package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.KefuMsgItem;
import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by jarry on 17/6/30.
 */

public class KefuMsgListCallback extends ApiBaseCallback<List<KefuMsgItem>>
{

    @Override
    public List<KefuMsgItem> parseResponse(ApiResponse responseData) throws Exception
    {
        String dataList = responseData.getJsonObject().getString("msgs");
        List<KefuMsgItem> msgs = JSON.parseArray(dataList, KefuMsgItem.class);

        Collections.sort(msgs, new Comparator<KefuMsgItem>() {
            @Override
            public int compare(KefuMsgItem o1, KefuMsgItem o2) {
                return o1.getXuhao()-o2.getXuhao();
            }
        });
        return msgs;
    }
}
