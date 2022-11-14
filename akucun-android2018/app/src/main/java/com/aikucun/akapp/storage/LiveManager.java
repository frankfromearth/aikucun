//package com.aikucun.akapp.storage;
//
//import com.aikucun.akapp.AppConfig;
//import com.aikucun.akapp.AppContext;
//import com.aikucun.akapp.api.entity.LiveControl;
//import com.aikucun.akapp.api.entity.LiveInfo;
//import com.aikucun.akapp.api.entity.Product;
//import com.aikucun.akapp.api.response.LiveStateResp;
//import com.aikucun.akapp.utils.StringUtils;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Random;
//
///**
// * Created by jarry on 2017/6/10.
// */
//
//public class LiveManager
//{
//    public int state;
//
//    public long updateTime;
//
//    public LiveControl liveControl;
//    public LiveControl liveOver;
//
//    public String notice;
//    public LiveStateResp liveStateResp;
//
//
//    //过期商品处理
//    private String liveLiveIds;
//    private String overLiveIds;
//
//
//    private static LiveManager _instance;
//
//    public static LiveManager getInstance()
//    {
//        if (_instance == null)
//        {
//            _instance = new LiveManager();
//        }
//        return _instance;
//    }
//
//    public static long periodTime()
//    {
//        int state = getInstance().state;
//        LiveControl control = null;
//
//        if (state == 1)
//        {
//            control = getInstance().liveControl;
//        }
//        else
//        {
//            control = getInstance().liveOver;
//        }
//
//        if (null == control)
//        {
//            return 30 * 1000;
//        }
//
//        if (control.getFlag() == 1 && control.getPeriod() > 0)
//        {
//            return control.getPeriod() * 1000;
//        }
//
//        Random rand = new Random();
//        long time = rand.nextInt(control.getMax());
//        return (control.getMin() + time) * 1000;
//    }
//
//    public String[] pinpaiAllNames()
//    {
//
//        List<String> lst = new LinkedList<String>();
//
//        lst.add("全部");
//        if (liveStateResp != null)
//        {
//            for (int i = 0; i < liveStateResp.getLiveinfos().size(); i++)
//            {
//                LiveInfo liveInfo = liveStateResp.getLiveinfos().get(i);
//                lst.add(liveInfo.getPinpaiming());
//            }
//        }
//
//        String[] pinpai = (String[]) lst.toArray(new String[lst.size()]);
//        return pinpai;
//    }
//
//    public String[] pinpaiNames()
//    {
//
//        List<String> lst = new LinkedList<String>();
//
//        if (liveStateResp != null)
//        {
//            for (int i = 0; i < liveStateResp.getLiveinfos().size(); i++)
//            {
//                LiveInfo liveInfo = liveStateResp.getLiveinfos().get(i);
//                lst.add(liveInfo.getPinpaiming());
//            }
//        }
//
//        String[] pinpai = (String[]) lst.toArray(new String[lst.size()]);
//        return pinpai;
//    }
//
//    public LiveInfo getPinpai(int index)
//    {
//        if (null != liveStateResp && null != liveStateResp.getLiveinfos() && index >= 0 && index
//                < liveStateResp.getLiveinfos().size())
//        {
//            return liveStateResp.getLiveinfos().get(index);
//        }
//
//        return null;
//    }
//
//    public String getNotice(String liveId)
//    {
//        if (liveStateResp != null)
//        {
//            String notice = liveStateResp.getNotice(liveId);
//            if (!StringUtils.isEmpty(notice))
//            {
//                return "公告：" + notice;
//            }
//        }
//        return "";
//    }
//
//    public ArrayList<Product> getYugaoProducts(String liveId)
//    {
//        if (liveStateResp != null)
//        {
//            return liveStateResp.getYugaoProducts(liveId);
//        }
//        return new ArrayList<Product>();
//    }
//
//    //过期商品处理
//
//    private void initLives()
//    {
//        this.liveLiveIds = AppContext.get(AppConfig.PREF_KEY_LIVE_LIVEIDS, "");
//        this.overLiveIds = AppContext.get(AppConfig.PREF_KEY_OVER_LIVEIDS, "");
//    }
//
//
//    public String getLiveLiveIds()
//    {
//        return liveLiveIds;
//    }
//
//    public void setLiveLiveIds(String liveLiveIds)
//    {
//
//        if (!StringUtils.isEmpty(this.liveLiveIds))
//        {
//
//            if (this.liveLiveIds.equals(liveLiveIds))
//            {
//                return;
//            }
//            StringBuilder builder = new StringBuilder();
//
//            String lives[] = this.liveLiveIds.split(",");
//
//            int index = 0;
//            for (String liveId : lives)
//            {
//
//                if (liveLiveIds.indexOf(liveId) == -1)
//                {
//
//                    if (index > 0)
//                    {
//                        builder.append(",");
//                    }
//                    builder.append(liveId);
//                    index++;
//                }
//            }
//            setOverPinpaiIds(builder.toString());
//
//        }
//
//        this.liveLiveIds = liveLiveIds;
//        AppContext.set(AppConfig.PREF_KEY_LIVE_LIVEIDS, StringUtils.isEmpty(liveLiveIds) ? "" :
//                liveLiveIds);
//    }
//
//    public String getOverLiveIds()
//    {
//        return overLiveIds;
//    }
//
//    public void setOverPinpaiIds(String overLiveIds)
//    {
//        this.overLiveIds = overLiveIds;
//        AppContext.set(AppConfig.PREF_KEY_OVER_LIVEIDS, StringUtils.isEmpty(overLiveIds) ? "" :
//                overLiveIds);
//    }
//
//    public LiveInfo getLiveById(String liveId)
//    {
//        if (liveStateResp == null)
//        {
//            return null;
//        }
//        for (LiveInfo item : liveStateResp.getLiveinfos())
//        {
//            if (item.getLiveid().equalsIgnoreCase(liveId))
//            {
//                return item;
//            }
//        }
//        return null;
//    }
//}
