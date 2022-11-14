package com.akucun.checkbill.Integration;

import com.akucun.checkbill.common.constant.DFSCloudEnum;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 腾讯云 对象存储cos 文件上传 工具
 * @ClassName: TencentCOSUtil  
 * @Description: TODO 
 * @author: aaa 
 * @date:2018年2月27日 下午3:22:25
 */
@Component
//@Scope("prototype")
public class TencentCOSIntegration
{
	private static Logger logger = LoggerFactory.getLogger(TencentCOSIntegration.class);
	
	/** APPID	开发者访问 COS 服务时拥有的用户维度唯一资源标识，用以标识资源
		SecretId	开发者拥有的项目身份识别 ID，用以身份认证
		SecretKey	开发者拥有的项目身份密钥
		Bucket	COS 中用于存储数据的容器
		Object	COS 中存储的具体文件，是存储的基本实体
		Region	域名中的地域信息。枚举值参见 可用地域 文档，如：ap-beijing, ap-hongkong, eu-frankfurt 
		ACL	        访问控制列表（Access Control List），是指特定 Bucket 或 Object 的访问控制信息列表
		CORS	 跨域资源共享（Cross-Origin Resource Sharing），指发起请求的资源所在域不同于该请求               所指向资源所在的域的 HTTP 请求
		Multipart Uploads	分块上传，腾讯云 COS 服务为上传文件提供的一种分块上传模式
	 * */

	@Value("${cos.default.secretID}")
	String secretid ;
	
	@Value("${cos.default.secretKey}")
	String secretkey;

	@Value("${cos.default.region}")
	String regin ;
	
	@Value("${cos.default.bucketName}")
	String bucketName ;

	@Value("${excel.dfs.cloud}")
	private String dfsCloud;
	
	private  COSClient cosclient = null;

	@PostConstruct
	public void init()
	{
	   DFSCloudEnum dsfCloudEnum=DFSCloudEnum.valueOf(dfsCloud);
       if(dsfCloudEnum == DFSCloudEnum.tecent )
       {
    		logger.info("dfs.cloud current is tecent init connection ");	  
    		logger.info(secretid+"      "+secretkey+"        "+bucketName);
    		System.out.println(secretid+"      "+secretkey+"        "+bucketName+"      "+regin);
    		// 1 初始化用户身份信息(secretId, secretKey)
    		COSCredentials cred = new BasicCOSCredentials(secretid, secretkey);
    		// 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
    		ClientConfig clientConfig = new ClientConfig(new Region(regin));
    		// 3 生成cos客户端
    		cosclient = new COSClient(cred, clientConfig);
       }
	}
	@PreDestroy
	public void close() {
		if (cosclient != null) {
			cosclient.shutdown();
			cosclient = null;
		}
	}

	
	/**
	 * 创建成功返回大于0，如果已存在会直接覆盖，请使用existsFile(key)来检查是否存在
	 * 
	 * @param file
	 * @param cosname
	 * @return
	 * @throws Throwable
	 */
	public int createFile(File file, String cosname) {
		int ret = -1;
		if (!file.exists()) {
			if (logger.isDebugEnabled())
				logger.debug("文件{" + file.getAbsolutePath() + "}不存在,无法创建");
			return -2;
		}
		try {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,cosname, file);
			PutObjectResult result = cosclient.putObject(putObjectRequest);

			if (result != null) {
				ret = 1;
			} else {
				// TODO: handle exception
				if (logger.isDebugEnabled())
					logger.debug("create(" + cosname + ")的结果为:" + result);
			}
		} catch (Exception oe) {

			oe.printStackTrace();
			throw new RuntimeException("createFile(" + cosname + ")异常:", oe);

		} catch (Throwable th) {
			logger.error("上传文件" + cosname + "异常", th);

		} 
		return ret;
	}
	/**
	 * 删除 桶 里面文件
	 * @Title: deleteFile  
	 * @Description: TODO 
	 * @param @param cosname  文件访问路径s
	 * @param @return 
	 * @return int 
	 * @throws
	 */
	public int deleteFile(String cosname) {
		int ret = 1;
		try {
			 logger.info("删除cos上文件，文件名为:"+cosname);
			 cosclient.deleteObject(bucketName,cosname);
		} catch (Exception oe) {
            ret = -1 ;
			oe.printStackTrace();
			throw new RuntimeException("createFile(" + cosname + ")异常:");

		}
		return ret;
	}
	
	/**
	 * 获取上传文件地址
	 * @Title: getURL  
	 * @Description: TODO 
	 * @param @param key
	 * @param @return 
	 * @return String 
	 * @throws
	 */
	public String getURL(String cosname) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.add(Calendar.DATE, 1);// +1天
		URL url = cosclient.generatePresignedUrl(bucketName, cosname, cal.getTime());
		if (url != null)
		{
			String strURL=url.toString();
			int index=strURL.indexOf("?");
			String resURL=strURL.substring(0,index);
			return resURL;
		}
		return null;
	}
	
	public static void main(String[] args) {

		TencentCOSIntegration tc = new TencentCOSIntegration();
			File file =  new File("E:\\123.txt");
			String fname="123.txt";

			tc.init();
			int ret = tc.createFile(file, fname);
			tc.close();
			System.out.println(ret);
			//int sss = tc.deleteFile(fname);//删除
			String url = tc.getURL(fname);//获取下载地址


	}
}
