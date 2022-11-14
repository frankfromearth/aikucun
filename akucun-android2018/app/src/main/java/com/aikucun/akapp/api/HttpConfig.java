package com.aikucun.akapp.api;

import android.app.Application;
import android.text.TextUtils;

import com.aikucun.akapp.AppContext;
import com.aikucun.akapp.utils.StringUtils;
import com.aikucun.akapp.utils.TDevice;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * API 接口相关全局配置
 * Created by jarry on 16/5/11.
 */
public class HttpConfig {

    public static final boolean isOnline = true;
//    public static final boolean isAccountTest = true;   //是否开启测试帐号登录
    // 默认服务器地址
//    public static final String SERVER_HOST_DEFAULT = "http://akucun.dev.iamcoder.top";
//    public static final String SERVER_HOST_DEFAULT = "http://test.akucun.com";
    public static final String SERVER_HOST_DEFAULT = isOnline ? "http://api.aikucun.com" : "http://api.test.akucun.com";

    //
    public static final String APP_TERMS_URL = "http://www.aikucun.com/terms/terms.html?t=";

    // 物流查询地址
    public static final String WULIU_URL_PREFER = "https://m.kuaidi100.com/result.jsp?nu=";

    // API Version
    public static final String API_VERSION = "v2.0";
    public static final String CLIENT_API_VERSION = "2.0";

    // APP ID & Secret
    private static String API_APP_ID = null;//"28769828";
    private static String API_APP_SECRET = null;//"41b913099b0b48268d42c33b461a29c2";

    // 默认的超时时间
    public static final int DEFAULT_TIMEOUT = 20000;

    private static HttpConfig mInstance;

    private Map<String, String> headers;

    private String hostName = SERVER_HOST_DEFAULT + "/api/" + API_VERSION;

    private static String did = null;

    public static HttpConfig getInstance() {
        if (mInstance == null) {
            synchronized (HttpConfig.class) {
                if (mInstance == null) {
                    mInstance = new HttpConfig();
                }
            }
        }
        return mInstance;
    }

    private HttpConfig() {
//        if (StringUtils.isEmpty(API_APP_ID)) {
//            API_APP_ID = JNIAKuCun.appKey();
//        }
//        if (StringUtils.isEmpty(API_APP_SECRET)) {
//            API_APP_SECRET = JNIAKuCun.appSecret();
//        }
    }

    public void setKeyAndSecret(String[] values) {
        if (values.length >= 2) {
            API_APP_ID = values[0];
            API_APP_SECRET = values[1];
        }
    }

    public static String getHost() {
        return getInstance().getHostName();
    }

