package com.akucun.checkbill.test;

import com.akucun.checkbill.SpringBootApp;
import com.akucun.checkbill.facade.CheckBillFacade;
import com.akucun.checkbill.facade.bean.CheckBillResponse;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=SpringBootApp.class) 
@SpringBootConfiguration
@ContextConfiguration 
public class AkucunCheckbillApplicationTests 
{
	@Autowired
	private CheckBillFacade checkBillFacade;

	@Test
	public void contextLoads() {

	}
	
//	@Test
//	public void testQuery() {
//		Calendar calendar=Calendar.getInstance();
//		Date endTime=calendar.getTime();
//		calendar.add(Calendar.DATE,-10);
//		Date startTime=calendar.getTime();
//
//		CheckBillResponse resp=checkBillFacade.userCheckBillQuery("userId",startTime,endTime,1,20);
//		System.out.println(resp);
//	}
	
 
	@AfterClass
	public static void destory()
	{
		try {
			Thread.currentThread().sleep(2*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testApplyByMonth() {
		String userId="7653a67f643907a355967096e59795d4";
		String dateYearMonth="2018-03"; 
		CheckBillResponse resp=checkBillFacade.userCheckBillApplyByMonth(userId,dateYearMonth);
		System.out.println(resp);
		 
	}

	@Test
	public void testApplyByDelivery() {

		// select * from sc_akucun_deliver_table where adorderid='AD20180310661667260987' and caigouzhe='7653a67f643907a355967096e59795d4'

		String userId="29e3e18ab2c78364ecce30343d2c4c08";
		String deliveryOrderNo="AD20180318660183763613";
		CheckBillResponse resp=checkBillFacade.userCheckBillApplyByDelivery(userId,deliveryOrderNo);
		System.out.println(resp);
		
	 
	}
	
	@Test
	public void testApplyByActivity() {

		//select * from sc_akucun_deliver_table where liveid='2c908993622a9fe3016232be9bd1457b' and caigouzhe='7653a67f643907a355967096e59795d4'


		String userId="7653a67f643907a355967096e59795d4";
//		String activityId="2c908993618375670161e0f3cdbd7421"; 
		String activityId="2c908993622a9fe3016232be9bd1457b";//cancel
		CheckBillResponse resp=checkBillFacade.userCheckBillApplyByActivity(userId,activityId);
		System.out.println(resp);
		 
	}
}
