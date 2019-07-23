package com.dangjia.acg.api.web.store;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.store.StoreUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("dangjia-service-master")
@Api(value = "门店人员管理接口", description = "门店人员管理接口")
public interface StoreUserAPI {
    @PostMapping("storeUser/addStoreUser")
    @ApiOperation(value = "新增门店成员", notes = "新增门店成员")
    ServerResponse addStoreUser(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("storeUser") StoreUser storeUser);

    @PostMapping("storeUser/queryStoreUser")
    @ApiOperation(value = "查询门店成员", notes = "查询门店成员")
    ServerResponse queryStoreUser(@RequestParam("storeId") String storeId,
                                  @RequestParam("userName") String userName,
                                  @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("storeUser/updateStoreUser")
    @ApiOperation(value = "编辑门店成员", notes = "编辑门店成员")
    ServerResponse updateStoreUser(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("storeUser") StoreUser storeUser);

    @PostMapping("storeUser/delStoreUser")
    @ApiOperation(value = "删除门店成员", notes = "删除门店成员")
    ServerResponse delStoreUser(@RequestParam("storeUserId") String storeUserId);

}