    /**
     * 全局HTTP参数配置
     * 在APP打开时初始化配置
     *
     * @param app APP应用程序类
     */
    public static void init(Application app) {
        HttpHeaders httpHeaders = new HttpHeaders();

        // 自定义
        String model = android.os.Build.MODEL.replace(" ", "");
        String os = "android";
        String osVer = android.os.Build.VERSION.RELEASE;
        String appVer = TDevice.getVersionName();
        String buildVer = "" + TDevice.getVersionCode();

        httpHeaders.put("AKC-MODEL", model);
        httpHeaders.put("AKC-OS", os);
        httpHeaders.put("AKC-OS-VERSION", osVer);
        httpHeaders.put("AKC-APP-VERSION", appVer);
        httpHeaders.put("AKC-APP-API-VERSION", CLIENT_API_VERSION);
        httpHeaders.put("AKC-APP-BUILD-VERSION", buildVer);
        httpHeaders.put("AKC-DID", HttpConfig.getDid());
        //渠道号
        String channel = AppContext.getInstance().getChannel();
        if (!TextUtils.isEmpty(channel)) {
            httpHeaders.put("AKC-APP-CHANNEL", channel);
        }
        // User-Agent
        httpHeaders.put("User-Agent", model + " " + os + " " + osVer + " " + appVer + " " +
                CLIENT_API_VERSION);

        // Content-Type
        httpHeaders.put("Content-Type", "application/json");

        //
        Map<String, String> headers = new LinkedHashMap<>();
        headers.putAll(httpHeaders.headersMap);

        setHeaders(headers);

        // 必须调用初始化
        OkGo.init(app);

        // 以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        // 好处是全局参数统一,特定请求可以特别定制参数
        try {
            // 以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
                    .debug("OkGo")

                    .setConnectTimeout(DEFAULT_TIMEOUT)  //全局的连接超时时间
                    .setReadTimeOut(DEFAULT_TIMEOUT)     //全局的读取超时时间
                    .setWriteTimeOut(DEFAULT_TIMEOUT)    //全局的写入超时时间

                    // 可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github
                    // .com/jeasonlzy0216/
                    .setCacheMode(CacheMode.NO_CACHE)

                    // 可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    // 设置全局公共头
                    .addCommonHeaders(httpHeaders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getHeaders() {
        return getInstance().headers;
    }

    public static void setHeaders(Map<String, String> headers) {
        getInstance().headers = headers;
    }

    public static void addHttpHeader(String key, String value) {
        getHeaders().put(key, value);
    }

    /**
     * 生成完整的带参数和签名字串的请求URL
     *
     * @param url      URL Path
     * @param actionId 请求Action
     * @param params   请求参数
     * @return 返回完整的URL
     */
    public static String httpParamsUrl(String url, String actionId, Map<String, String> params) {
        Map<String, String> newParams = new HashMap<>();

        // App ID
        newParams.put("appid", API_APP_ID);
        // Nonce Str
        String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        newParams.put("noncestr", nonceStr);
        // 获取系统时间的时间戳
        long time = System.currentTimeMillis() / 1000;
        String timeStr = String.valueOf(time);
        newParams.put("timestamp", timeStr);
        // Action
        newParams.put("action", actionId);

        // User ID
        String userId = AppContext.getInstance().getUserId();
        if (!StringUtils.isEmpty(userId)) {
            newParams.put("userid", userId);
        }
        // Token
        String token = AppContext.getInstance().getToken();
        if (!StringUtils.isEmpty(token)) {
            newParams.put("token", token);
        }
        String subuserid = AppContext.getInstance().getSubUserId();
        if (!StringUtils.isEmpty(subuserid)) {
            newParams.put("subuserid", subuserid);
        }

        // did
        String did = HttpConfig.getDid();
        if (!StringUtils.isEmpty(did)) {
            newParams.put("did", did);
        }

        if (params != null) {
            newParams.putAll(params);
        }

        List<Map.Entry<String, String>> list = new ArrayList<>();
        list.addAll(newParams.entrySet());

        KeyComparator kc = new KeyComparator();
        Collections.sort(list, kc);

        StringBuffer urlString = new StringBuffer(url);
        urlString.append("?");
        int i = 0;
        for (Iterator<Map.Entry<String, String>> it = list.iterator(); it.hasNext(); ) {
            if (i > 0) {
                urlString.append("&");
            }
            Map.Entry<String, String> entry = it.next();
            urlString.append(entry.getKey() + "=" + entry.getValue());
            i++;
        }

        String sig = sign(API_APP_ID, API_APP_SECRET, nonceStr, timeStr, urlString.toString());
        urlString.append("&sig=" + sig);
        //        HttpParams httpParams = new HttpParams();
        //        httpParams.put(newParams);
        return urlString.toString();
    }

    /**
     * API 签名算法处理
     * 生成sig值字符串，经过SHA-1算法加密，再转成16进制字符串，共计40位长
     *
     * @param appid    APP ID，APP软件内置，需要带在URL参数中
     * @param secret   APP Secret，APP软件内置，不能带在URL参数中，只参与计算
     * @param nonceStr 由客户端形成的随机字符串
     * @param openedmp 由客户端调用接口前获取手机的时间戳，单位为秒
     * @param url      请求的 URL
     * @return sig值字符串
     */
    public static String sign(String appid, String secret, String nonceStr, String openedmp,
                              String url) {
        // 字段由小到大排列
        String plain = "appid=" + appid + "&noncestr=" + nonceStr + "&timestamp=" + openedmp +
                "&secret=" + secret + "&url=" + url;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.reset();
            sha1.update(plain.getBytes("UTF-8"));
            return bytesToHex(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }


    private static class KeyComparator implements Comparator<Map.Entry<String, String>> {
        public int compare(Map.Entry<String, String> m, Map.Entry<String, String> n) {
            return m.getKey().compareTo(n.getKey());
        }
    }

    public static String getDid() {
        if (did == null) {
            did = TDevice.getIMEI();
        }
        return did;
    }
}
