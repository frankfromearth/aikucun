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

package com.jxccp.ui.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.jxccp.ui.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情库工具类
 */
public class JXSmileUtils {
    public static final String jx_expression_1 = "[):]";
    public static final String jx_expression_2 = "[:D]";
    public static final String jx_expression_3 = "[;)]";
    public static final String jx_expression_4 = "[:-o]";
    public static final String jx_expression_5 = "[:p]";
    public static final String jx_expression_6 = "[(H)]";
    public static final String jx_expression_7 = "[:@]";
    public static final String jx_expression_8 = "[:s]";
    public static final String jx_expression_9 = "[:$]";
    public static final String jx_expression_10 = "[:(]";
    public static final String jx_expression_11 = "[:'(]";
    public static final String jx_expression_12 = "[:|]";
    public static final String jx_expression_13 = "[(a)]";
    public static final String jx_expression_14 = "[8o|]";
    public static final String jx_expression_15 = "[8-|]";
    public static final String jx_expression_16 = "[+o(]";
    public static final String jx_expression_17 = "[<o)]";
    public static final String jx_expression_18 = "[|-)]";
    public static final String jx_expression_19 = "[*-)]";
    public static final String jx_expression_20 = "[:-#]";
    public static final String jx_expression_21 = "[:-*]";
    public static final String jx_expression_22 = "[^o)]";
    public static final String jx_expression_23 = "[8-)]";
    public static final String jx_expression_24 = "[(|)]";
    public static final String jx_expression_25 = "[(u)]";
    public static final String jx_expression_26 = "[(S)]";
    public static final String jx_expression_27 = "[(*)]";
    public static final String jx_expression_28 = "[(#)]";
    public static final String jx_expression_29 = "[(R)]";
    public static final String jx_expression_30 = "[({)]";
    public static final String jx_expression_31 = "[(})]";
    public static final String jx_expression_32 = "[(k)]";
    public static final String jx_expression_33 = "[(F)]";
    public static final String jx_expression_34 = "[(W)]";
    public static final String jx_expression_35 = "[(D)]";

    private static final Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {

        addPattern(emoticons, jx_expression_1, R.drawable.jx_expression_1);
        addPattern(emoticons, jx_expression_2, R.drawable.jx_expression_2);
        addPattern(emoticons, jx_expression_3, R.drawable.jx_expression_3);
        addPattern(emoticons, jx_expression_4, R.drawable.jx_expression_4);
        addPattern(emoticons, jx_expression_5, R.drawable.jx_expression_5);
        addPattern(emoticons, jx_expression_6, R.drawable.jx_expression_6);
        addPattern(emoticons, jx_expression_7, R.drawable.jx_expression_7);
        addPattern(emoticons, jx_expression_8, R.drawable.jx_expression_8);
        addPattern(emoticons, jx_expression_9, R.drawable.jx_expression_9);
        addPattern(emoticons, jx_expression_10, R.drawable.jx_expression_10);
        addPattern(emoticons, jx_expression_11, R.drawable.jx_expression_11);
        addPattern(emoticons, jx_expression_12, R.drawable.jx_expression_12);
        addPattern(emoticons, jx_expression_13, R.drawable.jx_expression_13);
        addPattern(emoticons, jx_expression_14, R.drawable.jx_expression_14);
        addPattern(emoticons, jx_expression_15, R.drawable.jx_expression_15);
        addPattern(emoticons, jx_expression_16, R.drawable.jx_expression_16);
        addPattern(emoticons, jx_expression_17, R.drawable.jx_expression_17);
        addPattern(emoticons, jx_expression_18, R.drawable.jx_expression_18);
        addPattern(emoticons, jx_expression_19, R.drawable.jx_expression_19);
        addPattern(emoticons, jx_expression_20, R.drawable.jx_expression_20);
        addPattern(emoticons, jx_expression_21, R.drawable.jx_expression_21);
        addPattern(emoticons, jx_expression_22, R.drawable.jx_expression_22);
        addPattern(emoticons, jx_expression_23, R.drawable.jx_expression_23);
        addPattern(emoticons, jx_expression_24, R.drawable.jx_expression_24);
        addPattern(emoticons, jx_expression_25, R.drawable.jx_expression_25);
        addPattern(emoticons, jx_expression_26, R.drawable.jx_expression_26);
        addPattern(emoticons, jx_expression_27, R.drawable.jx_expression_27);
        addPattern(emoticons, jx_expression_28, R.drawable.jx_expression_28);
        addPattern(emoticons, jx_expression_29, R.drawable.jx_expression_29);
        addPattern(emoticons, jx_expression_30, R.drawable.jx_expression_30);
        addPattern(emoticons, jx_expression_31, R.drawable.jx_expression_31);
        addPattern(emoticons, jx_expression_32, R.drawable.jx_expression_32);
        addPattern(emoticons, jx_expression_33, R.drawable.jx_expression_33);
        addPattern(emoticons, jx_expression_34, R.drawable.jx_expression_34);
        addPattern(emoticons, jx_expression_35, R.drawable.jx_expression_35);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
            int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    /**
     * replace existing spannable with smiles
     * 
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    /**
     * 将包含表情符号的字符串转化为Spannable
     * @param context
     * @param text
     * @return
     * <p>
     * 示例:
     * <br>
     * Spannable span = SmileUtils.getSmiledText(context, text);
     * <br>
     *  textView.setText(span, BufferType.SPANNABLE);
     * </p>
     */
    public static Spannable getSmiledText(Context context, CharSequence text) {
        if(text == null){
            text = "";
        }
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    public static boolean containsKey(String key) {
        boolean b = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }
        return b;
    }

}