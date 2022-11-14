package com.akucun.checkbill.Integration;


import com.akucun.checkbill.common.StrUtils;
import com.akucun.checkbill.common.constant.DFSCloudEnum;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 阿里云OSS上传文件工具
 */
@Component()
//@Scope("prototype")
public class AliyunOssIntegrationImpl implements AliyunOssIntegration
{
	private static Logger logger = LoggerFactory.getLogger(AliyunOssIntegrationImpl.class);

	private OSSClient client = null;
	
	@Value("${oss.bucketName}")
	private String bucketName = null;
	
	@Value("${oss.endpoint}")
	private String endpoint;
	
	@Value("${oss.cdnhost}")
	private String cdnHost;
	
	@Value("${oss.accessKeyId}")
	private String keyid;
	
	@Value("${oss.accessKeySecret}")
	private String keySecret;
	
	@Value("${excel.dfs.cloud}")
	private String dfsCloud;
	
	@PostConstruct
	public int init()
	{
		int ret = 0;
		DFSCloudEnum dsfCloudEnum=DFSCloudEnum.valueOf(dfsCloud);
       if(dsfCloudEnum == DFSCloudEnum.alibaba )
       {
    		logger.info("dfs.cloud current is alibaba init connection ");	   
			this.endpoint = StrUtils.null2string(this.endpoint).trim();
			if (this.endpoint.endsWith("/"))
				this.endpoint = this.endpoint.substring(0, this.endpoint.length() - 1);
			String accessKeyId = StrUtils.null2string(this.keyid).trim();
			String accessKeySecret = StrUtils.null2string(this.keySecret).trim();
			this.cdnHost = StrUtils.null2string(this.cdnHost).trim();
			this.bucketName = StrUtils.null2string(this.bucketName).trim();
			if (StrUtils.isEmpty(this.cdnHost)) {// 如果无设置则自动为OSS访问地址
				this.cdnHost = "http://" + this.bucketName + "." + this.endpoint;
			} else {
				if (this.cdnHost.endsWith("/"))
					this.cdnHost = this.cdnHost.substring(0, this.cdnHost.length() - 1);
				if (!this.cdnHost.startsWith("http://") && !this.cdnHost.startsWith("https://"))
					this.cdnHost = "http://" + this.cdnHost;
			}
	
			client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
			logger.debug("current.bucketName:" + this.bucketName);
			/*if (!client.doesBucketExist(this.bucketName)) {
				this.close();
				throw new RuntimeException("Bucket={" + this.bucketName + "}不存在，无法初始化");
			}*/
	    }
		return ret;
      
	}
	
	@PreDestroy
	public void close() {
		if (client != null) {
			client.shutdown();
			client = null;
		}
	}

	public String getBucketName() {
		return this.bucketName;
	}

	public boolean existsFile(String ossKey) {
		boolean ret = false;
		if (StrUtils.isEmpty(ossKey))
			return ret;
		try {
			ret = client.doesObjectExist(this.bucketName, ossKey);
		} catch (Error e) {
			logger.error("检查文件{" + ossKey + "}存在时异常:", e);
		}
		return ret;
	}

	/**
	 * 创建成功返回大于0，如果已存在会直接覆盖，请使用existsFile(key)来检查是否存在
	 * 
	 * @param file
	 * @param ossname
	 * @return
	 * @throws Throwable
	 */
	public int createFile(File file, String ossname) {
		int ret = -1;
		FileInputStream fin = null;
		if (!file.exists()) {
			if (logger.isDebugEnabled())
				logger.debug("文件{" + file.getAbsolutePath() + "}不存在,无法创建");
			return -2;
		}
		try {
			fin = new FileInputStream(file);

			PutObjectResult result = client.putObject(bucketName, ossname, fin);
			/*
			 * UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName,
			 * ossname); uploadFileRequest.setUploadFile(file.getAbsolutePath());
			 * UploadFileResult uploadResult = client.uploadFile(uploadFileRequest);
			 * CompleteMultipartUploadResult result =
			 * uploadResult.getMultipartUploadResult();
			 * System.out.println("etag-----"+result.getETag()+"---"+result.getKey());
			 */

			if (result != null/* && !StrUtil.isEmpty(result.getKey()) */) {
				ret = 1;
			} else {
				if (logger.isDebugEnabled())
					logger.debug("create(" + ossname + ")的结果为:" + result.getResponse().toString());
			}

		} catch (Exception oe) {

			throw new RuntimeException("createFile(" + ossname + ")异常:", oe);

		} catch (Throwable th) {
			logger.error("上传文件" + ossname + "异常", th);

		} finally {
			if (fin != null)
				IOUtils.closeQuietly(fin);
		}
		return ret;
	}

