package com.akucun.checkbill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author xiaojin_wu
 * @email wuxiaojin258@126.com
 * @date 2018年1月31日
 * @description 开发环境读取dubbo配置项
 */
@Configuration
@ImportResource({ "classpath*:dubbo/spring-dubbo*.xml" })
public class DubboConfig {

}
