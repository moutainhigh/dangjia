package com.dangjia.acg;


import com.dangjia.acg.common.error.ErrorAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpStatus;

@SpringBootApplication
@ComponentScan(basePackages = "com.dangjia.acg", excludeFilters={
@ComponentScan.Filter(type=FilterType.ASSIGNABLE_TYPE, value= ErrorAdvice.class)})
@EnableEurekaClient
@EnableZuulProxy
public class GatewayApplication {

	@Bean
	public ErrorFilter setExcepFilter(){
		return new ErrorFilter();
	}
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
		return new EmbeddedServletContainerCustomizer(){
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
				container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
				//container.addErrorPages(new ErrorPage(Exception.class),"ddd");
				container.addErrorPages(new ErrorPage(Throwable.class,"/error/throwable"));

			}
		};
	}
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
