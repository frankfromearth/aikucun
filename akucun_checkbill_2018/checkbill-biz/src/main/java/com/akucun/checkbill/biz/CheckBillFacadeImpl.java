package com.akucun.checkbill.biz;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.akucun.checkbill.Integration.PushAppNotify;
import com.akucun.checkbill.common.constant.CheckBillApplyStatusEnum;
import com.akucun.checkbill.common.constant.CheckBillApplyTypeCodeEnum;
import com.akucun.checkbill.common.constant.DFSCloudEnum;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;
import com.akucun.checkbill.dao.entity.User;
import com.akucun.checkbill.facade.CheckBillFacade;
import com.akucun.checkbill.facade.bean.CheckBillApply;
import com.akucun.checkbill.facade.bean.CheckBillResponse;
import com.akucun.checkbill.facade.bean.ErrorCode;
import com.akucun.checkbill.service.CheckBillApplyService;
import com.akucun.checkbill.service.IUserService;
import com.akucun.checkbill.service.UploadFileService;

/**
 * Created by zhaojin on 3/30/2018.
 */
@Service("checkBillFacade")
//@com.alibaba.dubbo.config.annotation.Service(version = "1.0.0",group = "dev")
public class CheckBillFacadeImpl implements CheckBillFacade
{

    public static final Logger LOG= LoggerFactory.getLogger(CheckBillFacadeImpl.class);

    @Autowired
    private UploadFileService uploadFileService;

    @Autowired
    private CheckBillApplyService  checkBillApplyService;
    
    @Autowired
    private IUserService userService;

    @Autowired
    private  PushAppNotify pushAppNotify;
    
    @Value("${excel.tmp.dir}")
    private String tmpDir;
 
    
    
    /**
     *  会员对账单申请 根据 月份
     * @param userId  会员ID
     * @param dateYearMonth 格式 YYYY-MM
     * @return  data为空
     */
    @Override
    public CheckBillResponse userCheckBillApplyByMonth(String userId, String dateYearMonth) 
    { 
        LOG.info("会员对账单申请 根据 月份  收到请求userId={}, dateYearMonth={}",userId,dateYearMonth);
        CheckBillResponse   resp=new CheckBillResponse();
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(dateYearMonth) || dateYearMonth.length()>7 )
        {
             resp.setRespCode(ErrorCode.PARAM_EMPTY.getCode());
            resp.setRespMsg(ErrorCode.PARAM_EMPTY.getMsg());
            return resp;
        }

