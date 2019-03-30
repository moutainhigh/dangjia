package com.dangjia.acg.api.complain;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
/**
 *
 */
@FeignClient("dangjia-service-master")
@Api(value = "申述接口", description = "申述接口")
public interface ComplainAPI {

    @PostMapping("/complain/addComplain")
    @ApiOperation(value = "添加申述", notes = "添加申述")
    ServerResponse addComplain(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("userToken") String userToken,
                               @RequestParam("complainType") Integer complainType,
                               @RequestParam("businessId") String businessId,
                               @RequestParam("houseId") String houseId);

    @PostMapping("/complain/getComplainList")
    @ApiOperation(value = "查询申述", notes = "查询申述")
    ServerResponse getComplainList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("complainType") Integer complainType,
                                   @RequestParam("state") Integer state,
                                   @RequestParam("searchKey") String searchKey);


    @PostMapping("/complain/updataComplain")
    @ApiOperation(value = "修改申述", notes = "修改申述")
    ServerResponse updataComplain(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("complainId") String complainId,
                                  @RequestParam("state") Integer state,
                                  @RequestParam("description") String description,
                                  @RequestParam("files") String files);

    @PostMapping("/complain/getComplain")
    @ApiOperation(value = "获取申述详情", notes = "获取申述详情")
    ServerResponse getComplain(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("complainId") String complainId);

}
