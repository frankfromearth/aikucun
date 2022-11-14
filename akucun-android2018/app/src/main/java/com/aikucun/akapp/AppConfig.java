package com.aikucun.akapp;

/**
 * Created by jarry on 16/5/13.
 */
public final class AppConfig {

    //主账号id
    public static final String PREF_KEY_USER_ID = "KEY_USER_ID";
    //子账户id
    public static final String PREF_KEY_SUBUSER_ID = "KEY_SUBUSER_ID";
    //用户token
    public static final String PREF_KEY_USER_TOKEN = "KEY_USER_TOKEN";

    public static final String PREF_KEY_SKU_UPDATE = "KEY_SKU_UPDATE";
    public static final String PREF_KEY_COMMENT_UPDATE = "PREF_KEY_COMMENT_UPDATE";

    //当天登录的日期
    public static final String LOGIN_DATE_TIME = "LOGIN_DATE_TIME";


    public static final String UDK_FORWARD_DATA = "UDK_FORWARD_DATA";


    //下单备注开关
    public static final String UDK_REMARK_DATA = "UDK_REMARK_DATA";

    //搜索
    public static final String SEACH_KEY_WORDS = "SEACH_KEY_WORDS";


    //快速转发按钮
    public static final String FASTWINDOW_SHOW = "FASTWINDOW_SHOW";
    public static final String FASTWINDOW_HIDE = "FASTWINDOW_HIDE";

    //评论
    public static final String COMMENT_SEND_MESSAGE = "COMMENT_SEND_MESSAGE";
    public static final String COMMENT_REPLY_MESSAGE = "COMMENT_REPLY_MESSAGE";
    public static final String COMMENT_DELETE_MESSAGE = "COMMENT_DELETE_MESSAGE";


    //
    public static final int PAY_TYPE_ALIPAY = 1;    // 支付宝
    public static final int PAY_TYPE_WXPAY = 2;     // 微信支付
    public static final int PAY_TYPE_YUEPAY = 3;    // 余额支付

    //
    public static final String BUNDLE_KEY_PRODUCT = "BUNDLE_KEY_PRODUCT";
    public static final String BUNDLE_KEY_PRODUCT_ID = "BUNDLE_KEY_PRODUCT_ID";
    public static final String BUNDLE_KEY_PRODUCT_SKU = "BUNDLE_KEY_PRODUCT_SKU";
    public static final String BUNDLE_KEY_CARTPROD_ID = "BUNDLE_KEY_CARTPROD_ID";
    public static final String BUNDLE_KEY_ORDER_IDS = "BUNDLE_KEY_ORDER_IDS";
    public static final String BUNDLE_KEY_ORDER_ID = "BUNDLE_KEY_ORDER_ID";
    public static final String BUNDLE_KEY_ORDER_TYPE = "BUNDLE_KEY_ORDER_TYPE";
    public static final String BUNDLE_KEY_AFTERSALE_TYPE = "BUNDLE_KEY_AFTERSALE_TYPE";

    public static final String BUNDLE_KEY_ORDER_AMOUNT = "BUNDLE_KEY_ORDER_AMOUNT";
    public static final String BUNDLE_KEY_ORDER_DIKOU = "BUNDLE_KEY_ORDER_DIKOU";
    public static final String BUNDLE_KEY_ORDER_YUNFEI = "BUNDLE_KEY_ORDER_YUNFEI";
    public static final String BUNDLE_KEY_ORDER_TOTAL = "BUNDLE_KEY_ORDER_TOTAL";

    //
    public static final String BUNDLE_KEY_ORDER_DETAIL_RESP = "BUNDLE_KEY_ORDER_DETAIL_RESP";
    public static final String BUNDLE_KEY_ADORDER_DETAIL_RESP = "BUNDLE_KEY_ADORDER_DETAIL_RESP";

    public static final String BUNDLE_KEY_WEB_TITLE = "BUNDLE_KEY_WEB_TITLE";
    public static final String BUNDLE_KEY_WEB_URL = "BUNDLE_KEY_WEB_URL";

    //过期商品

    public static final String PREF_KEY_LIVE_LIVEIDS = "PREF_KEY_LIVE_LIVEIDS";
    public static final String PREF_KEY_OVER_LIVEIDS = "PREF_KEY_OVER_LIVEIDS";


