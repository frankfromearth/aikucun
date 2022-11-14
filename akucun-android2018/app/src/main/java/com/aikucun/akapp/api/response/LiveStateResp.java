package com.aikucun.akapp.api.response;

import com.aikucun.akapp.api.entity.LiveControl;
import com.aikucun.akapp.api.entity.LiveInfo;
import com.aikucun.akapp.api.entity.Notice;
import com.aikucun.akapp.api.entity.Product;
import com.aikucun.akapp.api.entity.Trailer;
import com.aikucun.akapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarry on 2017/6/10.
 */
public class LiveStateResp
{
    private int state;
    private long lastupdate;

    private LiveControl living;
    private LiveControl liveover;
    private List<LiveInfo> liveinfos;
    private Notice notice;
    private List<Trailer> trailers;

    private List<String> products; // 下架的商品ID列表

    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        this.state = state;
    }

    public long getLastupdate()
    {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate)
    {
        this.lastupdate = lastupdate;
    }

    public LiveControl getLiving()
    {
        return living;
    }

    public void setLiving(LiveControl living)
    {
        this.living = living;
    }

    public LiveControl getLiveover()
    {
        return liveover;
    }

    public void setLiveover(LiveControl liveover)
    {
        this.liveover = liveover;
    }

    public List<LiveInfo> getLiveinfos()
    {
        return liveinfos;
    }

    public void setLiveinfos(List<LiveInfo> liveinfos)
    {
        this.liveinfos = liveinfos;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    public String getNotice(String liveId) {

        if (StringUtils.isEmpty(liveId)) {
            if (notice != null) {
                return notice.getContent();
            }
        } else {
            LiveInfo liveInfo = getLiveNotice(liveId);
            if (null != liveInfo) {
                return liveInfo.getContent();
            }
        }

        return "";
    }


    public LiveInfo getLiveNotice(String liveId) {
        for (LiveInfo liveInfo: getLiveinfos()) {
            if (liveInfo.getLiveid().equalsIgnoreCase(liveId))
                return liveInfo;
        }
        return null;
    }

    public ArrayList<Product> getYugaoProducts(String liveId) {
        ArrayList<Product> products = new ArrayList<>();
        if (null != getTrailers()) {
            if (StringUtils.isEmpty(liveId)) {
                for (int i = 0;  i < getTrailers().size(); i++) {
                    products.add(Product.fromTrainer(getTrailers().get(i)));
                }
            } else {
                for (int i = 0;  i < getTrailers().size(); i++) {
                    Trailer trainer = getTrailers().get(i);
                    Product product = null;
                    if (trainer.getLiveid().equalsIgnoreCase(liveId)) {
                        product = Product.fromTrainer(trainer);
                        products.add(product);
                    }
                }
            }

        }

        if (null != getLiveinfos()) {
            if (StringUtils.isEmpty(liveId)) {
                for (int i = 0;  i < getLiveinfos().size(); i++) {
                    products.add(Product.fromLiveInfo(getLiveinfos().get(i)));
                }
            } else {
                for (int i = 0;  i < getLiveinfos().size(); i++) {
                    LiveInfo item = getLiveinfos().get(i);
                    Product product = null;
                    if (item.getLiveid().equalsIgnoreCase(liveId)) {
                        product = Product.fromLiveInfo(item);
                        products.add(product);
                    }
                }
            }
        }
        return products;
    }

    public List<String> getProducts()
    {
        return products;
    }

    public void setProducts(List<String> products)
    {
        this.products = products;
    }
}
