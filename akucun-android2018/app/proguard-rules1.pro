# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Volumes/MacOS/Android/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5 # 指定代码的压缩级别
-dontusemixedcaseclassnames # 是否使用大小写混合
-dontskipnonpubliclibraryclasses# 是否混淆第三方jar
-dontpreverify# 混淆时是否做预校验
-keepattributes SourceFile,LineNumberTable	# 混淆号错误信息里带上代码行
-verbose# 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*# 混淆时所采用的算法
-ignorewarnings
-renamesourcefileattribute SourceFile

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
native <methods>;
}
-keepclasseswithmembernames class * implements android.view.View{ *; }
-keepclassmembers class * implements android.view.View{ *; }
-keep class * implements android.view.View{ *; }
-keep class tv.danmaku.ijk.**{*;}

# 不需要混淆系统组件等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


#保证实体类不被混淆
-keep class * implements android.os.Parcelable{ *; }
-keepclasseswithmembernames class * implements android.os.Parcelable{ *; }
-keepclassmembers class * implements android.os.Parcelable {*; }


-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity { # 保持自定义控件类不被混淆
    public void *(android.view.View);
}
-keepclassmembers enum * {     # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable { # 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}

# fastjson
-keep class com.alibaba.fastjson.**{*;}
# keep 所有的 javabean
-dontwarn com.aikucun.akapp.api.entity.**
-keep class com.aikucun.akapp.api.entity.**{*;}
#-dontwarn com.aikucun.akapp.storage.**
#-keep class com.aikucun.akapp.storage.**{*;}
-dontwarn com.aikucun.akapp.api.response.**
-keep class com.aikucun.akapp.api.response.**{*;}
-keep class com.aikucun.akapp.storage.ProductDB{*;}

# keep 泛型
-keepattributes Signature

# 不需要混淆第三方类库
-dontwarn android.support.v4.**                                             #去掉警告
-keep class android.support.v4.** { *; }                                    #过滤android.support.v4
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment


#Glide
-keep class com.bumptech.glide.**{*;}
#-keep class com.wallstreetcn.news.lazyload.GlideWrapConfiguration{*;}
-keep class * implements com.bumptech.glide.module.GlideModule{ *; }

#Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }


#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
@butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
@butterknife.* <methods>;
}



# rxjava
-dontwarn rx.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

# rxjava2
-dontwarn io.reactivex.**
-keep class io.reactivex.schedulers.Schedulers {
    public static <methods>;
}
-keep class io.reactivex.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.TestScheduler {
    public <methods>;
}
-keep class io.reactivex.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class io.reactivex.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class io.reactivex.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}


##umeng 统计
-keepclassmembers class * {
 public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep public class com.wallstreetcn.news.R$*{
public static final int *;
}


# ProGuard configurations for Bugtags
-keepattributes LineNumberTable,SourceFile

-keep class com.bugtags.library.** {*;}
-dontwarn com.bugtags.library.**
-keep class io.bugtags.** {*;}
-dontwarn io.bugtags.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient


#rxpermissions
-keep class com.tbruyelle.**{*;}


#retrofit2

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}


#########################################################################################

# OkHttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** {*;}
-keep interface com.squareup.okhttp.** {*;}
-dontwarn okio.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# alipay SDK
# -libraryjars libs/alipaySingle-20170510.jar
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}

# Weixin SDK
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.**{*;}

# Pgyer SDK
# -libraryjars libs/pgyer_sdk_x.x.jar
-dontwarn com.pgyersdk.**
-keep class com.pgyersdk.** { *; }

# Bugly SDK
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep class android.support.**{*;}

# event bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#rxpermissions
-keep class com.tbruyelle.**{*;}


# 个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }


# zbar
-keep class net.sourceforge.zbar.** { *; }


# fastjson
-keep class com.alibaba.fastjson.**{*;}
# keep 所有的 javabean
-dontwarn com.aikucun.akapp.api.entity.**
-keep class com.aikucun.akapp.api.entity.**{*;}
#-dontwarn com.aikucun.akapp.storage.**
#-keep class com.aikucun.akapp.storage.**{*;}
-dontwarn com.aikucun.akapp.api.response.**
-keep class com.aikucun.akapp.api.response.**{*;}
-keep class com.aikucun.akapp.storage.ProductDB{*;}

# keep 泛型
-keepattributes Signature

# 不需要混淆第三方类库
-dontwarn android.support.v4.**                                             #去掉警告
-keep class android.support.v4.** { *; }                                    #过滤android.support.v4
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment



#########################################################################################
