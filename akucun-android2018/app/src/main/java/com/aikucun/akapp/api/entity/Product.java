package com.aikucun.akapp.api.entity;


import android.text.TextUtils;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.activity.ProductForwardSettingActivity;
import com.aikucun.akapp.storage.ProductDB;
import com.aikucun.akapp.storage.ProductManager;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品模型
 * Created by jarry on 2017/6/7.
 */

public class Product implements Serializable {
    private String id;

    private String name;
    private String pinpai;
    private String pinpaiurl;
    private String danwei;
    private String desc;
    //预告通知
    private String content;
    private String pinpaiid;
    private String liveid;
    private String corpid;

    private String kuanhao;
    private String jijie;

    private long shangjiashuzishijian;

    private String tupianURL;

    private int xuhao;
    private int lastxuhao;
    private int diaopaijia;
    private int xiaoshoujia;
    private int jiesuanjia;

    private List<ProductSKU> skus;
    private List<Comment> comments;

    private int productType = 0;


    private int forward;
    private int follow;
    //是否为虚拟商品1：是
    private int isvirtual;
    /**
     * 商品模型中增加字段：salestype
     0 :  无促销活动
     1 ：代购费翻倍活动
     */
    private int salestype;
    public int getIsvirtual() {
        return isvirtual;
    }

    public void setIsvirtual(int isvirtual) {
        this.isvirtual = isvirtual;
    }

    public int getSalestype() {
        return salestype;
    }

    public void setSalestype(int salestype) {
        this.salestype = salestype;
    }

    public static List<String> getImageUrls(Product p) {
        String[] arrayStr = p.tupianURL.replaceAll(" ", "").split(",");
        List<String> imagesList = java.util.Arrays.asList(arrayStr);
        return imagesList;
    }

