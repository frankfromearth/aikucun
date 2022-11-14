package com.akucun.checkbill.Integration;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.akucun.akucunapi.common.pojo.SendPushParam;
import com.akucun.akucunapi.mq.service.IRocketMqService;
/**
 *   发MQ 到消息平台，可以推送消息到手机端
 * @author zhaojin
 *
 */
@Component
public class PushAppNotify
{
	Logger LOG=LoggerFactory.getLogger(PushAppNotify.class);
	
	@Value("AKUCUN_SEND_PUSH")
	private String mqAppNotifyTopic;

	@Value("SEND_PUSH_WITHOUT_SYSTEM")
	private String mqAppNotifyTag;
	
	 @Autowired
	 private IRocketMqService          rocketMqService;

	 
	public void notityMsg(String userId,String msg)
	{
		  LOG.info("推送消息到手机端 发送MQ  开始  userId={},msg={}",userId,msg);
	      SendPushParam param = new SendPushParam();
	      param.setUserid(userId);
	      param.setBadge(1);
	      param.setContent(msg);// 	通知内容
//	      param.setMessages();//透传参数
	      //param.setIos(false);
	      try {
	    	  rocketMqService.asyncSend(mqAppNotifyTopic, mqAppNotifyTag, param, UUID.randomUUID().toString().replace("-", ""));  
	      }catch(Exception e)
	      {
	    	  LOG.info("推送消息到手机端  发送MQ  错误   !!!,不影响其它业务流程 userId={},msg={}",userId,msg,e);
	      }
	      LOG.info("推送消息到手机端 发送MQ   完成   userId={},msg={}",userId,msg);
	}
}
