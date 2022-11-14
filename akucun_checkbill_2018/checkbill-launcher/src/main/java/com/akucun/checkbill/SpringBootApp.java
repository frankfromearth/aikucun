package com.akucun.checkbill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages ="com.akucun.checkbill.dao.mapper")
public class SpringBootApp {
// java org.springframework.boot.loader.JarLauncher  --spring.profiles.active=test
	public static void main(String[] args) {
		SpringApplication.run(SpringBootApp.class, args);
		com.alibaba.dubbo.registry.support.AbstractRegistryFactory x;
	}
}
