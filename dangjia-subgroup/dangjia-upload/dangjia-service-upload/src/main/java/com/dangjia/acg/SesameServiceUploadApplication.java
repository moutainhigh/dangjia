package com.dangjia.acg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.cors.CorsConfiguration;

/**
 * @author: QiYuXiang
 * @date: 2018/3/23
 */

@EnableFeignClients
@SpringCloudApplication
@SpringBootApplication
public class SesameServiceUploadApplication {

  public static void main(String[] args) {
    SpringApplication.run(SesameServiceUploadApplication.class, args);
  }


  private CorsConfiguration buildConfig(){
    CorsConfiguration corsConfiguration=new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    return corsConfiguration;
  }

}
