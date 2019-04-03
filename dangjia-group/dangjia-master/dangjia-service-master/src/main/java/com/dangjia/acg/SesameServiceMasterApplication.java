package com.dangjia.acg;

import org.apache.shiro.codec.Base64;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import javax.servlet.MultipartConfigElement;

/**
 * @author: QiYuXiang
 * @date: 2018/3/23
 */

@EnableFeignClients
@EnableScheduling
@SpringCloudApplication
public class SesameServiceMasterApplication implements CommandLineRunner {

  @Bean
  @LoadBalanced
  RestTemplate restTemplate() {
    return new RestTemplate();
  }


  public static void main(String[] args) {
    SpringApplication.run(SesameServiceMasterApplication.class, args);
  }
 
  @Override
  public void run(String... args) throws Exception {
    String fozuStr = "CiAgICAgICAgICAgICAgICAgICBfb29Pb29fCiAgICAgICAgICAgICAgICAgIG84ODg4ODg4bwogICAgICAgICAgICAgICAgICA4OCIgLiAiODgKICAgICAgICAgICAgICAgICAgKHwgLV8tIHwpCiAgICAgICAgICAgICAgICAgIE9cICA9ICAvTwogICAgICAgICAgICAgICBfX19fL2AtLS0nXF9fX18KICAgICAgICAgICAgIC4nICBcXHwgICAgIHwvLyAgYC4KICAgICAgICAgICAgLyAgXFx8fHwgIDogIHx8fC8vICBcCiAgICAgICAgICAgLyAgX3x8fHx8IC06LSB8fHx8fC0gIFwKICAgICAgICAgICB8ICAgfCBcXFwgIC0gIC8vLyB8ICAgfAogICAgICAgICAgIHwgXF98ICAnJ1wtLS0vJycgIHwgICB8CiAgICAgICAgICAgXCAgLi1cX18gIGAtYCAgX19fLy0uIC8KICAgICAgICAgX19fYC4gLicgIC8tLS4tLVwgIGAuIC4gX18KICAgICAgLiIiICc8ICBgLl9fX1xfPHw+Xy9fX18uJyAgPiciIi4KICAgICB8IHwgOiAgYC0gXGAuO2BcIF8gL2A7LmAvIC0gYCA6IHwgfAogICAgIFwgIFwgYC0uICAgXF8gX19cIC9fXyBfLyAgIC4tYCAvICAvCj09PT09PWAtLl9fX19gLS5fX19cX19fX18vX19fLi1gX19fXy4tJz09PT09PQogICAgICAgICAgICAgICAgICAgYD0tLS09JwpeXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl5eXl4KICAgICAg5L2b56WW5L+d5L2RLOWkp+WQieWkp+WIqe+8jOebruagh+WQg+ifue+8jOawuOaXoEJVRw==";
    byte[] decode = Base64.decode(fozuStr);
    System.out.println("\n"+new String(decode));
  }


}
