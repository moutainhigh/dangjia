package com.dangjia.acg;


import com.dangjia.acg.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@EnableScheduling
@EnableFeignClients
@SpringCloudApplication
public class SesameServiceJobApplication implements CommandLineRunner {


  @Autowired
  private JobService jobService;

  public static void main(String[] args) {
    SpringApplication.run(SesameServiceJobApplication.class, args);
  }


  /**
   * Callback used to run the bean.
   *
   * @param args incoming member method arguments
   * @throws Exception on error
   */
  @Override
  public void run(String... args) throws Exception {
  }
}
