package com.dangjia.acg.api.web.finance;

import io.swagger.annotations.Api;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:48
 */
@FeignClient("dangjia-service-master")
@Api(value = "财务部功能", description = "财务部功能")
public interface WebFinanceAPI {
}
