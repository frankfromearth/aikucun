package com.akucun.checkbill.test;

import com.akucun.checkbill.common.constant.DFSCloudEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaojin on 4/3/2018.
 */
public class MainTest {

        public static void main(String[] args) {
            DFSCloudEnum dsfCloudEnum=DFSCloudEnum.valueOf("alibaba");
            System.out.println(dsfCloudEnum==DFSCloudEnum.alibaba);

            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM");
            Date fromDate=null;
            try {
                fromDate=format.parse("2018-2018-01");
                System.out.println(fromDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

}
