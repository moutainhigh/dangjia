package com.dangjia.acg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import javax.servlet.annotation.WebListener;

@SpringBootApplication
@EnableEurekaServer
@WebListener
public class SesameEurekaApplication {

	public static void main(String[] args) {
		//sadas
		SpringApplication.run(SesameEurekaApplication.class, args);
	}
}
