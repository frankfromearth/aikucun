package com.akucun.checkbill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.akucun.checkbill.common.constant.CheckBillApplyStatusEnum;
import com.akucun.checkbill.common.constant.CheckBillApplyTypeCodeEnum;
import com.akucun.checkbill.common.constant.CheckBillConstant;
import com.akucun.checkbill.dao.detail.CheckBillActivityDetail;
import com.akucun.checkbill.dao.detail.CheckBillDeliveryDetail;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;
import com.akucun.checkbill.dao.mapper.CheckBillApplyMapper;
import com.akucun.checkbill.service.CheckBillApplyService;

/**
 * Created by zhaojin on 3/30/2018.
 */
@Service
public class CheckBillApplyServiceImpl implements CheckBillApplyService
{

    public static final Logger LOG= LoggerFactory.getLogger(CheckBillApplyServiceImpl.class);

    @Autowired
    CheckBillApplyMapper checkBillApplyMapper;

    public List<CheckBillMonthDetail> queryExcelDataByDateRangePage(String userId, Date startDate, Date endDate, long pageNo) throws Exception
    {
        Map<String,Object> para=new HashMap<>();
        long startRow=(pageNo-1)*CheckBillConstant.PAGE_SIZE;
        para.put("startRow",startRow);
        para.put("pageSize",CheckBillConstant.PAGE_SIZE);
        para.put("userId",userId);
//        para.put("startDate",DateUtil.formateDateISO(startDate));
//        para.put("endDate",DateUtil.formateDateISO(endDate));

        para.put("startDate",startDate);
        para.put("endDate",endDate);

        return checkBillApplyMapper.queryExcelDataByDateRangePage(para);
    }
    public List<CheckBillDeliveryDetail> queryExcelDataByDeliveryPage(String userId, String deliveryOrderId,List<String> status,long pageNo) throws Exception
    {
    	  Map<String,Object> para=new HashMap<>();
          long startRow=(pageNo-1)*CheckBillConstant.PAGE_SIZE;
          para.put("startRow",startRow);
          para.put("pageSize",CheckBillConstant.PAGE_SIZE);
          
          para.put("userId",userId); 
          para.put("deliveryOrderId",deliveryOrderId); 
          para.put("status",status);  
          return checkBillApplyMapper.queryExcelDataByDeliveryPage(para);
    }
    public List<CheckBillActivityDetail> queryExcelDataByActivityPage(String userId, String activityId,List<String> status,long pageNo ) throws Exception
    {
    	Map<String,Object> para=new HashMap<>();
        long startRow=(pageNo-1)*CheckBillConstant.PAGE_SIZE;
        para.put("startRow",startRow);
        para.put("pageSize",CheckBillConstant.PAGE_SIZE);
        
        para.put("userId",userId); 
        para.put("activityId",activityId); 
        para.put("status",status);  
        return checkBillApplyMapper.queryExcelDataByActivityPage(para);
    }


    public boolean  isAllowMonthApply(String userId, String dateYearMonth) throws Exception
    {
        CheckBillApplyEntity para=new CheckBillApplyEntity();
        para.setUserId(userId);
        para.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
        para.setTypeCode( CheckBillApplyTypeCodeEnum.USER_MONTH.getCode());
        para.setTypeValue(dateYearMonth);
        List<CheckBillApplyEntity>  dataResult=checkBillApplyMapper.query(para);
        LOG.info("query isAllowMonthApply size {}",dataResult.size());
        return !(dataResult.size()>0);
    }
    public boolean  isAllowDeliveryApply(String userId, String deliveryOrderId) throws Exception
    {
        CheckBillApplyEntity para=new CheckBillApplyEntity();
        para.setUserId(userId);
        para.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
        para.setTypeCode( CheckBillApplyTypeCodeEnum.USER_DELIVERY.getCode());
        para.setTypeValue(deliveryOrderId);
        List<CheckBillApplyEntity>  dataResult=checkBillApplyMapper.query(para);
        LOG.info("query isAllowMonthDelivery size {}",dataResult.size());
        return !(dataResult.size()>0);
    }

    public boolean  isAllowActivityApply(String userId, String ActivityId) throws Exception
    {
    	 CheckBillApplyEntity para=new CheckBillApplyEntity();
         para.setUserId(userId);
         para.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
         para.setTypeCode( CheckBillApplyTypeCodeEnum.USER_ACTIVITY.getCode());
         para.setTypeValue(ActivityId);
         List<CheckBillApplyEntity>  dataResult=checkBillApplyMapper.query(para);
         LOG.info("query isAllowActivityApply size {}",dataResult.size());
         return !(dataResult.size()>0);
    }
  
     public List<String> queryDeliveryById(String id)
     {
    	 List<String> res= checkBillApplyMapper.queryDeliveryById(id);
    	 return res;
     }
    public List<String> queryDeliveryByNo(String deliveryOrderNo)
    {
        List<String> res= checkBillApplyMapper.queryDeliveryByNo(deliveryOrderNo);
        return res;
    }

     public List<String> queryActivityById(String id)
     {
    	 List<String> res= checkBillApplyMapper.queryActivityById(id);
    	 return res;
     }
     
     public String insert(CheckBillApplyEntity param) throws Exception {
         checkBillApplyMapper.insert(param);
         return param.getId();
     }

      public int updateById(CheckBillApplyEntity param) throws Exception {
         return checkBillApplyMapper.updateById(param);
     }
    public int updateActivityUrlActivityId(String userId,String activityId,String url)
    {
        Map<String,Object>  param=new HashMap<>();
        param.put("userId",userId);
        param.put("activityId",activityId);
        param.put("url",url);
        return checkBillApplyMapper.updateActivityUrl(param);
    }

    public int updateDeliveryUrlByDeliveryOrderNo(String userId,String deliveryOrderNo,String url)
    {
        Map<String,Object>  param=new HashMap<>();
        param.put("userId",userId);
        param.put("deliveryOrderNo",deliveryOrderNo);
        param.put("url",url);
        return checkBillApplyMapper.updateDeliveryUrl(param);
    }
     /*  
     public List<CheckBillApply> queryByPage(String userId, String typeCode,String typeValue, long pageNo, long pageSize) throws Exception
     {
         Map<String,Object> para=new HashMap<>();
         long startRow=(pageNo-1)*pageSize;
         para.put("startRow",startRow);
         para.put("pageSize",pageSize);
         para.put("userId",userId);
         para.put("startDate",startDate);
         para.put("endDate",endDate);

         List<CheckBillApply>  result=new ArrayList<>();
         List<CheckBillApplyEntity>  dataResult=checkBillApplyMapper.queryByPage(para);
         for(CheckBillApplyEntity entity:dataResult)
         {
             CheckBillApply item=new CheckBillApply();
             BeanUtils.copyBeanProperties(entity,item,true);
             result.add(item);
         }
         return result;
     }
     */
}
