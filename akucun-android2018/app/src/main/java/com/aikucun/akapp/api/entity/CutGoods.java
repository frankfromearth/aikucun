package com.aikucun.akapp.api.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ak123 on 2018/1/5.
 * 今日切货
 */

public class CutGoods implements Serializable {
    //     "begin": "2018-01-06 09:00:00",
//             "begintimestamp": 1515200400,
//             "buymodel": 0,
//             "comments": [],
//             "content": "【周五】12月29日晚19:00开播164款 飘蕾PEOLEO女装，全场一口价52-436元含代，本次活动2天，代购费10-40元，最晚于1月6日浙江温州发货。",
//             "corpid": "40289f1860780aa90160781af99f0007",
//             "end": "2018-01-08 00:00:00",
//             "endtimestamp": 1515340800,
//             "liveid": "8a9bf48c60bd07b10160c4da3d301aba",
//             "num": 0,
//             "pinpai": "40288002609be63f01609be7cf820007",
//             "pinpaiid": "40288002609be63f01609be7cf820007",
//             "pinpaiming": "修改测试1",
//             "pinpaiurl": "http://devdev.oss-cn-shanghai.aliyuncs.com/2017/12/Attach40288002609be63f01609be7cf5e0005.jpg",
//             "productcount": 0,
//             "skucount": 0,
//             "taskend": "",
//             "taskendtimestamp": 0,
//             "today": "",
//             "yugaoneirong": " 【周五】12月29日晚19:00开播164款 飘蕾PEOLEO女装，全场一口价52-436元含代，本次活动2天，飘蕾PEOLEO是浙江飘蕾服饰有限公司旗下原创女装品牌，精心设计和制作中显现女性天赋曲线和飒爽气质，表现女性个性独立的一面，从而使客人获得穿衣感受与心灵体验的一致性。",
//             "yugaotupian": "http://picture.akucun.com/ads/BBLLL1.jpg,http://picture.akucun.com/ads/BBLLL2.jpg,http://picture.akucun.com/ads/BBLLL3.jpg,http://picture.akucun.com/ads/BBLLL4.jpg,http://picture.akucun.com/ads/BBLLL5.jpg,http://picture.akucun.com/ads/BBLLL6.jpg,http://picture.akucun.com/ads/BBLLL7.jpg,http://picture.akucun.com/ads/BBLLL8.jpg,http://picture.akucun.com/ads/BBLLL9.jpg"
    private String begin;
    private long begintimestamp;
    private int buymodel;
    private List<Comment> comments;
    private String content;
    private String corpid;
    private String end;
    private long endtimestamp;
    private String liveid;
    private int num;
    private String pinpai;
    private String pinpaiid;
    private String pinpaiming;
    private String pinpaiurl;
    private int productcount;
    private int skucount;
    private String taskend;
    private int taskendtimestamp;
    private String today;
    private String yugaoneirong;
    private String yugaotupian;

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public long getBegintimestamp() {
        return begintimestamp;
    }

    public void setBegintimestamp(long begintimestamp) {
        this.begintimestamp = begintimestamp;
    }

    public int getBuymodel() {
        return buymodel;
    }

    public void setBuymodel(int buymodel) {
        this.buymodel = buymodel;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getEndtimestamp() {
        return endtimestamp;
    }

    public void setEndtimestamp(long endtimestamp) {
        this.endtimestamp = endtimestamp;
    }

    public String getLiveid() {
        return liveid;
    }

    public void setLiveid(String liveid) {
        this.liveid = liveid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPinpai() {
        return pinpai;
    }

    public void setPinpai(String pinpai) {
        this.pinpai = pinpai;
    }

    public String getPinpaiid() {
        return pinpaiid;
    }

    public void setPinpaiid(String pinpaiid) {
        this.pinpaiid = pinpaiid;
    }

    public String getPinpaiming() {
        return pinpaiming;
    }

    public void setPinpaiming(String pinpaiming) {
        this.pinpaiming = pinpaiming;
    }

    public String getPinpaiurl() {
        return pinpaiurl;
    }

    public void setPinpaiurl(String pinpaiurl) {
        this.pinpaiurl = pinpaiurl;
    }

    public int getProductcount() {
        return productcount;
    }

    public void setProductcount(int productcount) {
        this.productcount = productcount;
    }

    public int getSkucount() {
        return skucount;
    }

    public void setSkucount(int skucount) {
        this.skucount = skucount;
    }

    public String getTaskend() {
        return taskend;
    }

    public void setTaskend(String taskend) {
        this.taskend = taskend;
    }

    public int getTaskendtimestamp() {
        return taskendtimestamp;
    }

    public void setTaskendtimestamp(int taskendtimestamp) {
        this.taskendtimestamp = taskendtimestamp;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getYugaoneirong() {
        return yugaoneirong;
    }

    public void setYugaoneirong(String yugaoneirong) {
        this.yugaoneirong = yugaoneirong;
    }

    public String getYugaotupian() {
        return yugaotupian;
    }

    public void setYugaotupian(String yugaotupian) {
        this.yugaotupian = yugaotupian;
    }



    public static Product fromCutGoods(CutGoods info) {
        Product product = new Product();
        product.setContent(info.getContent());
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
        product.setProductType(2);

        product.setComments(info.getComments());

        return product;
    }
}
