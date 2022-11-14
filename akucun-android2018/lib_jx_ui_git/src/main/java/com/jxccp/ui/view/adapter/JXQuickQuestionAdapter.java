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
import android.widget.TextView;

import com.jxccp.ui.R;

import java.util.ArrayList;
import java.util.List;

public class JXQuickQuestionAdapter extends JXBasicAdapter< String , ListView>{
    
    private LayoutInflater mInflater;
    
    private List<String> fiveQuestionList = new ArrayList<String>();

    public JXQuickQuestionAdapter(Context context, List<String> list) {
        super(context, list);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (list.size()>5) {
            for(int i = 0 ; i<5 ; i++){
                fiveQuestionList.add(list.get(i));
            }
            list.clear();
            list.addAll(fiveQuestionList);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            /**复用满意度adapter**/
            convertView = mInflater.inflate(R.layout.jx_quick_question_item, null);
        }
        TextView evaluateView = (TextView)convertView.findViewById(R.id.tv_evaluate_item);
        evaluateView.setText(list.get(position).toString());
        
        return convertView;
    }

}
