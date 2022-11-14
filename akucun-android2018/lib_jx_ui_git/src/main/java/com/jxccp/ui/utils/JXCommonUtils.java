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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jxccp.im.util.DateUtil;
import com.jxccp.ui.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: TODO
 * @date 2015-9-7 下午1:59:48
 */
public class JXCommonUtils {


    /**
     * @Description: toast封装
     * @param context
     * @param text
     * @date 2015-9-7 下午2:01:24
     */
    public static void showToast(Context context, String text) {
        View view = View.inflate(context, R.layout.jx_layout_toast_tips, null);
        TextView textView = (TextView)view.findViewById(R.id.tv_toast);
        textView.setText(text);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * 根据Uri获取文件路径
     * 
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] {
                ImageColumns.DATA
            }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * @date 2015年9月29日 
     * @Description: 验证APPKEY拼写是否合法
     */
    public static boolean vaildAppkey(String appKey){
        if(!appKey.contains("#") || appKey.startsWith("#")){
            return false;
        }
        String[] apps = appKey.split("#");
        if(apps == null){
            return false;
        }
        if(apps.length <= 1){
            return false;
        }
        return true;
    }

    public static String readableTimeDifferenceFull(Context context, long time) {
        return readableTimeDifference(context, time, true);
    }

    private static String readableTimeDifference(Context context, long time, boolean fullDate) {
        if (time == 0) {
            return context.getString(R.string.jx_just_now);
        }
        Date date = new Date(time);
        long difference = (System.currentTimeMillis() - time) / 1000;
        if (difference < 60) {
            return context.getString(R.string.jx_just_now);
        } else if (difference < 60 * 2) {
            return context.getString(R.string.jx_minute_ago);
        } else if (difference < 60 * 15) {
            return context.getString(R.string.jx_minutes_ago, Math.round(difference / 60.0));
        } else if (today(date)) {
            java.text.DateFormat df = DateFormat.getTimeFormat(context);
            df.setTimeZone(TimeZone.getDefault());
            return df.format(date);
        } else {
            return DateUtil.formatDatetime(time, "yyyy-MM-dd HH:mm");
        }
    }

    private static boolean today(Date date) {
        return sameDay(date, new Date(System.currentTimeMillis()));
    }

    public static boolean sameDay(long timestamp1, long timestamp2) {
        return sameDay(new Date(timestamp1), new Date(timestamp2));
    }

    private static boolean sameDay(Date a, Date b) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(a);
        cal2.setTime(b);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static int[] getImageSize(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int height = options.outHeight;
        final int width = options.outWidth;
        int[] imgsize = new int[2];
        imgsize[0] = width;
        imgsize[1] = height;
        return imgsize;
    }

    public static int calculateInSampleSize(int width, int height, int reqWidth,
            int reqHeight) {
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger
            // inSampleSize).

            long totalPixels = width * height / inSampleSize;

            // Anything more than 2x the requested pixels we'll sample down
            // further
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
        // END_INCLUDE (calculate_sample_size)
    }
    
    /**
     * 判断是否为html消息
     * @param htmlstr
     * @return
     */
    public static boolean isHtmlText(String htmlstr){
        if (TextUtils.isEmpty(htmlstr)) {
            return false;
        }
        String html ="<(img|br|p|strong|em|h1|b|i|dfn|u|font|strike|ins|s)[^>]+>";
        Pattern p_html ;
        Matcher m_html;
        
        p_html = Pattern.compile(html , Pattern.CASE_INSENSITIVE);
        
        m_html = p_html.matcher(htmlstr);
        if (m_html.find()) {
            return true;        
        }else {
            return false;
        }
    }
    
    /**
     * 获取html中图片路径
     * @param htmlStr
     * @return
     */
    public static List<String> getImgStr(String htmlStr) {
        String img = "";
        Pattern p_image;
        Matcher m_image;
        List<String> pics = new ArrayList<String>();
        
        if (TextUtils.isEmpty(htmlStr)) {
            return pics;
        }

        // String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址

        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile(regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            img = img + "," + m_image.group();
            // Matcher m =
            // Pattern.compile("src=\"?(.*?)(\"|>|\\s+)").matcher(img); //匹配src

            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);

            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     * @param file
     */ 
    public static String getMIMEType(File file) { 
         
        String type="*/*"; 
        String fName = file.getName(); 
        //获取后缀名前的分隔符"."在fName中的位置。 
        int dotIndex = fName.lastIndexOf("."); 
        if(dotIndex < 0){ 
            return type; 
        } 
        /* 获取文件的后缀名*/ 
        String end=fName.substring(dotIndex,fName.length()).toLowerCase(); 
        if(end=="")return type; 
        //在MIME和文件类型的匹配表中找到对应的MIME类型。 
        for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？ 
            if(end.equals(MIME_MapTable[i][0])) 
                type = MIME_MapTable[i][1]; 
        }        
        return type; 
    } 
    
