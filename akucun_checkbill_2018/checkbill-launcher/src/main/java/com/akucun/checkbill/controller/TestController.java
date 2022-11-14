package com.akucun.checkbill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akucun.checkbill.facade.CheckBillFacade;
import com.akucun.checkbill.facade.bean.CheckBillResponse;
import com.akucun.checkbill.service.IUserService;


@RestController
@RequestMapping("/test")
public class TestController {

	@Autowired
	private IUserService userService;

//	@RequestMapping(value = "/test")
//	public List<User> test() {
//		User param = new User();
//
//		List<User> list = userService.selectList(param);
//		return list;
//	}


	@Autowired
	private CheckBillFacade checkBillFacade;


	@RequestMapping(value = "/testApply")
	public CheckBillResponse apply(String userId,String dateYearMonth) {

		CheckBillResponse resp=checkBillFacade.userCheckBillApplyByMonth(userId,dateYearMonth);
		System.out.println(resp);
		return resp;
	}
}