    public static Product yy_modelWithJSON(String json) {
        try {
            Product p = JSONObject.parseObject(json, Product.class);
            return p;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public void clearSKUState() {
        for (ProductSKU sku : skus) {
            sku.setSelected(false);
        }
    }

    public void updateSKU(ProductSKU sku) {
        for (ProductSKU item : skus) {
            if (TextUtils.equals(item.getId(), sku.getId())) {
                item.setShuliang(sku.getShuliang());
            }
        }
    }

    public int getMaxSkuLength() {
        int maxLength = 0;
        for (ProductSKU sku : getSkus()) {
            sku.setSelected(false);
            if (maxLength < sku.getChima().length()) {
                maxLength = sku.getChima().length();
            }
        }
        return maxLength;
    }

    public String weixinDesc() {
        if (0 != productType)
            return this.desc;
        StringBuilder skuDesc = new StringBuilder("尺码 ");
        int i = 0;
//        boolean timeCheck = (new Date().getTime() / 1000 - shangjiashuzishijian) >= (7200);
        boolean timeCheck;
        //缺货转发设置
        int type = AppContext.get(ProductForwardSettingActivity.FORWARD_OUTSTOCK_KEY, 1);
        if (type == 1) {
            //不转发
            timeCheck = false;
        } else if (type == 2) {
            //始终转发
            timeCheck = true;
        } else if (type == 3) {
            //一小时
            timeCheck = (new Date().getTime() / 1000 - shangjiashuzishijian) >= (3600);
        } else {
            //两小时
            timeCheck = (new Date().getTime() / 1000 - shangjiashuzishijian) >= (7200);
        }

        for (ProductSKU sku : skus) {
            if ((!timeCheck && sku.getShuliang() <= 0)) {
                continue;
            }
            if (i > 0) {
                skuDesc.append(" ");
            }
            skuDesc.append(sku.getChima());
            i++;
        }
        StringBuilder newDesc = new StringBuilder();
        String desc = this.desc;
        int index = desc.indexOf("\n");
        while (index > 0) {
            String temp = desc.substring(0, index);
            if (temp.startsWith("尺码")) {
                newDesc.append(skuDesc);
            } else {
                newDesc.append(temp);
            }
            newDesc.append("\n");
            desc = desc.substring(index + 1);
            index = desc.indexOf("\n");
        }
        newDesc.append(desc);
        newDesc.append("\n");
        return newDesc.toString();
    }

    public boolean isQuehuo() {
        int count = 0;
        for (ProductSKU sku : skus) {
            count += sku.getShuliang();
        }
        return (count <= 0);
    }

    public boolean enableForward() {
        boolean timeCheck;
        //缺货转发设置
        int type = AppContext.get(ProductForwardSettingActivity.FORWARD_OUTSTOCK_KEY, 1);
        if (type == 1) {
            //不转发
            timeCheck = false;
        } else if (type == 2) {
            //始终转发
            timeCheck = true;
        } else if (type == 3) {
            //一小时
            timeCheck = (new Date().getTime() / 1000 - shangjiashuzishijian) >= (3600);
        } else {
            //两小时
            timeCheck = (new Date().getTime() / 1000 - shangjiashuzishijian) >= (7200);
        }


        return timeCheck || !isQuehuo();

    }

    public boolean shouldUpdateSKU() {
        int count = 0;
        for (ProductSKU sku : skus) {
            if (sku.getShuliang() < 5)
                return true;
        }
        return false;
    }

    public void setTupianURL(String tupianURL) {
        this.tupianURL = tupianURL;

    }

    public long getShangjiashuzishijian() {
        return shangjiashuzishijian;
    }

    public void setShangjiashuzishijian(long shangjiashuzishijian) {
        this.shangjiashuzishijian = shangjiashuzishijian;
    }

    public String getTupianURL() {
        return tupianURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinpai() {
        return pinpai;
    }

    public void setPinpai(String pinpai) {
        this.pinpai = pinpai;
    }

    public String getPinpaiurl() {
        return pinpaiurl;
    }

    public void setPinpaiurl(String pinpaiurl) {
        this.pinpaiurl = pinpaiurl;
    }

    public String getDanwei() {
        return danwei;
    }

    public void setDanwei(String danwei) {
        this.danwei = danwei;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getKuanhao() {
        return kuanhao;
    }

    public void setKuanhao(String kuanhao) {
        this.kuanhao = kuanhao;
    }

    public String getJijie() {
        return jijie;
    }

    public void setJijie(String jijie) {
        this.jijie = jijie;
    }

    public int getXuhao() {
        return xuhao;
    }

    public void setXuhao(int xuhao) {
        this.xuhao = xuhao;
    }

    public int getLastxuhao() {
        return lastxuhao;
    }

    public void setLastxuhao(int lastxuhao) {
        this.lastxuhao = lastxuhao;
    }

    public int getDiaopaijia() {
        return diaopaijia;
    }

    public void setDiaopaijia(int diaopaijia) {
        this.diaopaijia = diaopaijia;
    }

    public int getXiaoshoujia() {
        return xiaoshoujia;
    }

    public void setXiaoshoujia(int xiaoshoujia) {
        this.xiaoshoujia = xiaoshoujia;
    }

    public int getJiesuanjia() {
        return jiesuanjia;
    }

    public void setJiesuanjia(int jiesuanjia) {
        this.jiesuanjia = jiesuanjia;
    }

    public List<ProductSKU> getSkus() {
        return skus;
    }

    public void setSkus(List<ProductSKU> skus) {
        this.skus = skus;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getPinpaiid() {
        return pinpaiid;
    }

    public void setPinpaiid(String pinpaiid) {
        this.pinpaiid = pinpaiid;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public int getProductType() {
        return productType;
    }

    public void setProductType(int productType) {
        this.productType = productType;
    }

    public boolean hasBegun() {
        return begintimestamp * 1000 < new Date().getTime();
    }

    public boolean hasNoSku() {
        return (skus == null || skus.size() == 0);
    }

    public int getForward() {
        return forward;
    }

    public void setForward(int forward) {
        this.forward = forward;
    }

    public int getFollow() {
        return follow;
    }

    public void setFollow(int follow) {
        this.follow = follow;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void updateSKU(List<ProductSKU> skus) {
        if (null == skus)
            return;

        //仅更新数量
        for (ProductSKU produckSKU : this.skus) {
            for (ProductSKU tmpSKU : skus) {
                if (tmpSKU.getId().equalsIgnoreCase(produckSKU.getId())) {
                    produckSKU.setShuliang(tmpSKU.getShuliang());
                    continue;
                }
            }
        }

        ProductDB productDB = ProductManager.getInstance().getProductById(getId());
        if (null != productDB) {
            Product product = productDB.productModel();
            ProductManager.getInstance().updateProduct(product);
        }
    }


    //coments

    public void addComment(Comment comment) {

        if (null == comment || isCommentEsixt(comment))
            return;

        getComments().add(comment);
        ProductManager.getInstance().updateProduct(this);
    }

    public void removeComment(Comment comment) {
        if (null == comment || !isCommentEsixt(comment))
            return;
        boolean didExist = false;
        for (int i = getComments().size() - 1; i >= 0; i--) {

            Comment item = getComments().get(i);
            if (item.getId().equalsIgnoreCase(comment.getId())) {
                comments.remove(i);
                didExist = true;
            }
        }

        ProductManager.getInstance().updateProduct(this);
    }

    public boolean isCommentEsixt(Comment comment) {

        if (null == comments) {
            return false;
        }
        for (Comment item : comments) {
            if (item.getId().equalsIgnoreCase(comment.getId())) {
                return true;
            }
        }
        return false;
    }


    //live info , trailer info

    private long begintimestamp;
    private long endtimestamp;

    public long getBegintimestamp() {
        return begintimestamp;
    }

    public void setBegintimestamp(long begintimestamp) {
        this.begintimestamp = begintimestamp;
    }

    public long getEndtimestamp() {
        return endtimestamp;
    }

    public void setEndtimestamp(long endtimestamp) {
        this.endtimestamp = endtimestamp;
    }

    public static Product fromTrainer(Trailer trailer) {
        Product product = new Product();

        product.setName(trailer.getPinpaiming());
        product.setPinpai(trailer.getPinpaiming());
        product.setPinpaiid(trailer.getPinpaiid());
        product.setPinpaiurl(trailer.getPinpaiurl());
        product.setTupianURL(trailer.getYugaotupian());
        product.setDesc(trailer.getYugaoneirong());
        product.setShangjiashuzishijian(trailer.getBegintimestamp());
        product.setBegintimestamp(trailer.getBegintimestamp());
        product.setEndtimestamp(trailer.getEndtimestamp());
        product.productType = 1;
        return product;
    }

    public static Product fromLiveInfo(LiveInfo info) {
        Product product = new Product();

        product.setName(info.getPinpaiming());
        product.setPinpai(info.getPinpaiming());
        product.setPinpaiid(info.getPinpai());
        product.setPinpaiurl(info.getPinpaiurl());
        product.setTupianURL(info.getYugaotupian());
        product.setDesc(info.getYugaoneirong());
        product.setLiveid(info.getLiveid());
        product.setShangjiashuzishijian(info.getBegintimestamp());
        product.setBegintimestamp(info.getBegintimestamp());
        product.setEndtimestamp(info.getEndtimestamp());
        product.productType = 2;

        product.setComments(info.getComments());

        return product;
    }

}
