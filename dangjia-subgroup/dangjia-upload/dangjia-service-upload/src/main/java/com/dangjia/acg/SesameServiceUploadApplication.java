package com.dangjia.acg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.MultipartConfigElement;

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

  @Bean
  MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setLocation("/data/dangjia/temporary/");
    return factory.createMultipartConfig();
  }

  private CorsConfiguration buildConfig(){
    CorsConfiguration corsConfiguration=new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    return corsConfiguration;
  }

}
