/**
 * Copyright (C) 2015-2016 Guangzhou Xunhong Network Technology Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jxccp.ui.entities;

import java.io.Serializable;

public class JXCommodity implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;

    /**
     * 商品标题
     */
    public String title;

    /**
     * 图片路径
     */
    public String imgUrl;

    /**
     * 商品显示内容
     */
    public String content;

    /**
     * 商品链接
     */
    public String url;
    
    public JXCommodity(){
        
    }

    public JXCommodity(String title, String imgUrl, String content, String url) {
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
