package com.aikucun.akapp.api.callback;

import com.aikucun.akapp.api.entity.Trailer;
import com.alibaba.fastjson.JSON;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ak123 on 2018/1/5.
 * 预告
 */

public class TrailerCallBack extends ApiBaseCallback<List<Trailer>> {

    @Override
    public List<Trailer> parseResponse(ApiResponse responseData) throws Exception {
        String dataList = responseData.getJsonObject().getString("list");
        List<Trailer> Trailers = JSON.parseArray(dataList, Trailer.class);

        Collections.sort(Trailers, new Comparator<Trailer>() {
            @Override
            public int compare(Trailer o1, Trailer o2) {
                long t1 = o1.getBegintimestamp();
                long t2 = o2.getBegintimestamp();
                if(t1 > t2) {
                    return 1;
                }
                else if (t1 < t2) {
                    return -1;
                }
                return 0;
            }
        });

        return Trailers;
    }
}