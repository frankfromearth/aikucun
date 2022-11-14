package com.aikucun.akapp.storage;

import android.text.TextUtils;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.R;
import com.aikucun.akapp.api.entity.LiveInfo;

import java.util.List;

/**
 * Created by ak123 on 2018/1/25.
 */

public class LiveInfosManager {

    private static LiveInfosManager _instance;
    private List<LiveInfo> liveInfos;


    public static LiveInfosManager getInstance() {
        if (_instance == null) {
            _instance = new LiveInfosManager();
        }
        return _instance;
    }

    public void setLiveInfos(List<LiveInfo> _liveInfos) {
        liveInfos = _liveInfos;
    }

    public List<LiveInfo> getLiveInfos() {
        return liveInfos;
    }

    /**
     * 获取预告通知内容
     *
     * @param liveId
     * @return
     */
    public String getNotice(String liveId) {
        if (TextUtils.isEmpty(liveId) || liveInfos == null) return "";
        String notice = "";
        for (int i = 0, size = liveInfos.size(); i < size; i++) {
            if (liveId.equals(liveInfos.get(i).getLiveid())) {
                notice = AppContext.getInstance().getString(R.string.public_notice) + liveInfos.get(i).getContent();
                break;
            }
        }
        return notice;
    }

    /**
     * 获取某个活动
     *
     * @param liveId
     * @return
     */
    public LiveInfo getLiveInfo(String liveId) {
        if (TextUtils.isEmpty(liveId) || liveInfos == null) return null;
        LiveInfo liveInfo = null;
        for (int i = 0, size = liveInfos.size(); i < size; i++) {
            if (liveId.equals(liveInfos.get(i).getLiveid())) {
                liveInfo = liveInfos.get(i);
                break;
            }
        }
        return liveInfo;
    }

    /**
     * 获取某个品牌
     *
     * @param index
     * @return
     */
    public LiveInfo getPinpai(int index) {
        if (null != liveInfos && null != liveInfos && index >= 0 && index
                < liveInfos.size()) {
            return liveInfos.get(index);
        }

        return null;
    }
}
