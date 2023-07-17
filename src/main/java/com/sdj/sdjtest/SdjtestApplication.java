package com.sdj.sdjtest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = "com.sdj.sdjtest")
@MapperScan(basePackages = "com.sdj.sdjtest.mapper")
@SpringBootApplication
public class SdjtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdjtestApplication.class, args);
	}

}
