package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:03
 */
@Api(description = "供应商注册管理接口")
@FeignClient("dangjia-service-master")
public interface DjRegisterApplicationAPI {

    @PostMapping("/sup/djRegisterApplication/registerSupAndStorefront")
    @ApiOperation(value = "供应商/店铺注册", notes = "供应商/店铺注册")
    ServerResponse registerSupAndStorefront(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("djRegisterApplication") DjRegisterApplication djRegisterApplication);


}
