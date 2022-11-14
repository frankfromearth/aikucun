package com.akucun.checkbill.dao.mapper;

import java.util.List;
import java.util.Map;

import com.akucun.checkbill.dao.detail.CheckBillActivityDetail;
import com.akucun.checkbill.dao.detail.CheckBillDeliveryDetail;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;

public interface CheckBillApplyMapper // extends BaseMapper<CheckBillApplyEntity>
{
    public int updateById(CheckBillApplyEntity bean);
    
    public int insert(CheckBillApplyEntity bean);

    //public List<CheckBillApplyEntity> queryByPage(Map<String, Object> param);
    
    public List<CheckBillApplyEntity> query(CheckBillApplyEntity param);
    
    public List<CheckBillMonthDetail> queryExcelDataByDateRangePage(Map<String, Object> param);
    
    public List<CheckBillDeliveryDetail> queryExcelDataByDeliveryPage(Map<String, Object> param);

    public List<CheckBillActivityDetail> queryExcelDataByActivityPage(Map<String, Object> param);
    
    public List<String>  queryDeliveryById(String id);
    public List<String>  queryDeliveryByNo(String deliveryNo);

    public List<String>  queryActivityById(String id);

    int updateDeliveryUrl(Map<String,Object> param);
    int updateActivityUrl(Map<String,Object> param);
}