package com.dangjia.acg;

import com.dangjia.acg.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author: QiYuXiang
 * @date: 2018/3/23
 */

@EnableFeignClients
@SpringCloudApplication
public class SesameServiceBasicApplication implements CommandLineRunner {

  @Autowired
  private ConfigService configService;

  public static void main(String[] args) {
    SpringApplication.run(SesameServiceBasicApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    configService.cacheConfig();
  }
}
