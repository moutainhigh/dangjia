package com.dangjia.acg;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author: QiYuXiang
 * @date: 2018/3/23
 */

@EnableFeignClients
@SpringCloudApplication
public class SesameServiceSearchApplication {

  public static void main(String[] args) {
    SpringApplication.run(SesameServiceSearchApplication.class, args);
  }

}
