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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.jxccp.im.chat.common.entity.JXSatisfication;
import com.jxccp.im.chat.common.entity.JXSatisfication.Option;
import com.jxccp.im.util.log.JXLog;
import com.jxccp.ui.R;
import com.jxccp.ui.listeners.JXChatFragmentListener;
import com.jxccp.ui.widget.JXFlexibleRatingBar;

import java.util.ArrayList;
import java.util.List;

public class JXEvaluateAdapter extends JXBasicAdapter<Option, ListView> {

    private LayoutInflater mInflater;

    private int SATISFICATION_TYPE = 2;

    private final String explainText = "5颗星：表示非常满意\n" + "4颗星：表示满意\n" + "3颗星：表示一般\n" + "2颗星：表示不满意\n"
            + "1颗星：表示非常不满意";

    private JXSatisfication satisfication;

    private List<Option> satisficationOptions = new ArrayList<Option>();
    
    private int value;

    private JXChatFragmentListener fragmentListener;

    public void setJXChatFragmentListener(JXChatFragmentListener fragmentListener){
        this.fragmentListener = fragmentListener;
    }

    public JXEvaluateAdapter(Context context, List<Option> list, JXSatisfication satisfication) {
        super(context, list);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.satisfication = satisfication;
        SATISFICATION_TYPE = satisfication.getSatisficationType();
        satisficationOptions = satisfication.getOptions();
        if (SATISFICATION_TYPE == 3) {
            list.clear();
            list.add(new Option());
        } else {
            swap();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (SATISFICATION_TYPE == 1) {
                convertView = mInflater.inflate(R.layout.jx_evaluate_satisfaction_item, null);
            } else if (SATISFICATION_TYPE == 2) {
                convertView = mInflater.inflate(R.layout.jx_evaluate_grade_item, null);
            } else if (SATISFICATION_TYPE == 3) {
                convertView = mInflater.inflate(R.layout.jx_evaluate_start_item, null);
            } else if (SATISFICATION_TYPE == 4) {
                convertView = mInflater.inflate(R.layout.jx_evaluate_satisfaction_item, null);
            }
        }

        if (SATISFICATION_TYPE == 1) {
            TextView evaluateView = (TextView)convertView.findViewById(R.id.tv_evaluate_item);
            evaluateView.setText(list.get(position).getText());
        } else if (SATISFICATION_TYPE == 2) {
            TextView evaluateView = (TextView)convertView.findViewById(R.id.tv_evaluate_item);
            evaluateView.setText(list.get(position).getText());
        } else if (SATISFICATION_TYPE == 3) {
            TextView explainTextView = (TextView)convertView.findViewById(R.id.tv_rate_explain);
            final JXFlexibleRatingBar ratingBar = (JXFlexibleRatingBar)convertView
                    .findViewById(R.id.rb_rate);
            explainTextView.setText(explainText);
            ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingbar, float rating, boolean fromUser) {
                    JXLog.e("rating = " + rating);
                    value = (int)rating;
                    if (value == 0) {
                        return;
                    }
                    showSubmitRatingDialog(ratingBar);
                }
            });
        } else if (SATISFICATION_TYPE == 4) {
            TextView evaluateView = (TextView)convertView.findViewById(R.id.tv_evaluate_item);
            evaluateView.setText(list.get(position).getText());
        }

        return convertView;
    }
    
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    public void showSubmitRatingDialog(final RatingBar ratingBar){
        builder.setMessage(R.string.jx_submit_evaluation);
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(fragmentListener != null){
                    fragmentListener.onEvaluateItemClick(value);
                }
                ratingBar.setRating(0);
            }
        });
        builder.setNegativeButton(android.R.string.no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                ratingBar.setRating(0);
            }
        });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ratingBar.setRating(0);
            }
        });
        builder.create().show();
    }

    public void swap() {
        int len = list.size();
        for (int i = 0; i < len / 2; i++) {
            Option option = list.get(i);
            list.set(i, list.get(len - 1 - i));
            list.set(len - 1 - i, option);
        }
    }

}
