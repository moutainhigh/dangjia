package com.dangjia.acg;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringCloudApplication
public class SesameServiceJobApplication implements CommandLineRunner {


  public static void main(String[] args) {
    SpringApplication.run(SesameServiceJobApplication.class, args);
  }
  @Override
  public void run(String... args) throws Exception {

  }
}
