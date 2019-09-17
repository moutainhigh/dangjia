package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/17
 * Time: 14:15
 */
@Api(description = "材料精算接口")
@FeignClient("dangjia-service-goods")
public interface DjActuaryBudgetMaterialAPI {

    @PostMapping("/product/djActuaryBudgetMaterial/makeBudgets")
    @ApiOperation(value = "生成精算", notes = "生成精算")
    ServerResponse makeBudgets(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("actuarialTemplateId") String actuarialTemplateId,
                               @RequestParam("houseId") String houseId,
                               @RequestParam("workerTypeId") String workerTypeId,
                               @RequestParam("listOfGoods") String listOfGoods);
}
