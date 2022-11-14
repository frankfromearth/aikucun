package com.akucun.checkbill.Integration;

import java.io.File;
import java.util.List;

/**
 * 阿里云OSS操作接口封装，使用的时候<br>
 * 需要调用init()和close();
 * @author yeriwei
 *
 */
public interface AliyunOssIntegration {

	public int init();
	
	public boolean existsFile(String ossKey);
	
	/**
	 * 创建成功返回大于0，如果已存在会直接覆盖，请使用existsFile(key)来检查是否存在
	 * 
	 * @param file
	 * @param ossname
	 * @return
	 * @throws Throwable
	 */
	public int createFile(File file, String ossname);
	
	public int createFolder(String dir);
	
	/**
	 * 根据对象id,生成CDN访问URL
	 * 
	 * @param objKey
	 * @param isPublic
	 *            as Boolean
	 * @return
	 */
	public String genCdnUrl(String objKey, boolean isPublic);
	
	/**
	 * 根据对象ID的前缀返回对应的ID列表，该方法限制于API接口，只返回前100个
	 * 
	 * @param keyPrefix
	 * @return
	 */
	public List<String> getObjectListByPrefix(String keyPrefix);
	
	public int deleteFile(String key);
		
	public int deleteFiles(List<String> keys);
	
	
	public void close();
}
