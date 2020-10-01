package com.advanced.protection.systems.positiondata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableReactiveFeignClients
public class PositionDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(PositionDataApplication.class, args);
	}

}
