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

# 混淆配置设定
-optimizationpasses 5 # 指定代码的压缩级别
-dontusemixedcaseclassnames # 是否使用大小写混合
-dontskipnonpubliclibraryclasses# 是否混淆第三方jar
-dontpreverify# 混淆时是否做预校验
-keepattributes SourceFile,LineNumberTable										# 混淆号错误信息里带上代码行
-verbose# 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*# 混淆时所采用的算法
-ignorewarnings
-renamesourcefileattribute SourceFile

# 不需要混淆系统组件等
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

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

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

-keep class * implements java.io.Serializable {*;}
-keepclassmembers class * implements java.io.Serializable {*;}

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

# 个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }


# zbar

-keep class net.sourceforge.zbar.** { *; }
#视频播放
-keep class fm.jiecao.jcvideoplayer_lib.**{*;}
# aliyun
-dontwarn com.google.common.cache.**
-dontwarn java.nio.file.**
-dontwarn sun.misc.**

-keep class com.liulishuo.filedownloader.** { *; }
-keep class java.nio.file.** { *; }
-keep class sun.misc.** { *; }
# aliyun player
-keep class com.alivc.player.**{*;}

-keep class com.qu.preview.** { *; }
-keep class com.qu.mp4saver.** { *; }
-keep class com.duanqu.transcode.** { *; }
-keep class com.duanqu.qupai.render.** { *; }
-keep class com.duanqu.qupai.player.** { *; }
-keep class com.duanqu.qupai.audio.** { *; }
-keep class com.aliyun.qupai.encoder.** { *; }
-keep class com.sensetime.stmobile.** { *; }
-keep class com.duanqu.qupai.yunos.** { *; }
-keep class com.aliyun.common.** { *; }
-keep class com.aliyun.jasonparse.** { *; }
-keep class com.aliyun.struct.** { *; }
-keep class com.aliyun.recorder.AliyunRecorderCreator { *; }
-keep class com.aliyun.recorder.supply.** { *; }
-keep class com.aliyun.querrorcode.** { *; }
-keep class com.qu.preview.callback.** { *; }
-keep class com.aliyun.qupaiokhttp.** { *; }
-keep class com.aliyun.crop.AliyunCropCreator { *; }
-keep class com.aliyun.crop.struct.CropParam { *; }
-keep class com.aliyun.crop.supply.** { *; }
-keep class com.aliyun.qupai.editor.pplayer.AnimPlayerView { *; }
-keep class com.aliyun.qupai.editor.impl.AliyunEditorFactory { *; }
-keep interface com.aliyun.qupai.editor.** { *; }
-keep interface com.aliyun.qupai.import_core.AliyunIImport { *; }
-keep class com.aliyun.qupai.import_core.AliyunImportCreator { *; }
-keep class com.aliyun.qupai.encoder.** { *; }
-keep class com.aliyun.leaktracer.** { *;}
-keep class com.duanqu.qupai.adaptive.** { *; }
-keep class com.aliyun.thumbnail.** { *;}
-keep class com.aliyun.demo.importer.media.MediaCache { *;}
-keep class com.aliyun.demo.importer.media.MediaDir { *;}
-keep class com.aliyun.demo.importer.media.MediaInfo { *;}
-keep class com.alivc.component.encoder.**{ *;}
-keep class com.aliyun.log.core.AliyunLogCommon { *;}
-keep class com.aliyun.log.core.AliyunLogger { *;}
-keep class com.aliyun.log.core.AliyunLogParam { *;}
-keep class com.aliyun.log.core.LogService { *;}
-keep class com.aliyun.log.struct.** { *;}
-keep class com.aliyun.demo.publish.SecurityTokenInfo { *; }

-keep class com.aliyun.vod.common.** { *; }
-keep class com.aliyun.vod.jasonparse.** { *; }
-keep class com.aliyun.vod.qupaiokhttp.** { *; }
-keep class com.aliyun.vod.log.core.AliyunLogCommon { *;}
-keep class com.aliyun.vod.log.core.AliyunLogger { *;}
-keep class com.aliyun.vod.log.core.AliyunLogParam { *;}
-keep class com.aliyun.vod.log.core.LogService { *;}
-keep class com.aliyun.vod.log.struct.** { *;}
-keep class com.aliyun.auth.core.**{*;}
-keep class com.aliyun.auth.common.AliyunVodHttpCommon{*;}
-keep class com.alibaba.sdk.android.vod.upload.exception.**{*;}
-keep class com.alibaba.sdk.android.vod.upload.auth.**{*;}
-keep class com.aliyun.auth.model.**{*;}

-keep class **.R$* { *; }

#佳信
-dontwarn org.jxmpp.** -dontwarn com.jxccp.** -dontwarn de.measite.minidns.** -dontwarn org.jivesoftware.smack.** -dontwarn org.jivesoftware.smackx.** -dontwarn android.support.v4.** -dontwarn android.support.v7.**
-keep class de.measite.minidns.** {*;}
-keep class org.jivesoftware.smack.** {*;}
-keep class org.jivesoftware.smackx.** {*;}
-keep class org.jxmpp.** {*;}
-keep class com.jxccp.** {*;}
# Keep the support library -keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
# Keep the support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class * implements com.jxccp.ui.glide.module.GlideModule
-keep public enum com.jxccp.ui.glide.load.resource.bitmap.ImageHeaderParser$** { **[] $VALUES; public *;}
#联动支付
#UMF支付混淆
-dontwarn com.umf.pay.**
-keep class com.umf.pay.** {*;}
#微信支付混淆
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}
#三星支付混淆
-dontwarn com.unionpay.**
-keep class com.unionpay.** {*;}
#其他
-keep class java.nio.file.** { *; }
#多渠道打包所需
#-keepattributes EnclosingMethod
#-keep class com.unionpay.mobile.android.**{*;}