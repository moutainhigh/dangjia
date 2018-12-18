package com.dangjia.acg.swagger;

import com.dangjia.acg.common.annotation.ApiMethod;
import com.google.common.base.Predicate;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({"dev", "test","int","pre"})
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        Predicate<RequestHandler> predicate = new Predicate<RequestHandler>() {
            @Override
            public boolean apply(RequestHandler input) {
                Class<?> declaringClass = input.declaringClass();
                ApiMethod method  =  input.getHandlerMethod().getMethodAnnotation(ApiMethod.class);
                if(!declaringClass.getPackage().getName().startsWith("com.dangjia.acg")){
                    return false;
                }
                if (declaringClass == BasicErrorController.class) {// 排除
                    return false;
                }
                if(declaringClass.isAnnotationPresent(RestController.class) && null != method) { // 被注解的类
                    return true;
                }
                if(input.isAnnotatedWith(ResponseBody.class) && null != method) { // 被注解的方法
                    return true;
                }

                    return false;
            }
        };
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(predicate)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("当家系统接口文档")//大标题
            .version("1.0")//版本
            .build();
    }
}
