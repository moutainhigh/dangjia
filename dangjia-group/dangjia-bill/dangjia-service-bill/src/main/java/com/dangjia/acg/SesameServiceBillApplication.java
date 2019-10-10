package com.dangjia.acg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author: QiYuXiang
 * @date: 2018/3/23
 */

@EnableFeignClients
@EnableScheduling
@SpringCloudApplication
public class SesameServiceBillApplication implements CommandLineRunner {

  @Bean
  @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(SesameServiceBillApplication.class, args);
  }
  @Override
  public void run(String... args) throws Exception {
    String log="0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000000000000      0000000000000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000000                 00000000000000000000000000000000000000000\n" +
            "000000000000000000000000000000000000000                         0000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000                               000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000                                    000000000000000000000000000000\n" +
            "0000000000000000000000000000000000                                      00000000000000000000000000000\n" +
            "000000000000000000000000000000000                                         000000000000000000000000000\n" +
            "00000000000000000000000000000000                                           00000000000000000000000000\n" +
            "00000000000000000000000000000000                                            0000000000000000000000000\n" +
            "0000000000000000000000000000000                                             0000000000000000000000000\n" +
            "0000000000000000000000000000000                                             0000000000000000000000000\n" +
            "00000000000000000000000000000000                                           00000000000000000000000000\n" +
            "000000000000000000000000000000000                                         000000000000000000000000000\n" +
            "0000000000000000000000000000000000                                      00000000000000000000000000000\n" +
            "00000000000000000000000000000000000                                    000000000000000000000000000000\n" +
            "000000000000000000000000000000000000                                 00000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000                             0000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000                      00000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000               000000000000000000000000000000000000000000\n" +
            "000000000000000                                                                          000000000000\n" +
            "0000000000000000                                                                       00000000000000\n" +
            "0000000000000000000                                                                   000000000000000\n" +
            "000000000000000000000                                                               00000000000000000\n" +
            "00000000000000000000000                                                           0000000000000000000\n" +
            "0000000000000000000000000                                                       000000000000000000000\n" +
            "000000000000000000000000000                                                   00000000000000000000000\n" +
            "00000000000000000000000000000                                               0000000000000000000000000\n" +
            "0000000000000000000000000000000                                           000000000000000000000000000\n" +
            "000000000000000000000000000000000                                       00000000000000000000000000000\n" +
            "00000000000000000000000000000000000                                   0000000000000000000000000000000\n" +
            "000000000000000000000000000000000000                                000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000                            00000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000                       00000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000                    0000000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000000               0000000000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000000000         0000000000000000000000000000000000000000000000\n" +
            "000000000000000000000000000000000000000000000000     000000000000000000000000000000000000000000000000\n" +
            "0000000000000000000000000000000000000000000000000  00000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n" +
            "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000\n";
    System.out.println(log);
    System.out.println("启动完成！！！");
  }


}