    //EVENT BUG

    public static final String MESSAGE_EVENT_ADD_FORWARD = "MESSAGE_EVENT_ADD_FORWARD";
    public static final String MESSAGE_EVENT_CLEAR_FORWARD = "MESSAGE_EVENT_CLEAR_FORWARD";
    public static final String MESSAGE_EVENT_ADD_CART = "MESSAGE_EVENT_ADD_CART";
    public static final String MESSAGE_EVENT_REFRESH_CART = "MESSAGE_EVENT_REFRESH_CART";
    public static final String MESSAGE_EVENT_CLEAR_CART = "MESSAGE_EVENT_CLEAR_CART";
    public static final String MESSAGE_EVENT_REFRESH_GOODS = "MESSAGE_EVENT_REFRESH_GOODS";
    //隐藏发现新消息
    public static final String MESSAGE_EVENT_HIDE_DISCOVER_NEW_MSG = "MESSAGE_EVENT_HIDE_DISCOVER_NEW_MSG";
    //刷新发现模块数据
    public static final String MESSAGE_EVENT_DISCOVER_LIST_REFRESH = "MESSAGE_EVENT_DISCOVER_LIST_REFRESH";
    //佳信客服信息消息数量
    public static final String MESSAGE_EVENT_JX_NEW_MSG_COUNT = "MESSAGE_EVENT_JX_NEW_MSG_COUNT";
    //刷新订单信息
    public static final String MESSAGE_EVENT_REFRESH_ORDER_STATUS = "MESSAGE_EVENT_REFRESH_ORDER_STATUS";
    //订单支付成功
    public static final String MESSAGE_EVENET_ORDER_PAY_SUCCESS = "MESSAGE_EVENET_ORDER_PAY_SUCCESS";
    //退出订单详情页面
    public static final String MESSAGE_EVENET_CLOSE_ORDER_DETIAL = "MESSAGE_EVENET_CLOSE_ORDER_DETIAL";
    //刷新直播商品
    public static final String MESSAGE_EVENT_REFRESH_ACTIVING_LIVES = "MESSAGE_EVENT_REFRESH_ACTIVING_LIVES";
    //刷新今日批发
    public static final String MESSAGE_EVENT_REFRESH_WHOLESALE = "MESSAGE_EVENT_REFRESH_WHOLESALE";
    //刷新今日预告
    public static final String MESSAGE_EVENT_REFRESH_TRAILER= "MESSAGE_EVENT_REFRESH_TRAILER";
    //刷新个人资料
    public static final String MESSAGE_EVENT_REFRESH_USER_INFO= "MESSAGE_EVENT_REFRESH_USER_INFO";

    public static final String MESSAGE_EVENT_FOLLOW_STATUS = "MESSAGE_EVENT_FOLLOW_STATUS";
    public static final String MESSAGE_EVENT_UNFOLLOW_STATUS = "MESSAGE_EVENT_UNFOLLOW_STATUS";
    public static final String MESSAGE_EVENT_FOLLOW_CLEAR_STATUS =
            "MESSAGE_EVENT_FOLLOW_CLEAR_STATUS";

    //同步活动商品数据
    public static final String MESSAGE_EVENT_SYNC_LIVEDATA = "MESSAGE_EVENT_SYNC_LIVEDATA";


    //APP PROVIDER
    public static final String PROVIDER_FILE_PATH = AppContext.getInstance().getPackageName()+".akfileprovider";


    //品牌ID发生变化
    public static final String MESSAGE_EVENT_FORWARD_PINPAIID_CHANGED =
            "MESSAGE_EVENT_FORWARD_PINPAIID_CHANGED";


    public static class MessageEvent {
        public final String messageId;
        public Object content;
        public Object content2;

        public MessageEvent(String messageId) {
            this.messageId = messageId;
        }

        public MessageEvent(String messageId, Object content) {
            this.messageId = messageId;
            this.content = content;
        }

        public MessageEvent(String messageId, Object content, Object content2) {
            this.messageId = messageId;
            this.content = content;
            this.content2 = content2;
        }
    }


}
