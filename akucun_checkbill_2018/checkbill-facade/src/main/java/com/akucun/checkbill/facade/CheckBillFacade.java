package com.akucun.checkbill.facade;

import com.akucun.checkbill.facade.bean.CheckBillResponse;

/**
 * 对账单通用接口
 * Created by zhaojin on 3/30/2018.
 */


public interface CheckBillFacade {

    /**
     *  会员对账单申请 根据 月份
     * @param userId  会员ID,主键ID
     * @param dateYearMonth 格式 YYYY-MM
     * @return  成功表示 收到消息，data为空，后台生成结果查DB
     */
    public CheckBillResponse userCheckBillApplyByMonth(String userId, String dateYearMonth);
    
    /**
     *  会员对账单申请    根据  发货单号
     * @param userId  会员ID,主键ID
     * @param deliveryOrderNo  发货单号,adOrderId  ,AD开头
     * @return  成功表示 收到消息，data为空，后台生成结果查DB
     */
    public CheckBillResponse userCheckBillApplyByDelivery(String userId, String deliveryOrderNo);

    /**
     *  会员对账单申请    根据  活动号
     * @param userId  会员ID,主键ID
     * @param activityId  活动号,主键ID
     * @return  成功表示 收到消息，data为空，后台生成结果查DB
     */
    public CheckBillResponse userCheckBillApplyByActivity(String userId, String activityId);

    
    /**
     * 查 对账单  
     * 此接口暂时无用
     * @param userId 会员ID 或者 商户ID,主键ID 
     * @param typeCode  UM=UserMonth用户按月,UD=UserDelivery用户按发货单,UA=UserAcitivty用户按活动
     * @param typeValue  对应于typeCode的值
     * @param pageNo 第几页，1开始
     * @param pageSize 页大小
     * @return  data为数据
     */
    public CheckBillResponse checkBillQuery(String userId, String typeCode,String typeValue,long pageNo,long pageSize);
   
    
    
    
}
