package com.aikucun.akapp.api.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ak123 on 2017/11/16.
 * 发现模型
 */

public class Discover implements Serializable {

    public String avatar;
    public String content;
    public String id;
    public String title;
    public long createtime;
    public String imagesUrl;
    public int imageWidth;
    public int imageHeight;
    /**
     * 0是图文， 1是视频
     */
    public int type;
    public String userid;
    public String username;
    public String videoId;
    public String videoUrl;
    /**
     * 评论列表
     */
    public ArrayList<Reply> comments = new ArrayList<>();


    public static List<String> getImageUrls(Discover discover) {
        String[] arrayStr = discover.imagesUrl.replaceAll(" ", "").split(",");
        List<String> imagesList = java.util.Arrays.asList(arrayStr);
        return imagesList;
    }

}
