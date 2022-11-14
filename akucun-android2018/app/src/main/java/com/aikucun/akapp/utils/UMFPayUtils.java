package com.aikucun.akapp.utils;

import android.app.Activity;

import com.umf.pay.plugin.UmfPayment;
import com.umf.pay.sdk.UmfRequest;

/**
 * Created by ak123 on 2017/12/8.
 * 联动支付
 */

public class UMFPayUtils {

    public static final int ALI_PAY = 11;
    public static final int WEXIN_PAY=12;
    public static final int UNION_PAY = 13;

    /**
     * 联动支付
     *
     * @param context
     * @param channel 支付渠道
     * @param merId
     * @param merCustId
     * @param tradeNo
     * @param amount
     * @param sign
     */
    public static void toPay(Activity context,String channel, String merId, String merCustId, String tradeNo, String amount, String sign) {
        UmfRequest request = new UmfRequest();
        request.channel = channel;
        request.merId = merId;
        request.merCustId = merCustId;
        request.tradeNo = tradeNo;
        request.amount = amount;
        request.sign = sign;
        UmfPayment.pay(context, request);
    }
}
