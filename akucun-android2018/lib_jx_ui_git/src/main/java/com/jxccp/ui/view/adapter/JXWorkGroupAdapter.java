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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jxccp.im.chat.mcs.entity.JXWorkgroup;
import com.jxccp.ui.R;

import java.util.List;

public class JXWorkGroupAdapter extends JXBasicAdapter<JXWorkgroup, ListView> {

    private LayoutInflater layoutInflater;

    public JXWorkGroupAdapter(Context context, List<JXWorkgroup> list) {
        super(context, list);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Viewholder viewholder = null;
        if (convertView == null) {
            viewholder = new Viewholder();
            convertView = layoutInflater.inflate(R.layout.jx_item_workgroup, parent, false);
            viewholder.nameTextView = (TextView)convertView.findViewById(R.id.tv_displayName);
            viewholder.rootLayout = (RelativeLayout)convertView.findViewById(R.id.rl_root);
            convertView.setTag(viewholder);
        } else {
            viewholder = (Viewholder)convertView.getTag();
        }
        viewholder.nameTextView.setText(list.get(position).getDisplayName());
        viewholder.rootLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (enterGroupListener != null) {
                    enterGroupListener.onEnter(list.get(position).getMcsId(), list.get(position).getDisplayName());
                }
            }
        });
        return convertView;
    }

    private OnEnterGroupListener enterGroupListener;

    public void setOnEnterGroupListener(OnEnterGroupListener enterGroupListener) {
        this.enterGroupListener = enterGroupListener;
    }

    public interface OnEnterGroupListener {
        public void onEnter(String mcsId, String displayName);
    }

    public void refresh(List<JXWorkgroup> result){
        list.clear();
        list.addAll(result);
        notifyDataSetChanged();
    }

    static class Viewholder {
        TextView nameTextView;

        RelativeLayout rootLayout;
    }
}
