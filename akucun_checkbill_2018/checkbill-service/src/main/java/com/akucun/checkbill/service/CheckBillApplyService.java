package com.akucun.checkbill.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.akucun.checkbill.dao.detail.CheckBillActivityDetail;
import com.akucun.checkbill.dao.detail.CheckBillDeliveryDetail;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;

/**
 * Created by zhaojin on 3/30/2018.
 */
public interface CheckBillApplyService
{

    public List<CheckBillMonthDetail> queryExcelDataByDateRangePage(String userId, Date startDate, Date endDate, long pageNo) throws Exception;
    
    public List<CheckBillDeliveryDetail> queryExcelDataByDeliveryPage(String userId, String deliveryOrderId,List<String> status,long pageNo ) throws Exception;

    public List<CheckBillActivityDetail> queryExcelDataByActivityPage(String userId, String deliveryOrderId,List<String> status,long pageNo ) throws Exception;

    public boolean  isAllowMonthApply(String userId, String dateYearMonth) throws Exception;
    
    public boolean  isAllowDeliveryApply(String userId, String deliveryOrderId) throws Exception;
    
    public boolean  isAllowActivityApply(String userId, String ActivityId) throws Exception;

    public List<String>  queryDeliveryById(String id);
    public List<String>  queryDeliveryByNo(String deliveryNo);

    public List<String> queryActivityById(String id);
    
    public String insert(CheckBillApplyEntity param) throws Exception  ;

    public int updateById(CheckBillApplyEntity param) throws Exception  ;


    int updateDeliveryUrlByDeliveryOrderNo(String userId,String deliveryOrderNo,String url);
    public int updateActivityUrlActivityId(String userId,String activityId,String url);

//  public List<CheckBillApply> queryByPage(String userId, String typeCode,String typeValue, long pageNo, long pageSize ) throws Exception  ;

	
    
}
