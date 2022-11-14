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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jxccp.ui.R;

import java.util.List;

public class JXChatFunctionPanelAdapter extends BaseAdapter {

    private Context context;

    private List<Drawable> functionImages;

    private List<String> functionName;

    public JXChatFunctionPanelAdapter(Context context, List<Drawable> functionImages,
            List<String> functionName) {
        this.context = context;
        this.functionImages = functionImages;
        this.functionName = functionName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.jx_chat_function_item, null);
        }
        TextView functionView = (TextView)convertView.findViewById(R.id.tv_function);

        Drawable drawable;
        functionView.setText(functionName.get(position));
        drawable = functionImages.get(position);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); // 设置边界
        functionView.setCompoundDrawables(null, drawable, null, null);
        
        return convertView;
    }

    @Override
    public int getCount() {
        return functionName == null ? 0 : functionName.size();
    }

    @Override
    public Object getItem(int position) {
        return functionName == null ? null : functionName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