        try
        {
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM");
            Date fromDate=null;
            try {
                fromDate=format.parse(dateYearMonth);
            } catch (ParseException e) {
                LOG.error("日期格式 错误",e);
                resp.setRespCode(ErrorCode.DATE_FORMAT_ERROR.getCode());
                resp.setRespMsg(ErrorCode.DATE_FORMAT_ERROR.getMsg());
                return resp;
            }

            User u=userService.queryUserById(userId);
            if(u==null || StringUtils.isEmpty(u.getUserNo()) || StringUtils.isEmpty(u.getName()) )
            {
            	LOG.error("userId ={} 不存在有效的数据!!!",userId);
                resp.setRespCode(ErrorCode.USER_NOT_EXIST.getCode());
                resp.setRespMsg(ErrorCode.USER_NOT_EXIST.getMsg());
                return resp;
            }
            
            Calendar calendar= Calendar.getInstance();
            calendar.setTime(fromDate);
            calendar.add(Calendar.MONTH,1);
            Date toDate=calendar.getTime();
            //查 userID，月份，当前最新的是否有处理中的，如有拒绝
            boolean isAllow= checkBillApplyService.isAllowMonthApply(userId,dateYearMonth);
            if(!isAllow)
            {
                LOG.error("userId={} 在 dateYearMonth={} 当前有处理中的，拒绝");
                resp.setRespCode(ErrorCode.DENYED.getCode());
                resp.setRespMsg(ErrorCode.DENYED.getMsg());
                    return  resp;
            }

            //存DB
            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setCreateTime(new Date());
            entity.setUserId(userId);
            entity.setUserName(u.getName());
            entity.setUserNo(u.getUserNo());
            entity.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
            entity.setTypeValue(dateYearMonth);
            entity.setTypeCode(CheckBillApplyTypeCodeEnum.USER_MONTH.getCode());

            String uuid=UUID.randomUUID().toString().replace("-","");
            entity.setId(uuid);
            LOG.info("会员对账单申请 根据 月份  生成uuid= {}",uuid);
            String checkBillApplyId=checkBillApplyService.insert(entity);
            LOG.info("会员对账单申请 根据 月份  保存处理中记录 {}",entity);

            ExecutorService executorService= Executors.newCachedThreadPool();
            CheckBillUserByMonthTask task=new CheckBillUserByMonthTask();
            task.setCheckBillApplyId(checkBillApplyId);
            task.setDateYearMonth(dateYearMonth);
            task.setUser(u);
            task.setFromDate(fromDate);
            task.setToDate(toDate);
            task.setTmpDir(tmpDir);
            task.setCheckBillApplyService(checkBillApplyService);
            task.setUploadFileService(uploadFileService);
            executorService.submit(task );
            LOG.info("会员对账单申请  根据 月份  线程启动完成 userId={}, dateYearMonth={},task",userId,dateYearMonth,task);

            resp.setRespCode(ErrorCode.SUCCESS.getCode());
            resp.setRespMsg(ErrorCode.SUCCESS.getMsg());
            LOG.info("会员对账单申请  根据 月份   返回{}",resp);
            return resp;

        } catch (Exception e) {
            LOG.error("会员对账单申请  根据 月份  userId={}, dateYearMonth={} 出错!!!",userId,dateYearMonth,e);
            resp=new CheckBillResponse();
            resp.setRespCode(ErrorCode.ERROR.getCode());
            resp.setRespMsg(ErrorCode.ERROR.getMsg());
        } 
        return resp;
    }
 
    @Override
    public CheckBillResponse checkBillQuery(String userId, String typeCode,String typeValue, long pageNo, long pageSize) {
        CheckBillResponse resp=new CheckBillResponse();
        /* 此接口暂时无用
        try {
            List<CheckBillApply> data=checkBillApplyService.queryByPage(userId,typeCode,typeValue,pageNo,pageSize);
            resp.setData(data);
            resp.setRespCode(ErrorCode.SUCCESS.getCode());
            resp.setRespMsg(ErrorCode.SUCCESS.getMsg());
        } catch (Exception e) {
            LOG.error("查询 出错",e);
            resp.setRespCode(ErrorCode.ERROR.getCode());
            resp.setRespMsg("查询出错");
        }
        */
        return resp;
    }


	@Override
	public CheckBillResponse userCheckBillApplyByDelivery(String userId, String deliveryOrderNo)
	{
		
	    CheckBillResponse   resp=new CheckBillResponse();
	    LOG.info("会员对账单申请 根据 发货单  收到请求userId={}, deliveryOrderNo={}",userId,deliveryOrderNo);
	   try 
	   {
		   
		    User u=userService.queryUserById(userId);
		    if(u==null || StringUtils.isEmpty(u.getUserNo()) || StringUtils.isEmpty(u.getName()) )
		    {
			      LOG.error("userId ={} 不存在有效的数据!!!",userId);
			      resp.setRespCode(ErrorCode.USER_NOT_EXIST.getCode());
			      resp.setRespMsg(ErrorCode.USER_NOT_EXIST.getMsg());
			      return resp;
		    }
	        
		    List<String> deliveryIds=checkBillApplyService.queryDeliveryByNo(deliveryOrderNo);
		    if(deliveryIds==null || deliveryIds.size()==0)
		    {
		    	 LOG.error("deliveryOrderNo={} 不存在有效的数据",deliveryOrderNo);
			      resp.setRespCode(ErrorCode.DELIVERY_NOT_EXIST.getCode());
			      resp.setRespMsg(ErrorCode.DELIVERY_NOT_EXIST.getMsg());
			      return resp;
		    }
            String deliveryOrderId=deliveryIds.get(0);
            //查 userID，发货单号，当前最新的是否有处理中的，如有拒绝
            boolean isAllow= checkBillApplyService.isAllowDeliveryApply(userId,deliveryOrderId);
            if(!isAllow)
            {
                LOG.error("userId={} 在 deliveryOrderId={} 当前有处理中的，拒绝");
                resp.setRespCode(ErrorCode.DENYED.getCode());
                resp.setRespMsg(ErrorCode.DENYED.getMsg());
                return  resp;
            }
            
		    //存DB
	        CheckBillApplyEntity entity=new CheckBillApplyEntity();
	        entity.setCreateTime(new Date());
	        entity.setUserId(userId);
	        entity.setUserName(u.getName());
	        entity.setUserNo(u.getUserNo());
	        entity.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
	        entity.setTypeCode(CheckBillApplyTypeCodeEnum.USER_DELIVERY.getCode());
	        entity.setTypeValue(deliveryOrderNo);
	        String uuid=UUID.randomUUID().toString().replace("-","");
	        entity.setId(uuid);
	        LOG.info("会员对账单申请 根据 发货单  生成uuid= {}",uuid);
	        String checkBillApplyId=checkBillApplyService.insert(entity);
	        LOG.info("会员对账单申请 根据 发货单  保存处理中记录 {}",entity);
        
	        ExecutorService executorService= Executors.newCachedThreadPool();
            CheckBillUserByDeliverOrderTask  task=new CheckBillUserByDeliverOrderTask();
            task.setCheckBillApplyId(checkBillApplyId);
            task.setDeliveryOrderId(deliveryOrderId);
            task.setUser(u); 
            task.setTmpDir(tmpDir);
            task.setCheckBillApplyService(checkBillApplyService);
            task.setPushAppNotify(pushAppNotify);
            task.setDeliveryOrderNo(deliveryOrderNo);
            task.setUploadFileService(uploadFileService);
            executorService.submit(task );
            LOG.info("会员对账单申请  根据 发货单  线程启动完成 userId={}, deliveryOrderId={},task",userId,deliveryOrderId,task);

            resp.setRespCode(ErrorCode.SUCCESS.getCode());
            resp.setRespMsg(ErrorCode.SUCCESS.getMsg());
            LOG.info("会员对账单申请  根据 发货单   返回{}",resp);
            
		 } catch (Exception e) {
	         LOG.error("会员对账单申请  根据 发货单  userId={}, deliveryOrderNo={} 出错!!!",userId,deliveryOrderNo,e);
	         resp=new CheckBillResponse();
	         resp.setRespCode(ErrorCode.ERROR.getCode());
	         resp.setRespMsg(ErrorCode.ERROR.getMsg());
	     } 
	     return resp;
	}

	@Override
	public CheckBillResponse userCheckBillApplyByActivity(String userId, String activityId) 
	{ 
		 CheckBillResponse   resp=new CheckBillResponse();
		 LOG.info("会员对账单申请 根据 活动  收到请求userId={}, activityId={}",userId,activityId);
		 try 
		 {
			 User u=userService.queryUserById(userId);
			 if(u==null || StringUtils.isEmpty(u.getUserNo()) || StringUtils.isEmpty(u.getName()) )
			 {
			      LOG.error("user={} 不存在有效的数据",u);
			      resp.setRespCode(ErrorCode.USER_NOT_EXIST.getCode());
			      resp.setRespMsg(ErrorCode.USER_NOT_EXIST.getMsg());
			      return resp;
		    }
		    
			List<String> activityNos=checkBillApplyService.queryActivityById(activityId);
		    if(activityNos==null || activityNos.size()==0)
		    {
		    	 LOG.error("activityId={} 不存在有效的数据",activityId);
			      resp.setRespCode(ErrorCode.ACTIVITY_NOT_EXIST.getCode());
			      resp.setRespMsg(ErrorCode.ACTIVITY_NOT_EXIST.getMsg());
			      return resp;
		    }
		     
            //查 userID，发货单号，当前最新的是否有处理中的，如有拒绝
            boolean isAllow= checkBillApplyService.isAllowActivityApply(userId, activityId);
            if(!isAllow)
            {
                LOG.error("userId={} 在 activityId={} 当前有处理中的，拒绝");
                resp.setRespCode(ErrorCode.DENYED.getCode());
                resp.setRespMsg(ErrorCode.DENYED.getMsg());
                return  resp;
            }

            
		    //存DB
	        CheckBillApplyEntity entity=new CheckBillApplyEntity();
	        entity.setCreateTime(new Date());
	        entity.setUserId(userId);
	        entity.setUserName(u.getName());
	        entity.setUserNo(u.getUserNo());
	        entity.setStatus(CheckBillApplyStatusEnum.PROESS.getStatus());
	        entity.setTypeCode(CheckBillApplyTypeCodeEnum.USER_ACTIVITY.getCode());
	        entity.setTypeValue(activityId);
	        String uuid=UUID.randomUUID().toString().replace("-","");
	        entity.setId(uuid);
	        LOG.info("会员对账单申请 根据 活动  生成uuid= {}",uuid);
	        String checkBillApplyId=checkBillApplyService.insert(entity);
	        LOG.info("会员对账单申请 根据 活动  保存处理中记录 {}",entity);
        
        
	        ExecutorService executorService= Executors.newCachedThreadPool();
            CheckBillUserByActivityTask  task=new CheckBillUserByActivityTask();
            task.setCheckBillApplyId(checkBillApplyId);
            task.setActivityId(activityId);
            task.setUser(u); 
            task.setTmpDir(tmpDir);
            task.setCheckBillApplyService(checkBillApplyService);
            task.setPushAppNotify(pushAppNotify);
            task.setActivityNo(activityNos.get(0));
            task.setUploadFileService(uploadFileService);
            executorService.submit(task );
            LOG.info("会员对账单申请  根据 活动  线程启动完成 userId={}, activityId={},task",userId,activityId,task);

            resp.setRespCode(ErrorCode.SUCCESS.getCode());
            resp.setRespMsg(ErrorCode.SUCCESS.getMsg());
            LOG.info("会员对账单申请  根据 活动   返回{}",resp);
	        
		 } catch (Exception e) {
	         LOG.error("会员对账单申请  根据 活动  userId={}, activityId={} 出错!!!",userId,activityId,e);
	         resp=new CheckBillResponse();
	         resp.setRespCode(ErrorCode.ERROR.getCode());
	         resp.setRespMsg(ErrorCode.ERROR.getMsg());
	     } 
	     return resp;
	}

}