	public int createFolder(String dir) {
		if (StrUtils.isEmpty(dir))
			return -1;
		if (!dir.endsWith("/"))
			dir += "/";
		int ret = 0;
		try {
			client.putObject(this.bucketName, dir, new ByteArrayInputStream(new byte[0]));
			ret = 1;
		} catch (Exception e) {
			logger.error("创建文件夹{" + dir + "}异常:", e);
		}
		return ret;
	}

	/**
	 * 根据对象id,生成CDN访问URL
	 * 
	 * @param objKey
	 * @param isPublic
	 *            as Boolean
	 * @return
	 */
	public String genCdnUrl(String objKey, boolean isPublic) {
		if (isPublic) {
			String uri = this.cdnHost + "/" + objKey;
			return uri;
		}
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.add(Calendar.DATE, 1);// +1天
		URL url = client.generatePresignedUrl(bucketName, objKey, cal.getTime());
		if (url != null)
			return url.toString();
		return null;
	}

	/**
	 * 根据对象ID的前缀返回对应的ID列表，该方法限制于API接口，只返回前100个
	 * 
	 * @param keyPrefix
	 * @return
	 */
	public List<String> getObjectListByPrefix(String keyPrefix) {
		List<String> list1 = new ArrayList<String>();
		try {
			ObjectListing objList = client.listObjects(bucketName, keyPrefix);
			List<OSSObjectSummary> sums = objList.getObjectSummaries();
			for (OSSObjectSummary s : sums) {
				list1.add(s.getKey());
			}
		} catch (Exception e) {
			logger.error("获取以prefix={" + keyPrefix + "}的对象异常：", e);
		}

		return list1;
	}

	public int deleteFile(String key) {
		int ret = 0;
		try {
			client.deleteObject(this.bucketName, key);
			ret = 1;
		} catch (Exception e) {
			logger.error("删除文件{" + key + "}异常:", e);
			ret = -1;
		}
		return ret;
	}

	public int deleteFiles(List<String> keys) {
		DeleteObjectsResult deleteObjectsResult = client
				.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
		int ret = -1;
		if (deleteObjectsResult != null) {
			ret = deleteObjectsResult.getResponse().getStatusCode();
		}
		return ret;

	}


	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("java.endorsed.dirs"));

		AliyunOssIntegrationImpl util = new AliyunOssIntegrationImpl();
		util.init();
		// 获取所有Bucket存储空间
		// List<Bucket> buckets = util.client.listBuckets();
		// for(Bucket bucket : buckets) {
		// System.out.println("----" + bucket.getName()+"--"+bucket.getCreationDate());
		// }

		String fname = "test" + RandomStringUtils.randomAlphabetic(32) + ".mp4";
		int ret = util.createFile(new File("C:\\Users\\Administrator\\Videos\\58ef612871e9ea14cc4ca69bb1acbeb4.mp4"),
				fname);
		System.out.println("upload result---" + fname + "---++++++++++" + ret);
		// util.deleteFile("2017/11//");
		// util.deleteFile("2017/11/Attach4028b8815fc321f4015fc32c23930005.png");
		List<String> ids = util.getObjectListByPrefix("2017/11/");
		System.out.println("upload result---" + ids);

		// String uri = util.genCdnUrl("test4028b8815fc2d406015fc2d406de0000.jpg");
		// System.out.println("uri------------"+uri);

		util.close();
	}

}
