package com.aikucun.akapp.storage;

import android.text.TextUtils;

import com.aikucun.akapp.api.entity.CutGoods;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by ak123 on 2018/2/5.
 */

public class ExplosionGoodsManager {

    private static ExplosionGoodsManager mExplosionGoodsManager;

    public static ExplosionGoodsManager getInstance() {
        if (mExplosionGoodsManager == null) mExplosionGoodsManager = new ExplosionGoodsManager();
        return mExplosionGoodsManager;
    }

    private List<CutGoods> cutGoodsList;

    public List<CutGoods> getCutGoodsList() {
        return cutGoodsList;
    }

    public void setCutGoodsList(List<CutGoods> cutGoodsList) {
        this.cutGoodsList = cutGoodsList;
    }

    public CutGoods getLiveInfo(String liveId) {
        if (TextUtils.isEmpty(liveId) || cutGoodsList == null) return null;
        CutGoods liveInfo = null;
        for (int i = 0, size = cutGoodsList.size(); i < size; i++) {
            if (liveId.equals(cutGoodsList.get(i).getLiveid())) {
                liveInfo = cutGoodsList.get(i);
                break;
            }
        }
        return liveInfo;
    }

    public List<LiveInfo> toLiveInfos() {
        if (cutGoodsList == null) {
            return null;
        }
        String json = JSON.toJSONString(cutGoodsList);
        try {
            List<LiveInfo> liveInfos = JSONObject.parseArray(json, LiveInfo.class);
            return liveInfos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
