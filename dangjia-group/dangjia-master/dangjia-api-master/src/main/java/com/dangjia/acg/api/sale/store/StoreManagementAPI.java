package com.dangjia.acg.api.sale.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.Store;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 11:04
 */
@FeignClient("dangjia-service-master")
@Api(value = "门店管理页接口", description = "门店管理页接口")
public interface StoreManagementAPI {

    @PostMapping(value = "sale/store/storeManagementPage")
    @ApiOperation(value = "门店管理页", notes = "门店管理页")
    ServerResponse storeManagementPage(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("pageDTO") PageDTO pageDTO);


}
