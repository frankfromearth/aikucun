package com.akucun.checkbill.dao.detail;

/**
 *
 * 会员对账单 按活动 导出excel格式
 * Created by zhaojin on 3/30/2018.
 */
public class CheckBillActivityDetail extends  AbstractDetail
{

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CheckBillActivityDetail [").append(super.toString()).append("]");
		return builder.toString();
	}
 
 
}