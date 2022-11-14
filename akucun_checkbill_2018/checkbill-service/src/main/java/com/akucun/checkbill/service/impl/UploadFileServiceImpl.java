package com.akucun.checkbill.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.akucun.checkbill.Integration.AliyunOssIntegration;
import com.akucun.checkbill.Integration.TencentCOSIntegration;
import com.akucun.checkbill.common.constant.DFSCloudEnum;
import com.akucun.checkbill.service.UploadFileService;

/**
 * 云服务封装
 * Created by zhaojin on 3/30/2018.
 */
@Service
public class UploadFileServiceImpl implements UploadFileService {
	public static final Logger LOG= LoggerFactory.getLogger(UploadFileServiceImpl.class);
    @Autowired
    AliyunOssIntegration aliyunOssIntegration;
   
    @Autowired
    TencentCOSIntegration tencentCOSIntegration;

	@Value("${excel.dfs.cloud}")
	private String dfsCloud;
	

    /**
     *
     * @param filename
     * @param file
     * @return  上传后的URL
     * @throws Exception
     */
    public String  uploadFile(String filename,File file) throws Exception
    {
    	LOG.info("dfsCloud={}",dfsCloud);
    	DFSCloudEnum dsfCloudEnum=DFSCloudEnum.valueOf(dfsCloud);
    	String  url=null;
        if(dsfCloudEnum == DFSCloudEnum.alibaba )
        {
        	url=alibabaUploadFile(filename,file);
        	
        }else if (dsfCloudEnum == DFSCloudEnum.tecent )
        {
        	url=tecentUploadFile(filename,file);
        }else 
        {
        	throw new Exception("云配置错误，找不到对应的云服务");
        }
        return url;
    }
    private String  alibabaUploadFile(String filename,File file)throws Exception
    {
    	LOG.info("alibabaUploadFile");
    	 //上传阿里云
//      aliyunOssIntegration.init();
      try{
          SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
          String dateStr= format.format(new Date());
          aliyunOssIntegration.createFolder(dateStr);

          String dateFileName=dateStr+"/"+filename;
          int ret = aliyunOssIntegration.createFile(file, dateFileName);
          if (ret > 0) {// 上传成功
             String  url = this.aliyunOssIntegration.genCdnUrl(dateFileName, true);// 得到获取图片oss路径
              return url ;
          } else {
              throw new Exception("上传失败");
          }
      }finally {
//          aliyunOssIntegration.close();
      }
    }
    
    
    private String  tecentUploadFile(String filename,File file) throws Exception
    {
    	LOG.info("tecentUploadFile");
        String url =null;
        try{
//            tencentCOSIntegration.init();
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
            String dateStr= format.format(new Date());
            String dateFileName=dateStr+"/"+filename;

            int ret = tencentCOSIntegration.createFile(file, dateFileName);
            //index=1  上传成功    index!=1 上传失败
            if(ret!=1)
                throw new  Exception(file.getAbsolutePath()+"上传失败");
            //int sss = tc.deleteFile(fname);//删除
             url = tencentCOSIntegration.getURL(dateFileName);//获取下载地址
        }finally {
//            tencentCOSIntegration.close();
        }
        return url;
    }
}
