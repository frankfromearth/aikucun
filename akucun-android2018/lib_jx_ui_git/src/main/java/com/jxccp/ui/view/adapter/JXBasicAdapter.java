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

package com.jxccp.ui.view.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @Description: Adapter基类
 * @date 2015-8-31 下午1:09:14
 */
public abstract class JXBasicAdapter<T, Q> extends BaseAdapter {

    public Context context;

    public List<T> list;//

    public Q view; // 这里不一定是ListView,比如GridView,CustomListView

    public JXBasicAdapter(Context context, List<T> list, Q view) {
        this.context = context;
        this.list = list;
        this.view = view;
    }

    public JXBasicAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