    public static final String[][] MIME_MapTable={ 
            //{后缀名，MIME类型} 
            {".3gp",    "video/3gpp"}, 
            {".apk",    "application/vnd.android.package-archive"}, 
            {".asf",    "video/x-ms-asf"}, 
            {".avi",    "video/x-msvideo"}, 
            {".bin",    "application/octet-stream"}, 
            {".bmp",    "image/bmp"}, 
            {".c",  "text/plain"}, 
            {".class",  "application/octet-stream"}, 
            {".conf",   "text/plain"}, 
            {".cpp",    "text/plain"}, 
            {".doc",    "application/msword"}, 
            {".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"}, 
            {".xls",    "application/vnd.ms-excel"},  
            {".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}, 
            {".exe",    "application/octet-stream"}, 
            {".gif",    "image/gif"}, 
            {".gtar",   "application/x-gtar"}, 
            {".gz", "application/x-gzip"}, 
            {".h",  "text/plain"}, 
            {".htm",    "text/html"}, 
            {".html",   "text/html"}, 
            {".jar",    "application/java-archive"}, 
            {".java",   "text/plain"}, 
            {".jpeg",   "image/jpeg"}, 
            {".jpg",    "image/jpeg"}, 
            {".js", "application/x-javascript"}, 
            {".log",    "text/plain"}, 
            {".m3u",    "audio/x-mpegurl"}, 
            {".m4a",    "audio/mp4a-latm"}, 
            {".m4b",    "audio/mp4a-latm"}, 
            {".m4p",    "audio/mp4a-latm"}, 
            {".m4u",    "video/vnd.mpegurl"}, 
            {".m4v",    "video/x-m4v"},  
            {".mov",    "video/quicktime"}, 
            {".mp2",    "audio/x-mpeg"}, 
            {".mp3",    "audio/x-mpeg"}, 
            {".mp4",    "video/mp4"}, 
            {".mpc",    "application/vnd.mpohun.certificate"},        
            {".mpe",    "video/mpeg"},   
            {".mpeg",   "video/mpeg"},   
            {".mpg",    "video/mpeg"},   
            {".mpg4",   "video/mp4"},    
            {".mpga",   "audio/mpeg"}, 
            {".msg",    "application/vnd.ms-outlook"}, 
            {".ogg",    "audio/ogg"}, 
            {".pdf",    "application/pdf"}, 
            {".png",    "image/png"}, 
            {".pps",    "application/vnd.ms-powerpoint"}, 
            {".ppt",    "application/vnd.ms-powerpoint"}, 
            {".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"}, 
            {".prop",   "text/plain"}, 
            {".rc", "text/plain"}, 
            {".rmvb",   "audio/x-pn-realaudio"}, 
            {".rtf",    "application/rtf"}, 
            {".sh", "text/plain"}, 
            {".tar",    "application/x-tar"},    
            {".tgz",    "application/x-compressed"},  
            {".txt",    "text/plain"}, 
            {".wav",    "audio/x-wav"}, 
            {".wma",    "audio/x-ms-wma"}, 
            {".wmv",    "audio/x-ms-wmv"}, 
            {".wps",    "application/vnd.ms-works"}, 
            {".xml",    "text/plain"}, 
            {".z",  "application/x-compress"}, 
            {".zip",    "application/x-zip-compressed"}, 
            {"",        "*/*"}   
        }; 
    
    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
 
        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }
    
    /**
     * 检测Sdcard是否存在
     * 
     * @return
     */
    public static boolean isExitsSdcard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }
    
    /**
     * 获取视频时长
     * @param videoFile
     * @return
     */
    public static int getVideoDuration(File videoFile){
        if (videoFile == null) {
            return 0;
        }
        int duration;
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(videoFile.getAbsolutePath());
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            return duration;
        } catch (IllegalArgumentException | SecurityException | IllegalStateException
                | IOException e) {
            return 0;
        }
    }


}
