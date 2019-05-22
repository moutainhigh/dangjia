package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/15
 * Time: 20:37
 */
@FeignClient("dangjia-service-master")
@Api(value = "更新人工商品", description = "更新人工商品")
public interface MasterMendWorkerAPI {

    @PostMapping("/repair/updateMendWorker")
    @ApiOperation(value = "修改商品库内商品,全局更新", notes ="修改商品库内商品,全局更新")
    ServerResponse updateMendWorker(@RequestParam("lists") String lists);

}
